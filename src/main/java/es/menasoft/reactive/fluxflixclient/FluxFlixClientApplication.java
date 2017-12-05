package es.menasoft.reactive.fluxflixclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@SpringBootApplication
public class FluxFlixClientApplication {

	@Bean
	WebClient client() {
		return WebClient.builder()
				.filter(ExchangeFilterFunctions.basicAuthentication("jjmena","developer"))
				.baseUrl("http://localhost:8080/movies")
				.build();
	}

	@Bean
	CommandLineRunner demo(WebClient client) {
		return args -> {
			client.get().retrieve().bodyToFlux(Movie.class)
					.filter(movie -> movie.getTitle().equalsIgnoreCase("Silence of the lambdas"))
					.flatMap(movie -> client.get()
						.uri("/{id}/events", movie.getId())
					.retrieve().bodyToFlux(MovieEvent.class))
					.subscribe(System.out::println);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(FluxFlixClientApplication.class, args);
	}
}

@Data
@AllArgsConstructor
class MovieEvent {
	private String movieId;
	private Date dateViewed;
}


@Data
@AllArgsConstructor
class Movie {
	private String id;

	private String title;
}
