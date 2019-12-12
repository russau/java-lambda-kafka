package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;

// https://stackoverflow.com/questions/54844983/guaranteed-to-run-function-before-aws-lambda-exits

public class Kafka {
    public Boolean myHandler(Map<String, Object> input, Context context)  throws IOException {
        System.out.println("*** Starting Basic Producer - zipped certificate ***");

        Path path = Paths.get("./example");
        Stream<Path> list = Files.list(path);
        list.limit(5).forEach(System.out::println);

        Properties settings = new Properties();
        settings.put("client.id", "basic-producer");
        settings.put("bootstrap.servers", "ec2-54-202-36-111.us-west-2.compute.amazonaws.com:9093");
        settings.put("key.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        settings.put("value.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        
        //configure the following three settings for SSL Encryption
        settings.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        settings.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "./kafka.kafka-1.truststore.jks");
        settings.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,  "confluent");

        final KafkaProducer<String, String> producer = new KafkaProducer<>(settings);
        final String topic = "hello-world-topic";
        for(int i=1; i<=5; i++){
            final String key = "key-" + i;
            final String value = "value-" + i;
            final ProducerRecord<String, String> record =
                new ProducerRecord<>(topic, key, value);
            producer.send(record);
        }

        producer.close();
        System.out.println("*** Ending Basic Producer ***");
        return true;
    }
}