package com.example.boot.kafka.reactor;

import com.example.boot.kafka.reactor.common.ContainerConfiguration;
import com.example.boot.kafka.reactor.common.TestKafkaProducer;
import com.example.boot.kafka.reactor.entity.MessageDTO;
import com.example.boot.kafka.reactor.util.AppConstants;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.test.StepVerifier;

@SpringBootTest(classes = {ContainerConfiguration.class, TestKafkaProducer.class})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class BootKafkaReactorConsumerApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(BootKafkaReactorConsumerApplicationTests.class);

    @Autowired
    KafkaSender<Integer, MessageDTO> sender;

    @Autowired
    protected WebTestClient webTestClient;

    @Test
    void loadDataAndConsume() throws InterruptedException {
        MessageDTO messageDTO = new MessageDTO(null, "hello1", LocalDateTime.now());
        Integer key = new SecureRandom().nextInt(Integer.MAX_VALUE);
        this.sender
                .send(Flux.just(
                        SenderRecord.create(new ProducerRecord<>(AppConstants.HELLO_TOPIC, key, messageDTO), key)))
                .doOnError(e -> log.error("Send failed", e))
                .subscribe(r -> {
                    RecordMetadata metadata = r.recordMetadata();
                    log.info(
                            "Message {} sent successfully, topic-partition={}-{} offset={} timestamp={}",
                            r.correlationMetadata(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            LocalDateTime.now());
                });
        // letting kafka process it, as it is lazy initialization takes time
        TimeUnit.SECONDS.sleep(5);

        // Send a GET request to the /messages endpoint and validate the response
        Flux<MessageDTO> responseFlux = webTestClient
                .get()
                .uri("/messages")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(MessageDTO.class)
                .getResponseBody();

        // Use StepVerifier to verify the behavior of the Flux
        StepVerifier.create(responseFlux).expectNextCount(1).thenCancel().verify();
    }
}
