package com.gerow.test.jmeter.client;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class KafkaSampleClient implements JavaSamplerClient {

    private KafkaProducer<String, String> producer;


    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        String broken = javaSamplerContext.getParameter("broken");
        Properties props = new Properties();
        props.put("bootstrap.servers", broken);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    private void sendMessage(String topic_name, String data) {
        ProducerRecord<String, String> msg = new ProducerRecord<>(topic_name, data);
        producer.send(msg);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        String topic_name = javaSamplerContext.getParameter("topic_name");
        String data = javaSamplerContext.getParameter("data");
        result.setRequestHeaders("send kafka data:\n" + data);
        result.sampleStart();
        try {
            sendMessage(topic_name, data);
            result.sampleEnd();
            result.setSuccessful(true);
            result.setResponseCodeOK();
            result.setResponseData("send kafka success", "UTF-8");
        } catch (Exception e) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseMessage(data);
            result.setResponseCode(e.toString());
        }
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {
        producer.close();
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("broken", "kafka1.kucoin:9092");
        arguments.addArgument("topic_name", "TI_TRADE_COMP");
        arguments.addArgument("data", "{\"type\":\"TI_2022_3H\",\"data\":{\"user_id\":\"622aee2567c3950001d088db\",\"snapshot_date_time\":\"2022-03-10 01:00:00\",\"api_daily_trade_amount\":\"1000\"}} ");
        return arguments;
    }
}