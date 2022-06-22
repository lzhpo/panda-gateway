package com.lzhpo.panda.gateway.servlet;

import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Caching {@link HttpServletRequest}, because {@link ServletInputStream} just can read once.
 *
 * @author lzhpo
 */
public class CachingServletRequestWrapper extends HttpServletRequestWrapper {

  public CachingServletRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    ServletInputStream inputStream = super.getInputStream();
    return new ServletInputStream() {

      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener listener) {
        // NOP
      }

      @Override
      public int read() throws IOException {
        return inputStream.read();
      }
    };
  }
}
