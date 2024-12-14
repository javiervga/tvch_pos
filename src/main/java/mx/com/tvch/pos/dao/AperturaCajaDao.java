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
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.entity.AperturaCajaEntity;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class AperturaCajaDao {
    
    private static AperturaCajaDao aperturaCajaDao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(AperturaCajaDao.class);
    
    public static AperturaCajaDao getAperturaCajaDao(){
        if(aperturaCajaDao == null)
            aperturaCajaDao = new AperturaCajaDao();
        return aperturaCajaDao;
    }
    
    public AperturaCajaDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    public void registrarAperturaCaja(Long aperturaCajaId, Sesion sesion, Double fondoFijo) throws Exception {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into aperturas_caja (id_apertura_caja, id_apertura_caja_server, id_caja, numero_caja, id_usuario, usuario, fondo_fijo, hora_apertura, estatus) values (");
            query.append(aperturaCajaId).append(",");
            query.append("null,");
            query.append(sesion.getCajaId()).append(",");
            query.append(sesion.getNumeroCaja()).append(",");
            query.append(sesion.getUsuarioId()).append(",'");
            query.append(sesion.getUsuario()).append("',");
            query.append(fondoFijo).append(",'");
            query.append(utilerias.obtenerFechaFormatoMysql()).append("',");
            query.append(Constantes.ESTATUS_ACTIVO).append(")");
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar apertura de caja: " + sw.toString());
            throw new Exception("Ocurrió un error al registrar su apertura de caja. Por favor reintente.");
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
    
    public AperturaCajaEntity obtenerAperturaCaja(Long aperturaCajaId) throws Exception{
        
        AperturaCajaEntity entity = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            if(aperturaCajaId != null)
                query.append("select * from aperturas_caja where id_apertura_caja = ").append(aperturaCajaId);
            else
                query.append("select * from aperturas_caja where estatus = 1");
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                entity = new AperturaCajaEntity();
                entity.setAperturaCajaId(rs.getLong("id_apertura_caja"));
                entity.setAperturaCajaServer(rs.getLong("id_apertura_caja_server"));
                entity.setCajaId(rs.getLong("id_caja"));
                entity.setNumeroCaja(rs.getInt("numero_caja"));
                entity.setEstatus(rs.getInt("estatus"));
                entity.setFondoFijo(rs.getDouble("fondo_fijo"));
                entity.setHoraApertura(rs.getTimestamp("hora_apertura"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                entity.setUsuario(rs.getString("usuario"));
                break;
            }
            
            return entity;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al validar si existe apertura de caja activa: \n" + sw.toString());
            throw new Exception("Ocurrió un error al obtener información de su apertura. Por favor reintente.");
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
