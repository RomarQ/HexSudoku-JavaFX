package Helpers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sqlite.SQLiteException;

import java.io.File;
import java.sql.*;

import static org.sqlite.SQLiteErrorCode.SQLITE_AUTH;

public class DatabaseConnector {

    final String connectionUrl = "jdbc:sqlite:src/hexSudoku.db";

    private boolean dbExists() {
        return new File ("src/hexSudoku.db").exists();
    }

    public void createTables() {

        if(!dbExists()) {

            try {
                Class.forName("org.sqlite.JDBC");
                Connection c = DriverManager.getConnection(connectionUrl);
                Statement stmt = c.createStatement();
                String userTable =
                        "CREATE TABLE User (" +
                        "Id             INTEGER    PRIMARY KEY    AUTOINCREMENT   NOT NULL," +
                        "Username           TEXT       UNIQUE         NOT NULL," +
                        "Password       TEXT       NOT NULL)";
                String scoreTable =
                        "CREATE TABLE Score (" +
                        "Id             INTEGER    PRIMARY KEY    AUTOINCREMENT   NOT NULL," +
                        "UserId         INTEGER    NOT NULL," +
                        "Difficulty     INTEGER    NOT NULL," +
                        "TimeLapsed     INTEGER    NOT NULL," +
                        "FOREIGN KEY(UserId) REFERENCES User(Id))";
                stmt.executeUpdate(userTable);
                stmt.executeUpdate(scoreTable);
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Tables created successfully");
        }
    }

    public User getUser(int UserId) {
        User user = null;

        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(connectionUrl);

            c.setAutoCommit(false);

            PreparedStatement ps = c.prepareStatement("SELECT Id, Username From User WHERE Id = ?");

            ps.setInt(1, UserId);
            ResultSet result = ps.executeQuery();

            if (result.isClosed())
                return null;

            user = new User(result.getInt(1), result.getString(2));

            ps.close();
            c.commit();
            c.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return user;
    }

    public ObservableList<Score> getScores() {

        ObservableList<Score> scores = FXCollections.observableArrayList();;

        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(connectionUrl);

            c.setAutoCommit(false);

            PreparedStatement ps = c.prepareStatement("SELECT UserId, TimeLapsed, Difficulty From Score ORDER BY Difficulty DESC, TimeLapsed ASC");
            ResultSet result = ps.executeQuery();

            if (result.isClosed()) {
                ps.close();
                c.close();
                return null;
            }
            while(result.next()) {
                int UserId = result.getInt(1);
                int time = result.getInt(2);
                int difficulty = result.getInt(3);

                User u = getUser(UserId);
                if ( u != null ) {
                    scores.add(new Score(u, time, difficulty));
                }
            }

            ps.close();
            c.commit();
            c.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return scores;
    }

    public User login(String username, String password) throws SQLiteException {

        User user = null;

        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(connectionUrl);

            c.setAutoCommit(false);

            PreparedStatement ps = c.prepareStatement("SELECT Id, Username From User WHERE UPPER(Username) LIKE ? AND Password LIKE ?");

            ps.setString(1, username.toUpperCase());
            ps.setString(2, password);
            ResultSet result = ps.executeQuery();

            if (result.isClosed()) {
                ps.close();
                c.close();
                return null;
            }

            user = new User(result.getInt(1), result.getString(2));

            ps.close();
            c.commit();
            c.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return user;
    }

        public int register(String username, String password) throws SQLiteException {

            int userId = 0;

            try {
                Class.forName("org.sqlite.JDBC");
                Connection c = DriverManager.getConnection(connectionUrl);

                // Needed to be able to return userId
                c.setAutoCommit(false);

                PreparedStatement ps = c.prepareStatement("INSERT INTO User (Username, Password) VALUES(?,?)", PreparedStatement.RETURN_GENERATED_KEYS);

                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                ResultSet result = ps.getGeneratedKeys();
                userId = result.getInt(1);

                ps.close();
                c.commit();
                c.close();

            } catch (ClassNotFoundException | SQLException e) {
                System.out.println(e.getMessage());
                if(e.getClass() == SQLiteException.class) {
                    throw new SQLiteException("Username already exists!", SQLITE_AUTH);
                }

                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            return userId != 0 ? userId : null;
        }

        public int addScore(int userId, int timeLapsed, int difficulty) {
            int scoreId = 0;

            try {
                Class.forName("org.sqlite.JDBC");
                Connection c = DriverManager.getConnection(connectionUrl);

                // Needed to be able to return scoreId
                c.setAutoCommit(false);

                PreparedStatement ps = c.prepareStatement("INSERT INTO Score (UserId, Difficulty, TimeLapsed) VALUES(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);

                ps.setInt(1, userId);
                ps.setInt(2, difficulty);
                ps.setInt(3, timeLapsed);
                ps.executeUpdate();
                ResultSet result = ps.getGeneratedKeys();
                scoreId = result.getInt(1);

                ps.close();
                c.commit();
                c.close();

            } catch (ClassNotFoundException | SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            return scoreId != 0 ? scoreId : null;
        }
}
