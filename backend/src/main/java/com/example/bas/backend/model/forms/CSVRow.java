package com.example.bas.backend.model.forms;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CSVRow {
    String gender;
    Integer glucose;
    Integer bloodPressure;
    Integer insulin;
    Double bmi;
    Integer age;
    Double outcome;
}
