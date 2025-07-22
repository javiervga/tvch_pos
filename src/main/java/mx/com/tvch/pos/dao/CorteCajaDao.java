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
                    + "total_salidas, cantidad_ingresos, total_ingresos, promociones_aplicadas,  total_solicitado, total_entregado, fecha_corte) "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getCorteCajaId());
            ps.setLong(2, entity.getAperturaCajaId());
            ps.setLong(3, entity.getUsuarioId());
            ps.setLong(4, entity.getSucursalId());
            ps.setDouble(5, entity.getFondoFijo());
            if(entity.getCantidadCobros() != null){
                ps.setInt(6, entity.getCantidadCobros());
                ps.setDouble(7, entity.getTotalCobros());
            }else{
                ps.setInt(6, 0);
                ps.setDouble(7, 0.0);
            }
            if(entity.getCantidadDescuentos() != null){
                ps.setInt(8, entity.getCantidadDescuentos());
                ps.setDouble(9, entity.getTotalDescuentos());
            }else{
                ps.setInt(8, 0);
                ps.setDouble(9, 0.0);
            }
            if(entity.getCantidadSalidas() != null){
                ps.setInt(10, entity.getCantidadSalidas());
                ps.setDouble(11, entity.getTotalSalidas());
            }else{
                ps.setInt(10, 0);
                ps.setDouble(11, 0.0);
            }
            if(entity.getCantidadIngresos() != null){
                ps.setInt(12, entity.getCantidadIngresos());
                ps.setDouble(13, entity.getTotalIngresos());
            }else{
                ps.setInt(12, 0);
                ps.setDouble(13, 0.0);
            }
            if(entity.getPromocionesAplicadas() != null)
                ps.setInt(14, entity.getPromocionesAplicadas());
            else
                ps.setInt(14, 0);
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
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<CorteCajaEntity> obtenerCortesCaja() throws Exception{

        List<CorteCajaEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_corte_caja, id_apertura_caja, ");
            query.append("id_usuario, id_sucursal, fondo_fijo, cantidad_cobros, total_cobros, cantidad_descuentos, ");
            query.append("total_descuentos, cantidad_salidas, total_salidas, cantidad_ingresos, total_ingresos, ");
            query.append("promociones_aplicadas, total_solicitado, total_entregado, fecha_corte ");
            query.append("FROM cortes_caja WHERE id_corte_caja_server is null ");
            query.append("order by id_corte_caja asc");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                CorteCajaEntity entity = new CorteCajaEntity();
                entity.setAperturaCajaId(rs.getLong("id_apertura_caja"));
                entity.setCantidadCobros(rs.getInt("cantidad_cobros"));
                entity.setCantidadDescuentos(rs.getInt("cantidad_descuentos"));
                entity.setCantidadSalidas(rs.getInt("cantidad_salidas"));
                entity.setCorteCajaId(rs.getLong("id_corte_caja"));
                entity.setFechaCorte(rs.getString("fecha_corte"));
                entity.setFondoFijo(rs.getDouble("fondo_fijo"));
                entity.setPromocionesAplicadas(rs.getInt("promociones_aplicadas"));
                entity.setSucursalId(rs.getLong("id_sucursal"));
                entity.setTotalCobros(rs.getDouble("total_cobros"));
                entity.setTotalDescuentos(rs.getDouble("total_descuentos"));
                entity.setTotalEntregado(rs.getDouble("total_entregado"));
                entity.setTotalSalidas(rs.getDouble("total_salidas"));
                entity.setTotalSolicitado(rs.getDouble("total_solicitado"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                entity.setCantidadIngresos(rs.getInt("cantidad_ingresos"));
                entity.setTotalIngresos(rs.getDouble("total_ingresos"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar cortes de caja en bd: \n" + sw.toString());
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
