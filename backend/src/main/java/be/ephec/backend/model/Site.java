package be.ephec.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "site")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private LocalTime heureOuverture;

    @Column(nullable = false)
    private LocalTime heureFermeture;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> joursRepos;
}