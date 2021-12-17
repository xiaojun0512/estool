package com.cetc10.utils.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {
    @Value("${es.user}")
    private String userName;
    @Value("${es.password}")
    private String password;
    @Value("${es.host}")
    private String host;
    @Value("${es.port}")
    private Integer port;

    @Bean(name = "myClient")
    public RestHighLevelClient restHighLevelClient(){
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(host, port)
        );
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                return builder.setConnectTimeout(60000).setSocketTimeout(90000);
            }
        });
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build()));
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
//        RestHighLevelClient client = new RestHighLevelClient (RestClient.builder (
//                //如果是集群就构建多个，如果不是集群就构建一个
//                new HttpHost (host, port, "http"),
//                new HttpHost (host, port, "http"),
//                new HttpHost (host, port, "http")
//        ));
        return restHighLevelClient;
    }
}
