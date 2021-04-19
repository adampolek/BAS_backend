package com.example.bas.backend.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Table
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AdditionalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    private AppUser user;
    private Integer cigarettesAmount;
    private Double sleepHours;
    private Integer glassesOfWater;
    private Double trainingHours;
    private Integer alcoholAmount;
    private Date entryDate;
}
