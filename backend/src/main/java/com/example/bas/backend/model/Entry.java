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
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    private AppUser user;
    private Double weight;
    private Integer glucose;
    private Integer insulin;
    private Integer bloodPressure;
    @Temporal(TemporalType.DATE)
    private Date entryDate;
    private Boolean healthy;
}
