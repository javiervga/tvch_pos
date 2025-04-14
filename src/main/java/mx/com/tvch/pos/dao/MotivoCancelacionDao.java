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
import mx.com.tvch.pos.entity.MotivoCancelacionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class MotivoCancelacionDao {
    
    private static MotivoCancelacionDao dao;
   
    Logger logger = LoggerFactory.getLogger(MotivoCancelacionDao.class);
    
    public static MotivoCancelacionDao getMotivoCancelacionDao(){
        if(dao == null)
            dao = new MotivoCancelacionDao();
         return dao;
    }
    
    /**
     * 
     * @return 
     */
    public List<MotivoCancelacionEntity> obtenerMotivosCancelacion() {

        List<MotivoCancelacionEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("select * from motivos_cancelacion");
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                MotivoCancelacionEntity entity = new MotivoCancelacionEntity();
                entity.setMotivoId(rs.getLong("id_motivo"));
                entity.setDescripcion(rs.getString("descripcion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al obtener motivos de cancelacion en bd: \n" + sw.toString());
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
