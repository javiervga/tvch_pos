/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.config.Sesion;
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
