package com.streamvisor.queryabletopics.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamingHistory {
    private final Random random = new Random();

    private final List<HistoryEvent> streamingHistory;

    private final Iterator<HistoryEvent> streamIterator;

    private final Set<String> artists;

    private final Set<String> tracks;

    public StreamingHistory(File historyFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.streamingHistory = objectMapper.readValue(historyFile, new TypeReference<>() {});
        this.streamIterator = streamingHistory.listIterator();
        this.artists =
                streamingHistory.stream().map(HistoryEvent::artistName).collect(Collectors.toSet());
        this.tracks =
                streamingHistory.stream().map(HistoryEvent::trackName).collect(Collectors.toSet());
    }

    public HistoryEvent nextStream() {
        if (streamIterator.hasNext()) {
            return streamIterator.next();
        } else {
            int randomIndex = random.nextInt(0, streamingHistory.size());
            return streamingHistory.get(randomIndex);
        }
    }

    public Set<String> getArtists() {
        return artists;
    }

    public Set<String> getTracks() {
        return tracks;
    }
}
