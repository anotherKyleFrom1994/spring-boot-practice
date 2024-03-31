package org.anotherkyle.democryptorap.common.model.view;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.anotherkyle.commonlib.BaseView;
import org.anotherkyle.democryptorap.common.cryptor.RSAUtil;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class RetrievePubKeyRequest extends BaseView {
    @NotNull
    private RSAUtil.Format format = RSAUtil.Format.HEX;
}
