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
import mx.com.tvch.pos.entity.DomicilioPorContratoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class DomicilioPorContratoDao {

    private static DomicilioPorContratoDao domicilioxContratoDao;

    Logger logger = LoggerFactory.getLogger(DomicilioPorContratoDao.class);

    public static DomicilioPorContratoDao getDomicilioPorContratoDao() {
        if (domicilioxContratoDao == null) {
            domicilioxContratoDao = new DomicilioPorContratoDao();
        }
        return domicilioxContratoDao;
    }

    public void registrarDomicilioPorContrato(DomicilioPorContratoEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("insert into domicilios_x_contrato (id_domicilio , id_contrato) values (");
            query.append(entity.getIdDomicilio()).append(",");
            query.append(entity.getIdContrato()).append(")");
            System.out.println("query domicilio x contrato: " + query.toString());
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar en tabla domicilios x contrato: " + sw.toString());
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
