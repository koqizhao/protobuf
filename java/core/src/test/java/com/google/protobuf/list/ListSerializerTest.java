package com.google.protobuf.list;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
 *         Jan 2, 2018
 */
public class ListSerializerTest {

  @Test
  public void listSerializerTest() throws IOException {
    List<Integer> expected = new ArrayList<Integer>();
    expected.add(11);
    expected.add(22);

    ProtobufListSerializer<Integer> serializer = ProtobufListSerializer.<Integer> newBuilder()
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

    List<Integer> actual;
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

}
