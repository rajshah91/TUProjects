package com.oppscience.sgevt.ged;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;


@Configuration
public class ElasticsearchClientConfiguration {
  @Value("${elasticsearch.host}")
  private String esHost;

  @Value("${elasticsearch.port}")
  private int esPort;

  @Value("${elasticsearch.cluster}")
  private String clusterName;

  @Value("${infomatrix.blocks-elasticsearch.sniffing:false}")
  private boolean sniffing;

  @Bean(destroyMethod = "close")
  public RestHighLevelClient esClient() {

    RestClientBuilder lowclient = RestClient.builder(
      new HttpHost(esHost, esPort, "http"))
      .setRequestConfigCallback(
        requestConfigBuilder -> requestConfigBuilder
          .setConnectTimeout(5000)
          .setSocketTimeout(600000));

    return new RestHighLevelClient(
      lowclient);
  }


  @PreDestroy
  public void destroy() {

  }
}
