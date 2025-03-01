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
import mx.com.tvch.pos.util.Constantes;

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

        UsuarioEntity entity = usuarioDao.autenticarUsuario(usuario, password);
        if(entity.getEstatus() == Constantes.ESTATUS_USUARIO_BLOQUEADO)
            throw new Exception("Su usuario esta bloqueado. Contacte a Soporte Técnico");
        if(entity.getEstatus() == Constantes.ESTATUS_USUARIO_INACTIVO)
            throw new Exception("El usuario ha sido dado de baja");
            
        return entity; 

    }
    
    public CajaEntity consultarCaja() throws  Exception {
        
        return cajaDao.obtenerCaja();
    }
    
    public SucursalEntity consultarSucursal() throws Exception {
        
        return sucursalDao.obtenerSucursal();
    }

}
