package com.younggeun.delivery.global.config;

import java.util.Arrays;
import java.util.Collection;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

  @Value("${spring.data.elasticsearch.uris}")
  private String elasticsearchUris;

  @Value("${spring.data.elasticsearch.username}")
  private String username;

  @Value("${spring.data.elasticsearch.password}")
  private String password;

  @Bean
  public RestHighLevelClient restHighLevelClient() {
    RestClientBuilder builder = RestClient.builder(HttpHost.create(elasticsearchUris))
        .setHttpClientConfigCallback(this::getHttpClientConfigCallback);
    return new RestHighLevelClient(builder);
  }

  private HttpAsyncClientBuilder getHttpClientConfigCallback(HttpAsyncClientBuilder httpClientBuilder) {
    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(username, password));
    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider).setDefaultHeaders(compatibilityHeaders());
  }

  private Collection<? extends Header> compatibilityHeaders() {
    Header[] headers = new Header[]{
        new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7"),
        new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.elasticsearch+json;compatible-with=7")
    };
    return Arrays.asList(headers);
  }


}