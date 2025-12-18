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
import java.util.List;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.entity.DomicilioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class DomicilioDao {

    private static DomicilioDao domicilioDao;

    Logger logger = LoggerFactory.getLogger(DomicilioDao.class);

    public static DomicilioDao getDomicilioDao() {
        if (domicilioDao == null) {
            domicilioDao = new DomicilioDao();
        }
        return domicilioDao;
    }
    
    /**
     * 
     * @param domicilioId 
     * @param estatus 
     */
    public void actualizarEstatusDomicilio(
            Long domicilioId, 
            Integer estatus) {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update domicilios set estatus = ");
            query.append(estatus);
            query.append(" where id_domicilio = ");
            query.append(domicilioId);
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar estatus de domicilio: " + sw.toString());
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
    public void actualizarDomicilio(DomicilioEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update domicilios set ");
            query.append("colonia = '").append(entity.getColonia()).append("', ");
            query.append("calle = '").append(entity.getCalle()).append("', ");
            query.append("numero_calle = '").append(entity.getNumeroCalle()).append("', ");
            query.append("ciudad = '").append(entity.getCiudad()).append("', ");
            query.append("calle1 = '").append(entity.getCalle1()).append("', ");
            query.append("calle2 = '").append(entity.getCalle2()).append("', ");
            query.append("referencia = '").append(entity.getReferencia()).append("' ");
            query.append("where id_domicilio = ").append(entity.getId());
                
                
            logger.debug("query update domicilio: "+query.toString());
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());
            

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar domicilio: "+sw.toString());
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
    public void registrarDomicilio(DomicilioEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("insert into domicilios (id_domicilio, colonia, calle, numero_calle, ciudad, calle1, calle2, referencia, estatus) values (");
            query.append(entity.getId()).append(",'");
            query.append(entity.getColonia()).append("','");
            query.append(entity.getCalle()).append("','");
            query.append(entity.getNumeroCalle()).append("','");
            query.append(entity.getCiudad()).append("','");
            query.append(entity.getCalle1()).append("','");
            query.append(entity.getCalle2()).append("','");
            query.append(entity.getReferencia()).append("',");
            query.append(entity.getEstatus()).append(")");

            System.out.println("query domicilio: " + query.toString());
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar domicilios: " + sw.toString());
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

    private boolean existeDomicilio(Long domicilioId, Statement stmt) {

        boolean existeDomicilio = false;

        try {

            StringBuilder query = new StringBuilder();
            query.append("select * from domicilios where id_domicilio = ").append(domicilioId);
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                Long idExistente = rs.getLong("id_domicilio");
                if (idExistente != null && idExistente > 0) {
                    existeDomicilio = true;
                }
            }

        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar suscriptor: " + sw.toString());
        }

        return existeDomicilio;

    }
    
    /**
     * 
     * @param domicilioId
     * @return
     * @throws Exception 
     */
    public DomicilioEntity consultarDomicilio(Long domicilioId) throws Exception{
        
        DomicilioEntity domicilioEntity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("select id_domicilio , id_domicilio_server , colonia, calle , numero_calle, ciudad, ");
            query.append("calle1 , calle2, referencia , estatus ");
            query.append("from domicilios WHERE id_domicilio = ");
            query.append(domicilioId);
            query.append(" order by id_domicilio asc");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                domicilioEntity = new DomicilioEntity();
                domicilioEntity.setId(rs.getLong("id_domicilio"));
                domicilioEntity.setIdServer(rs.getLong("id_domicilio_server"));
                domicilioEntity.setColonia(rs.getString("colonia"));
                domicilioEntity.setCalle(rs.getString("calle"));
                domicilioEntity.setNumeroCalle(rs.getString("numero_calle"));
                domicilioEntity.setCiudad(rs.getString("ciudad"));
                domicilioEntity.setCalle1(rs.getString("calle1"));
                domicilioEntity.setCalle2(rs.getString("calle2"));
                domicilioEntity.setReferencia(rs.getString("referencia"));
                domicilioEntity.setEstatus(rs.getInt("estatus"));
                break;
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar domicilio en bd: \n" + sw.toString());
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

        return domicilioEntity;
        
    }

}
