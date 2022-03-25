package com.gerow.test;

import com.gerow.test.utils.json.JsonPathUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TiTradeCompKafakaTest {

    Properties properties = new Properties();
    String topic = "TI_TRADE_COMP";
    Random random = new Random();
    KafkaProducer<String, String> producer;

    @BeforeClass
    public void setUp() {
        properties.put("bootstrap.servers", "kafka1.kucoin:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    @Test
    public void testCommonEvent() throws ExecutionException, InterruptedException {
        String userId = "62287aad17e4f800010d389c";
        int amount = random.nextInt(1000);

        //充值
        String s = "{\"type\":\"TI_2022_3H\",\"data\":{\"user_id\":\"622aee2567c3950001d088db\",\"snapshot_date_time\":\"2022-03-10 01:00:00\",\"api_daily_trade_amount\":\"1000\"}} ";
        s = JsonPathUtils.put(s, "$.data.user_id", userId);
        s = JsonPathUtils.put(s, "$.data.snapshot_date_time", "2022-03-24 02:00:00");
        s = JsonPathUtils.put(s, "$.data.api_daily_trade_amount", String.valueOf(amount));

        //同步获得Future对象的结果
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, s);
        Future<RecordMetadata> send = producer.send(record);
        RecordMetadata recordMetadata = send.get();

        System.out.printf("Produce ok: topic:%s partition:%d offset:%d%n",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        System.out.println(s);
        System.out.println("结束");
    }
}
