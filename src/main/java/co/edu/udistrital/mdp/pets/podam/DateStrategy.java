package co.edu.udistrital.mdp.pets.podam;

import java.lang.annotation.Annotation;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import uk.co.jemos.podam.common.AttributeStrategy;

public class DateStrategy implements AttributeStrategy<Date> {
    
    // CSPRNG para cumplir con el issue de seguridad de Sonar
    private final Random r = new SecureRandom();

    @Override
    public Date getValue(Class<?> attributeType, List<Annotation> annotations) {
        Calendar c = Calendar.getInstance();
        int maxYear = 2030; // Evita fechas demasiado lejanas para no romper la DB
        int minYear = 2020;
        
        c.set(Calendar.YEAR, r.nextInt(maxYear - minYear + 1) + minYear);
        c.set(Calendar.DAY_OF_YEAR,
                r.nextInt(c.getActualMaximum(Calendar.DAY_OF_YEAR) - c.getActualMinimum(Calendar.DAY_OF_YEAR) + 1)
                        + c.getActualMinimum(Calendar.DAY_OF_YEAR));
        
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        return c.getTime();
    }
}
