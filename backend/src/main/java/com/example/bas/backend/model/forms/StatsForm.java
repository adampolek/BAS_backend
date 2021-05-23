package com.example.bas.backend.model.forms;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class StatsForm {
    private Double sleepHours;
    private Double sleepHoursPercentage;
    private Boolean healthySleep;
}
