package com.jq.test.utils.assertion;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.Option;
import com.jq.test.task.ITestStep;
import com.jq.test.utils.TestUtils;
import com.jq.test.utils.data.DataSources;
import com.jq.test.utils.data.DataType;
import com.jq.test.utils.json.JsonPathUtils;
import io.qameta.allure.Allure;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.springframework.http.ResponseEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Assertion {
    /**
     * 数据来源
     */
    private DataSources type = DataSources.BODY;
    /**
     * 数据来源类型
     */
    private DataType dataType = DataType.JSON;
    /**
     * 值的类型
     */
    private ValueType valueType = ValueType.DEFAULT;
    /**
     * 判断类型
     */
    private AssertionType assertionType = AssertionType.EQ;
    /**
     * 值的key
     */
    private String key;

    private String data;
    /**
     * 期望值
     */
    private Object value;
    /**
     * json提取参数设置
     */
    private Option[] options = new Option[]{};
    private ITestStep step;
    private LinkedHashMap<String, Object> json = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> excel = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> constant = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> total = new LinkedHashMap<>();
    private int length = 0;
    private boolean need_decode = false;

    /**
     * 判断响应是否正确
     *
     * @param entity 响应
     * @param <T>    响应体类型
     */
    public <T> void check(ResponseEntity<T> entity, ITestStep step) {
        this.step = step;
        build(json, DataType.JSON, entity);
        build(excel, DataType.EXCEL, entity);
        build(constant, DataType.CONSTANT, entity);
        build(total, entity);
        if (StringUtils.isNotBlank(key)) {
            Allure.step("校验结果:" + key, () -> {
                Object actual = buildActual(entity);
                Object value = buildExpect(actual);
                assertion(actual, value);
            });
        }
    }

    private <T> void build(Map<String, Object> map, ResponseEntity<T> entity) {
        if (!map.isEmpty()) {
            for (String key : map.keySet()) {
                Assertion assertion = new Assertion();
                assertion.setKey(key);
                assertion.setValue(map.get(key));
                assertion.setOptions(options);
                assertion.setAssertionType(AssertionType.TOTAL);
                assertion.setDataType(dataType);
                assertion.setNeed_decode(need_decode);
                assertion.setType(type);
                assertion.setValueType(valueType);
                assertion.check(entity, this.step);
            }
        }
    }

    private <T> void build(Map<String, Object> map, DataType dataType, ResponseEntity<T> entity) {
        if (!map.isEmpty()) {
            for (String key : map.keySet()) {
                Assertion assertion = new Assertion();
                assertion.setKey(key);
                assertion.setValue(map.get(key));
                assertion.setOptions(options);
                assertion.setAssertionType(assertionType);
                assertion.setDataType(dataType);
                assertion.setType(type);
                assertion.setNeed_decode(need_decode);
                switch (dataType) {
                    case CONSTANT:
                        assertion.setType(DataSources.DEFAULT);
                        assertion.setValueType(ValueType.BIGDECIMAL);
                        break;
                    case JSON:
                    case EXCEL:
                    case XML:
                    case DEFAULT:
                    default:
                        assertion.setValueType(valueType);
                        break;
                }
                assertion.check(entity, this.step);
            }
        }
    }

    /**
     * 生成期望值
     *
     * @param actual 实际值
     * @return 期望值
     */
    private Object buildExpect(Object actual) {
        //根据值的类型转换类型
        switch (valueType) {
            case BIGDECIMAL:
                value = buildBigDecimal(this.value);
                break;
            case INTEGER:
                value = buildInteger(this.value);
                break;
            case STRING:
                value = this.value.toString();
                break;
            case DEFAULT:
            default:
                //默认状态下，类型与实际值保持一致
                if (actual instanceof Double) {
                    value = new BigDecimal(this.value.toString()).doubleValue();
                } else if (actual instanceof Integer) {
                    value = new BigDecimal(this.value.toString()).intValue();
                } else if (actual instanceof Collection) {
                    Optional any = ((Collection) actual).stream().findAny();
                    if (any.isPresent()) {
                        if (any.get() instanceof Double) {
                            if (this.value instanceof Collection) {
                                value = ((Collection) this.value).stream()
                                        .map(v -> new BigDecimal(v.toString()).doubleValue())
                                        .collect(Collectors.toList());
                            } else {
                                value = new BigDecimal(value.toString()).doubleValue();
                            }
                        } else if (any.get() instanceof Integer) {
                            if (this.value instanceof Collection) {
                                value = ((Collection) this.value).stream()
                                        .map(v -> new BigDecimal(v.toString()).intValue())
                                        .collect(Collectors.toList());
                            } else {
                                value = new BigDecimal(value.toString()).intValue();
                            }
                        }
                    }
                }
                break;
        }
        return value;
    }

    /**
     * 生成响应返回的值
     *
     * @param entity 响应实体
     * @param <T>    响应体类型
     * @return 实际值
     */
    private <T> Object buildActual(ResponseEntity<T> entity) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        Object actual = null;
        //根据数据来源提取响应的实际值
        switch (type) {
            case BODY:
                T body = entity.getBody();
                assert body != null;
                switch (dataType) {
                    case EXCEL:
                        String[] keys = StringUtils.split(key, "-");
                        HSSFWorkbook workbook = new HSSFWorkbook(new ByteArrayInputStream((byte[]) body));
                        actual = workbook.getSheetAt(Integer.valueOf(keys[0])).getRow(Integer.valueOf(keys[1])).getCell(Integer.valueOf(keys[2])).getStringCellValue();
                        break;
                    case JSON:
                    case DEFAULT:
                    default:
                        if (need_decode) {
                            String json = TestUtils.des3Cipher("828d1bc65eefc6c88ca1a5d4", "828d1bc6", 2,
                                    Objects.requireNonNull(entity.getBody()).toString());
                            Allure.addAttachment("解密结果：", json);
                            actual = JsonPathUtils.read(json, key, options);
                        } else {
                            actual = JsonPathUtils.read(body, key, options);
                        }
                        break;
                }
                break;
            case HEADER:
                String[] split = value.toString().split(":");
                actual = entity.getHeaders().get(split[0]);
                setAssertionType(AssertionType.CONTAINS);
                value = split[1];
                break;
            case STATUS:
                actual = entity.getStatusCode().value();
                break;
            case DATA:
                actual = JsonPathUtils.read(data, key, options);
                break;
            case DEFAULT:
            default:
                actual = key;
                break;
        }
        //根据值的类型转换类型
        switch (valueType) {
            case BIGDECIMAL:
                assert actual != null;
                actual = buildBigDecimal(actual);
                break;
            case INTEGER:
                assert actual != null;
                actual = buildInteger(actual);
                break;
            case STRING:
            case DEFAULT:
            default:
                if (actual instanceof BigDecimal) {
                    actual = ((BigDecimal) actual).doubleValue();
                }
                break;
        }
        return actual;
    }

    private Object buildInteger(Object actual) {
        if (actual instanceof Collection) {
            actual = ((Collection<Object>) actual).stream().map(o -> Integer.valueOf(o.toString())).toArray();
        } else {
            actual = Integer.valueOf(actual.toString());
        }
        return actual;
    }

    private Object buildBigDecimal(Object actual) {
        if (actual instanceof Collection) {
            actual = ((Collection<Object>) actual).stream().map(o -> new BigDecimal(o.toString()).doubleValue()).toArray();
        } else {
            actual = new BigDecimal(actual.toString()).doubleValue();
        }
        return actual;
    }

    /**
     * 判断实际值与期望值是否一致
     *
     * @param actual 实际值
     * @param value  期望值
     */
    private void assertion(Object actual, Object value) {
        if (assertionType == AssertionType.TOTAL) {
            this.step.addAssertionLength(key, Integer.parseInt(actual.toString()));
            Allure.step("判断结果:" + this.step.getAssertionLength().get(key) + " " + assertionType + " " + value, () -> {
                assertionExec(actual, value);
            });
        } else {
            Allure.step("判断结果:" + actual + " " + assertionType + " " + value, () -> {
                assertionExec(actual, value);
            });
        }
    }

    private void assertionExec(Object actual, Object value) {
        //根据不同的判断类型进行判断
        switch (assertionType) {
            case GREATEROREQUALTO:
                Assertions.assertThat(new BigDecimal(actual.toString())).isGreaterThanOrEqualTo(new BigDecimal(value.toString()));
                break;
            case LESSTHANOREQUALTO:
                Assertions.assertThat(new BigDecimal(actual.toString())).isLessThanOrEqualTo(new BigDecimal(value.toString()));
                break;
            case CONTAINS:
                if (actual instanceof Collection) {
                    Assertions.assertThat((Collection<Object>) actual).contains(value);
                } else {
                    Assertions.assertThat(actual.toString()).contains(value.toString());
                }
                break;
            case AllCONTAINS:
                ((Collection<Object>) actual).forEach(o -> Assertions.assertThat(o.toString()).contains(value.toString()));
                break;
            case ALLIS:
                Assertions.assertThat((Collection<Object>) actual).containsOnly(value);
                break;
            case ALLGREATEROREQUALTO:
                ((Collection<Object>) actual).forEach(o -> Assertions.assertThat(new BigDecimal(o.toString())).isGreaterThanOrEqualTo(new BigDecimal(value.toString())));
                break;
            case ALLLESSTHANOREQUALTO:
                ((Collection<Object>) actual).forEach(o -> Assertions.assertThat(new BigDecimal(o.toString())).isLessThanOrEqualTo(new BigDecimal(value.toString())));
                break;
            case TOTAL:
                Assertions.assertThat(this.step.getAssertionLength().get(key)).isEqualTo(value);
                break;
            case EQ:
            default:
                //null转换为空字符串进行判断
                Assertions.assertThat(actual == null ? "" : actual)
                        .as(key + "应该为：" + value)
                        .isEqualTo(value == null ? "" : value);
                break;
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
