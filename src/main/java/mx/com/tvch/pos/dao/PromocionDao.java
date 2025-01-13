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
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.PromocionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class PromocionDao {
    
    private static PromocionDao dao;
    
    Logger logger = LoggerFactory.getLogger(PromocionDao.class);
    
    public static PromocionDao getPromocionDao(){
        if(dao == null)
            dao = new PromocionDao();
        return dao;
    }
    
    /**
     * 
     * @param servicioId
     * @return
     * @throws Exception 
     */
    public List<PromocionEntity> obtenerPromocionesActivas(Long servicioId) throws Exception{

        List<PromocionEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_promocion, descripcion, id_sucursal, id_servicio, costo_promocion, meses_pagados, meses_gratis, estatus FROM promociones WHERE id_servicio =");
            query.append(servicioId);
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                PromocionEntity entity = new PromocionEntity();
                entity.setCostoPromocion(rs.getDouble("costo_promocion"));
                entity.setDescripcion(rs.getString("descripcion"));
                entity.setEstatus(rs.getInt("estatus"));
                entity.setMesesGratis(rs.getInt("meses_gratis"));
                entity.setMesesPagados(rs.getInt("meses_pagados"));
                entity.setPromocionId(rs.getLong("id_promocion"));
                entity.setServicioId(rs.getLong("id_servicio"));
                entity.setSucursalId(rs.getLong("id_sucursal"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar promociones en bd: \n" + sw.toString());
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
