package com.streamvisor.queryabletopics.service;

import static com.streamvisor.queryabletopics.util.PulsarUtil.deriveId;

import com.streamvisor.queryabletopics.config.Topics;
import com.streamvisor.queryabletopics.model.*;
import com.streamvisor.queryabletopics.util.PulsarUtil;
import java.util.List;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final PulsarUtil pulsarUtil;
    private final TableView<NameRecord> trackNameTableView;
    private final TableView<NameRecord> artistNameTableView;
    private final TableView<CountRecord> trackCountTableView;
    private final TableView<CountRecord> artistCountTableView;
    private final TableView<CountRecord> playCountTableView;
    private final TableView<ChartRecord> trackChartTableView;
    private final TableView<ChartRecord> artistChartTableView;

    @Autowired
    StatisticServiceImpl(PulsarUtil pulsarUtil) throws PulsarClientException {
        this.pulsarUtil = pulsarUtil;

        trackNameTableView = pulsarUtil.createTableView(NameRecord.class, Topics.TRACKS);
        artistNameTableView = pulsarUtil.createTableView(NameRecord.class, Topics.ARTISTS);
        artistCountTableView = pulsarUtil.createTableView(CountRecord.class, Topics.ARTIST_COUNTS);
        trackCountTableView = pulsarUtil.createTableView(CountRecord.class, Topics.TRACK_COUNTS);
        playCountTableView = pulsarUtil.createTableView(CountRecord.class, Topics.PLAY_COUNTS);
        trackChartTableView = pulsarUtil.createTableView(ChartRecord.class, Topics.TRACK_CHARTS);
        artistChartTableView = pulsarUtil.createTableView(ChartRecord.class, Topics.ARTIST_CHARTS);

        updateChartStream(artistCountTableView, Topics.ARTIST_CHARTS, artistChartTableView);
        updateChartStream(trackCountTableView, Topics.TRACK_CHARTS, trackChartTableView);
    }

    @PulsarListener(topics = Topics.STREAMS)
    private void createAggregatedStreams(StreamRecord streamRecord) {
        String trackId = deriveId(streamRecord.trackName());
        String artistId = deriveId(streamRecord.artistName());

        pulsarUtil.sendMessage(
                trackId, Topics.TRACKS, new NameRecord(trackId, streamRecord.trackName()));
        pulsarUtil.sendMessage(
                artistId, Topics.ARTISTS, new NameRecord(artistId, streamRecord.artistName()));

        updateCountStream(
                new CompositeKey(streamRecord.userId(), trackId).toString(),
                trackId,
                Topics.TRACK_COUNTS,
                trackCountTableView);
        updateCountStream(
                new CompositeKey(streamRecord.userId(), artistId).toString(),
                artistId,
                Topics.ARTIST_COUNTS,
                artistCountTableView);
        updateCountStream(
                streamRecord.userId(),
                streamRecord.userId(),
                Topics.PLAY_COUNTS,
                playCountTableView);
    }

    private void updateCountStream(
            String key, String recordId, String topic, TableView<CountRecord> countTableView) {
        CountRecord currentCount = countTableView.get(key);
        long newCount = (currentCount == null) ? 1 : (currentCount.count() + 1);
        CountRecord newRecord = new CountRecord(recordId, newCount);
        pulsarUtil.sendMessage(key, topic, newRecord);
    }

    private void updateChartStream(
            TableView<CountRecord> countRecordTableView,
            String topic,
            TableView<ChartRecord> chartRecordTableView) {
        countRecordTableView.forEachAndListen(
                (messageKey, countRecord) -> {
                    CompositeKey key = CompositeKey.from(messageKey);
                    String userId = key.getKey1();

                    ChartRecord currentCharts = chartRecordTableView.get(userId);
                    if (currentCharts == null) {
                        currentCharts = ChartRecord.create(userId, countRecord);
                        pulsarUtil.sendMessage(userId, topic, currentCharts);
                    } else {
                        boolean needsUpdate = currentCharts.updateCharts(countRecord);
                        if (needsUpdate) {
                            pulsarUtil.sendMessage(userId, topic, currentCharts);
                        }
                    }
                });
    }

    @Override
    public long getArtistPlayCount(String userId, String artistId) {
        CompositeKey compositeKey = new CompositeKey(userId, artistId);
        CountRecord countRecord = artistCountTableView.get(compositeKey.toString());
        return countRecord.count();
    }

    @Override
    public long getTrackPlayCount(String userId, String trackId) {
        CompositeKey compositeKey = new CompositeKey(userId, trackId);
        CountRecord countRecord = trackCountTableView.get(compositeKey.toString());
        return countRecord.count();
    }

    @Override
    public long getTotalPlayCount(String userId) {
        CountRecord countRecord = playCountTableView.get(userId);
        return countRecord.count();
    }

    @Override
    public List<String> getTopArtists(String userId) {
        ChartRecord chartRecord = artistChartTableView.get(userId);
        List<String> topIds = chartRecord.getTopIds();
        return topIds.stream().map(artistNameTableView::get).map(NameRecord::name).toList();
    }

    @Override
    public List<String> getTopTracks(String userId) {
        ChartRecord chartRecord = trackChartTableView.get(userId);
        List<String> topIds = chartRecord.getTopIds();
        return topIds.stream().map(trackNameTableView::get).map(NameRecord::name).toList();
    }
}
