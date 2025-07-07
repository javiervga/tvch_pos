/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.entity.SalidaCajaEntity;
import mx.com.tvch.pos.entity.SalidaExtraordinariaEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class SalidaExtraordinariaDao {
    
    private static SalidaExtraordinariaDao dao;
    
    private final Utilerias utilerias;
        
    Logger logger = LoggerFactory.getLogger(SalidaExtraordinariaDao.class);
    
    public static SalidaExtraordinariaDao getSalidaExtraordinariaDao(){
        if(dao == null)
            dao = new SalidaExtraordinariaDao();
        return dao;
    }
    
    public SalidaExtraordinariaDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param salidaId
     * @return
     * @throws Exception 
     */
    public SalidaExtraordinariaEntity obtenerSalidasPorId(Long salidaId) throws Exception{

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_salida_extraordinaria , id_salida_extraordinaria_server , observaciones, monto, fecha_salida, usuario ,id_usuario, id_caja FROM salida_extraordinaria WHERE id_salida_extraordinaria =");
            query.append(salidaId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                SalidaExtraordinariaEntity entity = new SalidaExtraordinariaEntity();
                entity.setSalidaExtraordinariaId(rs.getLong("id_salida_extraordinaria"));
                entity.setSalidaExtraordinariaServerId(rs.getLong("id_salida_extraordinaria_server"));
                entity.setObservaciones(rs.getString("observaciones"));
                entity.setMonto(rs.getDouble("monto"));
                entity.setFechaSalida(rs.getDate("fecha_salida"));
                entity.setUsuario(rs.getString("usuario"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                entity.setCajaId(rs.getLong("id_caja"));
                return entity;
            }

            return null;
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar salidas de caja en bd: \n" + sw.toString());
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
        
    }
    
    /**
     * 
     * @param sesion
     * @param montoSalida
     * @param observaciones
     * @return
     * @throws Exception 
     */
    public Long registrarSalidaExtraordinaria(Sesion sesion, Double montoSalida, String observaciones) throws Exception {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            Long id = utilerias.generarIdLocal();
            
            String query = "insert into salida_extraordinaria (id_salida_extraordinaria , observaciones, monto, fecha_salida, id_usuario, usuario, id_caja) values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, id);
            ps.setString(2, observaciones);
            ps.setDouble(3, montoSalida);
            ps.setString(4, utilerias.obtenerFechaFormatoMysql());
            ps.setLong(5, sesion.getUsuarioId());
            ps.setString(6, sesion.getUsuario());
            ps.setLong(7, sesion.getCajaId());
            ps.executeUpdate();
            
            /*ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                id = rs.getLong(1);
            }*/
            
            return id;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar salida extraordinaria: " + sw.toString());
            throw new Exception("Ocurrió un error al registrar su salida extraordinaria. Por favor reintente.");
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
