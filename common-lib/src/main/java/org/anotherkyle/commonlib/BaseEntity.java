package org.anotherkyle.commonlib;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Data
public abstract class BaseEntity<ID> implements Persistable<ID> {
    @Transient
    private boolean isNew = getId() == null;

    public boolean isNew() {
        return isNew;
    }
}
