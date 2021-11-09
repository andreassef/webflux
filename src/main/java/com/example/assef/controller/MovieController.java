package com.example.assef.controller;

import com.example.assef.model.Movie;
import com.example.assef.model.MovieEvent;
import com.example.assef.repository.NetflixRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/netflix")
public class MovieController {

    private NetflixRepository repository;

    public MovieController(NetflixRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Movie> getAllMovies() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Movie>> getMovie(@PathVariable String id) {
        return repository.findById(id)
                .map(movie -> ResponseEntity.ok(movie))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Movie> saveMovie(@RequestBody Movie movie) {
        return repository.save(movie);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Movie>> updateMovie(@PathVariable(value="id") String id, @RequestBody Movie movie) {
        return repository.findById(id)
                .flatMap(existingMovie -> {
                   existingMovie.setMovieName(movie.getMovieName());
                   existingMovie.setMovieType(movie.getMovieType());
                   existingMovie.setPrincipalActor(movie.getPrincipalActor());
                   existingMovie.setCreated_at(movie.getCreated_at());
                   return repository.save(existingMovie);
                })
                .map(updateMovie -> ResponseEntity.ok(updateMovie))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Object>> deleteMovie(@PathVariable(value="id") String id) {
        return repository.findById(id)
                .flatMap(existingMovie ->
                            repository.delete(existingMovie).then(Mono.just(ResponseEntity.ok().build()))
                        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteAllMovies() {return repository.deleteAll();}

    @GetMapping(value = "/netflix-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieEvent> getMovieEvents() {
        return Flux.interval(Duration.ofSeconds(5))
                .map(val -> new MovieEvent(val, "Evento da netflixx"));
    }
}
