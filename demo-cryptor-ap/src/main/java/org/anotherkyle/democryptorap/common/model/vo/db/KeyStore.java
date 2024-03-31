package org.anotherkyle.democryptorap.common.model.vo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("keystore")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyStore implements Persistable<Long> {
    @Id
    private Long id;
    @Column
    private byte[] data;
    @CreatedDate
    @Column
    private LocalDateTime created;

    @Override
    public boolean isNew() {
        return true;
    }
}
