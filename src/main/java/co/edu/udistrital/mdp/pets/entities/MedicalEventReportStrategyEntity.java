package co.edu.udistrital.mdp.pets.entities;

import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("MEDICAL")
public class MedicalEventReportStrategyEntity extends ReportStrategyEntity {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void generate(ReportEntity report) {
        if (report == null) {
            throw new IllegalArgumentException("Report no puede ser nulo.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Medical events report\n");
        sb.append("Generated: ").append(LocalDate.now().format(DATE_FMT)).append("\n\n");

        try {
            Object medicalHistory = safeInvoke(report, "getMedicalHistory");

            if (medicalHistory == null) {
                sb.append("No medical history associated with this report.\n");
            } else {
                Object eventsObj = safeInvoke(medicalHistory, "getMedicalEvents");

                if (eventsObj instanceof Collection) {
                    @SuppressWarnings("unchecked")
                    Collection<Object> events = (Collection<Object>) eventsObj;

                    sb.append("Total events: ").append(events.size()).append("\n");

                    if (!events.isEmpty()) {
                        List<Object> sorted = events.stream()
                                .sorted(Comparator.comparing(e -> {
                                    Object d = safeInvoke(e, "getEventDate");
                                    if (d instanceof java.time.LocalDate localDate) {
                                        return localDate;
                                    }
                                    return LocalDate.MIN;
                                }))
                                .collect(Collectors.toList());

                        Object first = sorted.get(0);
                        Object last = sorted.get(sorted.size() - 1);

                        Object firstDate = safeInvoke(first, "getEventDate");
                        Object lastDate = safeInvoke(last, "getEventDate");

                        if (firstDate instanceof java.time.LocalDate && lastDate instanceof java.time.LocalDate) {
                            sb.append("From: ").append(((java.time.LocalDate) firstDate).format(DATE_FMT))
                              .append(" To: ").append(((java.time.LocalDate) lastDate).format(DATE_FMT)).append("\n");
                        }

                        var typeCounts = events.stream()
                                .map(e -> {
                                    Object t = safeInvoke(e, "getEventType");
                                    return t == null ? "UNKNOWN" : t.toString();
                                })
                                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

                        sb.append("\nEvents by type:\n");
                        typeCounts.forEach((type, count) -> sb.append(" - ").append(type).append(": ").append(count).append("\n"));

                        sb.append("\nLast events (up to 5):\n");
                        sorted.stream()
                                .skip(Math.max(0, sorted.size() - 5))
                                .forEach(e -> {
                                    Object date = safeInvoke(e, "getEventDate");
                                    Object type = safeInvoke(e, "getEventType");
                                    Object desc = safeInvoke(e, "getDescription");
                                    String dateStr = date instanceof java.time.LocalDate ? ((java.time.LocalDate) date).format(DATE_FMT) : "n/a";
                                    sb.append(" * ").append(dateStr)
                                      .append(" | ").append(type == null ? "UNKNOWN" : type.toString())
                                      .append(" | ").append(desc == null ? "-" : desc.toString())
                                      .append("\n");
                                });
                    }
                } else {
                    sb.append("No medical events found or medicalEvents is not a collection.\n");
                }
            }

            boolean written = trySetReportContent(report, sb.toString());
            if (!written) {
                trySetReportContentAlternative(report, sb.toString());
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error generating medical event report: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private Object safeInvoke(Object target, String methodName) {
        if (target == null) return null;
        try {
            Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
        } catch (NoSuchMethodException nsme) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private boolean trySetReportContent(ReportEntity report, String content) {
        if (report == null) return false;
        try {
            Method setContent = report.getClass().getMethod("setContent", String.class);
            setContent.invoke(report, content);
            return true;
        } catch (NoSuchMethodException nsme) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    @SuppressWarnings("UseSpecificCatch")
    private boolean trySetReportContentAlternative(ReportEntity report, String content) {
        String[] alternatives = { "setReportText", "setBody", "setSummary", "setReportContent" };
        for (String name : alternatives) {
            try {
                Method m = report.getClass().getMethod(name, String.class);
                m.invoke(report, content);
                return true;
            } catch (NoSuchMethodException nsme) {
            } catch (Exception e) {
            }
        }
        return false;
    }
}