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
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class DetalleCobroTransaccionDao {
    
    private static DetalleCobroTransaccionDao dao;
    
    Logger logger = LoggerFactory.getLogger(DetalleCobroTransaccionDao.class);
    
    public static DetalleCobroTransaccionDao getDetalleCobroTransaccionDao(){
        if(dao == null)
            dao = new DetalleCobroTransaccionDao();
        return dao;
    }
    
    /**
     * 
     * @param transaccionId
     * @return
     * @throws Exception 
     */
    public List<DetalleCobroTransaccionEntity> obtenerDetallesCobroPorTransaccion(Long transaccionId) throws Exception{

        List<DetalleCobroTransaccionEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_detalle, id_servicio, id_transaccion, id_tipo_cobro,  monto FROM detalle_cobro_transaccion WHERE id_transaccion =");
            query.append(transaccionId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                DetalleCobroTransaccionEntity entity = new DetalleCobroTransaccionEntity();
                entity.setDetalleId(rs.getLong("id_detalle"));
                entity.setMonto(rs.getDouble("monto"));
                entity.setServicioId(rs.getLong("id_servicio"));
                entity.setTipoCobroId(rs.getLong("id_tipo_cobro"));
                entity.setTransaccionId(rs.getLong("id_transaccion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar detalles de cobro de transaccion en bd: \n" + sw.toString());
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
    public Long registrarDetalleTransaccion(DetalleCobroTransaccionEntity entity) throws Exception{

        Long id = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            
            String query = "insert into detalle_cobro_transaccion (id_servicio, id_transaccion, id_tipo_cobro, monto, id_orden) values(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getServicioId());
            ps.setLong(2, entity.getTransaccionId());
            ps.setLong(3, entity.getTipoCobroId());
            ps.setDouble(4, entity.getMonto());
            if(entity.getOrdenId() != null)
                ps.setLong(5, entity.getOrdenId());
            else
                ps.setObject(5, null);
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
            logger.error("Error al registrar detalle de cobro de transaccion: " + sw.toString());
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
