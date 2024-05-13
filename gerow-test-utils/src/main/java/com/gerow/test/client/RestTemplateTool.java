package com.gerow.test.client;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.gerow.test.utils.data.ConfigManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 使用RestTemplate发送请求
 */
public class RestTemplateTool extends RestTemplate {

    private static RestTemplateTool restTemplateTool;

    public static RestTemplateTool getInstance() {
        if (restTemplateTool == null) {
            restTemplateTool = new RestTemplateTool();
        }
        return restTemplateTool;
    }

    private RestTemplateTool() {
        super();
        //设置client
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .disableRedirectHandling()
                .setRedirectStrategy(new LaxRedirectStrategy())
//                .disableCookieManagement()
                .build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(ConfigManager.getTimeOut() * 1000);
        httpRequestFactory.setConnectTimeout(ConfigManager.getTimeOut() * 1000);
        setRequestFactory(httpRequestFactory);

        //使用fastJson
        useFastJSON();

        //设置默认请求头
        setInterceptors(Collections.singletonList(new UserAgentInterceptor()));
        setErrorHandler(new MyResponseErrorHandler());
    }

    private void useFastJSON() {
        List<HttpMessageConverter<?>> httpMessageConverterList = getMessageConverters();
        httpMessageConverterList.removeIf(converter -> converter instanceof AbstractJackson2HttpMessageConverter);
        httpMessageConverterList.add(new StringHttpMessageConverter(Charset.forName("utf-8")));
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        httpMessageConverterList.add(0, fastJsonHttpMessageConverter);
    }

    /**
     * 构造userAgent
     */
    private static class UserAgentInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            HttpHeaders headers = request.getHeaders();
            if (!cookies.isEmpty()) {
                if (headers.containsKey(HttpHeaders.COOKIE)) {
                    Objects.requireNonNull(headers.get(HttpHeaders.COOKIE)).addAll(cookies);
                } else {
                    headers.add(HttpHeaders.COOKIE, String.join(";", cookies));
                }
            }
            return execution.execute(request, body);
        }
    }

    private static List<String> cookies = new ArrayList<>();

    private static class MyResponseErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) {
            return true;
        }

        @Override
        public void handleError(ClientHttpResponse response) {
            HttpHeaders headers = response.getHeaders();
            List<String> cookie = headers.get("Set-Cookie");
            if (cookie != null) {
                cookies.addAll(cookie);
            }
        }
    }
}
