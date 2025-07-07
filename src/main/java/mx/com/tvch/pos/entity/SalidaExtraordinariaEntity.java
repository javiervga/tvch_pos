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
public class SalidaExtraordinariaEntity {
    
    private Long salidaExtraordinariaId;
    
    private Long salidaExtraordinariaServerId;

    private Long usuarioId;
    
    private String usuario;

    private String observaciones;

    private Double monto;
    
    private Date fechaSalida;
    
    private Long cajaId;

    public Long getSalidaExtraordinariaId() {
        return salidaExtraordinariaId;
    }

    public void setSalidaExtraordinariaId(Long salidaExtraordinariaId) {
        this.salidaExtraordinariaId = salidaExtraordinariaId;
    }

    public Long getSalidaExtraordinariaServerId() {
        return salidaExtraordinariaServerId;
    }

    public void setSalidaExtraordinariaServerId(Long salidaExtraordinariaServerId) {
        this.salidaExtraordinariaServerId = salidaExtraordinariaServerId;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Long getCajaId() {
        return cajaId;
    }

    public void setCajaId(Long cajaId) {
        this.cajaId = cajaId;
    }
}
