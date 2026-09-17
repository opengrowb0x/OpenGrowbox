package persistence;

import persistence.model.GrowPeriodEntity;

import java.util.List;

public interface GrowPeriodApi {

    void createGrowPeriod(GrowPeriodEntity growPeriodEntity);

    List<GrowPeriodEntity> getGrowPeriods();
    void updateGrowPeriod(GrowPeriodEntity growPeriodEntity);

    GrowPeriodEntity getGrowPeriod(int id);
}
