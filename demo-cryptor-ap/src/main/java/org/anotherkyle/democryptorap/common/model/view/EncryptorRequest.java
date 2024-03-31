package org.anotherkyle.democryptorap.common.model.view;

import org.anotherkyle.commonlib.BaseView;
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
public class EncryptorRequest extends BaseView {
    @NotBlank
    private String message;
    @NotNull
    private Boolean useUrlSafeBase64 = false;
}
