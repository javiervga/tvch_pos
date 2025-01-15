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
import java.util.ArrayList;
import java.util.List;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.entity.SalidaCajaEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class SalidaCajaDao {
    
    private static SalidaCajaDao dao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(SalidaCajaDao.class);
    
    public static SalidaCajaDao getSalidaCajaDao(){
        if(dao == null)
            dao = new SalidaCajaDao();
        return dao;
    }
    
    public SalidaCajaDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param aperturaCajaId
     * @return
     * @throws Exception 
     */
    public List<SalidaCajaEntity> obtenerSalidasPorAperturaCaja(Long aperturaCajaId) throws Exception{

        List<SalidaCajaEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_salida_caja , id_apertura_caja , id_tipo_salida , observaciones, monto, hora_salida FROM salidas_caja WHERE id_apertura_caja =");
            query.append(aperturaCajaId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                SalidaCajaEntity entity = new SalidaCajaEntity();
                entity.setAperturaCajaId(rs.getLong("id_apertura_caja"));
                entity.setMonto(rs.getDouble("monto"));
                entity.setObservaciones(rs.getString("observaciones"));
                entity.setSalidaCajaId(rs.getLong("id_salida_caja"));
                entity.setTipoSalidaId(rs.getLong("id_tipo_salida"));
                list.add(entity);
            }

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

        return list;

        
    }
    
    /**
     * 
     * @param sesion
     * @param montoSalida
     * @param tipoSalidaId
     * @param observaciones
     * @throws Exception 
     */
    public void registrarSalidaCaja(Sesion sesion, Double montoSalida, Long tipoSalidaId, String observaciones) throws Exception {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into salidas_caja ( id_apertura_caja, id_tipo_salida, observaciones, monto, hora_salida) values (");
            query.append(sesion.getAperturaCajaId()).append(",");
            query.append(tipoSalidaId).append(",'");
            query.append(observaciones).append("',");
            query.append(montoSalida).append(",'");
            query.append(utilerias.obtenerFechaFormatoMysql()).append("')");
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar salida de caja: " + sw.toString());
            throw new Exception("Ocurrió un error al registrar su salida de caja. Por favor reintente.");
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
