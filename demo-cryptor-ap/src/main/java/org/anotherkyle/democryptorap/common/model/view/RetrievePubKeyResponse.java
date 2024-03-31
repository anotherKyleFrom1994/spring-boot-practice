package org.anotherkyle.democryptorap.common.model.view;


import org.anotherkyle.commonlib.BaseView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class RetrievePubKeyResponse extends BaseView {
    private String key;
}
