package com.lzhpo.service.sample;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lzhpo
 */
@Slf4j
@RestController
@RequestMapping("/")
public class SampleApiController {

  @GetMapping("/hello")
  public String hello(HttpServletRequest request) {
    log.info("[IP: {}] hello...", request.getRemoteAddr());
    return "Hello";
  }

  @GetMapping("/noReturn")
  public void noReturn(HttpServletRequest request) {
    log.info("[IP: {}] noReturn...", request.getRemoteAddr());
    log.info("noReturn...");
  }

  @GetMapping("/params1")
  public String params1(HttpServletRequest request, String username) {
    log.info("[IP: {}] params1..., username: {}", request.getRemoteAddr(), username);
    return username;
  }

  @GetMapping("/params2")
  public String params2(HttpServletRequest request, @RequestParam String username) {
    log.info("[IP: {}] params2..., username: {}", request.getRemoteAddr(), username);
    return username;
  }

  @GetMapping("/path/{username}")
  public String path(HttpServletRequest request, @PathVariable String username) {
    log.info("[IP: {}] params..., username: {}", request.getRemoteAddr(), username);
    return username;
  }

  @PostMapping("/body")
  public String body(HttpServletRequest request, @RequestBody Animal animal) {
    Map<String, String> headers = ServletUtil.getHeaderMap(request);
    log.info("Request body [{}] with headers [{}]", animal, headers);
    return JSONUtil.toJsonPrettyStr(animal);
  }

  @PostMapping("/upload")
  public void upload(HttpServletRequest request, @RequestParam MultipartFile file) {
    log.info("[IP: {}] upload file [{}]", request.getRemoteAddr(), file.getOriginalFilename());
  }
}
