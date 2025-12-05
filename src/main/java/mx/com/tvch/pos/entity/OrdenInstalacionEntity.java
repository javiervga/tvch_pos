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
public class OrdenInstalacionEntity {

    private Long ordenId;

    private Long ordenServerId;

    private Long contratoId;

    private Long usuarioId;

    private Long suscriptorId;

    private Date fechaRegistro;

    private String observacionesRegistro;

    private Long estatusId;

    private Long domicilioId;

    private Integer tvs;

    private Long servicioId;

    private Double costo;

    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getOrdenServerId() {
        return ordenServerId;
    }

    public void setOrdenServerId(Long ordenServerId) {
        this.ordenServerId = ordenServerId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSuscriptorId() {
        return suscriptorId;
    }

    public void setSuscriptorId(Long suscriptorId) {
        this.suscriptorId = suscriptorId;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getObservacionesRegistro() {
        return observacionesRegistro;
    }

    public void setObservacionesRegistro(String observacionesRegistro) {
        this.observacionesRegistro = observacionesRegistro;
    }

    public Long getEstatusId() {
        return estatusId;
    }

    public void setEstatusId(Long estatusId) {
        this.estatusId = estatusId;
    }

    public Long getDomicilioId() {
        return domicilioId;
    }

    public void setDomicilioId(Long domicilioId) {
        this.domicilioId = domicilioId;
    }

    public Integer getTvs() {
        return tvs;
    }

    public void setTvs(Integer tvs) {
        this.tvs = tvs;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

}
