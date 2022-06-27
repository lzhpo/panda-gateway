package com.lzhpo.sample.gateway.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lzhpo
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayWebfluxApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayWebfluxApplication.class, args);
  }
}
