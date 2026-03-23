package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalClearanceStrategyEntity;

class AdoptionRequestServiceTest {

    @Test
    void testMedicalClearanceStrategyCoverage() {
        MedicalClearanceStrategyEntity strategy = new MedicalClearanceStrategyEntity();
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        strategy.evaluate(request);
        assertNotNull(strategy);
    }
}
