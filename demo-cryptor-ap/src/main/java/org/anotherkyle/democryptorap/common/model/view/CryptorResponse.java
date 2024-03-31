package org.anotherkyle.democryptorap.common.model.view;

import org.anotherkyle.commonlib.BaseView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CryptorResponse extends BaseView {
    private String result;
}
