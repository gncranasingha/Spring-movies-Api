package com.movieflex.MovieApi.repositories;

import com.movieflex.MovieApi.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
