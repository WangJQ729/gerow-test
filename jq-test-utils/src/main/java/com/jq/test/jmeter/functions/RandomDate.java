package com.jq.test.jmeter.functions;

import com.jq.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomDate extends AbstractFunction {

    /**
     * 调用的key 即可使用${__RandomDay(format,plusDay,varName)}调用
     * <p>
     * format格式化参数：yyyy-MM-dd
     * <p>
     * plusDay：180
     * <p>
     * varName：endDate
     */
    private static final String KEY = "__RandomDay";

    private CompoundVariable plusDay;

    private CompoundVariable format;

    private CompoundVariable varName;

    private String result;

    /**
     * 执行并返回结果
     *
     * @param previousResult
     * @param currentSampler
     * @return
     */
    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        build();
        TestUtils.saveVariables(varName, result);
        return result;
    }

    /**
     * 根据传入的参数获取结果
     */
    private void build() {
        //如果没有传日期格式，使用yyyy-MM-dd
        String fm = format.execute().trim();
        if (StringUtils.isBlank(fm)) {
            fm = "yyyy-MM-dd";
        }
        //根据传入的天数，随机获取一个数字
        int plusDay = new Random().nextInt(Integer.valueOf(this.plusDay.execute().trim()));
        //根据当前时间加上随机数字
        LocalDate result = LocalDate.now().plusDays(plusDay);
        //根据格式赋值
        this.result = result.format(DateTimeFormatter.ofPattern(fm));
    }

    /**
     * 初始化参数
     *
     * @param parameters
     * @throws InvalidVariableException
     */
    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 3, 3);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.format = compoundVariables[0];
        this.plusDay = compoundVariables[1];
        this.varName = compoundVariables[2];
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
