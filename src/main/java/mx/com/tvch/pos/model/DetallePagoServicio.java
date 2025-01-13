/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class DetallePagoServicio {

    private Integer tipoDetalle;

    private String concepto;

    private String cadenaMonto;

    private Double monto;

    private String motivoDescuento;

    private Long promocionId;

    private Long tipoDescuentoId;

    private String fechaProximoPago;

    private Integer mesesGratis;

    public Integer getTipoDetalle() {
        return tipoDetalle;
    }

    public void setTipoDetalle(Integer tipoDetalle) {
        this.tipoDetalle = tipoDetalle;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getCadenaMonto() {
        return cadenaMonto;
    }

    public void setCadenaMonto(String cadenaMonto) {
        this.cadenaMonto = cadenaMonto;
    }

    public String getMotivoDescuento() {
        return motivoDescuento;
    }

    public void setMotivoDescuento(String motivoDescuento) {
        this.motivoDescuento = motivoDescuento;
    }

    public Long getPromocionId() {
        return promocionId;
    }

    public void setPromocionId(Long promocionId) {
        this.promocionId = promocionId;
    }

    public Long getTipoDescuentoId() {
        return tipoDescuentoId;
    }

    public void setTipoDescuentoId(Long tipoDescuentoId) {
        this.tipoDescuentoId = tipoDescuentoId;
    }

    public String getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(String fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public Integer getMesesGratis() {
        return mesesGratis;
    }

    public void setMesesGratis(Integer mesesGratis) {
        this.mesesGratis = mesesGratis;
    }

}
