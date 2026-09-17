package persistence.model;

import java.time.LocalDate;

public record DailyData(LocalDate date, float minimum, float maximum, float average) {
}
