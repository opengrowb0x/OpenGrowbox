package persistence.sqlite;

import persistence.model.DailyData;
import persistence.DbManager;
import persistence.PersistenceApi;
import persistence.model.GrowPeriodEntity;
import persistence.model.PortDataEntity;
import persistence.model.PortEntity;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class DefaultPersistenceApi implements PersistenceApi {

    private static final Logger log = Logger.getLogger(DefaultPersistenceApi.class.getSimpleName());

    private static DbManager db;

    public DefaultPersistenceApi(DbManager dbManager) {
        db = dbManager;
    }

    @Override
    public void createPortData(PortDataEntity portDataEntity) {
        try {
            log.info("saving data port " + portDataEntity.inputOutputId + " = " + portDataEntity.value);
            db.createPortData(portDataEntity);
        } catch (Exception e) {
            log.severe(e.getMessage() + "  while doing createPortData db operation");
        }
    }

    @Override
    public int getGPIOPort(String inputOutputId) {
        return db.getPort(inputOutputId).gpio();
    }

    @Override
    public PortEntity getPort(String water) {
        return db.getPort(water);
    }

    @Override
    public List<PortEntity> getPorts() {
        return db.listPorts();
    }

    @Override
    public PortDataEntity getPortData(String inputOutputId) {
        return db.getPortData(inputOutputId);
    }

    @Override
    public List<PortDataEntity> getLatestPortData(String portId, int limit) {
        return db.listPortData(portId, limit);
    }

    @Override
    public List<DailyData> getDailyPortData(LocalDate startDate, LocalDate endDate, String portId) {
        return db.getDailyPortData(startDate, endDate, portId);
    }

    @Override
    public void updateGrowPeriod(GrowPeriodEntity growPeriodEntity) {
        try {
            db.updateGrowPeriod(growPeriodEntity);
        } catch (SQLException | ClassNotFoundException e) {
            log.severe(e.getMessage() + "  while doing updateGrowPeriod db operation");
        }
    }

    @Override
    public GrowPeriodEntity getGrowPeriod(int id) {
        return db.getGrowPeriod(id);
    }

    @Override
    public List<GrowPeriodEntity> getGrowPeriods() {
        return db.listGrowPeriods();
    }

    @Override
    public void createGrowPeriod(GrowPeriodEntity growPeriodEntity) {
        try {
            db.insertGrowPeriod(growPeriodEntity);
        } catch (SQLException | ClassNotFoundException e) {
            log.severe(e.getMessage() + "  while doing createGrowPeriods db operation");
        }
    }

}
