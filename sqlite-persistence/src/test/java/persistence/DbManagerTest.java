package persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import persistence.model.DailyData;
import persistence.model.GrowPeriodEntity;
import persistence.model.PortDataEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DbManagerTest {
    public static final String TEST_URL = "../control/src/test/resources/growbox2/growbox.db";

    public static final String TEST_URL_HOME = "/tmp";


    private final DbManager db = DbManager.instantiateDbManager(TEST_URL_HOME);

    final GrowPeriodEntity growPeriodEntity = new GrowPeriodEntity(1, "test grow period 1", 2, 10,
            10, 5, 8,
            2, null, "STOPPED", 10, 2, 2, 30);

    @BeforeClass
    public static void initTestDb() {
        try {
            Files.copy(Paths.get(TEST_URL), Paths.get("/tmp/growbox.db"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void finishTest() {
        try {
            Files.delete(Paths.get("/tmp/growbox.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyGrowPeriodInsert() throws SQLException, ClassNotFoundException {
        List<GrowPeriodEntity> growPeriods = db.listGrowPeriods();
        int previousSize = growPeriods.size();
        final GrowPeriodEntity currentPeriod = growPeriods.get(0);
        System.out.println("current " + currentPeriod.toString());

        db.insertGrowPeriod(growPeriodEntity);
        growPeriods = db.listGrowPeriods();
        final GrowPeriodEntity newPeriod = growPeriods.get(0);
        int newSize = growPeriods.size();

        System.out.println("newPeriod " + newPeriod.toString());
        int expectedId = previousSize + 1;
        assertEquals(expectedId, newSize);
        System.out.println("current " + newPeriod.toString());

    }

    @Test
    public void verifyGrowPeriodUpdate() throws SQLException, ClassNotFoundException {
        final GrowPeriodEntity currentPeriod = db.listGrowPeriods().get(0);
        System.out.println("current " + currentPeriod.toString());

        final GrowPeriodEntity growPeriodEntity2 = new GrowPeriodEntity(currentPeriod.id, "test grow period update", 2, 10,
                33, 5, 8,
                5, LocalDateTime.now(), "STOPPED", 10, 2, 2,0);

        db.updateGrowPeriod(growPeriodEntity2);

        final GrowPeriodEntity updatedPeriod = db.listGrowPeriods().get(0);
        System.out.println("updated " + updatedPeriod.toString());
        assertEquals(currentPeriod.id, updatedPeriod.id);
        assertEquals(33, updatedPeriod.totalDays);

    }

    @Test
    public void verifyDateSaving() {
        LocalDate startDate = LocalDate.now();
        long startDateInMillis = startDate.toEpochDay();
        System.out.println(startDateInMillis);
    }


    @Test
    public void createWaterReadingTest() {
        db.createPortData(new PortDataEntity("water", 10.0f));
        PortDataEntity data = db.getPortData("water");
        System.out.println(data.toString());
        assertEquals(10.0, data.value, 0.1);
    }

    @Test
    public void queryPortData() {
        createWaterReadingTest();
        PortDataEntity waterData = db.queryPortData("water");
        assertNotNull(waterData);
        System.out.println(waterData.toString());
        assertEquals(10.0, waterData.value, 0.1);
    }

    @Test
    public void queryDailyPortData() {
        PortDataEntity waterData = db.queryPortData("temperature");
        assertNotNull(waterData);
        System.out.println(waterData.toString());
        final GrowPeriodEntity firstPeriod = db.listGrowPeriods().get(0);

        List<DailyData> dailyData = db.getDailyPortData(firstPeriod.startDate.toLocalDate(), null, waterData.inputOutputId);

        for (DailyData dailyDatum : dailyData) {
            System.out.printf("daily " + dailyDatum.toString());
            assertTrue(dailyDatum.maximum() >= dailyDatum.minimum());
        }
    }


    @Test
    public void verifyPortDataInsertForDailyPicture() {
        PortDataEntity dailyPicData = db.queryPortData("camera");
        assertNotNull(dailyPicData);
        System.out.println(dailyPicData);
        db.createPortData(new PortDataEntity("camera", 1.0f));

        dailyPicData = db.queryPortData("camera");
        System.out.println(dailyPicData);
    }

    @Test
    public void insertTests() {
        boolean insert = true;
        int ITEMS_TO_INSERT = 2;
        if (insert) {
            for (int i = 0; i < ITEMS_TO_INSERT; i++) {
                db.createPortData(new PortDataEntity("humidity", 60 + i));
            }
            for (int i = 0; i < ITEMS_TO_INSERT; i++) {
                db.createPortData(new PortDataEntity("humiditySoil", 70 + i));
            }
            for (int i = 0; i < ITEMS_TO_INSERT; i++) {
                db.createPortData(new PortDataEntity("lights", 10 + i));
            }
            for (int i = 0; i < ITEMS_TO_INSERT; i++) {
                db.createPortData(new PortDataEntity("temperature", 12 + i));
            }
        }

        List<PortDataEntity> tempData = db.listPortData("temperature", ITEMS_TO_INSERT);
    }
}

