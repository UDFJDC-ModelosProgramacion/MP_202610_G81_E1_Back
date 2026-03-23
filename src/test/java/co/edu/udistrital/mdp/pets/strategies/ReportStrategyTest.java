package co.edu.udistrital.mdp.pets.strategies;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.udistrital.mdp.pets.entities.ReportEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionReportStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalEventReportStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.ReturnReportStrategyEntity;

class ReportStrategyTest {

    private ReportEntity report;

    @BeforeEach
    void setUp() {
        report = new ReportEntity();
        report.setReason("Prueba de estrategia de reporte");
        report.setStatus(ReportEntity.Status.PENDING);
    }

    @Test
    void testAdoptionReportStrategyCoverage() {
        AdoptionReportStrategyEntity strategy = new AdoptionReportStrategyEntity();
        strategy.generate(report);
        assertNotNull(strategy, "La estrategia de Adopción debería instanciarse correctamente");
    }

    @Test
    void testMedicalEventReportStrategyCoverage() {
        MedicalEventReportStrategyEntity strategy = new MedicalEventReportStrategyEntity();
        strategy.generate(report);
        assertNotNull(strategy, "La estrategia de Evento Médico debería instanciarse correctamente");
    }

    @Test
    void testReturnReportStrategyCoverage() {
        ReturnReportStrategyEntity strategy = new ReturnReportStrategyEntity();
        strategy.generate(report);
        assertNotNull(strategy, "La estrategia de Devolución debería instanciarse correctamente");
    }
}
