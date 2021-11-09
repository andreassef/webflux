package com.example.assef.repository;

import com.example.assef.model.Movie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NetflixRepository extends ReactiveMongoRepository<Movie, String> {
}
