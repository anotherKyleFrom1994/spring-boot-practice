package org.anotherkyle.commonlib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {
    public static final String ZERO_HOUR_OFFSET = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static boolean parseAndCompare(String dateString, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateString).after(new Date());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
