package ch.makery.address.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

}