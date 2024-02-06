package com.streamvisor.queryabletopics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChartRecord {
    private static final int MAX_SIZE = 5;
    private String id;
    private Map<String, Long> playsById;

    public static ChartRecord create(String id, CountRecord countRecord) {
        HashMap<String, Long> initialPlaysById = new HashMap<>();
        initialPlaysById.put(countRecord.id(), countRecord.count());

        return new ChartRecordBuilder().id(id).playsById(initialPlaysById).build();
    }

    public boolean updateCharts(CountRecord countRecord) {
        if (playsById.size() < MAX_SIZE) {
            playsById.put(countRecord.id(), countRecord.count());
            return true;
        } else {
            if (playsById.containsKey(countRecord.id())) {
                playsById.put(countRecord.id(), countRecord.count());
                return true;
            } else {
                // replace if bigger than smallest
                Stream<Map.Entry<String, Long>> sorted =
                        playsById.entrySet().stream()
                                .sorted(Comparator.comparingLong(Map.Entry::getValue));
                Map.Entry<String, Long> lowestCountRecord = sorted.findFirst().get();
                if (countRecord.count() > lowestCountRecord.getValue()) {
                    playsById.remove(lowestCountRecord.getKey());
                    playsById.put(countRecord.id(), countRecord.count());
                    return true;
                }
            }
        }
        return false;
    }

    @JsonIgnore
    public List<String> getTopIds() {
        return playsById.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();
    }
}
