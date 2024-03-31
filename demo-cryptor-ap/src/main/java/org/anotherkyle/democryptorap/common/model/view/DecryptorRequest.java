package org.anotherkyle.democryptorap.common.model.view;

import org.anotherkyle.commonlib.BaseView;
import org.anotherkyle.commonlib.validator.LegalBase64;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DecryptorRequest extends BaseView {
    @NotBlank
    @LegalBase64
    private String message;
    @NotNull
    private Boolean useUrlSafeBase64 = false;
}
