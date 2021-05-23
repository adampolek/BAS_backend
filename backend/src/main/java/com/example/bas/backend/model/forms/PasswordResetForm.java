package com.example.bas.backend.model.forms;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PasswordResetForm {

    private String password;
    private String token;

}
