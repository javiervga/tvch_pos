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
import mx.com.tvch.pos.entity.ContratoEntity;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class ContratoDao {

    private static ContratoDao dao;
    private final Utilerias util;
    Logger logger = LoggerFactory.getLogger(ContratoDao.class);

    public static ContratoDao getContratoDao() {
        if (dao == null) {
            dao = new ContratoDao();
        }
        return dao;
    }

    public ContratoDao() {
        this.util = Utilerias.getUtilerias();
    }
    
    
    
    /**
     * 
     * @param folioCOntrato
     * @return
     * @throws Exception 
     */
    public boolean existeContrato(Long folioCOntrato) throws Exception{

        Long contratoExistente = null;

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT folio_contrato FROM contratos where folio_contrato = ");
            query.append(folioCOntrato);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                contratoExistente = rs.getLong("folio_contrato");
            }
            
            if(contratoExistente != null)
                return true;
            else
                return false;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar folio de contrato en bd: \n" + sw.toString());
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
     * @param seActualizaEstatus 
     */
    public void actualizarContrato(ContratoEntity entity, boolean seActualizaEstatus) {

        Connection conn = null;
        Statement stmt = null;
        int actualizacion = Constantes.TIPO_ACTUALIZACION_CONTRATO_INFORMACION;
        if(seActualizaEstatus)
            actualizacion = Constantes.TIPO_ACTUALIZACION_CONTRATO_INFORMACION_Y_ESTATUS;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
                
            query.append("update contratos set ");
            query.append("id_estatus = ").append(entity.getEstatus()).append(", ");
            if(entity.getTvs() != null)
                query.append("tvs_contratadas = ").append(entity.getTvs()).append(", ");
            else
                query.append("tvs_contratadas = null, ");
            //query.append("fecha_proximo_pago = '").append(util.convertirDateTime2String(entity.getFechaProximoPago(), Constantes.FORMATO_FECHA_MYSQL)).append("', ");
            if(entity.getTipoServicioId() != null)
                query.append("id_tipo_servicio = ").append(entity.getTipoServicioId()).append(", ");
            else
                query.append("id_tipo_servicio = null, ");
            query.append("folio_placa = ").append(entity.getFolioPlaca()).append(", ");
            if(entity.getColorPlaca() != null)
                query.append("color_placa = '").append(entity.getColorPlaca()).append("', ");
            else
                query.append("color_placa = null, ");
            if(entity.getOnu() != null)
                query.append("onu = '").append(entity.getOnu()).append("', ");
            else
                query.append("onu = null, ");
            if(entity.getNap() != null)
                query.append("nap = '").append(entity.getNap()).append("', ");
            else
                query.append("nap = null, ");
            query.append("actualizacion = ").append(actualizacion);
            query.append(" where id_contrato = ").append(entity.getId());
            System.out.println("update contrato: "+query.toString());
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar contrato: "+sw.toString());
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
    public void registrarContrato(ContratoEntity entity) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into contratos (id_contrato, folio_contrato, id_estatus, tvs_contratadas, fecha_proximo_pago, "
                    + "id_tipo_servicio, folio_placa, color_placa, onu, id_usuario, "
                    + "dia_primer_pago, mes_primer_pago, anio_primer_pago, nap, numero_caja ) values (");
            query.append(entity.getId()).append(",");
            query.append(entity.getFolioContrato()).append(",");
            query.append(entity.getEstatus()).append(",");
            query.append(entity.getTvs()).append(",'");
            query.append(entity.getFechaProximoPago()).append("',");
            query.append(entity.getTipoServicioId()).append(",");
            query.append(entity.getFolioPlaca()).append(",'");
            query.append(entity.getColorPlaca()).append("','");
            query.append(entity.getOnu()).append("',");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getPrimerDiaPago()).append(",");
            query.append(entity.getPrimerMesPago()).append(",");
            query.append(entity.getPrimerAnioPago()).append(",");
            if(entity.getNap() != null)
                query.append("'").append(entity.getNap()).append("', ");
            else
                query.append("null, ");
            query.append(entity.getNumeroCaja()).append(")");
   
            logger.info("query insert contrato: "+query);
            stmt.executeUpdate(query.toString());
            query.delete(0, query.length());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar contratos: " + sw.toString());
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
     * @param contratoId
     * @param estatus 
     */
    public void actualizarEstatus(Long contratoId, Long estatus) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update contratos set id_estatus = ");
            query.append(estatus).append(", ");
            query.append("actualizacion = 1 ");
            query.append(" where id_contrato = ");
            query.append(contratoId);
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar estatus de contrato: " + sw.toString());
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
     * @param contratoId
     * @param estatus 
     */
    public void actualizarNumeroTvs(Long contratoId, Integer tvs) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update contratos set tvs_contratadas = ").append(tvs).append(", ");
            query.append("actualizacion = 1 ");
            query.append(" where id_contrato = ");
            query.append(contratoId);
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar tvs en contrato: " + sw.toString());
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
     * @param contratoId
     * @param fechaPago 
     */
    public void actualizarFechaPagoContrato(Long contratoId, String fechaPago) {

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("update contratos set fecha_proximo_pago = '");
            query.append(fechaPago);
            query.append("', actualizacion = 1  where id_contrato = ");
            query.append(contratoId);
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al actualizar fecha de pago de contrato: " + sw.toString());
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
