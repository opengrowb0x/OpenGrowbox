package persistence;

import persistence.config.PersistenceConstants;
import persistence.model.DailyData;
import persistence.model.GrowPeriodEntity;
import persistence.model.PortDataEntity;
import persistence.model.PortEntity;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DbManager {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String NUTRIENT_SECONDS = "nutrientACSeconds";
    public static final String NUTRIENT_B_SECONDS = "nutrientBSeconds";
    public static final String FLOOD_SECONDS = "floodSeconds";

    private static final Logger log = Logger.getLogger(DbManager.class.getName());
    private final String dbUrl;
    private Connection mConnection;

    private DbManager(String dbUrl) {
        super();
        this.dbUrl = dbUrl;
    }

    public static DbManager instantiateDbManager(String homeDirectory) {
        String dbUrl = PersistenceConstants.getDbUrl(homeDirectory);
        log.info("init db manager with file " + dbUrl);
        return new DbManager(dbUrl);
    }

    private Connection getConnection(String dbUrl) throws ClassNotFoundException, SQLException {
        log.finer("getConnection to database " + dbUrl);
        if (mConnection == null) {
            Class.forName("org.sqlite.JDBC");
            mConnection = DriverManager.getConnection(dbUrl);
            log.fine("Opened database successfully");
        }
        return mConnection;
    }

    public PortDataEntity getPortData(String inputOutputId) {
        return queryPortData(inputOutputId);
    }

    public List<PortEntity> listPorts() {
        List<PortEntity> found = new ArrayList<>();
        Connection c;
        PreparedStatement stmt;
        try {
            c = getConnection(dbUrl);
            String sql = "SELECT * FROM ports";
            stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                final PortEntity s = constructPortFromParts(rs);
                found.add(s);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            log.warning("Error listing ports:" + e.getClass().getName() + ": " + e.getMessage());
        }

        return found;
    }

    public List<PortDataEntity> listPortData(String inputOutputId, int limit) {
        List<PortDataEntity> found = new ArrayList<>();
        Connection c;
        PreparedStatement stmt;
        try {
            c = getConnection(dbUrl);
            String sql = "SELECT * FROM port_data WHERE portId = ? ORDER BY readingId DESC LIMIT ?";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, inputOutputId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("readingId");
                String date = rs.getString("date");
                Float value = rs.getFloat("value");
                final LocalDateTime calendar = getCalendarFromTimeString(date);
                PortDataEntity sd = new PortDataEntity(id, inputOutputId, calendar, value);
                found.add(sd);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            log.warning("Error listing port data:" + e.getClass().getName() + ": " + e.getMessage());
        } finally {
        }
        return found;
    }

    private LocalDateTime getCalendarFromTimeString(String date) {
        if (date == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, dateTimeFormatter);
        } catch (Exception e) {
            log.warning("Error in getCalendarFromTimeString:" + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    private LocalDate getLocalDateFromString(String date) {
        if (date == null) {
            return null;
        }
        try {
            return LocalDate.parse(date, dateFormatter);
        } catch (Exception e) {
            log.warning("Error:" + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    public PortEntity getPort(String inputOutputId) {
        PortEntity found = null;
        Connection c;
        PreparedStatement stmt;
        try {
            c = getConnection(dbUrl);
            String sql = "SELECT * FROM ports WHERE portId = ?";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, inputOutputId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                found = constructPortFromParts(rs);
                break;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            log.warning("Error:" + e.getClass().getName() + ": " + e.getMessage());
        }

        log.finest("Query port successful. Found: " + (found != null));
        return found;
    }

    private PortEntity constructPortFromParts(ResultSet rs) throws SQLException {
        final String id = rs.getString("portId");
        final String name = rs.getString("name");
        final Integer gpio = rs.getInt("gpio");
        final boolean enabled = rs.getInt("enabled") == 1;
        final PortEntity.PortType portType = PortEntity.PortType.valueOf(rs.getString("portType"));
        return new PortEntity(id, name, portType, gpio, enabled);
    }

    protected PortDataEntity queryPortData(String inputOutputId) {
        PortDataEntity found = null;
        Connection c;
        PreparedStatement stmt;
        try {
            c = getConnection(dbUrl);
            String sql = "SELECT * FROM port_data WHERE portId = ? ORDER BY readingId DESC LIMIT 1";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, inputOutputId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("portId");
                Integer readingId = rs.getInt("readingId");
                String date = rs.getString("date");
                Float value = rs.getFloat("value");
                found = new PortDataEntity(readingId, id, getCalendarFromTimeString(date), value);
                break;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            log.warning("Error:" + e.getClass().getName() + ": " + e.getMessage());
        }
        return found;
    }

    public GrowPeriodEntity getGrowPeriod(int id) {
        GrowPeriodEntity found = null;
        Connection c;
        PreparedStatement stmt;
        try {
            c = getConnection(dbUrl);
            String sql = "SELECT * FROM grow_period WHERE id = ? ORDER BY id DESC";
            stmt = c.prepareStatement(sql);
            stmt.setInt(0, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                found = getGrowPeriodEntityFromResultSet(rs);
                break;
            }
            log.finest("GrowPeriod found: " + (found != null ? found.toString() : "not found"));
            rs.close();
            stmt.close();
        } catch (Exception e) {
            log.warning("Error:" + e.getClass().getName() + ": " + e.getMessage());
        }

        return found;
    }


    public List<GrowPeriodEntity> listGrowPeriods() {
        List<GrowPeriodEntity> found = new ArrayList<>();
        Connection c;
        PreparedStatement stmt;
        try {
            c = getConnection(dbUrl);
            String sql = "SELECT * FROM grow_period ORDER BY id DESC";
            stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                final GrowPeriodEntity gpe = getGrowPeriodEntityFromResultSet(rs);
                found.add(gpe);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            log.warning("Query config successful. Found: " + found);

        }
        return found;
    }


    public void createPortData(PortDataEntity reading) {
        try {
            insertPortData(reading);
        } catch (SQLException | ClassNotFoundException e) {
            log.warning("error creating port data" + e.getMessage());
        }
    }


    public void updateGrowPeriod(GrowPeriodEntity growPeriodEntity) throws SQLException, ClassNotFoundException {
        Connection c = getConnection(dbUrl);
        PreparedStatement stmt;
        try {
            String sql = "UPDATE grow_period SET growPeriod = ?, sunriseHour = ?, photoPeriod = ?, totalDays = ?, vegetationDays = ?, " +
                    "vegetationDaylightHours = ?, floweringDaylightHours = ?, startDate = ?, wateringSeconds = ?, growboxState = ? , " + NUTRIENT_SECONDS + " = ? ,nutrientBSeconds = ?, floodSeconds = ? " +
                    " WHERE id = ?;";

            stmt = c.prepareStatement(sql);
            addGrowPeriodAttributesToStatement(growPeriodEntity, stmt);

            //WHERE
            stmt.setInt(14, growPeriodEntity.id);

            stmt.executeUpdate();
            stmt.close();
            log.info("updated growPeriod entity id: " + growPeriodEntity.id + " successfully");
        } catch (Exception e) {
            log.severe("SQL error while updating grow period. " + e.getMessage() + " Rolling back");
            c.rollback();
        }
    }

    public void insertGrowPeriod(GrowPeriodEntity b) throws SQLException, ClassNotFoundException {
        Connection c = getConnection(dbUrl);
        c.setAutoCommit(true);
        PreparedStatement stmt;
        try {
            String sql = "insert into grow_period (growPeriod, sunriseHour, photoPeriod, totalDays, vegetationDays, " +
                    "vegetationDaylightHours, floweringDaylightHours, startDate, wateringSeconds, growboxState, " + NUTRIENT_SECONDS + ", " + NUTRIENT_B_SECONDS + ", " + FLOOD_SECONDS + ") " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            stmt = c.prepareStatement(sql);

            addGrowPeriodAttributesToStatement(b, stmt);

            stmt.executeUpdate();
            stmt.close();

        } catch (Exception e) {
            log.severe(e.getClass().getName() + ": " + e.getMessage());
            log.severe("Rolling back");
            c.rollback();
        } finally {
        }
    }

    private void addGrowPeriodAttributesToStatement(GrowPeriodEntity growPeriodEntity, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, growPeriodEntity.growDescription);
        stmt.setInt(2, growPeriodEntity.sunriseHour);
        stmt.setInt(3, growPeriodEntity.photoPeriod);
        stmt.setInt(4, growPeriodEntity.totalDays);
        stmt.setInt(5, growPeriodEntity.vegetationDays);
        stmt.setInt(6, growPeriodEntity.vegetationDaylightHours);
        stmt.setInt(7, growPeriodEntity.floweringDaylightHours);
        stmt.setString(8, growPeriodEntity.startDate != null ? growPeriodEntity.startDate.format(dateTimeFormatter) : null);
        stmt.setInt(9, growPeriodEntity.wateringSeconds);
        stmt.setString(10, growPeriodEntity.growboxState);
        stmt.setFloat(11, growPeriodEntity.nutrientACDosingSeconds);
        stmt.setFloat(12, growPeriodEntity.nutrientBDosingSeconds);
        stmt.setInt(13, growPeriodEntity.floodSeconds);
    }

    private void insertPortData(PortDataEntity b) throws SQLException, ClassNotFoundException {
        Connection c = getConnection(dbUrl);
        c.setAutoCommit(true);
        PreparedStatement stmt;
        try {
            String sql = "insert into port_data (portId, date, value) " +
                    "values (?, ?, ?);";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, b.inputOutputId);
            stmt.setString(2, b.date.format(dateTimeFormatter));
            stmt.setFloat(3, b.value);

            stmt.executeUpdate();
            stmt.close();

            log.fine("saved  " + b.inputOutputId + " data successfully. value " + b.value);

        } catch (Exception e) {
            log.severe("error writing data. rolling back");
            c.rollback();
        }
    }

    public void close() {
        try {
            if (getConnection(dbUrl) != null)
                getConnection(dbUrl).close();
        } catch (ClassNotFoundException | SQLException e) {
            log.info("exception closing db connection " + e.getMessage());
        }
    }

    private GrowPeriodEntity getGrowPeriodEntityFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String growPeriod = rs.getString("growPeriod");
        int sunriseHour = rs.getInt("sunriseHour");

        int photoPeriod = rs.getInt("photoPeriod");
        int totalDays = rs.getInt("totalDays");
        int vegetationDays = rs.getInt("vegetationDays");
        int vegetationDaylightHours = rs.getInt("vegetationDaylightHours");
        int floweringDaylightHours = rs.getInt("floweringDaylightHours");
        String startDate = rs.getString("startDate");
        String growboxState = rs.getString("growboxState");
        int wateringSeconds = rs.getInt("wateringSeconds");
        float nutrientACDosingSeconds = rs.getFloat(NUTRIENT_SECONDS);
        float nutrientBDosingSeconds = rs.getFloat(NUTRIENT_B_SECONDS);
        int floodSeconds = rs.getInt(FLOOD_SECONDS);

        return new GrowPeriodEntity(id, growPeriod, sunriseHour, photoPeriod, totalDays, vegetationDays,
                vegetationDaylightHours, floweringDaylightHours, getCalendarFromTimeString(startDate), growboxState, wateringSeconds, nutrientACDosingSeconds, nutrientBDosingSeconds, floodSeconds);
    }

    public List<DailyData> getDailyPortData(LocalDate startDate, LocalDate endDate, String portId) {
        List<DailyData> found = new ArrayList<>();
        Connection c;
        PreparedStatement stmt;
        try {
            c = getConnection(dbUrl);
            String sql = "select distinct(substr(date,0,11)) as daily, min(value), max(value), avg(value) from port_data WHERE portId = ? AND date > ? GROUP BY daily;";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, portId);
            stmt.setString(2, dateFormatter.format(startDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString(1);
                float min = rs.getFloat(2);
                float max = rs.getFloat(3);
                float avg = rs.getFloat(4);
                final DailyData gpe = new DailyData(getLocalDateFromString(date), min, max, avg);
                found.add(gpe);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            log.warning(e.getClass().getName() + ": " + e.getMessage());
        }
        return found;
    }
}

