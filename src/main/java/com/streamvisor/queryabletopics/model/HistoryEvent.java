package com.streamvisor.queryabletopics.model;

public record HistoryEvent(String endTime, String artistName, String trackName, long msPlayed) {}
