package com.streamvisor.queryabletopics.model;

import java.util.List;

public record WrappedDto(List<String> topArtists, List<String> topTracks, Long totalPlays) {}
