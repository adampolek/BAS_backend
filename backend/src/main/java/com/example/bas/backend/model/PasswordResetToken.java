package com.example.bas.backend.model;

import lombok.*;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private Date expiryDate;

    public void setExpiryDate(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.expiryDate = now.getTime();
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}