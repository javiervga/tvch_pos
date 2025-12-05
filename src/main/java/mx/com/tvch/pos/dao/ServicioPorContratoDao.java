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
     * @param contratoId
     * @return
     * @throws Exception 
     */
    public List<ServicioPorContratoEntity> consultarServiciosPorContrato(Long contratoId) throws Exception{
        
        List<ServicioPorContratoEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("select id_servicioxcontrato  , id_servicio  , id_contrato , estatus ");
            query.append("from servicios_x_contrato WHERE id_contrato = ");
            query.append(contratoId);
            query.append(" order by id_servicioxcontrato  asc");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                ServicioPorContratoEntity entity = new ServicioPorContratoEntity();
                entity.setId(rs.getLong("id_servicioxcontrato"));
                entity.setIdContrato(rs.getLong("id_contrato"));
                entity.setIdServicio(rs.getLong("id_servicio"));
                entity.setEstatus(rs.getInt("estatus"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar servicios en servicios x contrato en bd: \n" + sw.toString());
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
     * @param entity 
     */
    public void actualizarEstatusServicioPorContrato(ServicioPorContratoEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("update servicios_x_contrato set estatus = ").append(entity.getEstatus());
            query.append(" where id_servicio = ").append(entity.getIdServicio()).append(" and ");
            query.append(" id_contrato = ").append(entity.getIdContrato());
            System.out.println("query update estatus servicio x contrato: " + query.toString());
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar estatus en tabla servicios x contrato: " + sw.toString());
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
