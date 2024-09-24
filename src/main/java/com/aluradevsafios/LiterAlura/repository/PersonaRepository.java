package com.aluradevsafios.LiterAlura.repository;

import com.aluradevsafios.LiterAlura.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

   Optional<Persona> findByNombre(String nombre);
}
