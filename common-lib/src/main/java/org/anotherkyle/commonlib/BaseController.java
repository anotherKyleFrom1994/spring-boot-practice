package org.anotherkyle.commonlib;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping({"/api/v1"})
public class BaseController {
    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
}
