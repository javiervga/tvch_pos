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

import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.entity.TransaccionTicketEntity;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class TransaccionDao {
    
    private static TransaccionDao dao;
    private static Utilerias util;
    
    Logger logger = LoggerFactory.getLogger(TransaccionDao.class);
    
    public static TransaccionDao getTransaccionDao(){
        if(dao == null)
            dao = new TransaccionDao();
        return dao;
    }
    
    public TransaccionDao(){
        util = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param aperturaCajaId
     * @return
     * @throws Exception 
     */
    public List<TransaccionTicketEntity> obtenerTransaccionesxTipoCobro(Integer tipoCobroId, String fechaInicio, String fechaFin) throws Exception{

        List<TransaccionTicketEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("select t.fecha_transaccion, t.id_transaccion, t.id_transaccion_server, tc.id_tipo_cobro, tc.descripcion AS tipoCobro, c.id_contrato, c.id_contrato_anterior, c.fecha_proximo_pago, \n" +
"		       t.id_apertura_caja, s.nombre, s.apellido_paterno, s.apellido_materno, d.calle, d.numero_calle, d.colonia, ser.nombre as servicio, s.telefono, t.monto \n" +
"		  from transacciones t \n" +
"		 inner\n" +
"		  join detalle_cobro_transaccion dct \n" +
"		    on t.id_transaccion = dct.id_transaccion \n" +
"		 inner \n" +
"		  join tipos_cobro tc \n" +
"		    on dct.id_tipo_cobro = tc.id_tipo_cobro \n" +
"		 inner \n" +
"		  join contratos_x_suscriptor cxs  \n" +
"		    on cxs.id_contrato = t.id_contrato \n" +
"		 inner \n" +
"		  join contratos c \n" +
"		    on cxs.id_contrato = c.id_contrato \n" +
"		 inner \n" +
"		  join suscriptores s \n" +
"		    on s.id_suscriptor = cxs.id_suscriptor \n" +
"		 inner \n" +
"		  join servicios_x_contrato sxc \n" +
"		    on sxc.id_contrato = cxs.id_contrato \n" +
"		 inner \n" +
"		  join servicios ser \n" +
"		    on ser.id_servicio = sxc.id_servicio \n" +
"		 inner \n" +
"		  join domicilios_x_contrato dxc \n" +
"		    on dxc.id_contrato = cxs.id_contrato \n" +
"		 inner \n" +
"		  join domicilios d \n" +
"		    on d.id_domicilio = dxc.id_domicilio \n" +
"		 where tc.id_tipo_cobro =");
            query.append(tipoCobroId);
            query.append(" and t.fecha_transaccion BETWEEN '");
            query.append(fechaInicio);
            query.append("' AND '");
            query.append(fechaFin);
            query.append("'");
            System.out.println("query: ".concat(query.toString()));
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                TransaccionTicketEntity entity = new TransaccionTicketEntity();
                entity.setApellidoMaterno(rs.getString("apellido_materno"));
                entity.setApellidoPaterno(rs.getString("apellido_paterno"));
                entity.setCalle(rs.getString("calle"));
                entity.setColonia(rs.getString("colonia"));
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setContratoAnteriorId(rs.getLong("id_contrato_anterior"));
                entity.setAperturaCajaId(rs.getLong("id_apertura_caja"));
                entity.setDescripcionTipoCobro(rs.getString("tipoCobro"));
                entity.setFechaProximoPago(util.convertirDateTime2String(rs.getDate("fecha_proximo_pago"), Constantes.FORMATO_FECHA_WEB_SERVICE));
                entity.setFechaTransaccion(util.convertirDateTime2String(rs.getTimestamp("fecha_transaccion"), Constantes.FORMATO_FECHA_WEB_SERVICE));
                entity.setMonto(rs.getDouble("monto"));
                entity.setNombre(rs.getString("nombre"));
                entity.setNumeroCalle(rs.getString("numero_calle"));
                entity.setServicio(rs.getString("servicio"));
                entity.setTelefono(rs.getString("telefono"));
                entity.setTipoCobroId(rs.getInt("id_tipo_cobro"));
                entity.setTransaccionId(rs.getLong("id_transaccion"));
                entity.setTransaccionServerId(rs.getLong("id_transaccion_server"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar transacciones para reimpresion de ticket en bd: \n" + sw.toString());
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
     * @param aperturaCajaId
     * @return
     * @throws Exception 
     */
    public List<TransaccionEntity> obtenerTransaccionesxAperturaCaja(Long aperturaCajaId) throws Exception{

        List<TransaccionEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_transaccion , id_apertura_caja, id_contrato, monto, fecha_transaccion FROM transacciones WHERE id_apertura_caja =");
            query.append(aperturaCajaId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                TransaccionEntity entity = new TransaccionEntity();
                entity.setAperturaCajaId(rs.getLong("id_apertura_caja"));
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setFechaTransaccion(String.valueOf(rs.getDate("fecha_transaccion")));
                entity.setMonto(rs.getDouble("monto"));
                entity.setTransaccionId(rs.getLong("id_transaccion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar transacciones en bd: \n" + sw.toString());
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
     * @return
     * @throws Exception 
     */
    public Long registrarTransaccion(TransaccionEntity entity) throws Exception{

        Long id = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            
            String query = "insert into transacciones (id_transaccion ,id_apertura_caja, id_contrato, monto, fecha_transaccion) values(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getTransaccionId());
            ps.setLong(2, entity.getAperturaCajaId());
            ps.setLong(3, entity.getContratoId());
            ps.setDouble(4, entity.getMonto());
            ps.setString(5, entity.getFechaTransaccion());
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                id = rs.getLong(1);
            }
            
            return id;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar transaccion: " + sw.toString());
            throw new Exception("Ocurrió un error al intentar realizar la operación. Por favor intente de nuevo, si el problema persiste contacte a soporte");
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
