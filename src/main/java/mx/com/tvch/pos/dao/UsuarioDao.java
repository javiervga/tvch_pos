/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.naming.AuthenticationException;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.entity.UsuarioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author fvega
 */
public class UsuarioDao {

    private static UsuarioDao usuarioDao;

    Logger logger = LoggerFactory.getLogger(UsuarioDao.class);

    public static UsuarioDao getUsuarioDao() {
        if (usuarioDao == null) {
            usuarioDao = new UsuarioDao();
        }
        return usuarioDao;
    }

    public UsuarioEntity autenticarUsuario(String usuario, String password) throws Exception {

        Connection conn = null;
        Statement stmt = null;

        try {

            DbConfig dbConfig = DbConfig.getdDbConfig();
            conn = dbConfig.getConnection();
            stmt = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("select * from usuarios where usuario = \'").append(usuario).append("\'");
            ResultSet rs = stmt.executeQuery(query.toString());
            List<UsuarioEntity> list = new ArrayList<>();
            while (rs.next()) {
                UsuarioEntity entity = new UsuarioEntity();
                entity.setApellidoMaterno(rs.getString("ap_materno"));
                entity.setApellidoPaterno(rs.getString("ap_paterno"));
                entity.setEstatus(rs.getLong("id_estatus"));
                entity.setNombre(rs.getString("nombre"));
                entity.setPassword(rs.getString("password"));
                entity.setUsuario(rs.getString("usuario"));
                entity.setUsuarioId(rs.getLong("id_usuario"));
                list.add(entity);
            }

            if (list.size() == 0) {
                throw new NoSuchElementException();
            } else if (list.size() > 1) {
                throw new Exception("Existe mas de un registros con el mismo usuario. Por favor contacte a soporte.");
            } else {
                
                //validar las credenciales
                if(validarPassword(password, list.get(0).getPassword()))
                    return list.get(0);
                else
                    throw new AuthenticationException();
            }

        } catch (AuthenticationException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error de autenticacion de usuario: \n" + sw.toString());
            throw new Exception("Usuario o contraseña incorrectos. Por favor, verifique.");
        } catch (NoSuchElementException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("usuario no encontrado en vd: \n" + sw.toString());
            throw new Exception("Usuario no registrado.");
        }catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al obtener usuario en bd: \n" + sw.toString());
            throw new Exception("Error al validar usuario. Por favor intente de nuevo.");
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

    private boolean validarPassword(String passwordRecibido, String passwordEncriptadoDB) throws NoSuchAlgorithmException {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(passwordRecibido, passwordEncriptadoDB);

    }

}
