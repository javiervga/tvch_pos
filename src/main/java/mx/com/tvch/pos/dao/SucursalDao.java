/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.entity.SucursalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class SucursalDao {
    
    private static SucursalDao sucursalDao;
    
    Logger logger = LoggerFactory.getLogger(SucursalDao.class);
    
    public static SucursalDao getSucursalDao(){
        if(sucursalDao == null)
            sucursalDao = new SucursalDao();
        return sucursalDao;
    }
    
    public SucursalEntity obtenerSucursal() {

        SucursalEntity entity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("select * from sucursales");
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                entity = new SucursalEntity();
                entity.setEstatus(rs.getInt("estatus"));
                entity.setNombre(rs.getString("nombre"));
                entity.setSucursalId(rs.getLong("id_sucursal"));
                entity.setZonaId(rs.getLong("id_zona"));
                break;
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al obtener caja en bd: \n" + sw.toString());
        } finally {
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return entity;

    }
    
}
