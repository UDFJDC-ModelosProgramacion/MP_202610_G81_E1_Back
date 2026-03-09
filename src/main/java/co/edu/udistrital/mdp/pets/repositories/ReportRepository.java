package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    /**
     * Finds reports generated on a specific date.
     */
    List<ReportEntity> findByGeneratedDate(Date generatedDate);

    /**
     * Finds reports by their strategy type.
     */
    List<ReportEntity> findByReportStrategy(String reportStrategy);
}
