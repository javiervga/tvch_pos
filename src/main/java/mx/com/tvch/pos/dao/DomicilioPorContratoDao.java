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
    
    /**
     * 
     * @param contratoId
     * @return
     * @throws Exception 
     */
    public List<DomicilioPorContratoEntity> consultarDomiciliosPorContrato(Long contratoId) throws Exception{
        
        List<DomicilioPorContratoEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("select id_domicilioxcontrato , id_domicilio , id_contrato ");
            query.append("from domicilios_x_contrato WHERE id_contrato = ");
            query.append(contratoId);
            query.append(" order by id_domicilioxcontrato  asc");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                DomicilioPorContratoEntity entity = new DomicilioPorContratoEntity();
                entity.setId(rs.getLong("id_domicilioxcontrato"));
                entity.setIdContrato(rs.getLong("id_contrato"));
                entity.setIdDomicilio(rs.getLong("id_domicilio"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar domicilios en domicilios x contrato en bd: \n" + sw.toString());
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
