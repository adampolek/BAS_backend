package com.example.bas.backend.model.forms;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class DailyEntry {

    Date entryDate;
    Integer cigarettesAmount;
    Double sleepHours;
    Integer glassesOfWater;
    Double trainingHours;
    Integer alcoholAmount;
    Integer glucose;
    Integer bloodPressure;
    Integer insulin;
    Double weight;
    Boolean healthy;
}
