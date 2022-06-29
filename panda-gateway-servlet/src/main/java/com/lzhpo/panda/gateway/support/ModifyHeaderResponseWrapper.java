package com.lzhpo.panda.gateway.support;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Can add header or remove header for {@link HttpServletResponse}
 *
 * <p>Notes: removeHeaders and addHeaders should not have same header name
 *
 * @author lzhpo
 */
public class ModifyHeaderResponseWrapper extends HttpServletResponseWrapper {

  private final List<String> removeHeaders;
  private final Map<String, String> addHeaders;

  public ModifyHeaderResponseWrapper(HttpServletResponse response) {
    super(response);
    this.removeHeaders = new ArrayList<>();
    this.addHeaders = new HashMap<>();
  }

  public ModifyHeaderResponseWrapper(
      HttpServletResponse response, List<String> removeHeaders, Map<String, String> addHeaders) {
    super(response);
    this.removeHeaders = removeHeaders;
    this.addHeaders = addHeaders;
  }

  public static ModifyHeaderResponseWrapper addHeaders(
      HttpServletResponse response, Map<String, String> addHeaders) {
    return new ModifyHeaderResponseWrapper(response, Collections.emptyList(), addHeaders);
  }

  public static ModifyHeaderResponseWrapper removeHeaders(
      HttpServletResponse response, List<String> removeHeaders) {
    return new ModifyHeaderResponseWrapper(response, removeHeaders, Maps.newHashMap());
  }

  public void addHeader(String name, String value) {
    this.addHeaders.put(name, value);
  }

  public void removeHeader(String name) {
    this.removeHeaders.add(name);
  }

  @Override
  public boolean containsHeader(String name) {
    return !isRemoveHeader(name) && super.containsHeader(name);
  }

  @Override
  public void setHeader(String name, String value) {
    if (!isRemoveHeader(name)) {
      super.setHeader(name, value);
    }
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
  public Collection<String> getHeaders(String name) {
    if (isRemoveHeader(name)) {
      return null;
    }

    Set<String> finalHeaders = new HashSet<>(addHeaders.values());
    Collection<String> headers = super.getHeaders(name);
    finalHeaders.addAll(headers);
    return finalHeaders;
  }

  @Override
  public Collection<String> getHeaderNames() {
    Collection<String> headerNames = super.getHeaderNames();
    List<String> finalHeaderNames = new ArrayList<>(addHeaders.keySet());
    for (String headerName : headerNames) {
      if (!isRemoveHeader(headerName)) {
        finalHeaderNames.add(headerName);
      }
    }
    return finalHeaderNames;
  }

  private boolean isRemoveHeader(String name) {
    return removeHeaders.stream().anyMatch(removeHeader -> removeHeader.equalsIgnoreCase(name));
  }
}
