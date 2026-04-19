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
import mx.com.tvch.pos.entity.OrdenInstalacionEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class OrdenInstalacionDao {
    
    private static OrdenInstalacionDao dao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(OrdenInstalacionDao.class);
    
    public static OrdenInstalacionDao getOrdenInstalacionDao(){
        if(dao == null)
            dao = new OrdenInstalacionDao();
        return dao;
    }
    
    public OrdenInstalacionDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param entity 
     */
    public void registrarOrdenInstalacion(OrdenInstalacionEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into ordenes_instalacion (id_orden_instalacion , id_contrato , id_usuario , id_suscriptor , observaciones_registro, "
                    + "id_estatus , id_domicilio , tvs_contratadas, id_servicio , costo, terminal_serie ) values (");
            query.append(entity.getOrdenId()).append(",");
            query.append(entity.getContratoId()).append(",");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getSuscriptorId()).append(",'");
            query.append(entity.getObservacionesRegistro()).append("',");
            query.append(entity.getEstatusId()).append(",");
            query.append(entity.getDomicilioId()).append(",");
            query.append(entity.getTvs()).append(",");
            query.append(entity.getServicioId()).append(",");
            query.append(entity.getCosto()).append(",");
            if(entity.getTerminalSerie() != null)
                query.append("'").append(entity.getTerminalSerie()).append("')");
            else
                query.append("null)");
                
            logger.info("query insert orden de instalacion: "+query);
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar orden de instalacion: " + sw.toString());
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
