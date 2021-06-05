package me.xemor.superheroes2.data.storage;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.data.SuperheroPlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MySQLStorage implements Storage {

    private final HeroHandler heroHandler;
    private final Superheroes2 superheroes2;
    private final ConfigHandler configHandler;
    private final MysqlDataSource dataSource;

    public MySQLStorage(HeroHandler heroHandler) {
        this.heroHandler = heroHandler;
        this.superheroes2 = heroHandler.getPlugin();
        this.configHandler = superheroes2.getConfigHandler();
        String name = configHandler.getDatabaseName();
        String host = configHandler.getDatabaseHost();
        int port = configHandler.getDatabasePort();
        String user = configHandler.getDatabaseUsername();
        String password = configHandler.getDatabasePassword();
        Bukkit.getLogger().warning(name);
        Bukkit.getLogger().warning(host);
        Bukkit.getLogger().warning(String.valueOf(port));
        Bukkit.getLogger().warning(user);
        Bukkit.getLogger().warning(password);
        dataSource = initMySQLDataSource(name, host, port, user, password);
        setupTable();
    }

    @Override
    public void saveSuperheroPlayer(@NotNull SuperheroPlayer superheroPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(superheroes2, () -> {
            try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                    "REPLACE INTO superhero_players(uuid, hero, hero_cmd_timestamp) VALUES(?, ?, ?);"
            )) {
                stmt.setString(1, superheroPlayer.getUUID().toString());
                stmt.setString(2, superheroPlayer.getSuperhero().getName());
                stmt.setLong(3, superheroPlayer.getHeroCommandTimestamp());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public SuperheroPlayer loadSuperheroPlayer(UUID uuid) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM superhero_players WHERE uuid = ?;"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Superhero hero = heroHandler.getSuperhero(resultSet.getString("hero"));
                if (hero != null) {
                    return new SuperheroPlayer(
                            uuid,
                            hero,
                            resultSet.getLong("hero_cmd_timestamp")
                    );
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CompletableFuture<SuperheroPlayer> loadSuperheroPlayerAsync(@NotNull UUID uuid) {
        CompletableFuture<SuperheroPlayer> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(superheroes2, () -> future.complete(loadSuperheroPlayer(uuid)));
        return future;
    }

    private MysqlDataSource initMySQLDataSource(String dbName, String host, int port, String user, String password) {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setDatabaseName(dbName);
        dataSource.setServerName(host);
        dataSource.setPortNumber(port);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        Bukkit.getLogger().warning(dataSource.getDatabaseName());
        Bukkit.getLogger().warning(dataSource.getServerName());
        Bukkit.getLogger().warning(String.valueOf(dataSource.getPortNumber()));
        Bukkit.getLogger().warning(dataSource.getUser());
        Bukkit.getLogger().warning(dataSource.getPassword());
        testDataSource(dataSource);
        return dataSource;
    }

    private void testDataSource(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1000)) {
                throw new SQLException("Could not establish database connection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        String setup;
        try (InputStream in = MySQLStorage.class.getClassLoader().getResourceAsStream("dbsetup.sql")) {
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            superheroes2.getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
            e.printStackTrace();
            return;
        }
        String[] queries = setup.split(";");
        for (String query : queries) {
            if (query.isEmpty()) continue;
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        superheroes2.getLogger().info("Database setup complete.");
    }

    public Connection conn() {
        try {
            return dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

}
