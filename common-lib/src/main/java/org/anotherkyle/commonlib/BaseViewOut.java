package org.anotherkyle.commonlib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BaseViewOut extends BaseView {
    private ApplicationStatus status = ApplicationStatus.SUCCESS;
    private String message;

    public void setMessage(String format, Object... messages) {
        this.message = String.format(format, messages);
    }
}
