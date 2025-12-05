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
public class CambioServicioEntity {

    private Long cambioId;

    private Long cambioServerId;

    private Long contratoId;

    private Long servicioActualId;

    private Long servicioNuevoId;

    private Date fechaCambio;

    private Double costo;

    private Long sucursalId;

    private Long usuarioId;

    private Long ordenServicioId;

    public Long getCambioId() {
        return cambioId;
    }

    public void setCambioId(Long cambioId) {
        this.cambioId = cambioId;
    }

    public Long getCambioServerId() {
        return cambioServerId;
    }

    public void setCambioServerId(Long cambioServerId) {
        this.cambioServerId = cambioServerId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getServicioActualId() {
        return servicioActualId;
    }

    public void setServicioActualId(Long servicioActualId) {
        this.servicioActualId = servicioActualId;
    }

    public Long getServicioNuevoId() {
        return servicioNuevoId;
    }

    public void setServicioNuevoId(Long servicioNuevoId) {
        this.servicioNuevoId = servicioNuevoId;
    }

    public Date getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Date fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getOrdenServicioId() {
        return ordenServicioId;
    }

    public void setOrdenServicioId(Long ordenServicioId) {
        this.ordenServicioId = ordenServicioId;
    }

}
