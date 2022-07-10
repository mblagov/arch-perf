import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

public class Kafka2Clickhouse {

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:ch://mblagov-students-server:8123";
        Properties properties = new Properties();
        properties.setProperty("client_name", "Agent #1");

        String sql = "insert into mblagov.person_data (id, first_name, last_name, middle_name, date_of_birth, address, comment)\n" +
                "values (?, ?, ?, ?, ?, ?, ?)";
        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "first_id");
            stmt.setString(2, "first_name");
            stmt.setString(3, "last_name");
            stmt.setString(4, "middle_name");
            stmt.setDate(5, Date.valueOf(LocalDate.of(2022,1,1)));
            stmt.setString(6, "address");
            stmt.setString(7, "comment");
            int updatedRecords = stmt.executeUpdate();
            System.out.printf("Updated %i records%n", updatedRecords);
        }

    }
}
