package com.lzhpo.sample.gateway.servlet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lzhpo
 */
@Controller
@RequestMapping("/")
public class IndexController {

  //  @RequestMapping("/")
  //  public String index() {
  //    return "index";
  //  }

  @GetMapping("/hello")
  @ResponseBody
  //  @CrossOrigin(origins = "http://localhost:9002")
  public String hello() {
    return "hello";
  }
}
