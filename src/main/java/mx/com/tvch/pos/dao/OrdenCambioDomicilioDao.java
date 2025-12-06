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
import mx.com.tvch.pos.entity.OrdenCambioDomicilioEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class OrdenCambioDomicilioDao {
    
    private static OrdenCambioDomicilioDao dao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(OrdenCambioDomicilioDao.class);
    
    public static OrdenCambioDomicilioDao getOrdenCambioDomicilioDao(){
        if(dao == null)
            dao = new OrdenCambioDomicilioDao();
        return dao;
    }
    
    public OrdenCambioDomicilioDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param entity 
     */
    public void registrarCambioDomicilio(OrdenCambioDomicilioEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into ordenes_cambio_domicilio (id_orden_cambio_domicilio , id_contrato , id_usuario , id_suscriptor , costo, "
                    + "observaciones_registro, id_domicilio, id_domicilio_sucursal_nuevo, id_servicio, id_estatus, colonia, calle, ciudad, calle1, "
                    + "calle2, numero_calle, referencia ) values (");
            query.append(entity.getOrdenId()).append(",");
            query.append(entity.getContratoId()).append(",");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getSuscriptorId()).append(",");
            query.append(entity.getCosto()).append(",'");
            query.append(entity.getObservacionesRegistro()).append("',");
            query.append(entity.getDomicilioId()).append(",");
            query.append(entity.getDomicilioNuevoId()).append(",");
            query.append(entity.getServicioId()).append(",");
            query.append(entity.getEstatusId()).append(",'");
            query.append(entity.getColonia()).append("','");
            query.append(entity.getCalle()).append("','");
            query.append(entity.getCiudad()).append("','");
            query.append(entity.getCalle1()).append("','");
            query.append(entity.getCalle2()).append("','");
            query.append(entity.getNumeroCalle()).append("','");
            query.append(entity.getReferencia()).append("')");
   
            logger.info("query insert orden de cambio de domicilio: "+query);
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar orden de cambio de domicilio: " + sw.toString());
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
