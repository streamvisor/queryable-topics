package com.streamvisor.queryabletopics.util;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TableView;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

@Component
public class PulsarUtil {

    @Autowired PulsarClient pulsarClient;

    @Autowired PulsarTemplate<Object> pulsarTemplate;

    public <T> TableView<T> createTableView(Class<T> clazz, String topic)
            throws PulsarClientException {
        return pulsarClient
                .newTableView(Schema.JSON(clazz))
                .topic(topic)
                .autoUpdatePartitionsInterval(10, TimeUnit.SECONDS)
                .create();
    }

    public <T> void sendMessage(String key, String topic, T message) {
        try {
            pulsarTemplate
                    .newMessage(message)
                    .withTopic(topic)
                    .withMessageCustomizer(m -> m.key(key))
                    .send();
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    }

    public static String deriveId(String input) {
        return Hex.toHexString(input.getBytes(StandardCharsets.UTF_8));
    }
}
