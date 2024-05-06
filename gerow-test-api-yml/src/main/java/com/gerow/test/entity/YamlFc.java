package com.gerow.test.entity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class YamlFc<T> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YAMLFactory yamlFactory;
    private final ObjectMapper mapper;
    private final Class<T> klass;

    /**
     * @param klass class类型
     */
    public YamlFc(Class<T> klass) {
        this.klass = klass;
        this.yamlFactory = new YAMLFactory();
        this.mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 读取yml
     *
     * @param path 路径
     * @return 读取到的实体
     */
    public T read(File path) {
        try {
            InputStream input = new FileInputStream(path);
            YAMLParser yamlParser = yamlFactory.createParser(input);
            final JsonNode node = mapper.readTree(yamlParser);
            TreeTraversingParser treeTraversingParser = new TreeTraversingParser(node);
            return mapper.readValue(treeTraversingParser, klass);
        } catch (Exception e) {
            logger.error(path.getName() + "读取错误：");
            e.printStackTrace();
            return null;
        }
    }
}
