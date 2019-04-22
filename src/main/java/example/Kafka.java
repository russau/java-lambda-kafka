package example;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

// https://stackoverflow.com/questions/54844983/guaranteed-to-run-function-before-aws-lambda-exits

public class Kafka {
    public Boolean myHandler(Map<String, Object> input, Context context) {
        System.out.println("*** Starting Basic Producer ***");
        
        Properties settings = new Properties();
        settings.put("client.id", "basic-producer");
        settings.put("bootstrap.servers", "ec2-54-184-34-142.us-west-2.compute.amazonaws.com:9092");
        settings.put("key.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        settings.put("value.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        
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