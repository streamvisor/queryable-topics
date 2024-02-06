package com.streamvisor.queryabletopics.controller;

import com.streamvisor.queryabletopics.model.WrappedDto;
import com.streamvisor.queryabletopics.service.StatisticService;
import com.streamvisor.queryabletopics.service.StatisticServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class UserStatsController {

    private final StatisticService statisticService;

    @Autowired
    UserStatsController(StatisticServiceImpl statisticServiceImpl) {
        this.statisticService = statisticServiceImpl;
    }

    @RequestMapping(value = "/{userId}/plays", method = RequestMethod.GET)
    public ResponseEntity<Long> getPlays(@PathVariable String userId) {
        long totalPlayCount = statisticService.getTotalPlayCount(userId);
        return ResponseEntity.ok(totalPlayCount);
    }

    @RequestMapping(value = "/{userId}/artists/{artistId}/plays", method = RequestMethod.GET)
    public ResponseEntity<Long> getPlaysForArtist(
            @PathVariable String userId, @PathVariable String artistId) {
        long artistPlayCount = statisticService.getArtistPlayCount(userId, artistId);
        return ResponseEntity.ok(artistPlayCount);
    }

    @RequestMapping(value = "/{userId}/tracks/{trackId}/plays", method = RequestMethod.GET)
    public ResponseEntity<Long> getPlaysForTrack(
            @PathVariable String userId, @PathVariable String trackId) {
        long trackPlayCount = statisticService.getTrackPlayCount(userId, trackId);
        return ResponseEntity.ok(trackPlayCount);
    }

    @RequestMapping(value = "/{userId}/tracks/top", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getTopTracks(@PathVariable String userId) {
        List<String> topTracks = statisticService.getTopTracks(userId);
        return ResponseEntity.ok(topTracks);
    }

    @RequestMapping(value = "/{userId}/artists/top", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getTopArtists(@PathVariable String userId) {
        List<String> topArtists = statisticService.getTopArtists(userId);
        return ResponseEntity.ok(topArtists);
    }

    @RequestMapping(value = "/{userId}/wrapped", method = RequestMethod.GET)
    public ResponseEntity<WrappedDto> getWrappedStatistic(@PathVariable String userId) {
        List<String> topArtists = getTopArtists(userId).getBody();
        List<String> topTracks = getTopTracks(userId).getBody();
        Long totalPlays = getPlays(userId).getBody();
        WrappedDto wrappedDto = new WrappedDto(topArtists, topTracks, totalPlays);
        return ResponseEntity.ok(wrappedDto);
    }
}
