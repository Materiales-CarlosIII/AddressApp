package ch.makery.address.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.*;
import java.io.*;

public class AddressDatabase {

    public String dbms;
    public String dbName;
    public String userName;
    public String password;
    public String urlString;

    private String driver;
    private String serverName;
    private int portNumber;
    private Properties prop;
    
    private final String PROPERTIES_FILE = "properties/mysql-properties.xml";

    public AddressDatabase() throws FileNotFoundException,
            IOException,
            InvalidPropertiesFormatException {
        super();
        this.setProperties(PROPERTIES_FILE);
    }

    private void setProperties(String fileName) throws FileNotFoundException,
            IOException,
            InvalidPropertiesFormatException {
        this.prop = new Properties();
        FileInputStream fis = new FileInputStream(fileName);
        prop.loadFromXML(fis);

        this.dbms = this.prop.getProperty("dbms");
        this.driver = this.prop.getProperty("driver");
        this.dbName = this.prop.getProperty("database_name");
        this.userName = this.prop.getProperty("user_name");
        this.password = this.prop.getProperty("password");
        this.serverName = this.prop.getProperty("server_name");
        this.portNumber = Integer.parseInt(this.prop.getProperty("port_number"));

        System.out.println("Set the following properties:");
        System.out.println("dbms: " + dbms);
        System.out.println("driver: " + driver);
        System.out.println("dbName: " + dbName);
        System.out.println("userName: " + userName);
        System.out.println("serverName: " + serverName);
        System.out.println("portNumber: " + portNumber);

    }

    public Connection getConnectionToDatabase() throws SQLException {
        {
            Connection conn = null;
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.userName);
            connectionProps.put("password", this.password);

            // Using a driver manager:
//        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            conn
                    = DriverManager.getConnection("jdbc:" + dbms + "://" + serverName
                            + ":" + portNumber + "/" + dbName,
                            connectionProps);
            conn.setCatalog(this.dbName);
            System.out.println("Connected to database");
            return conn;
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        String currentUrlString = null;
        currentUrlString = "jdbc:" + this.dbms + "://" + this.serverName
                + ":" + this.portNumber + "/";
        conn
                = DriverManager.getConnection(currentUrlString,
                        connectionProps);

        this.urlString = currentUrlString + this.dbName;
        conn.setCatalog(this.dbName);
        System.out.println("Connected to database");
        return conn;
    }

    public Connection getConnection(String userName,
            String password) throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        conn
                = DriverManager.getConnection("jdbc:" + this.dbms + "://" + this.serverName
                        + ":" + this.portNumber + "/",
                        connectionProps);
        conn.setCatalog(this.dbName);
        return conn;
    }

    public static void closeConnection(Connection connArg) {
        System.out.println("Releasing all open resources ...");
        try {
            if (connArg != null) {
                connArg.close();
                connArg = null;
            }
        } catch (SQLException sqle) {
            System.out.println(sqle);
        }
    }
    
    public static ResultSet getPersons(Connection con) throws SQLException {
        // Consulta que vamos a realizar
        String query = "SELECT * FROM person";
        
        // Indicamos que vamos a lanzar una sentencia
        // utilizando la conexión que hemos abierto previamente
        Statement stmt = con.createStatement();
        
        // la ejecución de la sentencia devuelve un objeto ResultSet con los datos
        ResultSet rs = stmt.executeQuery(query);
        
        // Devolvemos el objeto ResultSet obtenido.
        return rs;
    }
    
    public static int newPerson(Connection con, Person p) throws SQLException {
        String sql = "INSERT INTO person(firstName, lastName, street, postalCode, city, birthday) "
                + "VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);

        // set parameters for statement
        pstmt.setString(1, p.getFirstName());
        pstmt.setString(2, p.getLastName());
        pstmt.setString(3, p.getStreet());
        pstmt.setInt(4, p.getPostalCode());
        pstmt.setString(5, p.getCity());
        pstmt.setDate(6, java.sql.Date.valueOf(p.getBirthday()));

        int rowAffected = pstmt.executeUpdate();

        int personId = -1;
        if (rowAffected == 1) {
                       // get person id
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                personId = rs.getInt(1);
            }
        }
        return personId;
    }    

        public static boolean updatePerson(Connection con, Person p) throws SQLException {
        String sqlUpdate = "UPDATE person "
                + "SET "
                + " firstName = ?,"
                + " lastName = ?,"
                + " street = ?,"
                + " postalCode = ?,"
                + " city = ?,"
                + " birthday = ? "
                + "WHERE id = ?";

        PreparedStatement pstmt = con.prepareStatement(sqlUpdate);

        // set parameters for statement
        pstmt.setString(1, p.getFirstName());
        pstmt.setString(2, p.getLastName());
        pstmt.setString(3, p.getStreet());
        pstmt.setInt(4, p.getPostalCode());
        pstmt.setString(5, p.getCity());
        pstmt.setDate(6, java.sql.Date.valueOf(p.getBirthday()));
        pstmt.setInt(7, p.getId());

        int rowAffected = pstmt.executeUpdate();

        return (rowAffected == 1);
    }

    public static boolean deletePerson(Connection myConnection, Person selectedPerson) throws SQLException  {
                String sqlDelete = "DELETE FROM person "
                + "WHERE id = ?";

        PreparedStatement pstmt = myConnection.prepareStatement(sqlDelete);

        pstmt.setInt(1, selectedPerson.getId());

        int rowAffected = pstmt.executeUpdate();

        return (rowAffected == 1);
    }

}
