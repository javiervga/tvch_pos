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
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.entity.CorteCajaEntity;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CorteCajaDao {
    
    private static CorteCajaDao dao;
    
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(CorteCajaDao.class);
    
    public static  CorteCajaDao getCorteCajaDao(){
        if(dao == null)
            dao = new CorteCajaDao();
        return dao;
    }
    
    public CorteCajaDao(){
        utilerias = Utilerias.getUtilerias();
    }
    
    
    /**
     * 
     * @param entity
     * @return
     * @throws Exception 
     */
    public Long registrarCorteCaja(CorteCajaEntity entity) throws Exception{

        Long id = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            
            String query = "insert into cortes_caja (id_corte_caja, id_apertura_caja, id_usuario, id_sucursal, "
                    + "fondo_fijo, cantidad_cobros, total_cobros, cantidad_descuentos, total_descuentos, cantidad_salidas, "
                    + "total_salidas, cantidad_ingresos, total_ingresos, promociones_aplicadas,  total_solicitado, total_entregado, hora_corte) "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getCorteCajaId());
            ps.setLong(2, entity.getAperturaCajaId());
            ps.setLong(3, entity.getUsuarioId());
            ps.setLong(4, entity.getSucursalId());
            ps.setDouble(5, entity.getFondoFijo());
            ps.setInt(6, entity.getCantidadCobros());
            ps.setDouble(7, entity.getTotalCobros());
            ps.setInt(8, entity.getCantidadDescuentos());
            ps.setDouble(9, entity.getTotalDescuentos());
            ps.setInt(10, entity.getCantidadSalidas());
            ps.setDouble(11, entity.getTotalSalidas());
            ps.setInt(12, entity.getCantidadIngresos());
            ps.setDouble(13, entity.getTotalIngresos());
            ps.setInt(14, entity.getPromocionesAplicadas());
            ps.setDouble(15, entity.getTotalSolicitado());
            ps.setDouble(16, entity.getTotalEntregado());
            ps.setString(17, entity.getFechaCorte());
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
    
    /*public void registrarCorteCaja(CorteCajaEntity entity) throws Exception {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("insert into cortes_caja (id_corte_caja, id_corte_caja_server, id_apertura_caja, id_usuario, id_sucursal, "
                    + "fondo_fijo, total_cobros, total_descuentos, total_salidas, total_solicitado, total_entregado, hora_corte) values (");
            query.append(entity.getCorteCajaId()).append(",");
            query.append("null,");
            query.append(entity.getAperturaCajaId()).append(",");
            query.append(entity.getUsuarioId()).append(",");
            query.append(entity.getSucursalId()).append(",");
            query.append(entity.getFondoFijo()).append(",");
            query.append(entity.getTotalCobros()).append(",");
            query.append(entity.getTotalDescuentos()).append(",");
            query.append(entity.getTotalSalidas()).append(",");
            query.append(entity.getTotalSolicitado()).append(",");
            query.append(entity.getTotalEntregado()).append(",");
            query.append(utilerias.obtenerFechaFormatoMysql()).append("',");
            query.append(Constantes.ESTATUS_ACTIVO).append(")");
            stmt.executeUpdate(query.toString());

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al registrar apertura de caja: " + sw.toString());
            throw new Exception("Ocurrió un error al registrar su apertura de caja. Por favor reintente.");
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
        
    }*/
    
}
