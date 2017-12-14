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
import com.google.protobuf.Descriptors.FieldDescriptor;
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
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      demo.writeTo(os);
      bytes = os.toByteArray();
      System.out.println("bytes length: " + bytes.length);
    } finally {
      os.close();
    }

    System.out.println();

    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    try {
      Demo demo2 = Demo.parseFrom(is);
      System.out.println(demo2);

      Assert.assertEquals(demo.getTitle(), demo2.getTitle());
      Assert.assertEquals(demo.getUrl(), demo2.getUrl());
      Assert.assertEquals(demo.getSnippetsList(), demo2.getSnippetsList());
      Assert.assertEquals(demo.getMetadataMap(), demo2.getMetadataMap());
    } finally {
      is.close();
    }
  }

  @Test
  public void NestedMapDemoObjTest() throws IOException {
    Map<String, String> mapValue = new HashMap<String, String>();
    mapValue.put("ok1", "ok1_value");
    mapValue.put("ok2", "ok2_value");
    NestedMapDemo demo = NestedMapDemo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok")
        .putMetadata(1, mapValue).build();
    System.out.println(demo);

    byte[] bytes = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      demo.writeTo(os);
      bytes = os.toByteArray();
      System.out.println("bytes length: " + bytes.length);
    } finally {
      os.close();
    }

    System.out.println();

    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    try {
      NestedMapDemo demo2 = NestedMapDemo.parseFrom(is);
      System.out.println(demo2);

      Assert.assertEquals(demo.getTitle(), demo2.getTitle());
      Assert.assertEquals(demo.getUrl(), demo2.getUrl());
      Assert.assertEquals(demo.getSnippetsList(), demo2.getSnippetsList());
      Assert.assertEquals(demo.getMetadataMap(), demo2.getMetadataMap());
    } finally {
      is.close();
    }
  }

  @Test
  public void NestedMapDemoObjTest2() throws IOException {
    Map<String, Integer> mapValue = new HashMap<String, Integer>();
    mapValue.put("ok1", 1);
    mapValue.put("ok2", 2);
    NestedMapDemoOuterClass2.NestedMapDemo demo = NestedMapDemoOuterClass2.NestedMapDemo.newBuilder().setTitle("ok")
        .setUrl("http://test").addSnippets("ok").putMetadata(1, mapValue).build();
    System.out.println(demo);

    byte[] bytes = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      demo.writeTo(os);
      bytes = os.toByteArray();
      System.out.println("bytes length: " + bytes.length);
    } finally {
      os.close();
    }

    System.out.println();
    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    try {
      NestedMapDemoOuterClass2.NestedMapDemo demo2 = NestedMapDemoOuterClass2.NestedMapDemo.parseFrom(is);
      System.out.println(demo2);

      Assert.assertEquals(demo.getTitle(), demo2.getTitle());
      Assert.assertEquals(demo.getUrl(), demo2.getUrl());
      Assert.assertEquals(demo.getSnippetsList(), demo2.getSnippetsList());
      Assert.assertEquals(demo.getMetadataMap(), demo2.getMetadataMap());
    } finally {
      is.close();
    }
  }

  @Test
  public void customFieldType() throws InvalidProtocolBufferException, DescriptorValidationException {
    testMapDescriptor(FieldType.BOOL, null);
    testMapDescriptor(FieldType.BOOL, FieldType.STRING);
    testMapDescriptor(FieldType.STRING, FieldType.MESSAGE);
  }

  private void testMapDescriptor(FieldType keyType, FieldType valueType) {
    Descriptor map = valueType == null ? MapDescriptors.newDescriptorForParent(keyType)
        : MapDescriptors.newDescriptorForLeaf(keyType, valueType);

    FieldDescriptor key = map.getFields().get(0);
    Assert.assertEquals(key.getName(), "key");
    Assert.assertEquals(key.getNumber(), 1);
    Assert.assertEquals(key.isRequired(), true);
    Assert.assertEquals(key.getType().toString(), keyType.toString());

    FieldDescriptor value = map.getFields().get(1);
    Assert.assertEquals(key.getName(), "key");
    Assert.assertEquals(value.getName(), "value");
    Assert.assertEquals(value.getNumber(), 2);
    if (valueType == null)
      valueType = FieldType.MESSAGE;
    Assert.assertEquals(value.isRepeated(), valueType == FieldType.MESSAGE);
    Assert.assertEquals(value.getType().toString(), valueType.toString());
  }

  @Test
  public void testToType() {
    Type type = MapDescriptors.toType(FieldType.BOOL);
    Assert.assertEquals(Type.TYPE_BOOL, type);
  }

}
