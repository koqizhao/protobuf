package com.google.protobuf.util;

import java.util.Calendar;

import com.google.protobuf.DateTime;
import com.google.protobuf.TimeSpanScale;

/**
 * @author koqizhao
 *
 *         Jan 4, 2018
 */
public class DateTimes {

  // milliseconds between 9999-12-31 23:59:59.999 and 1970-0-0 00:00:00.000
  protected static final long MAX_MILLISECONDS = 253402271999999L;
  // milliseconds between 1970-0-0 00:00:00.000 and 0000-0-0 00:00:00.000
  protected static final long MIN_MILLISECONDS = -62135798400000L;

  protected static final long TICKS_PER_MILLISECOND = 10000L;
  protected static final long TICKS_PER_SECOND = TICKS_PER_MILLISECOND * 1000;
  protected static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
  protected static final long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;
  protected static final long TICKS_PER_DAY = TICKS_PER_HOUR * 24;

  protected static final long MAXMIN_MAX = 1L;
  protected static final long MAXMIN_MIN = -1L;

  protected static final long LOCAL_TIME_ZONE_OFFSET = Calendar.getInstance().get(Calendar.ZONE_OFFSET);

  public static DateTime valueOf(Calendar calendar) {
    long milliseconds = calendar.getTimeInMillis() + LOCAL_TIME_ZONE_OFFSET;
    DateTime.Builder result = DateTime.newBuilder();
    if (milliseconds >= MAX_MILLISECONDS) {
      result.setValue(MAXMIN_MAX);
      result.setScale(TimeSpanScale.MINMAX);
    } else if (milliseconds <= MIN_MILLISECONDS) {
      result.setValue(MAXMIN_MIN);
      result.setScale(TimeSpanScale.MINMAX);
    } else {
      result.setValue(milliseconds * TICKS_PER_MILLISECOND);
      result.setScale(TimeSpanScale.TICKS);
    }

    return result.build();
  }

  public static Calendar toCalendar(DateTime dateTime) {
    Calendar result = Calendar.getInstance();
    result.setTimeInMillis(toMilliseconds(dateTime) - LOCAL_TIME_ZONE_OFFSET);
    return result;
  }

  protected static long toMilliseconds(DateTime dateTime) {
    long ticks = toTicks(dateTime);
    if (ticks == Long.MAX_VALUE)
      return MAX_MILLISECONDS;

    if (ticks == Long.MIN_VALUE)
      return MIN_MILLISECONDS;

    return ticks / TICKS_PER_MILLISECOND;
  }

  protected static long toTicks(DateTime dateTime) {
    switch (dateTime.getScale()) {
      case DAYS:
        return dateTime.getValue() * TICKS_PER_DAY;
      case HOURS:
        return dateTime.getValue() * TICKS_PER_HOUR;
      case MINUTES:
        return dateTime.getValue() * TICKS_PER_MINUTE;
      case SECONDS:
        return dateTime.getValue() * TICKS_PER_SECOND;
      case MILLISECONDS:
        return dateTime.getValue() * TICKS_PER_MILLISECOND;
      case TICKS:
        return dateTime.getValue();
      case MINMAX:
        if (dateTime.getValue() == MAXMIN_MAX)
          return Long.MAX_VALUE;
        else if (dateTime.getValue() == MAXMIN_MIN)
          return Long.MIN_VALUE;
        else
          throw new IllegalArgumentException("Unknown min/max value: " + dateTime.getValue());
      default:
        throw new IllegalArgumentException("Unknown timescale: " + dateTime.getScale());
    }
  }

}
