package com.gerow.test;

import com.gerow.test.utils.json.JsonPathUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KucoinKafakaTest {


    @Test(invocationCount = 1)
    public void testCommonEvent() throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka1.kucoin:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        String topic = "COMMON-EVENT";
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        Random random = new Random();

        String userId = "62287aad17e4f800010d389c";
        float amount = 500;

        //充值
        String s = "{\"id\": \"6176264d9d8c16000158d770\",\"data\": {\"amount\": \"150\",\"address\": \"0x7b7a9d02227c7fd2992061d187673fad21a94699\",\"toAccountType\": \"TRADE\",\"accountType\": \"MAIN\",\"fee\": \"0\",\"memo\": \"\",\"depositAddressId\":\"6088c6be26b63900092d061d\",\"remark\": \"\",\"confirmation\": 0,\"userId\": \"5ffffe0328d96400084d2987\",\"walletTxId\": null,\"domainId\": \"kucoin\",\"isInner\": true,\"accountTag\": \"DEFAULT\",\"feeDesc\": null,\"chainId\": \"eth\",\"noticeAt\": 1635133005339,\"context\": null,\"currency\": \"USDT\",\"id\": \"6176264d9d8c16000158d769\",\"nftTokenId\": null,\"isAutoTransfer\": true,\"status\": \"SUCCESS\"},\"c\": 10021892,\"sv\": \"1.0\",\"ts\": 1635133015618,\"cid\": \"5ffffe0328d96400084d2241\",\"uid\": \"5ffffe0328d96400084d2241\",\"d\": \"\"}";
        s = JsonPathUtils.put(s, "$.data.isInner", false);
        s = JsonPathUtils.put(s, "$.data.userId", userId);
        s = JsonPathUtils.put(s, "$.data.currency", "USDT");
        s = JsonPathUtils.put(s, "$.data.amount", String.valueOf(amount));
        s = JsonPathUtils.put(s, "$.data.noticeAt", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).getEpochSecond() * 1000);
        s = JsonPathUtils.put(s, "$.data.id", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + String.format("%04d", random.nextInt(9999)));


        //otc
//        String s = "{\"id\":\"61028da858f894000687b298\",\"data\":{\"seller\":\"5f24ca542e18e5000609c566\",\"orderType\":\"SELECTED\",\"appealAt\":null,\"appealStatus\":\"NORMAL\",\"legalCurrencyAmount\":700.00000000000000000000,\"orderAmountType\":\"\",\"rateAmount\":0E-20,\"channel\":\"ANDROID\",\"currencyAmount\":13.08411200000000000000,\"platform\":\"KUCOIN\",\"createdAt\":1637056800000,\"legalCurrency\":\"PHP\",\"price\":53.50000000000000000000,\"currency\":\"USDT\",\"payCoinAt\":1637056800000,\"id\":\"61028d3e58f894000687b290\",\"updatedAt\":1637056800000,\"payMoneyAt\":1637056800000,\"buyer\":\"60f2e8b63677e10003260q0q\",\"payTypeCode\":\"BANK\",\"adId\":\"608fad9e16cff50006ff82cf\",\"freezeAccountTxId\":null,\"makerDealAmount\":13.08411200000000000000,\"adSide\":\"SELL\",\"status\":\"PAYED_MONEY\"},\"c\":10013121,\"sv\":\"1.0\",\"ts\":1637056800000,\"cid\":\"60f2e8b63677e10003260q0q\",\"uid\":\"60f2e8b63677e10003260q0q\",\"d\":\"\"}";
//        s = JsonPathUtils.put(s, "$.data.currencyAmount", amount);
//        s = JsonPathUtils.put(s, "$.data.currency", "USDT");
//        s = JsonPathUtils.put(s, "$.uid", userId);
//        s = JsonPathUtils.put(s, "$.data.buyer", userId);
//        s = JsonPathUtils.put(s, "$.data.id", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + String.format("%04d", random.nextInt(9999)));

//        挂单方向 BUY买币 / SELL卖币
//        s = JsonPathUtils.put(s, "$.data.adSide", "SELL");
//        s = JsonPathUtils.put(s, "$.data.adSide", "BUY");
        // 订单状态: CREATED 订单创建完成; PAYED_MONEY 已付款; PAYED_COIN 已放币; CHARGED 已充值; CANCELED 已取消
//        s = JsonPathUtils.put(s, "$.data.status", "PAYED_MONEY");
//        s = JsonPathUtils.put(s, "$.data.status", "PAYED_MONEY");
//        s = JsonPathUtils.put(s, "$.data.createdAt", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).getEpochSecond() * 1000);


        //信用卡入金
//        String s = "{\"id\":\"6183e7018c606100013bc09c\",\"data\":{\"createdAt\":1640076632000,\"fiatAmount\":2.8,\"cryptoCurrency\":\"BTC\",\"cryptoQuantity\":10,\"refType\":0,\"fiatCurrency\":\"USD\",\"cardId\":\"6183e6a58c606100013bc096\",\"ip\":\"221.236.30.90\",\"orderStatus\":1,\"orderId\":\"4654645\",\"userId\":\"61baa2b960a94500014cc603\",\"updatedAt\":1640076632000},\"c\":10031902,\"sv\":\"1.0\",\"ts\":1636034305171,\"cid\":\"61b20670f250440001e60aef\",\"uid\":\"61b20670f250440001e60aef\",\"d\":\"\"}";
//        s = JsonPathUtils.put(s, "$.data.cryptoQuantity", amount);
//        s = JsonPathUtils.put(s, "$.data.userId", userId);
//        s = JsonPathUtils.put(s, "$.data.cryptoCurrency", "USDT");
//        s = JsonPathUtils.put(s, "$.data.createdAt", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).getEpochSecond() * 1000);
//        s = JsonPathUtils.put(s, "$.data.orderStatus", 1);
//        s = JsonPathUtils.put(s, "$.data.orderId", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + String.format("%04d", random.nextInt(9999)));

        //同步获得Future对象的结果
        int i = 0;
        Future<RecordMetadata> send = producer.send(new ProducerRecord<String, String>(topic, s));
        RecordMetadata recordMetadata = send.get();
        System.out.printf("Produce ok: topic:%s partition:%d offset:%d%n",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        System.out.println(s);
        System.out.println("结束");
    }
}
