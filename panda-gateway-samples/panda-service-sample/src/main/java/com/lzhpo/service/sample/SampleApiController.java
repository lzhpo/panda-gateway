package com.lzhpo.service.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzhpo
 */
@RestController
@RequestMapping("/")
public class SampleApiController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello";
  }
}
