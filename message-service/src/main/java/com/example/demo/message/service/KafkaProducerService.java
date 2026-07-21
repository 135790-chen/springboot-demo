package com.example.demo.message.service;

public interface KafkaProducerService {
    void send(String topic, String message);
    boolean isKafkaAvailable();
}
