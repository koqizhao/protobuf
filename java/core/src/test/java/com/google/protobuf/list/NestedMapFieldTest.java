package com.google.protobuf.list;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ProtobufMapSerializer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapEntry.MapDescriptors;
import com.google.protobuf.WireFormat.FieldType;
import com.google.protobuf.list.DemoOuterClass.Demo;
import com.google.protobuf.list.NestedMapDemoOuterClass.NestedMapDemo;

/**
 * @author koqizhao
 *
 *         Dec 12, 2017
 */
public class NestedMapFieldTest {

  @Test
  public void normalDemoObjTest() throws IOException {
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
      Assert.assertEquals(demo, demo2);
    } finally {
      is.close();
    }
  }

  @Test
  public void nestedMapDemoObjTest() throws IOException {
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
      Assert.assertEquals(demo, demo2);
    } finally {
      is.close();
    }
  }

  @Test
  public void nestedMapDemoObjTest2() throws IOException {
    Map<String, Integer> mapValue = new HashMap<String, Integer>();
    mapValue.put("ok1", 1);
    mapValue.put("ok2", 2);
    NestedMapDemo2.NestedMapDemo demo = NestedMapDemo2.NestedMapDemo.newBuilder().setTitle("ok").setUrl("http://test")
        .addSnippets("ok").putMetadata(1, mapValue).build();
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
      NestedMapDemo2.NestedMapDemo demo2 = NestedMapDemo2.NestedMapDemo.parseFrom(is);
      System.out.println(demo2);

      Assert.assertEquals(demo.getTitle(), demo2.getTitle());
      Assert.assertEquals(demo.getUrl(), demo2.getUrl());
      Assert.assertEquals(demo.getSnippetsList(), demo2.getSnippetsList());
      Assert.assertEquals(demo.getMetadataMap(), demo2.getMetadataMap());
      Assert.assertEquals(demo, demo2);
    } finally {
      is.close();
    }
  }

  @Test
  public void nestedMapDemoObjTest3() throws IOException {
    Map<Integer, Map<Integer, Integer>> expected = new HashMap<Integer, Map<Integer, Integer>>();
    expected.put(11, new HashMap<Integer, Integer>());
    expected.get(11).put(12, 13);

    expected.put(21, new HashMap<Integer, Integer>());
    expected.get(21).put(22, 23);
    expected.get(21).put(24, 25);

    NestedMapDemo3.NestedMapDemo demo = NestedMapDemo3.NestedMapDemo.newBuilder().putAllMetadata(expected).build();

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
    NestedMapDemo3.NestedMapDemo demo2;
    try {
      demo2 = NestedMapDemo3.NestedMapDemo.parseFrom(is);
    } finally {
      is.close();
    }

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(demo2.getMetadataMap());
    System.out.println();

    Assert.assertEquals(demo, demo2);
  }

  @Test
  public void nestedMapDemoObjTest4() throws IOException {
    Map<String, Demo> mapValue = new HashMap<String, Demo>();
    mapValue.put("ok1", Demo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok").putMetadata(1, "11")
        .putMetadata(2, "22").build());
    mapValue.put("ok2", Demo.newBuilder().setTitle("ok2").setUrl("http://test2").addSnippets("ok2").putMetadata(3, "33")
        .putMetadata(4, "44").build());
    NestedMapDemo4.NestedMapDemo demo = NestedMapDemo4.NestedMapDemo.newBuilder().setTitle("ok").setUrl("http://test")
        .addSnippets("ok").putMetadata(1, mapValue).build();
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
      NestedMapDemo4.NestedMapDemo demo2 = NestedMapDemo4.NestedMapDemo.parseFrom(is);
      System.out.println(demo2);

      Assert.assertEquals(demo.getTitle(), demo2.getTitle());
      Assert.assertEquals(demo.getUrl(), demo2.getUrl());
      Assert.assertEquals(demo.getSnippetsList(), demo2.getSnippetsList());
      Assert.assertEquals(demo.getMetadataMap(), demo2.getMetadataMap());
      Assert.assertEquals(demo, demo2);
    } finally {
      is.close();
    }
  }

  @Test
  public void protobufMapTest() throws IOException {
    Map<Integer, Map<Integer, Integer>> expected = new HashMap<Integer, Map<Integer, Integer>>();
    expected.put(11, new HashMap<Integer, Integer>());
    expected.get(11).put(12, 13);

    expected.put(21, new HashMap<Integer, Integer>());
    expected.get(21).put(22, 23);
    expected.get(21).put(24, 25);

    ProtobufMapSerializer<Integer, Map<Integer, Integer>> serializer = ProtobufMapSerializer
        .<Integer, Map<Integer, Integer>> newBuilder().keyTypes(FieldType.INT32, FieldType.INT32)
        .valueType(FieldType.INT32).build();

    byte[] bytes = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      serializer.serialize(os, expected);
      bytes = os.toByteArray();
      System.out.println("bytes length: " + bytes.length);
    } finally {
      os.close();
    }

    System.out.println();

    Map<Integer, Map<Integer, Integer>> actual;
    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    try {
      actual = serializer.deserialize(is);
    } finally {
      is.close();
    }

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(actual);
    System.out.println();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void protobufMapTest2() throws IOException {
    Map<Integer, Map<Integer, Integer>> expected = new HashMap<Integer, Map<Integer, Integer>>();
    expected.put(11, new HashMap<Integer, Integer>());
    expected.get(11).put(12, 13);

    expected.put(21, new HashMap<Integer, Integer>());
    expected.get(21).put(22, 23);
    expected.get(21).put(24, 25);

    ProtobufMapSerializer<Integer, Map<Integer, Integer>> serializer = ProtobufMapSerializer
        .<Integer, Map<Integer, Integer>> newBuilder().keyTypes(FieldType.INT32, FieldType.INT32)
        .valueType(FieldType.INT32).build();

    byte[] bytes = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      serializer.serialize(os, expected);
      bytes = os.toByteArray();
      System.out.println("bytes length: " + bytes.length);
    } finally {
      os.close();
    }

    System.out.println();

    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    NestedMapDemo3.NestedMapDemo demo2;
    try {
      demo2 = NestedMapDemo3.NestedMapDemo.parseFrom(is);
    } finally {
      is.close();
    }

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(demo2.getMetadataMap());
    System.out.println();
  }

  @Test
  public void nestedMapDemoObjTestForDotnetInput() throws IOException {
    Map<String, String> nestedMapValue = new HashMap<String, String>();
    nestedMapValue.put("k1", "v1");
    nestedMapValue.put("k2", "v2");

    Map<Integer, Map<String, String>> mapValue = new HashMap<Integer, Map<String, String>>();
    mapValue.put(1, nestedMapValue);

    NestedMapDemo expected = NestedMapDemo.newBuilder().setUrl("http://www.ctrip.com").setTitle("test")
        .addSnippets("t1").addSnippets("t2").putAllMetadata(mapValue).build();

    InputStream is = null;
    NestedMapDemo demo;
    try {
      is = NestedMapFieldTest.class.getResourceAsStream("demo.bin");
      demo = NestedMapDemo.parseFrom(is);
    } finally {
      if (is != null)
        is.close();
    }

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(demo);
    System.out.println();

    Assert.assertEquals(expected, demo);
  }

  @Test
  public void protobufMapTestForDotnetInput() throws IOException {
    Map<Integer, Map<Integer, Integer>> expected = new HashMap<Integer, Map<Integer, Integer>>();
    expected.put(11, new HashMap<Integer, Integer>());
    expected.get(11).put(12, 13);

    expected.put(21, new HashMap<Integer, Integer>());
    expected.get(21).put(22, 23);
    expected.get(21).put(24, 25);

    ProtobufMapSerializer<Integer, Map<Integer, Integer>> serializer = ProtobufMapSerializer
        .<Integer, Map<Integer, Integer>> newBuilder().keyTypes(FieldType.INT32, FieldType.INT32)
        .valueType(FieldType.INT32).build();

    Map<Integer, Map<Integer, Integer>> actual;
    InputStream is = null;
    try {
      is = NestedMapFieldTest.class.getResourceAsStream("mapOf3Int.bin");
      actual = serializer.deserialize(is);
    } finally {
      if (is != null)
        is.close();
    }

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(actual);
    System.out.println();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void customFieldType() throws InvalidProtocolBufferException, DescriptorValidationException {
    testMapDescriptor(FieldType.BOOL, null);
    testMapDescriptor(FieldType.BOOL, FieldType.STRING);
    testMapDescriptor(FieldType.STRING, FieldType.MESSAGE);
  }

  private void testMapDescriptor(FieldType keyType, FieldType valueType) {
    boolean isParent = valueType == null;
    Descriptor map = isParent ? MapDescriptors.newDescriptorForParent(keyType)
        : MapDescriptors.newDescriptorForLeaf(keyType, valueType);

    FieldDescriptor key = map.getFields().get(0);
    Assert.assertEquals(key.getName(), "key");
    Assert.assertEquals(key.getNumber(), 1);
    Assert.assertEquals(key.isOptional(), true);
    Assert.assertEquals(key.getType().toString(), keyType.toString());

    FieldDescriptor value = map.getFields().get(1);
    Assert.assertEquals(value.getName(), "value");
    Assert.assertEquals(value.getNumber(), 2);
    if (isParent)
      valueType = FieldType.MESSAGE;
    Assert.assertEquals(value.isRepeated(), isParent);
    Assert.assertNotEquals(value.isOptional(), isParent);
    Assert.assertEquals(value.getType().toString(), valueType.toString());
  }

  @Test
  public void testToType() {
    Type type = MapDescriptors.toType(FieldType.BOOL);
    Assert.assertEquals(Type.TYPE_BOOL, type);
  }

}
