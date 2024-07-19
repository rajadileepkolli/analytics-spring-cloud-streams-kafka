/* (C)2023 */
package com.example.cloudkafkasample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestCloudKafkaSampleApplication {

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
        KafkaContainer kafkaContainer =
                new KafkaContainer(DockerImageName.parse("apache/kafka").withTag("3.7.1")).withReuse(true);
        dynamicPropertyRegistry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);
        return kafkaContainer;
    }

    public static void main(String[] args) {
        SpringApplication.from(CloudKafkaSampleApplication::main)
                .with(TestCloudKafkaSampleApplication.class)
                .run(args);
    }
}
