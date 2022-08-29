package com.gerow.test;

import com.gerow.test.utils.json.JsonPathUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class QuickSilverKafakaTest {


    @DataProvider
    public Object[][] data() {
        return new Object[][]{{"BTC-USDT", "1h", 25000, 30400},
                {"BTC-USDT", "12h", 18000, 30400},
                {"BTC-USDT", "24h", 18000, 31400},
                {"KCS-USDT", "1h", 10, 19},
                {"KCS-USDT", "12h", 10, 20},
                {"KCS-USDT", "24h", 9, 21}};
    }

    @Test(dataProvider = "data")
    public void testCommonEvent(String symbol, String type, float min_price, float max_price) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka1.kucoin:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        String topic = "QUICKSILVER_CURRENCY_DETAIL_PUSH";
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


        String s = "{\"start_minutes\": \"2022-07-11 20:15\",\"symbol\": \"BTC-USDT\",\"type\": \"12h\",\"kc_seller_total_cnt\": 54,\"all_user_total_cnt\": 133574,\"max_price\": 0.02109300000000000000,\"min_price\": 0.01748100000000000000,\"end_minutes\": \"2022-07-12 08:15\",\"kc_user_total_cnt\": 100,\"kc_total_funds_usdt_buy\": 14137.45322903000000000000,\"kc_buyer_total_cnt\": 81,\"kc_total_funds_usdt_sell\": 14137.45322903000000000000,\"ts\": \"2022-07-12 08:30:36.0\"}";

//        String symbol = "BTC-USDT";
//        String symbol = "KCS-USDT";
//        String symbol = "ETH-USDT";
//        float max_price = 60000;
//        float min_price = 10000;

        int all_user_total_cnt = 1;
        int kc_user_total_cnt = 1;

        int kc_buyer_total_cnt = 1;
        int kc_seller_total_cnt = 1;

        float kc_total_funds_usdt_buy = 1;
        float kc_total_funds_usdt_sell = 1;


        s = JsonPathUtils.put(s, "$.symbol", symbol);
        s = JsonPathUtils.put(s, "$.type", type);
        s = JsonPathUtils.put(s, "$.max_price", max_price);
        s = JsonPathUtils.put(s, "$.min_price", min_price);
        s = JsonPathUtils.put(s, "$.kc_buyer_total_cnt", kc_buyer_total_cnt);
        s = JsonPathUtils.put(s, "$.kc_seller_total_cnt", kc_seller_total_cnt);
        s = JsonPathUtils.put(s, "$.kc_total_funds_usdt_buy", kc_total_funds_usdt_buy);
        s = JsonPathUtils.put(s, "$.kc_total_funds_usdt_sell", kc_total_funds_usdt_sell);
        s = JsonPathUtils.put(s, "$.all_user_total_cnt", all_user_total_cnt);
        s = JsonPathUtils.put(s, "$.kc_user_total_cnt", kc_user_total_cnt);

        Future<RecordMetadata> send = producer.send(new ProducerRecord<String, String>(topic, s));
        RecordMetadata recordMetadata = send.get();
        System.out.printf("Produce ok: topic:%s partition:%d offset:%d%n",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        System.out.println(s);
        System.out.println("结束");
    }
}
