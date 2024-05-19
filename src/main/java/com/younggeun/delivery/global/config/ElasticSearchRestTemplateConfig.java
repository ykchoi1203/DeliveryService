package com.younggeun.delivery.global.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

@Configuration
public class ElasticSearchRestTemplateConfig {
  private final RestHighLevelClient restHighLevelClient;

  @Autowired
  public ElasticSearchRestTemplateConfig(RestHighLevelClient restHighLevelClient) {
    this.restHighLevelClient = restHighLevelClient;
  }

  @Bean
  public ElasticsearchConverter elasticsearchConverter() {
    return new MappingElasticsearchConverter(elasticsearchMappingContext());
  }

  @Bean
  public SimpleElasticsearchMappingContext elasticsearchMappingContext() {
    return new SimpleElasticsearchMappingContext();
  }

  @Bean(name = "elasticsearchTemplate")
  public ElasticsearchRestTemplate elasticsearchRestTemplate() {
    return new ElasticsearchRestTemplate(restHighLevelClient);
  }

}
