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
            
            String query = "insert into cancelaciones_contrato (id_cancelacion, id_contrato, fecha_cancelacion, id_usuario, id_servicio, id_motivo, observaciones) values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getCancelacionId());
            ps.setLong(2, entity.getContratoId());
            ps.setString(3, entity.getFechaCancelacion());
            ps.setLong(4, entity.getUsuarioId());
            ps.setLong(5, entity.getServicioId());
            ps.setLong(6, entity.getMotivoId());
            ps.setString(7, entity.getObservaciones());
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
    
}
