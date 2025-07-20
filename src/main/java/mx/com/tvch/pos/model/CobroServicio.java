/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

import java.util.Date;

/**
 *
 * @author fvega
 */
public class CobroServicio {

    private String concepto;

    private String cadenaMonto;

    private Double montoServicio;

    private Double montoRecargo;
    
    private Double montoSugerido;

    private Double montoTotal;

    private boolean seCobraRecargo;

    private Date fechaProximoPago;
    
    private String fechaProximoPagoTicket;

    private Integer mesesPagados;

    private String observaciones;

    private DescuentoCobro descuento;

    private PromocionCobro promocion;

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getCadenaMonto() {
        return cadenaMonto;
    }

    public void setCadenaMonto(String cadenaMonto) {
        this.cadenaMonto = cadenaMonto;
    }

    public Double getMontoServicio() {
        return montoServicio;
    }

    public void setMontoServicio(Double montoServicio) {
        this.montoServicio = montoServicio;
    }

    public Double getMontoRecargo() {
        return montoRecargo;
    }

    public void setMontoRecargo(Double montoRecargo) {
        this.montoRecargo = montoRecargo;
    }

    public Double getMontoSugerido() {
        return montoSugerido;
    }

    public void setMontoSugerido(Double montoSugerido) {
        this.montoSugerido = montoSugerido;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public boolean isSeCobraRecargo() {
        return seCobraRecargo;
    }

    public void setSeCobraRecargo(boolean seCobraRecargo) {
        this.seCobraRecargo = seCobraRecargo;
    }

    public Date getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(Date fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public String getFechaProximoPagoTicket() {
        return fechaProximoPagoTicket;
    }

    public void setFechaProximoPagoTicket(String fechaProximoPagoTicket) {
        this.fechaProximoPagoTicket = fechaProximoPagoTicket;
    }

    public Integer getMesesPagados() {
        return mesesPagados;
    }

    public void setMesesPagados(Integer mesesPagados) {
        this.mesesPagados = mesesPagados;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public DescuentoCobro getDescuento() {
        return descuento;
    }

    public void setDescuento(DescuentoCobro descuento) {
        this.descuento = descuento;
    }

    public PromocionCobro getPromocion() {
        return promocion;
    }

    public void setPromocion(PromocionCobro promocion) {
        this.promocion = promocion;
    }

}
