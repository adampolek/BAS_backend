package com.example.bas.backend.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ClassifierEntry {
    List<String> Gender;
    List<Integer> Glucose;
    List<Integer> BloodPressure;
    List<Integer> Insulin;
    List<Double> BMI;
    List<Integer> Age;
}
