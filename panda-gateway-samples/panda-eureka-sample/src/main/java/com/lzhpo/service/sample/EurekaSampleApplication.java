package com.lzhpo.service.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author lzhpo
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaSampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(EurekaSampleApplication.class, args);
  }
}
