package org.anotherkyle.commonlib.util;

import lombok.NonNull;

public class MessageUtil {
    public static String truncateMsg(@NonNull String msg, int greaterThan,int keptLength) {
        int length = msg.length();
        if (length > greaterThan)
            return String.format("%s......%s", msg.substring(0, keptLength), msg.substring(length - keptLength, length));
        return msg;
    }
}
