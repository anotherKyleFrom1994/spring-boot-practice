package org.anotherkyle.commonlib.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.anotherkyle.commonlib.ApiStatus;
import org.anotherkyle.commonlib.ApplicationStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ApplicationException extends RuntimeException {
    private ApiStatus apiStatus = ApplicationStatus.INTERNAL_SERVER_ERROR;
    private Long imageId;
    private Long userId;

    public ApplicationException(Throwable t) {
        super(t);
        if (t instanceof ApplicationException)
            this.apiStatus = ((ApplicationException) t).getApiStatus();
    }

    public ApplicationException(String msg) {
        super(msg);
    }

    public ApplicationException(String msg, Throwable t) {
        super(msg, t);
        if (t instanceof ApplicationException)
            this.apiStatus = ((ApplicationException) t).getApiStatus();
    }

    public ApplicationException(String msg, ApiStatus apiStatus) {
        super(msg);
        this.apiStatus = apiStatus;
    }

    public ApplicationException(ApiStatus apiStatus, Throwable t) {
        super(apiStatus.getMessage(), t);
        this.apiStatus = apiStatus;
    }

    public ApplicationException(String msg, ApiStatus apiStatus, Throwable t) {
        super(msg, t);
        this.apiStatus = apiStatus;
    }

    public ApplicationException(ApiStatus apiStatus) {
        super(apiStatus.getMessage());
        this.apiStatus = apiStatus;
    }

    public static ApplicationException initWithSuppressed(ApiStatus apiStatus, Throwable t) {
        ApplicationException res = new ApplicationException(apiStatus);
        res.addSuppressed(t);
        return res;
    }

    public static ApplicationException initWithSuppressed(String msg, ApiStatus apiStatus, Throwable t) {
        ApplicationException res = new ApplicationException(msg, apiStatus);
        res.addSuppressed(t);
        return res;
    }

    @Override
    public String toString() {
        return String.format("%s: (apiStatus=%s ,message=%s)", this.getClass().getName(), apiStatus, this.getMessage());
    }
}
