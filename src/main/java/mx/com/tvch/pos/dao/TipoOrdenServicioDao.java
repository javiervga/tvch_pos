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
import mx.com.tvch.pos.entity.TipoIngresoEntity;
import mx.com.tvch.pos.entity.TipoOrdenServicioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class TipoOrdenServicioDao {
    
    private static TipoOrdenServicioDao dao;
    
    Logger logger = LoggerFactory.getLogger(TipoOrdenServicioDao.class);

    public static TipoOrdenServicioDao getTipoOrdenServicioDao() {
        if (dao == null) {
            dao = new TipoOrdenServicioDao();
        }
        return dao;
    }
    
    public List<TipoOrdenServicioEntity> obtenerTiposOrdenServicio() {

        List<TipoOrdenServicioEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("select * from tipos_orden_servicio");
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                TipoOrdenServicioEntity entity = new TipoOrdenServicioEntity();
                entity.setTipoOrdenId(rs.getLong("id_tipo_orden"));
                entity.setDescripcion(rs.getString("descripcion"));
                entity.setCosto(rs.getDouble("costo"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al obtener tipos de ordenes de servicio en bd: \n" + sw.toString());
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
