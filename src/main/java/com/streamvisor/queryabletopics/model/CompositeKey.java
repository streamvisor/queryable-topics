package com.streamvisor.queryabletopics.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CompositeKey {
    private String key1;
    private String key2;

    public static CompositeKey from(String compositeKey) {
        String[] keys = compositeKey.split("\\+");
        return new CompositeKey(keys[0], keys[1]);
    }

    @Override
    public String toString() {
        return key1 + "+" + key2;
    }
}
