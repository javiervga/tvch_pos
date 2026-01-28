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
import mx.com.tvch.pos.entity.CancelacionEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CancelacionDao {
    
    private static CancelacionDao dao;
    private static Utilerias util;
    
    Logger logger = LoggerFactory.getLogger(CancelacionDao.class);
    
    public static CancelacionDao getCancelacionDao(){
        if(dao == null)
            dao = new CancelacionDao();
        return dao;
    }
    
    public CancelacionDao(){
        util = Utilerias.getUtilerias();
    }
    
    
    
    /**
     * 
     * @param entity
     * @return
     * @throws Exception 
     */
    public Long registrarCancelacion(CancelacionEntity entity) throws Exception{

        Long id = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            
            String query = "insert into cancelaciones_contrato (id_cancelacion, id_contrato, fecha_cancelacion, id_usuario, id_servicio, id_motivo, id_sucursal, observaciones) values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getCancelacionId());
            ps.setLong(2, entity.getContratoId());
            ps.setString(3, entity.getFechaCancelacion());
            ps.setLong(4, entity.getUsuarioId());
            ps.setLong(5, entity.getServicioId());
            ps.setLong(6, entity.getMotivoId());
            ps.setLong(7, entity.getSucursalId());
            ps.setString(8, entity.getObservaciones());
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
            logger.error("Error al registrar cancelacion: " + sw.toString());
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
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<CancelacionEntity> obtenerCancelaciones() throws Exception{

        List<CancelacionEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_cancelacion, id_cancelacion_server, id_contrato, ");
            query.append("fecha_cancelacion, id_usuario, id_servicio, id_motivo, id_sucursal, observaciones ");
            query.append("FROM cancelaciones_contrato WHERE id_cancelacion_server is null ");
            query.append("order by id_cancelacion asc"); 
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                CancelacionEntity entity = new CancelacionEntity();
                entity.setCancelacionId(rs.getLong("id_cancelacion"));
                entity.setCancelacionServerId(null);
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setFechaCancelacion(rs.getString("fecha_cancelacion"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                entity.setServicioId(rs.getLong("id_servicio"));
                entity.setMotivoId(rs.getLong("id_motivo"));
                entity.setSucursalId(rs.getLong("id_sucursal"));
                entity.setObservaciones(rs.getString("observaciones"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar cancelaciones en bd: \n" + sw.toString());
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
    
}
