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
public class RecuperacionContratoEntity {

    private Long recuperacionId;

    private Long recuperacionServerId;

    private Long usuarioId;

    private Long contratoId;

    private Date fechaRecuperacion;

    private Long sucursalId;

    private Double costo;

    private String observaciones;

    private String nuevaFechaPago;

    public Long getRecuperacionId() {
        return recuperacionId;
    }

    public void setRecuperacionId(Long recuperacionId) {
        this.recuperacionId = recuperacionId;
    }

    public Long getRecuperacionServerId() {
        return recuperacionServerId;
    }

    public void setRecuperacionServerId(Long recuperacionServerId) {
        this.recuperacionServerId = recuperacionServerId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Date getFechaRecuperacion() {
        return fechaRecuperacion;
    }

    public void setFechaRecuperacion(Date fechaRecuperacion) {
        this.fechaRecuperacion = fechaRecuperacion;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNuevaFechaPago() {
        return nuevaFechaPago;
    }

    public void setNuevaFechaPago(String nuevaFechaPago) {
        this.nuevaFechaPago = nuevaFechaPago;
    }

}
