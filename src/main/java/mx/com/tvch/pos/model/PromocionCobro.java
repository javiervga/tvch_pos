/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class PromocionCobro {

    private Long promocionId;

    private Integer mesesGratis;

    private Double costoPromocion;
    
    private String descripcion;

    public Long getPromocionId() {
        return promocionId;
    }

    public void setPromocionId(Long promocionId) {
        this.promocionId = promocionId;
    }

    public Integer getMesesGratis() {
        return mesesGratis;
    }

    public void setMesesGratis(Integer mesesGratis) {
        this.mesesGratis = mesesGratis;
    }

    public Double getCostoPromocion() {
        return costoPromocion;
    }

    public void setCostoPromocion(Double costoPromocion) {
        this.costoPromocion = costoPromocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
