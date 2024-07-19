package com.example.outboxpattern.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.3-alpine"));
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
        KafkaContainer kafkaContainer =
                new KafkaContainer(DockerImageName.parse("apache/kafka").withTag("3.7.1"));
        // Connect our Spring application to our Testcontainers Kafka instance
        dynamicPropertyRegistry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        dynamicPropertyRegistry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        return kafkaContainer;
    }

    @Bean
    @ServiceConnection(name = "openzipkin/zipkin")
    GenericContainer<?> zipkinContainer() {
        return new GenericContainer<>(DockerImageName.parse("openzipkin/zipkin:latest")).withExposedPorts(9411);
    }
}
