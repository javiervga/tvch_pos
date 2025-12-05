/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class OrdenAgregadaPago {

    private Integer numeroOrden;

    private Long contratoId;

    private Long tipoOrden;

    private String tipoOrdenDesc;

    private Long tipoOrdenServicio;

    private String tipoOrdenServicioDesc;

    private Long servicioId;

    private Long nuevoServicioId;

    private String observaciones;

    private Long suscriptorId;

    private Long usuarioId;

    private Long domicilioId;

    private Integer tvs;

    private Integer tvsAdicionales;

    private Double costo;

    private String calle;

    private String numeroCalle;

    private String colonia;

    private String ciudad;

    private String calle1;

    private String calle2;

    private String referencia;

    private Long sucursalId;

    public Integer getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(Integer numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public Long getTipoOrden() {
        return tipoOrden;
    }

    public void setTipoOrden(Long tipoOrden) {
        this.tipoOrden = tipoOrden;
    }

    public String getTipoOrdenDesc() {
        return tipoOrdenDesc;
    }

    public void setTipoOrdenDesc(String tipoOrdenDesc) {
        this.tipoOrdenDesc = tipoOrdenDesc;
    }

    public Long getTipoOrdenServicio() {
        return tipoOrdenServicio;
    }

    public void setTipoOrdenServicio(Long tipoOrdenServicio) {
        this.tipoOrdenServicio = tipoOrdenServicio;
    }

    public String getTipoOrdenServicioDesc() {
        return tipoOrdenServicioDesc;
    }

    public void setTipoOrdenServicioDesc(String tipoOrdenServicioDesc) {
        this.tipoOrdenServicioDesc = tipoOrdenServicioDesc;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getTvsAdicionales() {
        return tvsAdicionales;
    }

    public void setTvsAdicionales(Integer tvsAdicionales) {
        this.tvsAdicionales = tvsAdicionales;
    }

    public Long getNuevoServicioId() {
        return nuevoServicioId;
    }

    public void setNuevoServicioId(Long nuevoServicioId) {
        this.nuevoServicioId = nuevoServicioId;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
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

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCalle1() {
        return calle1;
    }

    public void setCalle1(String calle1) {
        this.calle1 = calle1;
    }

    public String getCalle2() {
        return calle2;
    }

    public void setCalle2(String calle2) {
        this.calle2 = calle2;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getSuscriptorId() {
        return suscriptorId;
    }

    public void setSuscriptorId(Long suscriptorId) {
        this.suscriptorId = suscriptorId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

}
