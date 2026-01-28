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
import mx.com.tvch.pos.entity.CambioServicioEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CambioServicioDao {
    
    private static CambioServicioDao dao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(CambioServicioDao.class);
    
    public static CambioServicioDao getCambioServicioDao(){
        if(dao == null)
            dao = new CambioServicioDao();
        return dao;
    }
    
    public CambioServicioDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param entity 
     */
    public void registrarCambioServicio(CambioServicioEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into cambios_servicio (id_cambio_servicio , id_contrato , id_servicio_actual , id_servicio_nuevo , costo, "
                    + "id_sucursal , id_usuario , id_orden_servicio ) values (");
            query.append(entity.getCambioId()).append(",");
            query.append(entity.getContratoId()).append(",");
            query.append(entity.getServicioActualId()).append(",");
            query.append(entity.getServicioNuevoId()).append(",");
            query.append(entity.getCosto()).append(",");
            query.append(entity.getSucursalId()).append(",");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getOrdenServicioId()).append(")");
   
            logger.info("query insert cambio de servicio: "+query);
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar cambio de servicio: " + sw.toString());
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
