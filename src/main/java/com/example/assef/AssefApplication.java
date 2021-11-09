package com.example.assef;

import com.example.assef.model.Movie;
import com.example.assef.repository.NetflixRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@SpringBootApplication
public class AssefApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssefApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@Bean
	CommandLineRunner init(ReactiveMongoOperations operations, NetflixRepository repository) {
		return args -> {
			Flux<Movie>  movieFlux = Flux.just(
					new Movie(null, "Matrix", "Acao", "Keanu Reaves", 1995),
					new Movie(null, "Diario de uma paixao", "Romance", "Ryan Gosling", 2003),
					new Movie(null, "1987", "Drama", "George Mackay", 2020)
			).flatMap(repository::save);

			movieFlux.thenMany(repository.findAll()).subscribe(System.out::println);
		};
	}
}
