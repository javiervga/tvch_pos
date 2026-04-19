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
import mx.com.tvch.pos.entity.ContratoJoinOnuEntity;
import mx.com.tvch.pos.entity.OnuEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class OnuDao {

    private static OnuDao onuDao;

    Logger logger = LoggerFactory.getLogger(OnuDao.class);

    public static OnuDao getOnuDao() {
        if (onuDao == null) {
            onuDao = new OnuDao();
        }
        return onuDao;
    }

    /**
     *
     * @param onuId
     * @param serie
     * @param estatusId
     */
    public void actualizarOnu(
            Long onuId,
            String serie,
            Long estatusId) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update onus set serie = '");
            query.append(serie).append("', ");
            query.append("id_estatus = ").append(estatusId).append(", ");
            query.append("actualizacion = 1 where id_onu = ");
            query.append(onuId);
            logger.info("actualizando onu: " + query.toString());
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar onu: " + sw.toString());
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
     * @param serie
     * @return
     */
    public boolean existeOnu(String serie) {

        boolean existeOnu = false;

        Connection conn = null;
        Statement stmt = null;

        try {

            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("select * from onus where serie = '").append(serie).append("'");
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                Long idExistente = rs.getLong("id_onu");
                if (idExistente != null && idExistente > 0) {
                    existeOnu = true;
                }
            }

        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar onu: " + sw.toString());
        }

        return existeOnu;

    }

    /**
     *
     * @param entity
     */
    public void registrarOnu(OnuEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("insert into onus (id_onu, serie, id_estatus, id_usuario, id_sucursal, actualizacion) values (");
            query.append(entity.getOnuId()).append(",'");
            query.append(entity.getSerie()).append("',");
            query.append(entity.getEstatusId()).append(",");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getSucursalId()).append(", 1)");

            System.out.println("query insert onu: " + query.toString());
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar onu: " + sw.toString());
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
     * @param onuId
     * @return
     * @throws Exception
     */
    public OnuEntity consultarOnu(Long onuId) throws Exception {

        OnuEntity onuEntity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("select id_onu, id_onu_server, serie, id_estatus, id_sucursal, fecha_registro, id_usuario, actualizacion ");
            query.append("from onus WHERE id_onu = ");
            query.append(onuId);
            query.append(" order by id_onu asc");

            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                onuEntity = new OnuEntity();
                onuEntity.setOnuId(rs.getLong("id_onu"));
                onuEntity.setOnuServerId(rs.getLong("id_onu_server"));
                onuEntity.setSerie(rs.getString("serie"));
                onuEntity.setEstatusId(rs.getLong("id_estatus"));
                onuEntity.setSucursalId(rs.getLong("id_sucursal"));
                onuEntity.setFechaRegistro(rs.getDate("fecha_registro"));
                onuEntity.setUsuarioId(rs.getLong("id_usuario"));
                onuEntity.setActualizacion(rs.getInt("actualizacion"));
                break;
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar onu en bd: \n" + sw.toString());
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

        return onuEntity;

    }
    
    /**
     * 
     * @param serie
     * @return
     * @throws Exception 
     */
    public OnuEntity consultarOnuPorSerie(String serie) throws Exception {

        OnuEntity onuEntity = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("select id_onu, id_onu_server, serie, id_estatus, id_sucursal, fecha_registro, id_usuario, actualizacion ");
            query.append("from onus WHERE serie = '");
            query.append(serie).append("' ");
            query.append("order by id_onu asc");

            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                onuEntity = new OnuEntity();
                onuEntity.setOnuId(rs.getLong("id_onu"));
                onuEntity.setOnuServerId(rs.getLong("id_onu_server"));
                onuEntity.setSerie(rs.getString("serie"));
                onuEntity.setEstatusId(rs.getLong("id_estatus"));
                onuEntity.setSucursalId(rs.getLong("id_sucursal"));
                onuEntity.setFechaRegistro(rs.getDate("fecha_registro"));
                onuEntity.setUsuarioId(rs.getLong("id_usuario"));
                onuEntity.setActualizacion(rs.getInt("actualizacion"));
                break;
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar onu en bd: \n" + sw.toString());
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

        return onuEntity;

    }
    
    /**
     * 
     * @param serieOnu
     * @param estatusId
     * @return
     * @throws Exception 
     */
    public List<ContratoJoinOnuEntity> consultarOnus(String serieOnu, Long estatusId) throws Exception {

        List<ContratoJoinOnuEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();

            query.append("select c.id_contrato , c.id_contrato_server , c.folio_contrato, c.id_estatus , c.tvs_contratadas, c.fecha_proximo_pago, ");
            query.append("c.id_tipo_servicio , c.folio_placa, c.color_placa, c.onu, c.id_onu, c.fecha_registro, c.dia_primer_pago, c.mes_primer_pago, ");
            query.append("c.anio_primer_pago, c.nap, c.id_usuario, c.numero_caja, " );
            query.append("o.id_onu as onuId, o.id_onu_server, o.serie, o.id_estatus as estatus_onu , o.id_sucursal, o.fecha_registro as fecha_registro_onu, o.id_usuario as id_usuario_onu, o.actualizacion ");
            query.append("from onus o left join contratos c on o.id_onu = c.id_onu ");
            query.append("WHERE o.serie like '%").append(serieOnu).append("%' ");
            if(estatusId != null && estatusId > 0)
                query.append("and o.id_estatus = ").append(estatusId);
            query.append(" order by o.serie asc");

            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                ContratoJoinOnuEntity onuEntity = new ContratoJoinOnuEntity();
                onuEntity.setId(rs.getLong("id_contrato")); 
                onuEntity.setServerId(rs.getLong("id_contrato_server"));
                onuEntity.setFolioContrato(rs.getLong("folio_contrato"));
                onuEntity.setEstatus(rs.getLong("id_estatus"));
                onuEntity.setTvs(rs.getInt("tvs_contratadas"));
                onuEntity.setFechaProximoPago(rs.getString("fecha_proximo_pago"));
                onuEntity.setTipoServicioId(rs.getLong("id_tipo_servicio"));
                onuEntity.setFolioPlaca(rs.getLong("folio_placa"));
                onuEntity.setColorPlaca(rs.getString("color_placa"));
                onuEntity.setOnu(rs.getString("onu"));
                onuEntity.setFechaRegistro(rs.getDate("fecha_registro"));
                onuEntity.setPrimerDiaPago(rs.getInt("dia_primer_pago"));
                onuEntity.setPrimerMesPago(rs.getInt("mes_primer_pago"));
                onuEntity.setPrimerAnioPago(rs.getInt("anio_primer_pago"));
                onuEntity.setNap(rs.getString("nap"));
                onuEntity.setUsuarioId(rs.getLong("id_usuario"));
                onuEntity.setNumeroCaja(rs.getInt("numero_caja"));
                onuEntity.setOnuId(rs.getLong("onuId"));
                onuEntity.setOnuServerId(rs.getLong("id_onu_server"));
                onuEntity.setSerie(rs.getString("serie"));
                onuEntity.setEstatusOnuId(rs.getLong("estatus_onu"));
                onuEntity.setSucursalOnuId(rs.getLong("id_sucursal"));
                onuEntity.setFechaRegistroOnu(rs.getDate("fecha_registro_onu"));
                onuEntity.setUsuarioIdOnu(rs.getLong("id_usuario_onu"));
                onuEntity.setActualizacion(rs.getInt("actualizacion"));
                list.add(onuEntity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar onus en bd: \n" + sw.toString());
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
