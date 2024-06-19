package me.xemor.superheroes.data.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.ConfigHandler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.data.SuperheroPlayer;
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
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MySQLStorage implements Storage {

    private final HeroHandler heroHandler;
    private final Superheroes superheroes;
    private HikariDataSource source;
    private final ReentrantLock lock = new ReentrantLock();

    public MySQLStorage() {
        this.superheroes = Superheroes.getInstance();
        this.heroHandler = superheroes.getHeroHandler();
        ConfigHandler configHandler = superheroes.getConfigHandler();
        String name = configHandler.getDatabaseName();
        String host = configHandler.getDatabaseHost();
        int port = configHandler.getDatabasePort();
        String user = configHandler.getDatabaseUsername();
        String password = configHandler.getDatabasePassword();
        initMySQLDataSource(name, host, port, user, password);
        setupTable();
    }

    public void saveSuperheroPlayer(@NotNull SuperheroPlayer superheroPlayer) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
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
    public SuperheroPlayer loadSuperheroPlayer(@NotNull UUID uuid) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
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
        Superheroes.getScheduling().asyncScheduler().run(() -> {
            for (SuperheroPlayer superheroPlayer : superheroPlayers) {
                saveSuperheroPlayer(superheroPlayer);
            }
        });
        return CompletableFuture.allOf(completableFuture);
    }

    @Override
    public List<SuperheroPlayer> exportSuperheroPlayers() {
        List<SuperheroPlayer> players = new ArrayList<>();
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
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

    private void initMySQLDataSource(String dbName, String host, int port, String user, String password) {
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource");
        props.setProperty("dataSource.serverName", host);
        props.setProperty("dataSource.portNumber", String.valueOf(port));
        props.setProperty("dataSource.user", user);
        props.setProperty("dataSource.password", password);
        props.setProperty("dataSource.databaseName", dbName);
        HikariConfig hikariConfig = new HikariConfig(props);
        hikariConfig.setMaximumPoolSize(2);
        source = new HikariDataSource(hikariConfig);
        testDataSource(source);
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
            superheroes.getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
            e.printStackTrace();
            return;
        }
        String[] queries = setup.split(";");
        for (String query : queries) {
            if (query.isEmpty()) continue;
            try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        superheroes.getLogger().info("Database setup complete.");
    }
}
