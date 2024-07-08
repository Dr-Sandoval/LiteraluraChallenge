package com.aluraone.literalura.repository;

import com.aluraone.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByIdiomasContains(String idiomas);
}
