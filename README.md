# 一、项目结构

##### 1、jq-test-utils相关工具类
##### 2、jq-test-api-yml测试框架实现代码
##### 3、ipa-interface-test小程序测试用例


# 二、环境配置

### 1、maven、java 

    参考：https://www.runoob.com/maven/maven-setup.html maven
          https://www.runoob.com/java/java-environment-setup.html
    
### 2、allure安装

    1、安装powershell（win10自带，任意位置shift+鼠标右键，选择在此处打开powershell窗口）

    2、安装scoop方法 ：运行 powershell 输入 ： iex (new-object net.webclient).downloadstring('https://get.scoop.sh') 然后根据提示安装即可

    3、安装allure ：scoop install allure

# 三、测试执行

    1、在根目录下运行命令：mvn clean install -DskipTests将代码编译到本地的maven仓库
    
    2、进入ipa-interface-test直接运行mvn test。
    
## 可选参数
    
###  1、执行环境 
      
    -Dspring.profiles.active=xxx   
    
    需要对应的配置文件

### 2、单独用例执行
    
    1、根据催单场景选择  -Dstory=xxx,xxx
    
        example:    -Dstory=下单未付款,已付款
    
    2、根据组件选择     -Dcomponent=xxx,xxx
    
        example:    -Dcomponent=任务开关,催单消息
                
### example
       
    执行催单场景“下单未付款”、“已付款”场景下“任务开关”和“催单消息”组件
    
    -ea -Dspring.profiles.active=ipa-test -DtestDir=淘宝 -Dplatform=融合版 -Dfeatures=催单 -Dtest.severity=ALL -Denv=mini-test -DshopName=wangjq_1990 -Dstory=下单未付款,已付款 -Dcomponent=任务开关,催单消息 -Dtest.name=
                
    执行完成后会在ipa-interface-test\target\allure-results生成allure测试结果
    
    注：powershell会将“.”拆分，用cmd执行，在powershell先执行命令cmd即可。

# 四、生成测试结果

    执行命令：allure serve xxxx\target\allure-results
 
# 五、多环境配置

    1、同springboot配置
    
    2、application-xxx.properties 即为各个环境的配置
    
    3、在测试用例中可调用的参数格式为data.test.xxxx，在测试中调用格式为：${xxxx}
    
### example 

####  参数配置
    
    data:
      test:
        shopName: "xxxxxx" -------------------全局变量通过${shopName}调用
    
#####    调用格式

######      example1：
    - name: 获取MP后台地址
      url: /api/auth/mp_switcher
      variables:
        subnick: ${shopName}
      method: GET
      assertion:
        - json:
            $.code: 0
      extractor:
        - json:
            auth_url: $.url
    - name: 进入主页
      host: ""
      url: ${auth_url}
      method: GET
      responseType: DEFAULT
    - name: 获取shop_id
      url: /api/admin/user/logined
      method: GET
      extractor:
        - json:
            shop_id: $.user.shop_id
            shop_category_id: $.default_shop.category_id
          site: TESTSUIT
      assertion: [json: {$.code: 0}]
# 六、JMeter Functions支持

### 1、function调用
    
####    example

        给customerRemark赋值一个随机字符串并保存，后续可以调用：
         
###### 赋值格式
       
        ${__RandomString(8,zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890,customerRemark)}
         
###### 调用格式

        ${customerRemark}
        
### 2、自定义function
        
        Module：jq-test-utils
        
        package：com.jq.test.jmeter.functions

    
#### RandomDate代码示例如下：
        
````
public class RandomDate extends AbstractFunction {

    private static final String KEY = "__RandomDay";

    private CompoundVariable plusDay;

    private CompoundVariable format;

    private CompoundVariable varName;

    private String result;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        build();
        TestUtils.saveVariables(varName, result);
        return result;
    }

    private void build() {
        String fm = format.execute().trim();
        if (StringUtils.isBlank(fm)) {
            fm = "yyyy-MM-dd";
        }
        int plusDay = new Random().nextInt(Integer.valueOf(this.plusDay.execute().trim()));
        LocalDate result = LocalDate.now().plusDays(plusDay);
        this.result = result.format(DateTimeFormatter.ofPattern(fm));
    }

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
````   

        调用${__RandomDay(yyyy-MM-dd,180,endDate)}：取当前时间开始往后180天随机的某一天，并赋值给endDate

# 七、测试用例编写

    详见xiaoduo-interface-test下的
    
    [Cache](xiaoduo-interface-test "直接跳转") 直接跳转.
