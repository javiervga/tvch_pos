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
import mx.com.tvch.pos.entity.RecuperacionContratoEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class RecuperacionContratoDao {
    
    private static RecuperacionContratoDao dao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(RecuperacionContratoDao.class);
    
    public static RecuperacionContratoDao getRecuperacionContratoDao(){
        if(dao == null)
            dao = new RecuperacionContratoDao();
        return dao;
    }
    
    public RecuperacionContratoDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param entity 
     */
    public void registrarRecuperacionContrato(RecuperacionContratoEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into recuperaciones_contrato (id_recuperacion , id_usuario , id_contrato, "
                    + "id_sucursal , costo, nueva_fecha_pago ) values (");
            query.append(entity.getRecuperacionId()).append(",");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getContratoId()).append(",");
            query.append(entity.getSucursalId()).append(",");
            query.append(entity.getCosto()).append(",'");
            query.append(entity.getNuevaFechaPago()).append("')");
   
            logger.info("query insert recuperacion de contrato: "+query);
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar recuperacion de contrato: " + sw.toString());
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
