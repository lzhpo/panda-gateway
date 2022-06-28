package com.lzhpo.panda.gateway.support;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Can add header or remove header for {@link HttpServletRequest}
 *
 * <p>Notes: removeHeaders and addHeaders should not have same header name
 *
 * @author lzhpo
 */
public class ModifyHeaderRequestWrapper extends HttpServletRequestWrapper {

  private final List<String> removeHeaders;
  private final Map<String, String> addHeaders;

  public ModifyHeaderRequestWrapper(HttpServletRequest request) {
    super(request);
    this.removeHeaders = new ArrayList<>();
    this.addHeaders = new HashMap<>();
  }

  public ModifyHeaderRequestWrapper(
      HttpServletRequest request, List<String> removeHeaders, Map<String, String> addHeaders) {
    super(request);
    this.removeHeaders = removeHeaders;
    this.addHeaders = addHeaders;
  }

  public static ModifyHeaderRequestWrapper addHeaders(
      HttpServletRequest request, Map<String, String> addHeaders) {
    return new ModifyHeaderRequestWrapper(request, Collections.emptyList(), addHeaders);
  }

  public static ModifyHeaderRequestWrapper removeHeaders(
      HttpServletRequest request, List<String> removeHeaders) {
    return new ModifyHeaderRequestWrapper(request, removeHeaders, Maps.newHashMap());
  }

  public void addHeader(String name, String value) {
    this.addHeaders.put(name, value);
  }

  public void removeHeader(String name) {
    this.removeHeaders.add(name);
  }

  @Override
  public String getHeader(String name) {
    if (isRemoveHeader(name)) {
      return null;
    }

    if (addHeaders.containsKey(name)) {
      return addHeaders.get(name);
    }

    return super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    if (isRemoveHeader(name)) {
      return null;
    }

    Enumeration<String> headers = super.getHeaders(name);
    Set<String> finalHeaders = new HashSet<>(addHeaders.keySet());
    if (addHeaders.containsKey(name)) {
      while (headers.hasMoreElements()) {
        String header = headers.nextElement();
        finalHeaders.add(header);
      }
    }

    return Collections.enumeration(finalHeaders);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    Enumeration<String> headerNames = super.getHeaderNames();
    List<String> finalHeaderNames = new ArrayList<>(addHeaders.keySet());
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      if (!isRemoveHeader(headerName)) {
        finalHeaderNames.add(headerName);
      }
    }
    return Collections.enumeration(finalHeaderNames);
  }

  private boolean isRemoveHeader(String name) {
    return removeHeaders.stream().anyMatch(removeHeader -> removeHeader.equalsIgnoreCase(name));
  }
}
