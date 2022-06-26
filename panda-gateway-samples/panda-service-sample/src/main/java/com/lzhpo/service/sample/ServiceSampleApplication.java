package com.lzhpo.service.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lzhpo
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceSampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceSampleApplication.class, args);
  }
}
