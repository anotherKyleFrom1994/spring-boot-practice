package org.anotherkyle.commonlib;

import lombok.NonNull;
import org.anotherkyle.commonlib.util.MessageUtil;

public class BaseService {
    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    protected static String truncateMsg(@NonNull String msg) {
        return MessageUtil.truncateMsg(msg, 1000, 500);
    }
}
