package com.google.protobuf.dotnettype;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

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

    DateTime actualDateTime = DateTimes.valueOf(actual);
    System.out.println(actualDateTime);
    Assert.assertEquals(dateTime, actualDateTime);
  }

  @Test
  public void serializationTestForDotnetInputSpecific() throws IOException {
    Calendar expected = new GregorianCalendar(2018, 0, 4, 17, 59, 0);
    expected.set(Calendar.MILLISECOND, 333);

    serializationTestForDotnetInput("datetimeOfSpecific.bin", expected);
  }

  @Test
  public void serializationTestForDotnetInputMinValue() throws IOException {
    Calendar expected = new GregorianCalendar(1, 0, 1, 0, 0, 0);
    serializationTestForDotnetInput("datetimeOfMinValue.bin", expected);
  }

  @Test
  public void serializationTestForDotnetInputMaxValue() throws IOException {
    Calendar expected = new GregorianCalendar(9999, 11, 31, 23, 59, 59);
    expected.set(Calendar.MILLISECOND, 999);
    serializationTestForDotnetInput("datetimeOfMaxValue.bin", expected);
  }

  private void serializationTestForDotnetInput(String dotnetBinFile, Calendar expected) throws IOException {
    DateTime actualDateTime;
    Calendar actual;
    InputStream is = null;
    try {
      is = DateTimeTest.class.getResourceAsStream(dotnetBinFile);
      DateTimeDemo demo = DateTimeDemo.parseFrom(is);
      actualDateTime = demo.getDateTimeValue();
      actual = DateTimes.toCalendar(actualDateTime);
    } finally {
      if (is != null)
        is.close();
    }

    System.out.println(DateTimes.valueOf(expected));

    System.out.println("Expected: ");
    System.out.println(formatDate(expected));
    System.out.println();

    System.out.println(actualDateTime);

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
