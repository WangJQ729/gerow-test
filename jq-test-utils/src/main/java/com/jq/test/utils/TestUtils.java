package com.jq.test.utils;

import com.jq.test.task.ITest;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.Function;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.tomcat.util.buf.HexUtils;
import org.assertj.core.api.Assertions;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TestUtils {

    private final static Log logger = LogFactory.getLog(TestUtils.class);

    /**
     * 初始化JMeter的functions
     */
    public static void initJMeterFunctions() {
        Field functions;
        Object myFunctions = null;
        List<Class> classes = new ArrayList<>();
        Resource[] resources = new Resource[0];
        try {
            functions = CompoundVariable.class.getDeclaredField("functions");
            functions.setAccessible(true);
            myFunctions = functions.get(null);
            resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:*/**/jmeter/functions/**/*.class");
        } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
            e.printStackTrace();
        }
        for (Resource resource : resources) {
            try {
                classes.add(
                        ClassUtils.getClass(
                                new CachingMetadataReaderFactory()
                                        .getMetadataReader(resource)
                                        .getClassMetadata()
                                        .getClassName()
                        )
                );
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        for (Class clz : classes) {
            Function tempFunc;
            try {
                Object o = clz.getDeclaredConstructor().newInstance();
                if (o instanceof Function) {
                    tempFunc = (Function) o;
                    String referenceKey = tempFunc.getReferenceKey();
                    if (referenceKey.length() > 0 && myFunctions instanceof Map) { // ignore self
                        ((Map<String, Object>) myFunctions).put(referenceKey, tempFunc.getClass());
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException | ExceptionInInitializerError | ClassCastException e) {
            }
        }
    }

    public static String randomName(int size) {
        return RandomStringUtils.randomAlphabetic(size);
    }


    /**
     * @param content 所要替换的对象
     * @param test    测试对象
     * @param params  参数
     * @param times   第几次替换：达到5次后抛异常
     * @return 替换后的参数
     */
    public static String replace(String content, ITest test, Map<String, String> params, int times) {
        JMeterContextService.getContext().setVariables(new JMeterVariables());
        CompoundVariable variable = new CompoundVariable(test.replace(TestUtils.replace(content, params)));
        String result = variable.execute();
        TestUtils.addParams(params);
        if (times < 5) {
            times++;
            if (TestUtils.hasVariables(result)) return replace(result, test, params, times);
        } else {
            Assertions.fail("参数未找到:" + content);
        }
        return result;
    }

    /**
     * @param content 所要替换的字符串
     * @param map     参数
     * @return 替换后的字符串
     */
    public static String replace(String content, Map<String, String> map) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        Set<Map.Entry<String, String>> sets = map.entrySet();
        for (Map.Entry<String, String> entry : sets) {
            String regex = "${" + entry.getKey() + "}";
            content = StringUtils.replace(content, regex, entry.getValue());
        }
        return content;
    }

    /**
     * @param result 所要判断的字符串
     * @return 是否还有未替换的参数
     */
    private static boolean hasVariables(String result) {
        Matcher matcher = Pattern.compile("\\$\\{.*?}").matcher(result);
        return matcher.find();
    }

    /**
     * 保存CompoundVariable
     *
     * @param key   CompoundVariable
     * @param value 值
     */
    public static void saveVariables(CompoundVariable key, String value) {
        if (key != null) {
            JMeterVariables vars = JMeterContextService.getContext().getVariables();
            final String varTrim = key.execute().trim();
            if (vars != null && varTrim.length() > 0) {// vars will be null on TestPlan
                vars.put(varTrim, value);
            }
        }
    }

    /**
     * 如果pattern为空，返回true
     *
     * @param name    名称
     * @param pattern 所要运行的名称
     * @return 是否执行
     */
    public static boolean isRun(String name, String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return true;
        } else {
            return Arrays.asList(pattern.split(",")).contains(name);
        }
    }

    /**
     * 将JMeterVariables里的参数添加到params中
     *
     * @param params 参数
     */
    public static void addParams(Map<String, String> params) {
        JMeterVariables variables = JMeterContextService.getContext().getVariables();
        if (variables != null) {
            Iterator<Map.Entry<String, Object>> iterator = variables.getIterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> map = iterator.next();
                params.put(map.getKey(), map.getValue().toString());
            }
        }
    }


    /**
     * @param rootFile 根目录文件
     * @param filter   文件过滤器
     * @return 文件夹下所有文件
     */
    public static List<File> getAllFile(File rootFile, FileFilter filter) {
        if (rootFile.isDirectory()) {
            List<File> result = new ArrayList<>();
            File[] files = rootFile.listFiles(filter);
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getAllFile(file, filter));
                } else {
                    result.add(file);
                }
            }
            return result;
        } else {
            return Collections.singletonList(rootFile);
        }
    }

    /**
     * @param con con
     * @return 执行结果
     */
    public static String excFunction(String con) {
        return new CompoundVariable(con).execute().trim();
    }

    /**
     * @param items 字符串数组
     * @return 第一个不为空的字符串
     */
    public static Optional<String> firstNonEmpty(String... items) {
        return Stream.of(items).filter(Objects::nonNull).filter(item -> !item.isEmpty()).findFirst();
    }

    public static String des3Cipher(String key, String iv, int mode, String data) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(mode, deskey, ips);
        if (mode == 1) {
            return HexUtils.toHexString(cipher.doFinal(data.getBytes()));
        } else if (mode == 2) {
            byte[] bytes = HexUtils.fromHexString(data);
            String result = new String(cipher.doFinal(bytes));
            logger.info("decode:\n" + result);
            return result;
        } else {
            return "";
        }
    }
}
