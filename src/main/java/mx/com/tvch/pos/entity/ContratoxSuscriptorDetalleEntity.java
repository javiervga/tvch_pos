/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

import java.util.Date;
import java.util.List;
import mx.com.tvch.pos.model.OrdenAgregadaPago;

/**
 *
 * @author fvega
 */
public class ContratoxSuscriptorDetalleEntity {

    private Long contratoId;

    private Long folioContrato;

    private Long estatusContratoId;

    private String estatusContrato;

    private Integer tvsContratadas;

    private Date fechaProximoPago;
    
    private Date fechaRegistroContrato;

    private Long susucriptorId;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private String telefono;

    private Long estatusSuscriptorId;

    private Long domicilioId;

    private String colonia;

    private String calle;

    private String numeroCalle;

    private String referencia;

    private Long servicioId;

    private String servicio;

    private Double costoServicio;

    private Integer mesesGratis;

    private Integer mesesPorPagar;

    private Long tipoServicioInternet;

    private Long folioPlaca;

    private String colorPlaca;

    private String onu;
    
    private Long onuId;

    private String ciudad;

    private String calle1;

    private String calle2;

    private Integer estatusDomicilioId;

    private String nap;
    
    private List<OrdenAgregadaPago> ordenesPago;

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

    public Long getEstatusContratoId() {
        return estatusContratoId;
    }

    public void setEstatusContratoId(Long estatusContratoId) {
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

    public Date getFechaRegistroContrato() {
        return fechaRegistroContrato;
    }

    public void setFechaRegistroContrato(Date fechaRegistroContrato) {
        this.fechaRegistroContrato = fechaRegistroContrato;
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

    public Long getEstatusSuscriptorId() {
        return estatusSuscriptorId;
    }

    public void setEstatusSuscriptorId(Long estatusSuscriptorId) {
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

    public Integer getMesesGratis() {
        return mesesGratis;
    }

    public void setMesesGratis(Integer mesesGratis) {
        this.mesesGratis = mesesGratis;
    }

    public Integer getMesesPorPagar() {
        return mesesPorPagar;
    }

    public void setMesesPorPagar(Integer mesesPorPagar) {
        this.mesesPorPagar = mesesPorPagar;
    }

    public Long getTipoServicioInternet() {
        return tipoServicioInternet;
    }

    public void setTipoServicioInternet(Long tipoServicioInternet) {
        this.tipoServicioInternet = tipoServicioInternet;
    }

    public Long getFolioPlaca() {
        return folioPlaca;
    }

    public void setFolioPlaca(Long folioPlaca) {
        this.folioPlaca = folioPlaca;
    }

    public String getColorPlaca() {
        return colorPlaca;
    }

    public void setColorPlaca(String colorPlaca) {
        this.colorPlaca = colorPlaca;
    }

    public String getOnu() {
        return onu;
    }

    public void setOnu(String onu) {
        this.onu = onu;
    }

    public Long getOnuId() {
        return onuId;
    }

    public void setOnuId(Long onuId) {
        this.onuId = onuId;
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

    public Integer getEstatusDomicilioId() {
        return estatusDomicilioId;
    }

    public void setEstatusDomicilioId(Integer estatusDomicilioId) {
        this.estatusDomicilioId = estatusDomicilioId;
    }

    public String getNap() {
        return nap;
    }

    public void setNap(String nap) {
        this.nap = nap;
    }

    public List<OrdenAgregadaPago> getOrdenesPago() {
        return ordenesPago;
    }

    public void setOrdenesPago(List<OrdenAgregadaPago> ordenesPago) {
        this.ordenesPago = ordenesPago;
    }

}
