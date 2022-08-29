package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.json.JsonPathUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;


public class kafkaMessage extends AbstractFunction {

    private static final String KEY = "__kafkaMessage";
    private CompoundVariable userId;

    private static String TOPIC = "PROPHET_TRADE_DATA";

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka1.kucoin:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());

        String s = "{\"subject\":\"prophet_changeRate_01\",\"data\":{\"biz_no\":\"2022061708\",\"user_Id\":\"6007f18a092e40000a97d245\",\"usdt_amount\":\"1234\",\"trade_type\":\"SPOT\",\"created_at\":\"2022-06-01 11:12:00\"}}\n";
        s = JsonPathUtils.put(s, "$.subject", "prophet_changeRate_01");
        s = JsonPathUtils.put(s, "$.data.biz_no", String.valueOf(System.currentTimeMillis()));
        s = JsonPathUtils.put(s, "$.data.user_Id", userId.execute());
        s = JsonPathUtils.put(s, "$.data.usdt_amount", String.valueOf(300 + new Random().nextInt(5000)));
        s = JsonPathUtils.put(s, "$.data.trade_type", "SPOT");
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(now);

        s = JsonPathUtils.put(s, "$.data.created_at", time);

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        Future<RecordMetadata> send = producer.send(new ProducerRecord<String, String>(TOPIC, s));
        return "";
    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 1);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.userId = compoundVariables[0];
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return null;
    }

}