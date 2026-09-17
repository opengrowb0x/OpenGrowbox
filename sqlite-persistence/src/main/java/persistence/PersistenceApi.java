package persistence;

import persistence.model.DailyData;
import persistence.model.PortDataEntity;
import persistence.model.PortEntity;

import java.time.LocalDate;
import java.util.List;

public interface PersistenceApi extends GrowPeriodApi {

    int getGPIOPort(String portId) throws Exception;

    PortEntity getPort(String portId);

    List<PortEntity> getPorts();

    PortDataEntity getPortData(String portId);

    List<PortDataEntity> getLatestPortData(String portId, int limit);

    List<DailyData> getDailyPortData(LocalDate startDate, LocalDate endDate, String portId);

    void createPortData(PortDataEntity portDataEntity);

}