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
public class CobroProvisionalEntity {

    private Long cobroId;

    private Long cobroServerId;

    private Long contratoId;

    private Long folioContrato;

    private String suscriptor;
    
    private String domicilio;
    
    private String servicio;

    private String telefono;

    private String tipoOrden;
    
    private String tipoOrdenServicio;

    private String observaciones;

    private Date fecha;

    private Double monto;

    private Long usuarioId;

    private Long cajaId;

    private Integer estatus;

    public Long getCobroId() {
        return cobroId;
    }

    public void setCobroId(Long cobroId) {
        this.cobroId = cobroId;
    }

    public Long getCobroServerId() {
        return cobroServerId;
    }

    public void setCobroServerId(Long cobroServerId) {
        this.cobroServerId = cobroServerId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getFolioContrato() {
        return folioContrato;
    }

    public void setFolioContrato(Long folioContrato) {
        this.folioContrato = folioContrato;
    }

    public String getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(String suscriptor) {
        this.suscriptor = suscriptor;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoOrden() {
        return tipoOrden;
    }

    public void setTipoOrden(String tipoOrden) {
        this.tipoOrden = tipoOrden;
    }

    public String getTipoOrdenServicio() {
        return tipoOrdenServicio;
    }

    public void setTipoOrdenServicio(String tipoOrdenServicio) {
        this.tipoOrdenServicio = tipoOrdenServicio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getCajaId() {
        return cajaId;
    }

    public void setCajaId(Long cajaId) {
        this.cajaId = cajaId;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

}
