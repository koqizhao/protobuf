package com.google.protobuf.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.DateTime;
import com.google.protobuf.util.DateTimeDemoOuterClass.DateTimeDemo;

/**
 * @author koqizhao
 *
 *         Jan 4, 2018
 */
public class DateTimeTest {

  @Test
  public void testConversion() {
    long[] testData = new long[] { new Date().getTime(), Calendar.getInstance().getTimeInMillis(), 0 };
    for (long milliseconds : testData) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(milliseconds);
      testConversion(calendar);
    }
  }

  public void testConversion(Calendar expected) {
    System.out.println(expected.getTimeInMillis());
    System.out.println();

    DateTime dateTime = DateTimes.valueOf(expected);
    System.out.println(dateTime);

    Calendar actual = DateTimes.toCalendar(dateTime);
    System.out.println(expected.getTimeInMillis());
    System.out.println();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void serializationTestForDotnetInput() throws IOException {
    Calendar actual;
    InputStream is = null;
    try {
      is = DateTimeTest.class.getResourceAsStream("datetimeOfSpecific.bin");
      DateTimeDemo demo = DateTimeDemo.parseFrom(is);
      actual = DateTimes.toCalendar(demo.getDateTimeValue());
    } finally {
      if (is != null)
        is.close();
    }

    Calendar expected = new GregorianCalendar(2018, 0, 4, 17, 59, 0);
    expected.set(Calendar.MILLISECOND, 333);

    System.out.println("Expected: ");
    System.out.println(formatDate(expected));
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(formatDate(actual));
    System.out.println();

    Assert.assertEquals(expected.getTimeInMillis(), actual.getTimeInMillis());
  }

  private String formatDate(Calendar calendar) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    return format.format(calendar.getTime());
  }

}
