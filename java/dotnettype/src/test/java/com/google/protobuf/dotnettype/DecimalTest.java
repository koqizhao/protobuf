package com.google.protobuf.dotnettype;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author koqizhao
 *
 *         Jan 4, 2018
 */
public class DecimalTest {

  @Test
  public void testConversion() {
    String[] testData = new String[] { "10.01", "100000000000000000", "0.99999999999999999999", "-99.1",
        "-0.0000000000000000000000000001", "0" };
    for (String expected : testData) {
      testConversion(expected);
    }
  }

  private void testConversion(String data) {
    BigDecimal expected = new BigDecimal(data);
    System.out.println(expected);
    System.out.println();

    Decimal decimal = Decimals.valueOf(expected);
    System.out.println(decimal);

    BigDecimal actual = Decimals.bigDecimalValue(decimal);
    System.out.println(actual);
    System.out.println();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void serializationTestForDotnetInput() throws IOException {
    BigDecimal actual;
    InputStream is = null;
    try {
      is = DecimalTest.class.getResourceAsStream("decimalOfFloat01.bin");
      DecimalDemo demo = DecimalDemo.parseFrom(is);
      System.out.println(demo.getDecimalValue());
      actual = Decimals.bigDecimalValue(demo.getDecimalValue());
      System.out.println(actual);
      System.out.println();
    } finally {
      if (is != null)
        is.close();
    }

    BigDecimal expected = new BigDecimal("0.0000000000000000001");
    Decimal decimal = Decimals.valueOf(expected);
    System.out.println(decimal);
    System.out.println(Decimals.bigDecimalValue(decimal));
    System.out.println();

    System.out.println("Expected: ");
    System.out.println(expected);
    System.out.println();

    System.out.println("Actual: ");
    System.out.println(actual);
    System.out.println();

    Assert.assertEquals(expected, actual);
  }

}
