/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import mx.com.tvch.pos.util.LectorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class DbConfig {

    private static DbConfig dbConfig;

    private final LectorProperties properties;
    private static Connection connection;
    
    Logger logger = LoggerFactory.getLogger(DbConfig.class);

    public static DbConfig getdDbConfig() {
        if (dbConfig == null) {
            dbConfig = new DbConfig();
        }
        return dbConfig;
    }

    public DbConfig() {
        properties = LectorProperties.getLectorProperties();
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            return (Connection) DriverManager.getConnection(
                    properties.obtenerPropiedad("tvch.db.url"),
                    properties.obtenerPropiedad("tvch.db.user"),
                    properties.obtenerPropiedad("tvch.db.password"));
        } else {
            return connection;
        }
    }
    
    public Statement getStatement() throws SQLException{
        return getConnection().createStatement();
    }

    public boolean existeConectorMySql() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            logger.info("Driver encontrado, se continua con la operación");
            return true;
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error, el driver no esta instalado: \n"+sw.toString());
            return false;
        }
    }

}
