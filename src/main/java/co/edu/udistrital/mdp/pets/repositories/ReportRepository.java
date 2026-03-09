package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.ReportEntity;
import co.edu.udistrital.mdp.pets.entities.ReportStrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    /**
     * Finds reports generated on a specific date.
     */
    List<ReportEntity> findByGenerateDate(LocalDate generateDate);
	List<ReportEntity> findByReportStrategy(ReportStrategyEntity reportStrategy);}
