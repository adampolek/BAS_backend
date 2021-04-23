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
public class AdditionalInfoForm {

    private Long id;
    private Integer cigarettesAmount;
    private Double sleepHours;
    private Integer glassesOfWater;
    private Double trainingHours;
    private Integer alcoholAmount;

}
