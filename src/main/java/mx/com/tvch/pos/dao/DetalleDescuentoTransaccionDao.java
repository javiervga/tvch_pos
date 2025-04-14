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
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class DetalleDescuentoTransaccionDao {
    
    private static DetalleDescuentoTransaccionDao dao;
    
    Logger logger = LoggerFactory.getLogger(DetalleDescuentoTransaccionDao.class);
    
    public static DetalleDescuentoTransaccionDao getDetalleDescuentoTransaccionDao(){
        if(dao == null)
            dao = new DetalleDescuentoTransaccionDao();
        return dao;
    }
    
    /**
     * 
     * @param transaccionId
     * @return
     * @throws Exception 
     */
    public List<DetalleDescuentoTransaccionEntity> obtenerDetallesDescuentoPorTransaccion(Long transaccionId) throws Exception{

        List<DetalleDescuentoTransaccionEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_detalle, id_transaccion, id_tipo_descuento, observaciones, monto FROM detalle_descuento_transaccion WHERE id_transaccion =");
            query.append(transaccionId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                DetalleDescuentoTransaccionEntity entity = new DetalleDescuentoTransaccionEntity();
                entity.setDetalleId(rs.getLong("id_detalle"));
                entity.setMonto(rs.getDouble("monto"));
                entity.setObservaciones(rs.getString("observaciones"));
                entity.setTipoDescuentoId(rs.getLong("id_tipo_descuento"));
                entity.setTransaccionId(rs.getLong("id_transaccion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar detalles de descuentos de transaccion en bd: \n" + sw.toString());
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
    public Long registrarDetalleDescuento(DetalleDescuentoTransaccionEntity entity) throws Exception{

        Long id = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            
            String query = "insert into detalle_descuento_transaccion (id_transaccion, id_tipo_descuento, observaciones, monto) values(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getTransaccionId());
            ps.setLong(2, entity.getTipoDescuentoId());
            ps.setString(3, entity.getObservaciones());
            ps.setDouble(4, entity.getMonto());
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
            logger.error("Error al registrar detalle de descuento de transaccion: " + sw.toString());
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
