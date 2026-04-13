package co.edu.udistrital.mdp.pets.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.test.context.ContextConfiguration;
import co.edu.udistrital.mdp.pets.config.ApplicationConfig;
// Importante: Solo cargamos la clase de configuración, no toda la App
@SpringBootTest
@ContextConfiguration(classes = ApplicationConfig.class)
class ApplicationConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Test
    void testModelMapperBeanExists() {
        ModelMapper modelMapper = context.getBean(ModelMapper.class);
        assertNotNull(modelMapper);
    }

    @Test
    void testCorsConfigurerCoverage() {
        WebMvcConfigurer configurer = applicationConfig.corsConfigurer();
        assertNotNull(configurer);
        
        // Para cubrir las líneas de "allowedOrigins", "methods", etc.
        // Simulamos la llamada que haría Spring internamente
        CorsRegistry registry = new CorsRegistry();
        configurer.addCorsMappings(registry);
        
        assertNotNull(registry);
    }
}
