package com.sivalabs.springbootkafkaavro;

import com.sivalabs.springbootkafkaavro.model.Person;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class SpringBootKafkaAvroApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootKafkaAvroApplication.class, args);
    }

    @Autowired
    KafkaTemplate<String, Person> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        Person  person = Person.newBuilder().setId(1).setName("Siva").setAge(33).build();
        kafkaTemplate.send("persons", person);
    }

    @KafkaListener(topics = "persons")
    public void handler(ConsumerRecord<String,Person> cr) {
        Person person = cr.value();
        System.out.println(person.getName()+":"+person.getAge());
    }
}
