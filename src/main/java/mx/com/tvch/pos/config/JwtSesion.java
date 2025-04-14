/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.config;

import java.util.List;

/**
 *
 * @author fvega
 */
public class JwtSesion {

    private static JwtSesion sesion;

    public static JwtSesion getJwtSesion() {
        if (sesion == null) {
            sesion = new JwtSesion();
        }
        return sesion;
    }

    private String token;

    private List<String> perfiles;

    private String usuario;

    private Long usuarioId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getPerfiles() {
        return perfiles;
    }

    public void setPerfiles(List<String> perfiles) {
        this.perfiles = perfiles;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

}
