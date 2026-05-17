package be.ephec.backend.model;

import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMatch;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "match_padel")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "terrain_id", nullable = false)
    private Terrain terrain;

    @ManyToOne
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Joueur organisateur;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMatch typeMatch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMatch statutMatch = StatutMatch.PLANIFIE;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}