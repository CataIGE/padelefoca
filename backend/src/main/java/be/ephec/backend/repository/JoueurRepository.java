package be.ephec.backend.repository;

import be.ephec.backend.model.Joueur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    Optional<Joueur> findByMatricule(String matricule);

    boolean existsByMatricule(String matricule);

    @Modifying
    @Transactional
    @Query(value = "UPDATE joueur SET dtype = 'GLOBAL' WHERE matricule = :matricule", nativeQuery = true)
    void updateDtype(@Param("matricule") String matricule);
}