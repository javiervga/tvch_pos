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
            //stmt = conn.createStatement();
            
            

            /*StringBuilder query = new StringBuilder();
            query.append("insert into transacciones (id_apertura_caja, id_contrato, monto, fecha_transaccion) values (");
            query.append(entity.getAperturaCajaId()).append(",");
            query.append(entity.getContratoId()).append(",");
            query.append(entity.getMonto()).append(",'");
            query.append(entity.getFechaTransaccion()).append("')");*/
            
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
            
            
            //id = stmt.executeUpdate(query.toString());
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
