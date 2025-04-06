package com.vkr.matchmaking_service.kafka.producer.match;

import com.vkr.matchmaking_service.kafka.event.matchEnd.MatchEndEvent;
import com.vkr.matchmaking_service.kafka.event.matchStart.MatchStartEvent;
import com.vkr.matchmaking_service.kafka.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MatchStartProducer extends AbstractKafkaProducer<MatchStartEvent> {

    @Value("${spring.data.kafka.topics.topic-settings.match-start.name}")
    private String channelTopic;

    public MatchStartProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              Map<String, NewTopic> topicMap) {
        super(kafkaTemplate, topicMap);
    }

    @Override
    public String getTopic() {
        return channelTopic;
    }
}
