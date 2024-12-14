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
import mx.com.tvch.pos.entity.CajaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CajaDao {
    
    private static CajaDao cajaDao;
    
    Logger logger = LoggerFactory.getLogger(CajaDao.class);
    
    public static CajaDao getCajaDao(){
        if(cajaDao == null)
            cajaDao = new CajaDao();
        return cajaDao;
    }
    
    public CajaEntity obtenerCaja() {

        CajaEntity entity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("select * from cajas");
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                entity = new CajaEntity();
                entity.setEstatusId(rs.getLong("id_estatus"));
                entity.setCajaId(rs.getLong("id_caja"));
                entity.setNumero(rs.getInt("numero"));
                entity.setSucursalId(rs.getLong("id_sucursal"));
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
    
    public void actualizarEstatusCaja(Long cajaId, Integer estatus) {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update cajas set id_estatus = ");
            query.append(estatus);
            query.append(" where id_caja = ");
            query.append(cajaId);
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar estatus de caja: " + sw.toString());
            //throw new Exception("Ocurrió un error al registrar su apertura de caja. Por favor reintente.");
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
        
    }
    
}
