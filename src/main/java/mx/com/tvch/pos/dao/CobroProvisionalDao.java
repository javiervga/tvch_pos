/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.entity.CobroProvisionalEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroProvisionalDao {
    
    private static CobroProvisionalDao dao;
    
    private final Utilerias utilerias;
        
    Logger logger = LoggerFactory.getLogger(CobroProvisionalDao.class);
    
    public static CobroProvisionalDao getCobroProvisionalDao(){
        if(dao == null)
            dao = new CobroProvisionalDao();
        return dao;
    }
    
    public CobroProvisionalDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param fechaInicio
     * @param fechaFin
     * @return
     * @throws Exception 
     */
    public List<CobroProvisionalEntity> obtenerCobrosProvisionales(String fechaInicio, String fechaFin) throws Exception{

        List<CobroProvisionalEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("select id_cobro, id_cobro_server , id_contrato, folio_contrato, suscriptor, domicilio, servicio, telefono, \n" +
                        "tipo_orden, tipo_orden_servicio, observaciones, fecha, monto, id_usuario, id_caja, estatus \n" +
"		  from cobro_provisional t " +
"		 where fecha BETWEEN '");
            query.append(fechaInicio);
            query.append("' AND '");
            query.append(fechaFin);
            query.append("'");
            System.out.println("query: ".concat(query.toString()));
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                CobroProvisionalEntity entity = new CobroProvisionalEntity();
                entity.setCobroId(rs.getLong("id_cobro"));
                entity.setCobroServerId(rs.getLong("id_cobro_server"));
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setFolioContrato(rs.getLong("folio_contrato"));
                entity.setSuscriptor(rs.getString("suscriptor"));
                entity.setDomicilio(rs.getString("domicilio"));
                entity.setServicio(rs.getString("servicio"));
                entity.setTelefono(rs.getString("telefono"));
                entity.setTipoOrden(rs.getString("tipo_orden"));
                entity.setTipoOrdenServicio(rs.getString("tipo_orden_servicio"));
                entity.setObservaciones(rs.getString("observaciones"));
                entity.setFecha(rs.getDate("fecha"));
                //entity.setFecha(utilerias.convertirDateTime2String(rs.getDate("fecha"), Constantes.FORMATO_FECHA_WEB_SERVICE));
                entity.setMonto(rs.getDouble("monto"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                entity.setCajaId(rs.getLong("id_caja"));
                entity.setEstatus(rs.getInt("estatus"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar cobros provisionales para reimpresion de ticket en bd: \n" + sw.toString());
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
     * @param cobroId
     * @return
     * @throws Exception 
     */
    public CobroProvisionalEntity obtenerCobroProvisional(Long cobroId) throws Exception{

        CobroProvisionalEntity entity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_cobro , id_cobro_server , id_contrato, folio_contrato,  suscriptor, domicilio, servicio, telefono, tipo_orden, tipo_orden_servicio, "
                    + "observaciones, fecha, monto, id_usuario, id_caja, estatus FROM cobro_provisional WHERE id_cobro = ");
            query.append(cobroId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                entity = new CobroProvisionalEntity();
                entity.setCobroId(rs.getLong("id_cobro"));
                entity.setCobroServerId(rs.getLong("id_cobro_server"));
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setFolioContrato(rs.getLong("folio_contrato"));
                entity.setSuscriptor(rs.getString("suscriptor"));
                entity.setDomicilio(rs.getString("domicilio"));
                entity.setServicio(rs.getString("servicio"));
                entity.setTelefono(rs.getString("telefono"));
                entity.setTipoOrden(rs.getString("tipo_orden"));
                entity.setTipoOrdenServicio(rs.getString("tipo_orden_servicio"));
                entity.setObservaciones(rs.getString("observaciones"));
                entity.setFecha(rs.getDate("fecha"));
                entity.setMonto(rs.getDouble("monto"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                entity.setCajaId(rs.getLong("id_caja"));
                entity.setEstatus(rs.getInt("estatus"));
                break;
            }
            
            return entity;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar cobro provisional en bd: \n" + sw.toString());
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
     * @return
     * @throws Exception 
     */
    public Long registrarCobroProvisional(CobroProvisionalEntity entity) throws Exception{
       
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            //Long id = utilerias.generarIdLocal();
            
            String query = "insert into cobro_provisional (id_cobro , id_contrato, folio_contrato, suscriptor, domicilio, servicio, telefono, "
                    + "tipo_orden, tipo_orden_servicio, observaciones, fecha, monto, id_usuario, id_caja, estatus  ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getCobroId());
            ps.setLong(2, entity.getContratoId());
            ps.setLong(3, entity.getFolioContrato());
            ps.setString(4, entity.getSuscriptor());
            ps.setString(5, entity.getDomicilio());
            ps.setString(6, entity.getServicio());
            ps.setString(7, entity.getTelefono());
            ps.setString(8, entity.getTipoOrden());
            ps.setString(9, entity.getTipoOrdenServicio());
            ps.setString(10, entity.getObservaciones());
            ps.setString(11, utilerias.obtenerFechaFormatoMysql());
            ps.setDouble(12, entity.getMonto());
            ps.setLong(13, entity.getUsuarioId());
            ps.setLong(14, entity.getCajaId());
            ps.setLong(15, entity.getEstatus());
            ps.executeUpdate();
            
            /*ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                id = rs.getLong("id_cobro");
            }*/
            
            return entity.getCobroId();
            //return id;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar cobro provisional: " + sw.toString());
            throw new Exception("Ocurrió un error al registrar su cobro provisional. Por favor reintente.");
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
    public List<CobroProvisionalEntity> obtenerCobrosProvisionales() throws Exception{

        List<CobroProvisionalEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("select id_cobro, id_cobro_server , id_contrato, folio_contrato, suscriptor, domicilio, servicio, telefono, ");
            query.append("tipo_orden, tipo_orden_servicio, observaciones, fecha, monto, id_usuario, id_caja, estatus ");
            query.append("from cobro_provisional WHERE id_cobro_server is null ");
            query.append("order by id_cobro asc");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                CobroProvisionalEntity entity = new CobroProvisionalEntity();
                entity.setCobroId(rs.getLong("id_cobro"));
                entity.setCobroServerId(rs.getLong("id_cobro_server"));
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setFolioContrato(rs.getLong("folio_contrato"));
                entity.setSuscriptor(rs.getString("suscriptor"));
                entity.setDomicilio(rs.getString("domicilio"));
                entity.setServicio(rs.getString("servicio"));
                entity.setTelefono(rs.getString("telefono"));
                entity.setTipoOrden(rs.getString("tipo_orden"));
                entity.setTipoOrdenServicio(rs.getString("tipo_orden_servicio"));
                entity.setObservaciones(rs.getString("observaciones"));
                entity.setFecha(rs.getDate("fecha"));
                entity.setMonto(rs.getDouble("monto"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                entity.setCajaId(rs.getLong("id_caja"));
                entity.setEstatus(rs.getInt("estatus"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar cobros provisionales en bd: \n" + sw.toString());
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
    
}
