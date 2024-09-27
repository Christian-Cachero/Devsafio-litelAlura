package com.aluradevsafios.LiterAlura.repository;

import com.aluradevsafios.LiterAlura.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

   Optional<Persona> findByNombre(String nombre);

   Optional<List<Persona>> findByFechaMuerteGreaterThanEqualAndFechaNacimientoLessThanEqual(Integer periodoNacido, Integer periodoMuerte);
   //Optional<Persona> findByFechaMuerte();
}
