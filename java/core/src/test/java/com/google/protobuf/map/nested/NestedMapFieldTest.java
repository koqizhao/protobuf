package com.google.protobuf.map.nested;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.google.protobuf.map.nested.DemoOuterClass.Demo;

/**
 * @author koqizhao
 *
 * Dec 12, 2017
 */
public class NestedMapFieldTest {
  
  @Test
  public void NormalDemoObjTest() throws IOException {
    Demo demo = Demo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok").putMetadata(1, "o2").build();
    System.out.println(demo);
    byte[] bytes = null;
    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      demo.writeTo(os);
      bytes = os.toByteArray();
      System.out.println(bytes.length);
    };

    try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
      Demo demo2 = Demo.parseFrom(is);
      System.out.println(demo2);
    }
  }

}
