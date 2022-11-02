import java.sql.*;
import java.util.Random;

public final class ApartmnetsDB implements Apartmnets {
    private final Connection connection;

    public ApartmnetsDB(final Connection connection) throws SQLException {
        this.connection = connection;
        initDB();
    }

    private void initDB() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Apartments");
            st.execute("CREATE TABLE Apartments(" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "region VARCHAR(20) DEFAULT NULL, " +
                    "address VARCHAR(128) NOT NULL, " +
                    "area DOUBLE(5,2) DEFAULT NULL, " +
                    "rooms INT DEFAULT NULL, " +
                    "price DOUBLE(10,2) NOT NULL);");
        }
    }

    @Override
    public void addApartment(
            final String region,
            final String address,
            final double area,
            final int number,
            final double price

    ) throws SQLException {
        insert(region, address, area, number, price);
    }

    @Override
    public void insert(
            final String region, final String address,
            final double area, final int number, final double price
    ) throws SQLException {
        final String sql = "INSERT INTO Apartments (region, address, area, rooms, price) VALUES(?, ?, ?, ?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, region);
            ps.setString(2, address);
            ps.setDouble(3, area);
            ps.setInt(4, number);
            ps.setDouble(5, price);
            ps.executeUpdate();
        }
    }

    @Override
    public void insertRandomApartment() throws SQLException {
        final Random random = new Random();
        final String[] addresses = {"1, Sonyachna st., Boyarka, Ukraine",
                "2, Boyakrska st., Nove, Ukraine",
                "3, khreshatik st., Kyiv, Ukraine"};
        this.connection.setAutoCommit(false);
        try {
            for (String address : addresses) {
                insert(
                        null,
                        address,
                        random.nextDouble() * 1000,
                        random.nextInt(10),
                        random.nextDouble() * 100000
                );
            }
            this.connection.commit();
        } catch (Exception ex) {
            this.connection.rollback();
        } finally {
            this.connection.setAutoCommit(true);
        }
    }

    @Override
    public void deleteApartment(final int id) throws SQLException {
        final String sql = "DELETE FROM Apartments WHERE id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void changeApartmentPrice(final int id, final double price) throws SQLException {
        final String sql = "UPDATE Apartments SET price = ? WHERE id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, price);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void viewApartments() throws SQLException {
        viewApartments("SELECT * FROM Apartments;");
    }

    @Override
    public void viewApartments(final String str) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(str);
             ResultSet rs = ps.executeQuery()) {
            final ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                System.out.print(md.getColumnName(i) + "\t\t");
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        }
    }

    @Override
    public void selectionByRegion(final String region) throws SQLException {
        final String sql = "SELECT * FROM Apartments WHERE region = " + region + ";";
        viewApartments(sql);
    }

    @Override
    public void selectionByPrice(final double priceMin, final double priceMax) throws SQLException {
        final String sql = "SELECT * FROM Apartments WHERE price BETWEEN " + priceMin + " and " + priceMax + ";";
        viewApartments(sql);
    }

    @Override
    public void selectionByArea(final double areaMin, final double areaMax) throws SQLException {
        final String sql = "SELECT * FROM Apartments WHERE area BETWEEN " + areaMin + " AND " + areaMax + ";";
        viewApartments(sql);
    }

    @Override
    public void selectionByRoom(final double numberMin, final double numberMax) throws SQLException {
        final String sql = "SELECT * FROM Apartments WHERE rooms BETWEEN " + numberMin + " AND " + numberMax + ";";
        viewApartments(sql);
    }
}