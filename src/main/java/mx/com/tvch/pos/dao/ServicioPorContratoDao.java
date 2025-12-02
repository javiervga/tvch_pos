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
import mx.com.tvch.pos.entity.ServicioPorContratoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class ServicioPorContratoDao {

    private static ServicioPorContratoDao servicioxContratoDao;

    Logger logger = LoggerFactory.getLogger(ServicioPorContratoDao.class);

    public static ServicioPorContratoDao getServicioPorContratoDao() {
        if (servicioxContratoDao == null) {
            servicioxContratoDao = new ServicioPorContratoDao();
        }
        return servicioxContratoDao;
    }

    /**
     * 
     * @param entity 
     */
    public void registrarServicioPorContrato(ServicioPorContratoEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("insert into servicios_x_contrato (id_servicio , id_contrato, estatus) values (");
            query.append(entity.getIdServicio()).append(",");
            query.append(entity.getIdContrato()).append(",");
            query.append(entity.getEstatus()).append(")");
            System.out.println("query servicio x contrato: " + query.toString());
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar en tabla servicios x contrato: " + sw.toString());
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
