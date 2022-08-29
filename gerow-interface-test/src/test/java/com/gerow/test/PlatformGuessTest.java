package com.gerow.test;

import com.gerow.test.utils.json.JsonPathUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PlatformGuessTest {


    @DataProvider
    public Object[][] data() {
        return new Object[][]{{"62e2017e800dfd00019b9cb2"}};
    }

    @Test(dataProvider = "data")
    public void testCommonEvent(String user_Id) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka1.kucoin:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        String topic = "PROPHET_TRADE_DATA";
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


        String s = "{\"subject\":\"prophet_changeRate_01\",\"data\":{\"biz_no\":\"2022061708\",\"user_Id\":\"6007f18a092e40000a97d245\",\"usdt_amount\":\"1234\",\"trade_type\":\"SPOT\",\"created_at\":\"2022-06-01 11:12:00\"}}\n";

        s = JsonPathUtils.put(s, "$.subject", "prophet_changeRate_04");
        s = JsonPathUtils.put(s, "$.data.biz_no", String.valueOf(System.currentTimeMillis()));
        s = JsonPathUtils.put(s, "$.data.user_Id", user_Id);
        s = JsonPathUtils.put(s, "$.data.usdt_amount", "16049.99");
        s = JsonPathUtils.put(s, "$.data.trade_type", "SPOT");

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(now);

        s = JsonPathUtils.put(s, "$.data.created_at", time);

        Future<RecordMetadata> send = producer.send(new ProducerRecord<String, String>(topic, s));
        RecordMetadata recordMetadata = send.get();
        System.out.printf("Produce ok: topic:%s partition:%d offset:%d%n",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        System.out.println(s);
        System.out.println("结束");
    }
}
