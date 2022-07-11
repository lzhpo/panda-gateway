package com.lzhpo.sample.gateway.servlet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzhpo
 */
@RestController
@RequestMapping("/")
public class IndexController {

  @GetMapping("/hello")
  //  @CrossOrigin(origins = "http://localhost:9001")
  public String hello() {
    return "hello";
  }
}
