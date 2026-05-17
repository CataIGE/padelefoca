package be.ephec.backend.repository;

import be.ephec.backend.model.Joueur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    Optional<Joueur> findByMatricule(String matricule);

    boolean existsByMatricule(String matricule);
}