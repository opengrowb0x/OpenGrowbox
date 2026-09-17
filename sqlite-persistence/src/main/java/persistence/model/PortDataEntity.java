package persistence.model;

import java.time.LocalDateTime;

public class PortDataEntity {
    public Integer readingId;
    public final String inputOutputId;
    public final LocalDateTime date;
    public final float value;

    public PortDataEntity(String inputOutputId, float value) {
        this.inputOutputId = inputOutputId;
        this.date = LocalDateTime.now();
        this.value = value;
    }

    public PortDataEntity(String inputOutputId, LocalDateTime date, float value) {
        this.inputOutputId = inputOutputId;
        this.date = date;
        this.value = value;
    }

    public PortDataEntity(Integer readingId, String inputOutputId, LocalDateTime date, float value) {
        this.readingId = readingId;
        this.inputOutputId = inputOutputId;
        this.date = date;
        this.value = value;
    }


    @Override
    public String toString() {
        return "PortDataEntity{" +
                "readingId=" + readingId +
                ", inputOutputId='" + inputOutputId + '\'' +
                ", date='" + date + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
