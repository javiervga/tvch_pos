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
public class ContratoxSuscriptorEntity {

    private Long contratoId;

    private Long contratoAnteriorId;

    private Integer estatusContratoId;
    
    private String estatusContrato;

    private Integer tvsContratadas;

    private Date fechaProximoPago;

    private Long susucriptorId;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private String telefono;

    private Integer estatusSuscriptorId;

    private Long domicilioId;

    private String colonia;

    private String calle;

    private String numeroCalle;

    private String referencia;
    
    private Long servicioId;
    
    private String servicio;
    
    private Double costoServicio;

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getContratoAnteriorId() {
        return contratoAnteriorId;
    }

    public void setContratoAnteriorId(Long contratoAnteriorId) {
        this.contratoAnteriorId = contratoAnteriorId;
    }

    public Integer getEstatusContratoId() {
        return estatusContratoId;
    }

    public void setEstatusContratoId(Integer estatusContratoId) {
        this.estatusContratoId = estatusContratoId;
    }

    public String getEstatusContrato() {
        return estatusContrato;
    }

    public void setEstatusContrato(String estatusContrato) {
        this.estatusContrato = estatusContrato;
    }

    public Integer getTvsContratadas() {
        return tvsContratadas;
    }

    public void setTvsContratadas(Integer tvsContratadas) {
        this.tvsContratadas = tvsContratadas;
    }

    public Date getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(Date fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public Long getSusucriptorId() {
        return susucriptorId;
    }

    public void setSusucriptorId(Long susucriptorId) {
        this.susucriptorId = susucriptorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getEstatusSuscriptorId() {
        return estatusSuscriptorId;
    }

    public void setEstatusSuscriptorId(Integer estatusSuscriptorId) {
        this.estatusSuscriptorId = estatusSuscriptorId;
    }

    public Long getDomicilioId() {
        return domicilioId;
    }

    public void setDomicilioId(Long domicilioId) {
        this.domicilioId = domicilioId;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumeroCalle() {
        return numeroCalle;
    }

    public void setNumeroCalle(String numeroCalle) {
        this.numeroCalle = numeroCalle;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }
    
    public Double getCostoServicio() {
        return costoServicio;
    }

    public void setCostoServicio(Double costoServicio) {
        this.costoServicio = costoServicio;
    }
   
}
