package persistence.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GrowPeriodEntity implements Serializable {
    public final Integer id;
    public final String growDescription;
    public final int sunriseHour;
    public final int photoPeriod;
    public int totalDays;
    public final int vegetationDays;
    public final int vegetationDaylightHours;
    public final int floweringDaylightHours;
    public final LocalDateTime startDate;

    @Deprecated //calculated field using the current date and start and total days
    public String growboxState;

    public final int wateringSeconds;
    public final float nutrientACDosingSeconds;
    public final float nutrientBDosingSeconds;
    public final int floodSeconds;

    public GrowPeriodEntity(Integer id, String growDescription, int sunriseHour, int photoPeriod, int totalDays, int vegetationDays,
                            int vegetationDaylightHours, int floweringDaylightHours,
                            LocalDateTime startDate, String growboxState, int wateringSeconds,
                            float nutrientACDosingSeconds, float nutrientBDosingSeconds, int floodSeconds) {
        this.id = id;
        this.growDescription = growDescription;
        this.sunriseHour = sunriseHour;
        this.photoPeriod = photoPeriod;
        this.totalDays = totalDays;
        this.vegetationDays = vegetationDays;
        this.vegetationDaylightHours = vegetationDaylightHours;
        this.floweringDaylightHours = floweringDaylightHours;
        this.startDate = startDate;
        this.growboxState = growboxState;
        this.wateringSeconds = wateringSeconds;
        this.nutrientACDosingSeconds = nutrientACDosingSeconds;
        this.floodSeconds = floodSeconds;
        this.nutrientBDosingSeconds = nutrientBDosingSeconds;
    }

    public GrowPeriodEntity(String growDescription, int sunriseHour, int photoPeriod, int totalDays, int vegetationDays, int vegetationDaylightHours, int floweringDaylightHours,
                            LocalDateTime startDate, String growboxState, int wateringSeconds, float nutrientACDosingSeconds, float nutrientBDosingSeconds, int floodSeconds) {
        this(null, growDescription, sunriseHour, photoPeriod, totalDays, vegetationDays, vegetationDaylightHours, floweringDaylightHours, startDate, growboxState, wateringSeconds, nutrientACDosingSeconds, nutrientBDosingSeconds, floodSeconds);
    }

    @Override
    public String toString() {
        return "GrowPeriodEntity{" +
                "ud='" + id + '\'' +
                "growDescription='" + growDescription + '\'' +
                ", sunriseHour=" + sunriseHour +
                ", photoPeriod=" + photoPeriod +
                ", totalDays=" + totalDays +
                ", vegetationDays=" + vegetationDays +
                ", vegetationDaylightHours=" + vegetationDaylightHours +
                ", floweringDaylightHours=" + floweringDaylightHours +
                ", growboxState=" + growboxState +
                ", wateringSeconds=" + wateringSeconds +
                ", nutrientACDosingSeconds=" + nutrientACDosingSeconds +
                ", nutrientBDosingSeconds=" + nutrientBDosingSeconds +
                ", floodSeconds=" + floodSeconds +
                ", startDate=" + startDate +
                '}';
    }
}
