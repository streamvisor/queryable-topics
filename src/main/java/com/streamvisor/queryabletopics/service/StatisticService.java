package com.streamvisor.queryabletopics.service;

import java.util.List;

public interface StatisticService {

    long getArtistPlayCount(String userId, String artistId);

    long getTrackPlayCount(String userId, String trackId);

    long getTotalPlayCount(String userId);

    List<String> getTopArtists(String userId);

    List<String> getTopTracks(String userId);
}
