/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import mx.com.tvch.pos.dao.CajaDao;
import mx.com.tvch.pos.dao.SucursalDao;
import mx.com.tvch.pos.dao.UsuarioDao;
import mx.com.tvch.pos.entity.CajaEntity;
import mx.com.tvch.pos.entity.SucursalEntity;
import mx.com.tvch.pos.entity.UsuarioEntity;

/**
 *
 * @author fvega
 */
public class LoginController {

    private static LoginController loginController;

    private final UsuarioDao usuarioDao;
    private final CajaDao cajaDao;
    private final SucursalDao sucursalDao;

    public static LoginController getLoginController() {
        if (loginController == null) {
            loginController = new LoginController();
        }
        return loginController;
    }

    public LoginController() {
        usuarioDao = UsuarioDao.getUsuarioDao();
        cajaDao = CajaDao.getCajaDao();
        sucursalDao = SucursalDao.getSucursalDao();
    }

    public UsuarioEntity autenticarUsuario(String usuario, String password) throws Exception {

        return usuarioDao.autenticarUsuario(usuario, password); 

    }
    
    public CajaEntity consultarCaja() throws  Exception {
        
        return cajaDao.obtenerCaja();
    }
    
    public SucursalEntity consultarSucursal() throws Exception {
        
        return sucursalDao.obtenerSucursal();
    }

}
