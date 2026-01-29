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
import mx.com.tvch.pos.entity.ServicioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class ServicioDao {
    
    private static ServicioDao dao;

    Logger logger = LoggerFactory.getLogger(ServicioDao.class);

    public static ServicioDao getServicioDao() {
        if (dao == null) {
            dao = new ServicioDao();
        }
        return dao;
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public ServicioEntity consultarServicio(Long id) throws Exception{

        ServicioEntity entity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_servicio, nombre, descripcion, costo, id_sucursal, estatus, id_tipo_servicio, costo_instalacion ");
            query.append("FROM servicios where id_servicio = ");
            query.append(id);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                entity = new ServicioEntity();
                entity.setServicioId(rs.getLong("id_servicio"));
                entity.setNombre(rs.getString("nombre"));
                entity.setDescripcion(rs.getString("descripcion"));
                entity.setCosto(rs.getDouble("costo"));
                entity.setSucursalId(rs.getLong("id_sucursal"));
                entity.setEstatus(rs.getInt("estatus"));
                entity.setTipoServicioId(rs.getLong("id_tipo_servicio"));
                entity.setCostoInstalacion(rs.getDouble("costo_instalacion"));
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar servicio con id "+id+" en bd: \n" + sw.toString());
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

        return entity;
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<ServicioEntity> obtenerServicios() throws Exception{

        List<ServicioEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_servicio, nombre, descripcion, costo, id_sucursal, estatus, id_tipo_servicio, costo_instalacion ");
            query.append("FROM servicios where estatus = 1");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                ServicioEntity entity = new ServicioEntity();
                entity.setServicioId(rs.getLong("id_servicio"));
                entity.setNombre(rs.getString("nombre"));
                entity.setDescripcion(rs.getString("descripcion"));
                entity.setCosto(rs.getDouble("costo"));
                entity.setSucursalId(rs.getLong("id_sucursal"));
                entity.setEstatus(rs.getInt("estatus"));
                entity.setTipoServicioId(rs.getLong("id_tipo_servicio"));
                entity.setCostoInstalacion(rs.getDouble("costo_instalacion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar servicios en bd: \n" + sw.toString());
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
    
    public void actualizarServicio(ServicioEntity servicio) {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update servicios set nombre = '");
            query.append(servicio.getNombre()).append("', ");
            query.append("descripcion= '").append(servicio.getDescripcion()).append("', ");
            query.append("costo= ").append(servicio.getCosto()).append(", ");
            query.append("estatus= ").append(servicio.getEstatus()).append(", ");
            query.append("id_tipo_servicio = ").append(servicio.getTipoServicioId()).append(", ");
            query.append("costo_instalacion = ").append(servicio.getCostoInstalacion()).append(" ");
            query.append("where id_servicio  = ");
            query.append(servicio.getServicioId());
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar servicio: " + sw.toString());
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
     * @param servicio
     */
    public void registrarServicio(ServicioEntity servicio) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("insert into servicios (id_servicio, nombre, descripcion, costo, id_sucursal, estatus, id_tipo_servicio, costo_instalacion) values (");
            query.append(servicio.getServicioId()).append(",'");
            query.append(servicio.getNombre()).append("','");
            query.append(servicio.getDescripcion()).append("',");
            query.append(servicio.getCosto()).append(",");
            query.append(servicio.getSucursalId()).append(",");
            query.append(servicio.getEstatus()).append(",");
            query.append(servicio.getTipoServicioId()).append(",");
            query.append(servicio.getCostoInstalacion()).append(")");
            System.out.println("insert servicio: " + query.toString());
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar nuevo servicio: " + sw.toString());
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
