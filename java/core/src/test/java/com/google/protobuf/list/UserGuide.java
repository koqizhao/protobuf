package com.google.protobuf.list;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.ProtobufListSerializer;
import com.google.protobuf.ProtobufMapSerializer;
import com.google.protobuf.WireFormat.FieldType;

/**
 * @author koqizhao
 *
 *         Dec 21, 2017
 */
public class UserGuide {

  @Test
  public void nestedMapInMessage() throws IOException {
    // init the nested map
    Map<String, String> mapValue = new HashMap<String, String>();
    mapValue.put("ok1", "ok1_value");
    mapValue.put("ok2", "ok2_value");
    Map<Integer, Map<String, String>> nestedMap = new HashMap<Integer, Map<String, String>>();
    nestedMap.put(1, mapValue);

    // init the message with nested map field
    NestedMapDemo message = NestedMapDemo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok")
        .putAllMetadata(nestedMap).build();
    System.out.println(message);

    // serialization
    byte[] bytes = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      message.writeTo(os);
      bytes = os.toByteArray();
    } finally {
      os.close();
    }

    // deserialization
    NestedMapDemo message2 = null;
    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    try {
      message2 = NestedMapDemo.parseFrom(is);
      System.out.println(message2);
    } finally {
      is.close();
    }

    // same as origin
    Assert.assertEquals(message, message2);
  }

  @Test
  public void genericMapSerialization() throws IOException {
    // init a nested generic map
    Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();
    map.put(11, new HashMap<Integer, Integer>());
    map.get(11).put(12, 13);
    map.put(21, new HashMap<Integer, Integer>());
    map.get(21).put(22, 23);
    map.get(21).put(24, 25);
    System.out.println(map);

    // init pb map serializer, which is thread safe & reusable
    // nested map can be think as multiple keys & a single value:
    // key1, key2, ..., keyN, value
    ProtobufMapSerializer<Integer, Map<Integer, Integer>> serializer = ProtobufMapSerializer
        .<Integer, Map<Integer, Integer>> newBuilder().keyTypes(FieldType.INT32, FieldType.INT32)
        .valueType(FieldType.INT32).build();

    // Notice:
    // the Builder has another method:
    // valueDefault(Object valueDefault)
    // if valueType is Enum or Message, valueDefault method must be invoked.
    // For example:
    // valueDefault(SomeMessageClass.getDefaultInstance())

    // serialization
    byte[] bytes = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      serializer.serialize(os, map);
      bytes = os.toByteArray();
    } finally {
      os.close();
    }

    // deserialization
    Map<Integer, Map<Integer, Integer>> map2;
    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    try {
      map2 = serializer.deserialize(is);
      System.out.println(map2);
    } finally {
      is.close();
    }

    // same as origin
    Assert.assertEquals(map, map2);
  }

  @Test
  public void listSerializerTest() throws IOException {
    // init a generic list
    List<Integer> expected = new ArrayList<Integer>();
    expected.add(11);
    expected.add(22);

    // init pb list serializer, which is thread safe & reusable
    ProtobufListSerializer<Integer> serializer = ProtobufListSerializer.<Integer> newBuilder()
        .valueType(FieldType.INT32).build();

    // Notice:
    // the Builder has another method:
    // valueDefault(V valueDefault)
    // if valueType is Enum or Message, valueDefault method must be invoked.
    // For example:
    // valueDefault(SomeMessageClass.getDefaultInstance())

    // serialization
    byte[] bytes = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      serializer.serialize(os, expected);
      bytes = os.toByteArray();
    } finally {
      os.close();
    }

    // deserialization
    List<Integer> actual;
    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    try {
      actual = serializer.deserialize(is);
    } finally {
      is.close();
    }

    Assert.assertEquals(expected, actual);
  }

}
