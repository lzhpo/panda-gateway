package com.lzhpo.panda.gateway.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzhpo
 */
@Configuration
@EnableConfigurationProperties({GatewayProperties.class})
public class GatewayAutoConfiguration {}
