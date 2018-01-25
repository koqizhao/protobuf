package com.google.protobuf.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.dotnettype.DateTimeDemoOuterClass.DateTimeDemo;
import com.google.protobuf.dotnettype.DateTimes;
import com.google.protobuf.dotnettype.DecimalDemoOuterClass.DecimalDemo;
import com.google.protobuf.list.NestedMapDemo;

/**
 * @author koqizhao
 *
 *         Jan 19, 2018
 */
public class MoreJsonFormatTest {

  @Test
  public void serializerTestForDateTime() throws IOException {
    long[] testData = new long[] { new Date().getTime(), Calendar.getInstance().getTimeInMillis(), 0,
        DateTimes.defaultCalendar().getTimeInMillis() };
    for (long milliseconds : testData) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(milliseconds);
      serializerTestForDateTime(calendar);
    }
  }

  private void serializerTestForDateTime(Calendar calendar) throws IOException {
    DateTimeDemo expected = DateTimeDemo.newBuilder().setTitle("test").setDateTimeValue(calendar).build();

    String os = JsonFormat.printer().includingDefaultValueFields().preservingProtoFieldNames().print(expected);
    System.out.println(os);
    System.out.println();

    DateTimeDemo.Builder builder = DateTimeDemo.newBuilder();
    JsonFormat.parser().merge(os, builder);
    DateTimeDemo actual = builder.build();

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(actual);
    System.out.println();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void serializerTestForDecimal() throws IOException {
    String[] testData = new String[] { "10.01", "100000000000000000", "0.99999999999999999999", "-99.1",
        "-0.0000000000000000000000000001", "0" };
    for (String expected : testData) {
      serializerTestForDecimal(new BigDecimal(expected));
    }
  }

  private void serializerTestForDecimal(BigDecimal bigDecimal) throws IOException {
    DecimalDemo expected = DecimalDemo.newBuilder().setTitle("test").setDecimalValue(bigDecimal).build();

    String os = JsonFormat.printer().includingDefaultValueFields().preservingProtoFieldNames().print(expected);
    System.out.println(os);
    System.out.println();

    DecimalDemo.Builder builder = DecimalDemo.newBuilder();
    JsonFormat.parser().merge(os, builder);
    DecimalDemo actual = builder.build();

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(actual);
    System.out.println();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void serializerTestForNestedMap() throws IOException {
    Map<String, String> mapValue = new HashMap<String, String>();
    mapValue.put("ok1", "ok1_value");
    mapValue.put("ok2", "ok2_value");
    NestedMapDemo expected = NestedMapDemo.newBuilder().setTitle("ok").setUrl("http://test").addSnippets("ok")
        .putMetadata(1, mapValue).build();
    System.out.println(expected);

    String os = JsonFormat.printer().includingDefaultValueFields().preservingProtoFieldNames().print(expected);
    System.out.println(os);
    System.out.println();

    NestedMapDemo.Builder builder = NestedMapDemo.newBuilder();
    JsonFormat.parser().merge(os, builder);
    NestedMapDemo actual = builder.build();

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(actual);
    System.out.println();

    Assert.assertEquals(expected, actual);
  }

}
