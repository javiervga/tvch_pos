/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.com.tvch.pos.config.DbConfig;

import mx.com.tvch.pos.entity.TransaccionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class TransaccionDao {
    
    private static TransaccionDao dao;
    
    Logger logger = LoggerFactory.getLogger(TransaccionDao.class);
    
    public static TransaccionDao getTransaccionDao(){
        if(dao == null)
            dao = new TransaccionDao();
        return dao;
    }
    
    /**
     * 
     * @param aperturaCajaId
     * @return
     * @throws Exception 
     */
    public List<TransaccionEntity> obtenerTransaccionesxAperturaCaja(Long aperturaCajaId) throws Exception{

        List<TransaccionEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_transaccion , id_apertura_caja, id_contrato, monto, fecha_transaccion FROM transacciones WHERE id_apertura_caja =");
            query.append(aperturaCajaId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                TransaccionEntity entity = new TransaccionEntity();
                entity.setAperturaCajaId(rs.getLong("id_apertura_caja"));
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setFechaTransaccion(String.valueOf(rs.getDate("fecha_transaccion")));
                entity.setMonto(rs.getDouble("monto"));
                entity.setTransaccionId(rs.getLong("id_transaccion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar transacciones en bd: \n" + sw.toString());
            throw new Exception(ex.getMessage());
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

        return list;

        
    }
    
    /**
     * 
     * @param entity
     * @return
     * @throws Exception 
     */
    public Long registrarTransaccion(TransaccionEntity entity) throws Exception{

        Long id = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            
            String query = "insert into transacciones (id_apertura_caja, id_contrato, monto, fecha_transaccion) values(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getAperturaCajaId());
            ps.setLong(2, entity.getContratoId());
            ps.setDouble(3, entity.getMonto());
            ps.setString(4, entity.getFechaTransaccion());
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                id = rs.getLong(1);
            }
            
            return id;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar transaccion: " + sw.toString());
            throw new Exception("Ocurrió un error al intentar realizar la operación. Por favor intente de nuevo, si el problema persiste contacte a soporte");
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
