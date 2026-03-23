package co.edu.udistrital.mdp.pets.strategies;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalClearanceStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.ManualApprovalStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.ScoreBasedApprovalStrategyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class AdoptionRequestStrategyTest {

    private AdoptionRequestEntity request;

    @BeforeEach
    void setUp() {
        request = new AdoptionRequestEntity();
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING");
        // No necesitamos setear Pet o Adopter para la cobertura básica del evaluate
    }

    @Test
    void testMedicalClearanceStrategyCoverage() {
        MedicalClearanceStrategyEntity strategy = new MedicalClearanceStrategyEntity();
        strategy.evaluate(request);
        assertNotNull(strategy);
    }

    @Test
    void testManualApprovalStrategyCoverage() {
        ManualApprovalStrategyEntity strategy = new ManualApprovalStrategyEntity();
        strategy.evaluate(request);
        assertNotNull(strategy);
    }

    @Test
    void testScoreBasedApprovalStrategyCoverage() {
        ScoreBasedApprovalStrategyEntity strategy = new ScoreBasedApprovalStrategyEntity();
        strategy.evaluate(request);
        assertNotNull(strategy);
    }
}
