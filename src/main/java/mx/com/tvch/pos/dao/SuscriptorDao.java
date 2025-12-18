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
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.entity.SuscriptorEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class SuscriptorDao {
    
    private static SuscriptorDao dao;
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(SuscriptorDao.class);
    
    public static SuscriptorDao getSuscriptorDao(){
        if(dao == null)
            dao = new SuscriptorDao();
        return dao;
    }
    
    public SuscriptorDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public boolean existeSuscriptor(Long suscriptorId) throws Exception{

        Long suscriptorExistente = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_suscriptor FROM suscriptores where id_suscriptor = ");
            query.append(suscriptorId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                suscriptorExistente = rs.getLong("id_suscriptor");
            }
            
            if(suscriptorExistente != null)
                return true;
            else
                return false;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar id de suscriptor en bd: \n" + sw.toString());
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
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public Long obtenerMaxSuscriptorId() throws Exception{

        Long maxSuscriptorId = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT MAX(id_suscriptor) as max_suscriptor from suscriptores");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                maxSuscriptorId = rs.getLong("max_suscriptor");
            }
            
            return maxSuscriptorId;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar max suscriptorId en bd: \n" + sw.toString());
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
    }
    
    /**
     * 
     * @param entity 
     */
    public void registrarSuscriptor(SuscriptorEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            //for (ContratoxSuscriptorInstalador suscriptor : suscriptores) {
                
                //validar q no exista el suscriptor esdsadsadsadsadsa
                if(!existeSuscriptor(entity.getId(), stmt)){
                
                    query.append("insert into suscriptores (id_suscriptor, nombre, apellido_paterno, apellido_materno, telefono, id_usuario, id_estatus) values (");
                    query.append(entity.getId()).append(",'");
                    query.append(entity.getNombre()).append("','");
                    query.append(entity.getApellidoPaterno()).append("','");
                    query.append(entity.getApellidoMaterno()).append("','");
                    query.append(entity.getTelefono()).append("',");
                    query.append(entity.getUsuarioId()).append(",");
                    query.append(entity.getEstatus()).append(")");
                    System.out.println("insert suscriptor: "+query.toString());
                    stmt.executeUpdate(query.toString());
                    query.delete(0, query.length());
                
                }
            //}

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar suscriptores: "+sw.toString());
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
    public void actualizarSuscriptor( SuscriptorEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
                
            //validar q no exista el suscriptor esdsadsadsadsadsa
            if(existeSuscriptor(entity.getId(), stmt)){

                query.append("update suscriptores set ");
                query.append("nombre = '").append(entity.getNombre()).append("', ");
                query.append("apellido_paterno = '").append(entity.getApellidoPaterno()).append("', ");
                query.append("apellido_materno = '").append(entity.getApellidoMaterno()).append("', ");
                query.append("telefono = '").append(entity.getTelefono()).append("', ");
                query.append("id_estatus = ").append(String.valueOf(entity.getEstatus())).append(" ");
                query.append("where id_suscriptor = ").append(entity.getId());
                System.out.println("update suscriptor: "+query.toString());
                stmt.executeUpdate(query.toString());
                query.delete(0, query.length());

            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar suscriptor: "+sw.toString());
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
    
    private boolean existeSuscriptor(Long suscriptorId, Statement stmt){
        
        boolean existeSuscriptor = false;
        
        try {
              
            StringBuilder query = new StringBuilder();
            query.append("select * from suscriptores where id_suscriptor = ").append(suscriptorId);
            ResultSet rs = stmt.executeQuery(query.toString());
            while(rs.next()){
                Long idExistente = rs.getLong("id_suscriptor");
                if(idExistente != null && idExistente>0)
                    existeSuscriptor = true;
            }
            
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar suscriptor: "+sw.toString());
        }
        
        return existeSuscriptor;
        
    }
    
    /**
     * 
     * @param suscriptorId
     * @return
     * @throws Exception 
     */
    public SuscriptorEntity consultarSuscriptor(Long suscriptorId) throws Exception{
        
        SuscriptorEntity suscriptorEntity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("select id_suscriptor , id_suscriptor_server , nombre, apellido_paterno , apellido_materno, ");
            query.append("telefono , fecha_registro, id_usuario , id_estatus ");
            query.append("from suscriptores WHERE id_suscriptor = ");
            query.append(suscriptorId);
            query.append(" order by id_suscriptor asc");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                suscriptorEntity = new SuscriptorEntity();
                suscriptorEntity.setId(rs.getLong("id_suscriptor"));
                suscriptorEntity.setServerId(rs.getLong("id_suscriptor_server"));
                suscriptorEntity.setNombre(rs.getString("nombre"));
                suscriptorEntity.setApellidoPaterno(rs.getString("apellido_paterno"));
                suscriptorEntity.setApellidoMaterno(rs.getString("apellido_materno"));
                suscriptorEntity.setTelefono(rs.getString("telefono"));
                suscriptorEntity.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                suscriptorEntity.setUsuarioId(rs.getLong("id_usuario"));
                suscriptorEntity.setEstatus(rs.getLong("id_estatus"));
                break;
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar suscriptor en bd: \n" + sw.toString());
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

        return suscriptorEntity;
        
    }
    
}
