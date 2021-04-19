package com.example.bas.backend.model;

import lombok.*;

import java.util.Date;

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
