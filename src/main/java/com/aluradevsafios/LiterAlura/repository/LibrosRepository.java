package com.aluradevsafios.LiterAlura.repository;

import com.aluradevsafios.LiterAlura.models.Libros;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibrosRepository extends JpaRepository<Libros, Long> {
}
