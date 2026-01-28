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
import mx.com.tvch.pos.entity.TipoFallaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class TipoFallaDao {

    private static TipoFallaDao dao;

    Logger logger = LoggerFactory.getLogger(TipoFallaDao.class);

    public static TipoFallaDao getTipoFallaDao() {
        if (dao == null) {
            dao = new TipoFallaDao();
        }
        return dao;
    }
    
    public List<TipoFallaEntity> obtenerTiposFalla() throws Exception{

        List<TipoFallaEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            
            query.append("SELECT id_tipo_falla , descripcion ");
            query.append("FROM tipos_falla");
            
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                TipoFallaEntity entity = new TipoFallaEntity();
                entity.setId(rs.getLong("id_tipo_falla"));
                entity.setDescripcion(rs.getString("descripcion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al consultar tipos de falla en bd: \n" + sw.toString());
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
