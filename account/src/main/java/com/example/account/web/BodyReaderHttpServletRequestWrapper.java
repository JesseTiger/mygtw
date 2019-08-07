package com.example.account.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-08-06
 */
  public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] bytes;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
      super(request);
      try (BufferedInputStream bis = new BufferedInputStream(request.getInputStream());
          ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bis.read(buffer)) > 0) {
          baos.write(buffer, 0, len);
        }
        bytes = baos.toByteArray();
        String body = new String(bytes);
        System.out.println(body);
      } catch (IOException ex) {
        throw ex;
      }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
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
        public void setReadListener(ReadListener readListener) {

        }

        public int read() throws IOException {
          return byteArrayInputStream.read();
        }
      };
    }

    @Override
    public BufferedReader getReader() throws IOException {
      return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

  }