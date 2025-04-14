/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class Orden {

    private Long id;

    private Long contratoId;

    private Integer tipoOrdenId;

    private String tipoOrden;

    private Double costo;

    private String fechaRegistro;

    private Long promocionId;
    
    private Double costoPromocion;
    
    private String descripcionPromocion;

    private Long tipoDescuentoId;

    private Double importeDescuento;

    private String motivoDescuento;

    private Double importePagar;
    
    private String fechaProximoPago;
    
    private Long servicioId;
    
    private String servicio;
    
    private Integer mesesGratisPromocion;
    
    private String conceptoOrdenServicio;

    public Orden(Long id, Long contratoId, Integer tipoOrdenId, String tipoOrden,
            Double costo, String fechaRegistro, Double importePagar) {
        this.id = id;
        this.contratoId = contratoId;
        this.tipoOrdenId = tipoOrdenId;
        this.tipoOrden = tipoOrden;
        this.costo = costo;
        this.fechaRegistro = fechaRegistro;
        this.importePagar = importePagar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public String getTipoOrden() {
        return tipoOrden;
    }

    public void setTipoOrden(String tipoOrden) {
        this.tipoOrden = tipoOrden;
    }

    public Integer getTipoOrdenId() {
        return tipoOrdenId;
    }

    public void setTipoOrdenId(Integer tipoOrdenId) {
        this.tipoOrdenId = tipoOrdenId;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getPromocionId() {
        return promocionId;
    }

    public void setPromocionId(Long promocionId) {
        this.promocionId = promocionId;
    }

    public Double getCostoPromocion() {
        return costoPromocion;
    }

    public void setCostoPromocion(Double costoPromocion) {
        this.costoPromocion = costoPromocion;
    }

    public String getDescripcionPromocion() {
        return descripcionPromocion;
    }

    public void setDescripcionPromocion(String descripcionPromocion) {
        this.descripcionPromocion = descripcionPromocion;
    }

    public Long getTipoDescuentoId() {
        return tipoDescuentoId;
    }

    public void setTipoDescuentoId(Long tipoDescuentoId) {
        this.tipoDescuentoId = tipoDescuentoId;
    }

    public Double getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(Double importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public String getMotivoDescuento() {
        return motivoDescuento;
    }

    public void setMotivoDescuento(String motivoDescuento) {
        this.motivoDescuento = motivoDescuento;
    }

    public Double getImportePagar() {
        return importePagar;
    }

    public void setImportePagar(Double importePagar) {
        this.importePagar = importePagar;
    }

    public String getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(String fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public Integer getMesesGratisPromocion() {
        return mesesGratisPromocion;
    }

    public void setMesesGratisPromocion(Integer mesesGratisPromocion) {
        this.mesesGratisPromocion = mesesGratisPromocion;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getConceptoOrdenServicio() {
        return conceptoOrdenServicio;
    }

    public void setConceptoOrdenServicio(String conceptoOrdenServicio) {
        this.conceptoOrdenServicio = conceptoOrdenServicio;
    }

}
