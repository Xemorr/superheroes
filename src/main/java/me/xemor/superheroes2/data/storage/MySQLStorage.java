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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MySQLStorage implements Storage {

    private final HeroHandler heroHandler;
    private final Superheroes2 superheroes2;
    private final ConfigHandler configHandler;
    private final MysqlDataSource dataSource;
    private final ReentrantLock lock = new ReentrantLock();

    public MySQLStorage() {
        this.superheroes2 = Superheroes2.getInstance();
        this.heroHandler = superheroes2.getHeroHandler();
        this.configHandler = superheroes2.getConfigHandler();
        String name = configHandler.getDatabaseName();
        String host = configHandler.getDatabaseHost();
        int port = configHandler.getDatabasePort();
        String user = configHandler.getDatabaseUsername();
        String password = configHandler.getDatabasePassword();
        dataSource = initMySQLDataSource(name, host, port, user, password);
        setupTable();
    }

    public void saveSuperheroPlayer(@NotNull SuperheroPlayer superheroPlayer) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "REPLACE INTO superhero_players(uuid, hero, hero_cmd_timestamp) VALUES(?, ?, ?);"
        )) {
            stmt.setString(1, superheroPlayer.getUUID().toString());
            stmt.setString(2, superheroPlayer.getSuperhero().getName());
            stmt.setLong(3, superheroPlayer.getHeroCommandTimestamp());
            lock.lock();
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SuperheroPlayer loadSuperheroPlayer(UUID uuid) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM superhero_players WHERE uuid = ?;"
        )) {
            stmt.setString(1, uuid.toString());
            lock.lock();
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
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public CompletableFuture<Void> importSuperheroPlayers(List<SuperheroPlayer> superheroPlayers) {
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(Superheroes2.getInstance(), () -> {
            for (SuperheroPlayer superheroPlayer : superheroPlayers) {
                saveSuperheroPlayer(superheroPlayer);
            }
        });
        return CompletableFuture.allOf(completableFuture);
    }

    @Override
    public List<SuperheroPlayer> exportSuperheroPlayers() {
        List<SuperheroPlayer> players = new ArrayList<>();
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM superhero_players;"
        )) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Superhero superhero = heroHandler.getSuperhero(resultSet.getString(2));
                if (superhero == null) {
                    superhero = heroHandler.getNoPower();
                }
                players.add(new SuperheroPlayer(UUID.fromString(resultSet.getString(1)), superhero, resultSet.getLong(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    private MysqlDataSource initMySQLDataSource(String dbName, String host, int port, String user, String password) {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setDatabaseName(dbName);
        dataSource.setServerName(host);
        dataSource.setPortNumber(port);
        dataSource.setUser(user);
        dataSource.setPassword(password);
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
