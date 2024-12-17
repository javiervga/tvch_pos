/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class SalidaCajaEntity {

    private Long salidaCajaId;

    private Long aperturaCajaId;

    private Long tipoSalidaId;

    private String observaciones;

    private Double monto;

    public Long getSalidaCajaId() {
        return salidaCajaId;
    }

    public void setSalidaCajaId(Long salidaCajaId) {
        this.salidaCajaId = salidaCajaId;
    }

    public Long getAperturaCajaId() {
        return aperturaCajaId;
    }

    public void setAperturaCajaId(Long aperturaCajaId) {
        this.aperturaCajaId = aperturaCajaId;
    }

    public Long getTipoSalidaId() {
        return tipoSalidaId;
    }

    public void setTipoSalidaId(Long tipoSalidaId) {
        this.tipoSalidaId = tipoSalidaId;
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
