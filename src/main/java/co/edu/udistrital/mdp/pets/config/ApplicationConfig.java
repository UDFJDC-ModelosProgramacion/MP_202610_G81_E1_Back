package co.edu.udistrital.mdp.pets.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig {
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
			.setSkipNullEnabled(true)
			.setAmbiguityIgnored(true)
			.setFieldMatchingEnabled(false)
			.setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PUBLIC)
			.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
			
		return modelMapper;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@SuppressWarnings("null")
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						// Cambia "*" por los dominios específicos de tu desarrollo
						.allowedOrigins("http://localhost:3000", "http://localhost:4200", "http://localhost:8999", "http://localhost:8080", "http://localhost:5173","http://127.0.0.1")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(false); // Mantener en false si usas "*"
			}
		};
	}
}
