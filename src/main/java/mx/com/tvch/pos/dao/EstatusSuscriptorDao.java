
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
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.TipoSalidaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class EstatusSuscriptorDao {
    
    private static EstatusSuscriptorDao dao;
    
    Logger logger = LoggerFactory.getLogger(EstatusSuscriptorDao.class);
    
    public static EstatusSuscriptorDao getEstatusSuscriptorDao(){
        if(dao == null)
            dao = new EstatusSuscriptorDao();
        return dao;
    }
    
    /**
     * 
     * @return 
     */
    public List<EstatusSuscriptorEntity> obtenerEstatusSuscriptor() {

        List<EstatusSuscriptorEntity> list = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;

        try {
            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("select * from estatus_suscriptor");
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                EstatusSuscriptorEntity entity = new EstatusSuscriptorEntity();
                entity.setEstatusId(rs.getLong("id_estatus"));
                entity.setDescripcion(rs.getString("descripcion"));
                list.add(entity);
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al obtener estatus de suscriptor en bd: \n" + sw.toString());
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
