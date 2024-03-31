package org.anotherkyle.democryptorap.common.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedData {
    private String key;
    private String data;

    public void validate() {
        if (data == null || data.isEmpty()) throw new NullPointerException("data is null or empty");
        else if (key == null || key.isEmpty()) throw new NullPointerException("key is null or empty");
    }
}
