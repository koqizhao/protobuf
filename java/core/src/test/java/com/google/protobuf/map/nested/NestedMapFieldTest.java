package com.google.protobuf.map.nested;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapEntry.MapDescriptors;
import com.google.protobuf.WireFormat.FieldType;
import com.google.protobuf.map.nested.DemoOuterClass.Demo;
import com.google.protobuf.map.nested.NestedMapDemoOuterClass.NestedMapDemo;

/**
 * @author koqizhao
 *
 *         Dec 12, 2017
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
      System.out.println("bytes length: " + bytes.length);
    }
    ;

    System.out.println();

    try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
      Demo demo2 = Demo.parseFrom(is);
      System.out.println(demo2);
    }
  }

  @Test
  public void NestedMapDemoObjTest() throws IOException {
    Map<String, String> mapValue = new HashMap<>();
    mapValue.put("ok1", "ok1_value");
    mapValue.put("ok2", "ok2_value");
    NestedMapDemo demo = NestedMapDemo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok")
        .putMetadata(1, mapValue).build();

    System.out.println(demo.getMetadataMap());
    System.out.println();

    byte[] bytes = null;
    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      demo.writeTo(os);
      bytes = os.toByteArray();

      System.out.println("bytes length: " + bytes.length);
    }
    ;

    System.out.println();

    try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
      NestedMapDemo demo2 = NestedMapDemo.parseFrom(is);

      System.out.println(demo2.getMetadataMap());
    }
  }

  @Test
  public void NestedMapDemoObjTest2() throws IOException {
    Map<String, Integer> mapValue = new HashMap<>();
    mapValue.put("ok1", 1);
    mapValue.put("ok2", 2);
    NestedMapDemoOuterClass2.NestedMapDemo demo = NestedMapDemoOuterClass2.NestedMapDemo.newBuilder().setTitle("ok")
        .setUrl("http://test").addSnippets("ok").putMetadata(1, mapValue).build();

    System.out.println(demo.getMetadataMap());
    System.out.println();

    byte[] bytes = null;
    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      demo.writeTo(os);
      bytes = os.toByteArray();

      System.out.println("bytes length: " + bytes.length);
    }

    System.out.println();

    try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
      NestedMapDemoOuterClass2.NestedMapDemo demo2 = NestedMapDemoOuterClass2.NestedMapDemo.parseFrom(is);

      System.out.println(demo2.getMetadataMap());
    }
  }

  @Test
  public void customFieldType() throws InvalidProtocolBufferException, DescriptorValidationException {
    Descriptor leafMap = MapDescriptors.newLeafMapDescriptor(FieldType.BOOL, FieldType.STRING);
    System.out.println(leafMap);

    Descriptor nodeMap = MapDescriptors.newNodeMapDescriptor(FieldType.BOOL);
    System.out.println(nodeMap);
  }

  @Test
  public void testToType() {
    Type type = MapDescriptors.toType(FieldType.BOOL);
    Assert.assertEquals(Type.TYPE_BOOL, type);
  }

}
