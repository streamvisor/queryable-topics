package com.streamvisor.queryabletopics.service;

import com.streamvisor.queryabletopics.config.Topics;
import com.streamvisor.queryabletopics.model.HistoryEvent;
import com.streamvisor.queryabletopics.model.StreamRecord;
import com.streamvisor.queryabletopics.model.StreamingHistory;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class DataGenerator {
    @Autowired private PulsarTemplate<StreamRecord> streamTemplate;

    @Value("classpath:StreamingHistory0.json")
    private File historyFile0;

    @Value("classpath:StreamingHistory1.json")
    private File historyFile1;

    @Value("classpath:StreamingHistory2.json")
    private File historyFile2;

    @Getter private List<StreamingHistory> streamingHistories;

    @PostConstruct
    private void initialize() throws IOException {
        streamingHistories =
                List.of(
                        new StreamingHistory(historyFile0),
                        new StreamingHistory(historyFile1),
                        new StreamingHistory(historyFile2));

        // load some initial streams
        for (int i = 0; i < 1_000; i++) {
            generateStreams();
        }
    }

    @Scheduled(initialDelay = 5_000, fixedDelay = 1_000)
    private void generateStreams() throws PulsarClientException {
        for (int i = 0; i < streamingHistories.size(); i++) {
            StreamingHistory history = streamingHistories.get(i);
            HistoryEvent event = history.nextStream();
            StreamRecord streamRecord =
                    new StreamRecord(String.valueOf(i), event.artistName(), event.trackName());
            streamTemplate
                    .newMessage(streamRecord)
                    .withMessageCustomizer(m -> m.key(streamRecord.userId()))
                    .withTopic(Topics.STREAMS)
                    .send();
        }
    }
}
