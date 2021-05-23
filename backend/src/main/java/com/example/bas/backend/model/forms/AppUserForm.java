package com.example.bas.backend.model.forms;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AppUserForm {

    private Long id;
    private String firstName;
    private String lastName;
    private Integer height;
    private String gender;
    private Date birthDate;
    private String password;
    private String email;

}
