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
import mx.com.tvch.pos.entity.OrdenServicioEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class OrdenServicioDao {
    
    private static OrdenServicioDao dao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(OrdenServicioDao.class);
    
    public static OrdenServicioDao getOrdenServicioDao(){
        if(dao == null)
            dao = new OrdenServicioDao();
        return dao;
    }
    
    public OrdenServicioDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param entity 
     */
    public void registrarOrdenServicio(OrdenServicioEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into ordenes_servicio (id_orden_servicio , id_contrato , id_usuario , id_suscriptor , costo, "
                    + "observaciones_registro, id_tipo_orden_servicio, id_domicilio, id_servicio, id_estatus ) values (");
            query.append(entity.getOrdenId()).append(",");
            query.append(entity.getContratoId()).append(",");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getSuscriptorId()).append(",");
            query.append(entity.getCosto()).append(",'");
            query.append(entity.getObservacionesRegistro()).append("',");
            query.append(entity.getTipoOrdenServicioId()).append(",");
            query.append(entity.getDomicilioId()).append(",");
            query.append(entity.getServicioId()).append(",");
            query.append(entity.getEstatusId()).append(")");
   
            logger.info("query insert orden de servicio: "+query);
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar orden de servicio: " + sw.toString());
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
