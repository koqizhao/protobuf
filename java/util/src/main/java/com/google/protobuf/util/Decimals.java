package com.google.protobuf.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.protobuf.Decimal;

/**
 * @author koqizhao
 *
 *         Jan 4, 2018
 */
public class Decimals {

  public static byte byteValue(Decimal value) {
    return bigDecimalValue(value).byteValue();
  }

  public static int intValue(Decimal value) {
    return bigDecimalValue(value).intValue();
  }

  public static float floatValue(Decimal value) {
    return bigDecimalValue(value).floatValue();
  }

  public static long longValue(Decimal value) {
    return bigDecimalValue(value).longValue();
  }

  public static Decimal valueOf(long value) {
    Decimal.Builder result = Decimal.newBuilder();
    result.setSignScale(value < 0 ? 1 : 0);
    result.setLow(Math.abs(value));
    result.setHigh(0);
    return result.build();
  }

  public static double doubleValue(Decimal value) {
    return bigDecimalValue(value).doubleValue();
  }

  public static Decimal valueOf(double value) {
    return valueOf(BigDecimal.valueOf(value));
  }

  public static BigDecimal bigDecimalValue(Decimal value) {
    byte[] bytes = new byte[16];

    long low = value.getLow();
    int high = value.getHigh();
    int signScale = value.getSignScale();

    bytes[0] = (byte) ((signScale & 0xFF000000) >> 24);
    bytes[1] = (byte) ((signScale & 0x00FF0000) >> 16);
    bytes[2] = (byte) ((signScale & 0x0000FF00) >> 8);
    bytes[3] = (byte) (signScale & 0x000000FF);

    bytes[4] = (byte) ((high & 0xFF000000) >> 24);
    bytes[5] = (byte) ((high & 0x00FF0000) >> 16);
    bytes[6] = (byte) ((high & 0x0000FF00) >> 8);
    bytes[7] = (byte) (high & 0x000000FF);

    bytes[8] = (byte) ((low & 0xFF00000000000000L) >> 56);
    bytes[9] = (byte) ((low & 0x00FF000000000000L) >> 48);
    bytes[10] = (byte) ((low & 0x0000FF0000000000L) >> 40);
    bytes[11] = (byte) ((low & 0x000000FF00000000L) >> 32);
    bytes[12] = (byte) ((low & 0x00000000FF000000L) >> 24);
    bytes[13] = (byte) ((low & 0x0000000000FF0000L) >> 16);
    bytes[14] = (byte) ((low & 0x000000000000FF00L) >> 8);
    bytes[15] = (byte) (low & 0x00000000000000FFL);

    return Converter.decimalToBigDecimal(signScale, bytes);
  }

  public static Decimal valueOf(BigDecimal value) {
    byte[] bytes = Converter.bigDecimalToDecimal(value);

    long low = 0;
    low |= (long) bytes[8] << 56 & 0xFF00000000000000L;
    low |= (long) bytes[9] << 48 & 0x00FF000000000000L;
    low |= (long) bytes[10] << 40 & 0x0000FF0000000000L;
    low |= (long) bytes[11] << 32 & 0x000000FF00000000L;
    low |= (long) bytes[12] << 24 & 0x00000000FF000000L;
    low |= (long) bytes[13] << 16 & 0x0000000000FF0000L;
    low |= (long) bytes[14] << 8 & 0x000000000000FF00L;
    low |= (long) bytes[15] & 0x00000000000000FFL;

    int high = 0;
    high |= (int) bytes[4] << 24 & 0xFF000000;
    high |= (int) bytes[5] << 16 & 0x00FF0000;
    high |= (int) bytes[6] << 8 & 0x0000FF00;
    high |= (int) bytes[7] & 0x000000FF;

    int signScale = 0;
    signScale |= (int) bytes[0] << 24 & 0xFF000000;
    signScale |= (int) bytes[1] << 16 & 0x00FF0000;
    signScale |= (int) bytes[2] << 8 & 0x0000FF00;
    signScale |= (int) bytes[3] & 0x000000FF;

    signScale = signScale >> 15 & 0x01FE | signScale >> 31 & 0x0001;

    return Decimal.newBuilder().setLow(low).setHigh(high).setSignScale(signScale).build();
  }

  /**
   * Created by marsqing on 21/03/2017.
   */
  protected static class Converter {

    // code from
    // http://stackoverflow.com/questions/9258313/conversion-bigdecimal-java-to-c-like-decimal
    public static byte[] bigDecimalToDecimal(BigDecimal paramBigDecimal) throws IllegalArgumentException {
      byte[] result = new byte[16];

      // Unscaled absolute value
      BigInteger unscaledInt = paramBigDecimal.abs().unscaledValue();
      // Scale
      int scale = paramBigDecimal.scale();
      int bitLength = unscaledInt.bitLength();

      while (bitLength > 96) {
        unscaledInt = unscaledInt.divide(BigInteger.TEN);
        scale--;
        bitLength = unscaledInt.bitLength();
      }

      // Byte array
      byte[] unscaledBytes = unscaledInt.toByteArray();
      int unscaledFirst = 0;
      if (unscaledBytes[0] == 0) {
        unscaledFirst = 1;
      }

      if (scale > 28) {
        throw new IllegalArgumentException("BigDecimal scale exceeds Decimal limit of 28");
      }
      result[1] = (byte) scale;

      // Copy unscaled value to bytes 8-15
      for (int pSource = unscaledBytes.length - 1, pTarget = 15; (pSource >= unscaledFirst)
          && (pTarget >= 4); pSource--, pTarget--) {
        result[pTarget] = unscaledBytes[pSource];
      }

      // Signum at byte 0
      if (paramBigDecimal.signum() < 0) {
        result[0] = -128;
      }

      return result;
    }

    public static BigDecimal decimalToBigDecimal(int signScale, byte[] paramNetDecimal) {
      byte scale = (byte) ((signScale & 0x01FE) >> 1);
      int signum = (paramNetDecimal[3] & 0x01) == 1 ? -1 : 1;
      byte[] magnitude = new byte[12];
      for (int ptr = 0; ptr < 12; ptr++) {
        magnitude[ptr] = paramNetDecimal[ptr + 4];
      }

      BigInteger unscaledInt = new BigInteger(signum, magnitude);
      return new BigDecimal(unscaledInt, scale);
    }

  }

}
