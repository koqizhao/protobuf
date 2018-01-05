package com.google.protobuf.list;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.ProtobufListSerializer;
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
  public void listSerializerTest2() throws IOException {
    List<Demo> expected = new ArrayList<Demo>();
    Demo demo = Demo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok").putMetadata(1, "o1").build();
    Demo demo2 = Demo.newBuilder().setTitle("ok2").setUrl("http://test2").addSnippets("ok2").putMetadata(2, "o2")
        .build();
    expected.add(demo);
    expected.add(demo2);

    ProtobufListSerializer<Demo> serializer = ProtobufListSerializer.<Demo> newBuilder().valueType(FieldType.MESSAGE)
        .valueDefault(Demo.getDefaultInstance()).build();

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

    List<Demo> actual;
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
  public void listSerializerTest3() throws IOException {
    List<Demo> expected = new ArrayList<Demo>();
    Demo demo = Demo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok").putMetadata(1, "o1").build();
    Demo demo2 = Demo.newBuilder().setTitle("ok2").setUrl("http://test2").addSnippets("ok2").putMetadata(2, "o2")
        .build();
    expected.add(demo);
    expected.add(demo2);

    ProtobufListSerializer<Demo> serializer = ProtobufListSerializer.<Demo> newBuilder().valueType(FieldType.MESSAGE)
        .valueDefault(Demo.getDefaultInstance()).build();

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
    List<Demo> actual;
    try {
      DemoList demoList = DemoList.parseFrom(is);
      actual = demoList.getValueList();
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
  public void listSerializerTestForDotnetInput() throws IOException {
    ProtobufListSerializer<Integer> serializer = ProtobufListSerializer.<Integer> newBuilder()
        .valueType(FieldType.INT32).build();

    List<Integer> actual;
    InputStream is = null;
    try {
      is = ListSerializerTest.class.getResourceAsStream("listOfInt.bin");
      actual = serializer.deserialize(is);
    } finally {
      if (is != null)
        is.close();
    }

    List<Integer> expected = new ArrayList<Integer>();
    expected.add(1);
    expected.add(2);

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(actual);
    System.out.println();

    Assert.assertEquals(expected, actual);
  }

}
