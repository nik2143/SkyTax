package it.nik2143.skytax;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.leonhard.storage.Json;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@SuppressWarnings("UnstableApiUsage")
public class DataManager {

    private Json data;
    private DataStorageType dataStorageType;
    private HikariDataSource dataSource;
    @Setter private boolean shouldsave = true;

    public DataManager() {
        initialize();
    }

    private <T> CompletableFuture<T> future(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e){
                throw new CompletionException(e);
            }
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(SkyTax.getSkyTax(), r));
    }

    public void initialize() {
        this.dataStorageType = DataStorageType.fromName(SkyTax.getSkyTax().getConfiguration().get("database.type", "SQLite"));
        if (dataStorageType == DataStorageType.JSON) {
            return;
        } else if (dataStorageType == DataStorageType.SQ_LITE) {
            File file = new File(SkyTax.getSkyTax().getDataFolder(), "database.db");
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + file.getPath());
            hikariConfig.setConnectionTestQuery("SELECT 1");
            dataSource = new HikariDataSource(hikariConfig);
        } else if (dataStorageType == DataStorageType.MY_SQL) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mysql://" +
                    SkyTax.getSkyTax().getConfiguration().getString("database.address") + ":"
                    + SkyTax.getSkyTax().getConfiguration().getInt("database.port")
                    + "/" + SkyTax.getSkyTax().getConfiguration().getString("database.db-name")
                    + "?allowPublicKeyRetrieval=" + SkyTax.getSkyTax().getConfiguration().getOrDefault("database.allowPublicKeyRetrieval",true)
                    +"&useSSL=" + SkyTax.getSkyTax().getConfiguration().getOrDefault("database.useSSL",true));
            hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
            hikariConfig.setPassword(SkyTax.getSkyTax().getConfiguration().getString("database.password"));
            hikariConfig.setUsername(SkyTax.getSkyTax().getConfiguration().getString("database.username"));
            hikariConfig.setConnectionTestQuery("SELECT 1");
            dataSource = new HikariDataSource(hikariConfig);
        }
        createDatabase();
    }

    private void createDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT," +
                "uuid CHAR(36) NOT NULL," +
                "name CHAR(36) NOT NULL," +
                "lastPayement BIGINT NOT NULL," +
                "taxnotpayed INT NOT NULL," +
                "taxpayedoffline BIGINT NOT NULL," +
                "lockdown BOOLEAN NOT NULL," +
                "islandRemoved BOOLEAN NOT NULL," +
                "PRIMARY KEY (id), UNIQUE (uuid));";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException ignored) {
            }
            if (statement != null) try {
                statement.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public CompletableFuture<List<TaxUser>> loadData() {
        return future(() -> {
            List<TaxUser> users = new ArrayList<>();
            if (dataStorageType.equals(DataStorageType.JSON)){
                this.data = new Json("data", SkyTax.getSkyTax().getDataFolder().getAbsolutePath());
                Reader reader;
                try {
                    reader = new FileReader(this.data.getFile());
                    users = new ArrayList<>(((HashMap<String, TaxUser>)(new Gson()).fromJson(reader, (new TypeToken<HashMap<String, TaxUser>>() {}).getType())).values());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Connection connection = null;
                Statement s = null;
                ResultSet rs = null;
                try {
                    connection = dataSource.getConnection();
                    s = connection.createStatement();
                    rs = s.executeQuery("SELECT * from users");
                    while (rs.next()) {
                        users.add(new TaxUser(rs.getString("uuid"),
                                rs.getString("name"),
                                rs.getLong("lastPayement"),
                                rs.getInt("taxnotpayed"),
                                rs.getLong("taxpayedoffline"),
                                rs.getBoolean("lockdown"),
                                rs.getBoolean("islandRemoved"), false));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    if (connection != null) try {
                        connection.close();
                    } catch (SQLException ignored) {
                    }
                    if (s != null) try {
                        s.close();
                    } catch (SQLException ignored) {
                    }
                    if (rs != null) try {
                        rs.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
            return users;
        });
    }

    public void saveData() {
        if (dataStorageType == DataStorageType.JSON) {
            if (shouldsave) {
                File dataFile = new File(SkyTax.getSkyTax().getDataFolder() + "/data.json");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (FileWriter fw = new FileWriter(dataFile)){
                    fw.write(gson.toJson(SkyTax.getSkyTax().getUsers()));
                    fw.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            Connection connection = null;
            PreparedStatement ps = null;
            try {
                connection = dataSource.getConnection();
                if (dataStorageType == DataStorageType.SQ_LITE) {
                    ps = connection.prepareStatement("INSERT OR REPLACE INTO users(uuid, name, lastPayement, taxnotpayed, taxpayedoffline, lockdown, islandRemoved ) VALUES (?, ?, ?, ?, ?, ?, ?);");
                } else {
                    ps = connection.prepareStatement("INSERT INTO users(uuid, name, lastPayement, taxnotpayed, taxpayedoffline, lockdown, islandRemoved ) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY " +
                            "UPDATE name=VALUES(name) lastPayement=VALUES(lastPayement), taxnotpayed=VALUES(taxnotpayed), lockdown=VALUES(lockdown), islandRemoved=VALUES(islandRemoved)");
                }
                for (TaxUser user : SkyTax.getSkyTax().getUsers().values()) {
                    ps.setString(1, user.uuid);
                    ps.setString(2, user.name);
                    ps.setLong(3, user.lastPayement);
                    ps.setInt(4, user.taxnotpayed);
                    ps.setLong(5, user.taxpayedoffline);
                    ps.setBoolean(6, user.lockdown);
                    ps.setBoolean(7, user.islandRemoved);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (connection != null) try {
                    connection.close();
                } catch (SQLException ignored) {
                }
                if (ps != null) try {
                    ps.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public void close() {
        if (dataStorageType != DataStorageType.JSON && dataSource != null && !dataSource.isClosed()) { dataSource.close(); }
    }

    public enum DataStorageType {
        JSON, MY_SQL, SQ_LITE;

        private static DataStorageType fromName(String name) {
            if (name.equalsIgnoreCase("json")) {
                return DataStorageType.JSON;
            }
            if (name.equalsIgnoreCase("MySql")) {
                return DataStorageType.MY_SQL;
            }
            if (name.equalsIgnoreCase("SQLite")) {
                return DataStorageType.SQ_LITE;
            }
            return null;
        }

    }

}
