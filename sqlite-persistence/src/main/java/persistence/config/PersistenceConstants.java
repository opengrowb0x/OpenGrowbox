package persistence.config;


public class PersistenceConstants {
    public static String getDbUrl(String homeDirectory) {
        return "jdbc:sqlite:" + homeDirectory + "/growbox.db";
    }
}

