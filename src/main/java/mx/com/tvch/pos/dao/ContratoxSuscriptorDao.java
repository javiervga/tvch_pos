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
import java.util.Date;
import java.util.List;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.util.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class ContratoxSuscriptorDao {
    
    private static ContratoxSuscriptorDao dao;
    
    Logger logger = LoggerFactory.getLogger(ContratoxSuscriptorDao.class);
    
    public static ContratoxSuscriptorDao getContratoxSuscriptorDao(){
        if(dao == null)
            dao = new ContratoxSuscriptorDao();
        return dao;
    }
    
    /**
     * 
     * @param contratoId
     * @return
     * @throws Exception 
     */
    public List<ContratoxSuscriptorEntity> obtenerIdsContratoSuscriptor(Long contratoId) throws Exception{

        List<ContratoxSuscriptorEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_contrato, id_suscriptor FROM contratos_x_suscriptor WHERE id_contrato =");
            query.append(contratoId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                ContratoxSuscriptorEntity entity = new ContratoxSuscriptorEntity();
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setSusucriptorId(rs.getLong("id_suscriptor"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar suscriptores x contrato en bd: \n" + sw.toString());
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
    
    public List<ContratoxSuscriptorEntity> obtenerContratosSuscriptor(Long contratoId, int tipoBusquedaCobro, String cadenaBusqueda) throws Exception{

        List<ContratoxSuscriptorEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append(obtenerSqlCobro(tipoBusquedaCobro, contratoId, cadenaBusqueda));
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                ContratoxSuscriptorEntity entity = new ContratoxSuscriptorEntity();
                entity.setApellidoMaterno(rs.getString("apellido_materno"));
                entity.setApellidoPaterno(rs.getString("apellido_paterno"));
                entity.setCalle(rs.getString("calle"));
                entity.setColonia(rs.getString("colonia"));
                //if(rs.getLong("id_contrato_anterior"))
                entity.setContratoAnteriorId(rs.getLong("id_contrato_anterior"));
                entity.setContratoId(rs.getLong("id_contrato"));
                entity.setDomicilioId(rs.getLong("id_domicilio"));
                entity.setEstatusContratoId(rs.getInt("id_estatus_contrato"));
                entity.setEstatusContrato(rs.getString("estatus_contrato"));
                entity.setEstatusSuscriptorId(rs.getInt("estatus_suscriptor"));
                try{
                    if(rs.getDate("fecha_proximo_pago") != null)
                        entity.setFechaProximoPago(rs.getDate("fecha_proximo_pago"));
                    else
                        entity.setFechaProximoPago(new Date());
                }catch(Exception ex){
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    logger.error("Fallo recuperar contrato con fecha de pago con formato invalido: \n" + sw.toString());
                }
                entity.setNombre(rs.getString("nombre"));
                entity.setNumeroCalle(rs.getString("numero_calle"));
                entity.setReferencia(rs.getString("referencia"));
                entity.setSusucriptorId(rs.getLong("id_suscriptor"));
                entity.setTelefono(rs.getString("telefono"));
                entity.setTvsContratadas(rs.getInt("tvs_contratadas"));
                entity.setServicioId(rs.getLong("id_servicio"));
                entity.setServicio(rs.getString("nombre_servicio"));
                entity.setCostoServicio(rs.getDouble("costo_servicio"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar suscriptores x contrato en bd: \n" + sw.toString());
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
    
    private String obtenerSqlCobro(int tipoBusquedaCobro, Long contratoId, String cadenaBusqueda){
        
        StringBuilder builder = new StringBuilder();
        
        switch(tipoBusquedaCobro){
            case Constantes.TIPO_BUSQUEDA_CONTRATO:
                builder.append("SELECT c.id_contrato, c.id_contrato_anterior, c.id_estatus as id_estatus_contrato, ec.descripcion as estatus_contrato, c.tvs_contratadas, c.fecha_proximo_pago, sus.id_suscriptor, sus.nombre,\n" +
"               sus.apellido_paterno, sus.apellido_materno, sus.telefono, sus.id_estatus as estatus_suscriptor, d.id_domicilio, d.colonia, d.calle,\n" +
"               d.numero_calle, d.referencia, s.id_servicio, s.nombre as nombre_servicio, s.costo as costo_servicio\n" +
"          FROM contratos c\n" +
"    INNER JOIN contratos_x_suscriptor cs\n" +
"            ON c.id_contrato = cs.id_contrato\n" +
"    INNER JOIN domicilios_x_contrato dc\n" +
"            ON c.id_contrato = dc.id_contrato\n" +
"    INNER JOIN domicilios d \n" +
"            ON dc.id_domicilio = d.id_domicilio\n" +
"    INNER JOIN servicios_x_contrato sc \n" +
"            ON sc.id_contrato = c.id_contrato\n" +
"    INNER JOIN servicios s \n" +
"            ON s.id_servicio = sc.id_servicio\n" +
"    INNER JOIN suscriptores sus\n" +
"            ON cs.id_suscriptor = sus.id_suscriptor\n" +
"    INNER JOIN estatus_contrato ec\n" +
"            ON c.id_estatus = ec.id_estatus\n" +
"         WHERE c.id_contrato = ");
                builder.append(contratoId);
                //builder.append(" AND sus.id_estatus = 2");
                break;
            case Constantes.TIPO_BUSQUEDA_CONTRATO_ANTERIOR:
                builder.append("SELECT c.id_contrato, c.id_contrato_anterior, c.id_estatus as id_estatus_contrato, ec.descripcion as estatus_contrato, c.tvs_contratadas, c.fecha_proximo_pago, sus.id_suscriptor, sus.nombre,\n" +
"               sus.apellido_paterno, sus.apellido_materno, sus.telefono, sus.id_estatus as estatus_suscriptor, d.id_domicilio, d.colonia, d.calle,\n" +
"               d.numero_calle, d.referencia, s.id_servicio, s.nombre as nombre_servicio, s.costo as costo_servicio\n" +
"          FROM contratos c\n" +
"    INNER JOIN contratos_x_suscriptor cs\n" +
"            ON c.id_contrato = cs.id_contrato\n" +
"    INNER JOIN domicilios_x_contrato dc\n" +
"            ON c.id_contrato = dc.id_contrato\n" +
"    INNER JOIN domicilios d \n" +
"            ON dc.id_domicilio = d.id_domicilio\n" +
"    INNER JOIN servicios_x_contrato sc \n" +
"            ON sc.id_contrato = c.id_contrato\n" +
"    INNER JOIN servicios s \n" +
"            ON s.id_servicio = sc.id_servicio\n" +
"    INNER JOIN suscriptores sus\n" +
"            ON cs.id_suscriptor = sus.id_suscriptor\n" +
"    INNER JOIN estatus_contrato ec\n" +
"            ON c.id_estatus = ec.id_estatus\n" +
"         WHERE c.id_contrato_anterior = ");
                builder.append(contratoId);
                //builder.append(" AND sus.id_estatus = 2");
                break;
            case Constantes.TIPO_BUSQUEDA_NOMBRE:
                builder.append("SELECT c.id_contrato, c.id_contrato_anterior, c.id_estatus as id_estatus_contrato, ec.descripcion as estatus_contrato, c.tvs_contratadas, c.fecha_proximo_pago, sus.id_suscriptor, sus.nombre,\n" +
"               sus.apellido_paterno, sus.apellido_materno, sus.telefono, sus.id_estatus as estatus_suscriptor, d.id_domicilio, d.colonia, d.calle,\n" +
"               d.numero_calle, d.referencia, s.id_servicio, s.nombre as nombre_servicio, s.costo as costo_servicio\n" +
"          FROM contratos c\n" +
"    INNER JOIN contratos_x_suscriptor cs\n" +
"            ON c.id_contrato = cs.id_contrato\n" +
"    INNER JOIN domicilios_x_contrato dc\n" +
"            ON c.id_contrato = dc.id_contrato\n" +
"    INNER JOIN domicilios d \n" +
"            ON dc.id_domicilio = d.id_domicilio\n" +
"    INNER JOIN servicios_x_contrato sc \n" +
"            ON sc.id_contrato = c.id_contrato\n" +
"    INNER JOIN servicios s \n" +
"            ON s.id_servicio = sc.id_servicio\n" +
"    INNER JOIN suscriptores sus\n" +
"            ON cs.id_suscriptor = sus.id_suscriptor\n" +
"    INNER JOIN estatus_contrato ec\n" +
"            ON c.id_estatus = ec.id_estatus\n" +
"         WHERE sus.nombre like '%");
                builder.append(cadenaBusqueda);
                builder.append("%'");
                //builder.append(" AND sus.id_estatus = 2");
                break;
            case Constantes.TIPO_BUSQUEDA_APELLIDO_PATERNO:
                builder.append("SELECT c.id_contrato, c.id_contrato_anterior, c.id_estatus as id_estatus_contrato, ec.descripcion as estatus_contrato, c.tvs_contratadas, c.fecha_proximo_pago, sus.id_suscriptor, sus.nombre,\n" +
"               sus.apellido_paterno, sus.apellido_materno, sus.telefono, sus.id_estatus as estatus_suscriptor, d.id_domicilio, d.colonia, d.calle,\n" +
"               d.numero_calle, d.referencia, s.id_servicio, s.nombre as nombre_servicio, s.costo as costo_servicio\n" +
"          FROM contratos c\n" +
"    INNER JOIN contratos_x_suscriptor cs\n" +
"            ON c.id_contrato = cs.id_contrato\n" +
"    INNER JOIN domicilios_x_contrato dc\n" +
"            ON c.id_contrato = dc.id_contrato\n" +
"    INNER JOIN domicilios d \n" +
"            ON dc.id_domicilio = d.id_domicilio\n" +
"    INNER JOIN servicios_x_contrato sc \n" +
"            ON sc.id_contrato = c.id_contrato\n" +
"    INNER JOIN servicios s \n" +
"            ON s.id_servicio = sc.id_servicio\n" +
"    INNER JOIN suscriptores sus\n" +
"            ON cs.id_suscriptor = sus.id_suscriptor\n" +
"    INNER JOIN estatus_contrato ec\n" +
"            ON c.id_estatus = ec.id_estatus\n" +
"         WHERE sus.apellido_paterno like '%");
                builder.append(cadenaBusqueda);
                builder.append("%'");
                break;
            case Constantes.TIPO_BUSQUEDA_APELLIDO_MATERNO:
                builder.append("SELECT c.id_contrato, c.id_contrato_anterior, c.id_estatus as id_estatus_contrato, ec.descripcion as estatus_contrato, c.tvs_contratadas, c.fecha_proximo_pago, sus.id_suscriptor, sus.nombre,\n" +
"               sus.apellido_paterno, sus.apellido_materno, sus.telefono, sus.id_estatus as estatus_suscriptor, d.id_domicilio, d.colonia, d.calle,\n" +
"               d.numero_calle, d.referencia, s.id_servicio, s.nombre as nombre_servicio, s.costo as costo_servicio\n" +
"          FROM contratos c\n" +
"    INNER JOIN contratos_x_suscriptor cs\n" +
"            ON c.id_contrato = cs.id_contrato\n" +
"    INNER JOIN domicilios_x_contrato dc\n" +
"            ON c.id_contrato = dc.id_contrato\n" +
"    INNER JOIN domicilios d \n" +
"            ON dc.id_domicilio = d.id_domicilio\n" +
"    INNER JOIN servicios_x_contrato sc \n" +
"            ON sc.id_contrato = c.id_contrato\n" +
"    INNER JOIN servicios s \n" +
"            ON s.id_servicio = sc.id_servicio\n" +
"    INNER JOIN suscriptores sus\n" +
"            ON cs.id_suscriptor = sus.id_suscriptor\n" +
"    INNER JOIN estatus_contrato ec\n" +
"            ON c.id_estatus = ec.id_estatus\n" +
"         WHERE sus.apellido_materno like '%");
                builder.append(cadenaBusqueda);
                builder.append("%'");
                break;
            case Constantes.TIPO_BUSQUEDA_DOMICILIO:
                builder.append("SELECT c.id_contrato, c.id_contrato_anterior, c.id_estatus as id_estatus_contrato, ec.descripcion as estatus_contrato, c.tvs_contratadas, c.fecha_proximo_pago, sus.id_suscriptor, sus.nombre,\n" +
"               sus.apellido_paterno, sus.apellido_materno, sus.telefono, sus.id_estatus as estatus_suscriptor, d.id_domicilio, d.colonia, d.calle,\n" +
"               d.numero_calle, d.referencia, s.id_servicio, s.nombre as nombre_servicio, s.costo as costo_servicio\n" +
"          FROM contratos c\n" +
"    INNER JOIN contratos_x_suscriptor cs\n" +
"            ON c.id_contrato = cs.id_contrato\n" +
"    INNER JOIN domicilios_x_contrato dc\n" +
"            ON c.id_contrato = dc.id_contrato\n" +
"    INNER JOIN domicilios d \n" +
"            ON dc.id_domicilio = d.id_domicilio\n" +
"    INNER JOIN servicios_x_contrato sc \n" +
"            ON sc.id_contrato = c.id_contrato\n" +
"    INNER JOIN servicios s \n" +
"            ON s.id_servicio = sc.id_servicio\n" +
"    INNER JOIN suscriptores sus\n" +
"            ON cs.id_suscriptor = sus.id_suscriptor\n" +
"    INNER JOIN estatus_contrato ec\n" +
"            ON c.id_estatus = ec.id_estatus\n" +
"         WHERE d.calle like '%");
                builder.append(cadenaBusqueda);
                builder.append("%'  OR d.colonia like '%");
                builder.append(cadenaBusqueda);
                builder.append("%'");
                break;
            default:
                break;
        }
        
        return builder.toString();
        
    }
    
}
