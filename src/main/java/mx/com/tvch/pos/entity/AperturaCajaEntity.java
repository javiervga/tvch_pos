/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

import java.util.Date;

/**
 *
 * @author fvega
 */
public class AperturaCajaEntity {

    private Long aperturaCajaId;
    
    private Long aperturaCajaServer;

    private Long cajaId;
    
    private Integer numeroCaja;

    private Long usuarioId;
    
    private String usuario;

    private Double fondoFijo;
    
    private Date fechaApertura;

    private Integer estatus;

    public Long getAperturaCajaId() {
        return aperturaCajaId;
    }

    public void setAperturaCajaId(Long aperturaCajaId) {
        this.aperturaCajaId = aperturaCajaId;
    }

    public Long getAperturaCajaServer() {
        return aperturaCajaServer;
    }

    public void setAperturaCajaServer(Long aperturaCajaServer) {
        this.aperturaCajaServer = aperturaCajaServer;
    }

    public Long getCajaId() {
        return cajaId;
    }

    public void setCajaId(Long cajaId) {
        this.cajaId = cajaId;
    }

    public Integer getNumeroCaja() {
        return numeroCaja;
    }

    public void setNumeroCaja(Integer numeroCaja) {
        this.numeroCaja = numeroCaja;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Double getFondoFijo() {
        return fondoFijo;
    }

    public void setFondoFijo(Double fondoFijo) {
        this.fondoFijo = fondoFijo;
    }

    public Date getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(Date horaApertura) {
        this.fechaApertura = horaApertura;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

}
