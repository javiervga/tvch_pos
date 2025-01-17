/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class IngresoCajaEntity {

    private Long ingresoCajaId;

    private Long ingresoCajaServerId;

    private Long aperturaCajaId;

    private Long tipoIngresoId;
    
    private String observaciones;

    private Double monto;

    public Long getIngresoCajaId() {
        return ingresoCajaId;
    }

    public void setIngresoCajaId(Long ingresoCajaId) {
        this.ingresoCajaId = ingresoCajaId;
    }

    public Long getIngresoCajaServerId() {
        return ingresoCajaServerId;
    }

    public void setIngresoCajaServerId(Long ingresoCajaServerId) {
        this.ingresoCajaServerId = ingresoCajaServerId;
    }

    public Long getAperturaCajaId() {
        return aperturaCajaId;
    }

    public void setAperturaCajaId(Long aperturaCajaId) {
        this.aperturaCajaId = aperturaCajaId;
    }

    public Long getTipoIngresoId() {
        return tipoIngresoId;
    }

    public void setTipoIngresoId(Long tipoIngresoId) {
        this.tipoIngresoId = tipoIngresoId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

}
