/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class PromocionEntity {

    private Long promocionId;

    private String descripcion;

    private Long sucursalId;

    private Long servicioId;

    private Double costoPromocion;

    private Integer mesesPagados;

    private Integer mesesGratis;

    private Integer estatus;

    public Long getPromocionId() {
        return promocionId;
    }

    public void setPromocionId(Long promocionId) {
        this.promocionId = promocionId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public Double getCostoPromocion() {
        return costoPromocion;
    }

    public void setCostoPromocion(Double costoPromocion) {
        this.costoPromocion = costoPromocion;
    }

    public Integer getMesesPagados() {
        return mesesPagados;
    }

    public void setMesesPagados(Integer mesesPagados) {
        this.mesesPagados = mesesPagados;
    }

    public Integer getMesesGratis() {
        return mesesGratis;
    }

    public void setMesesGratis(Integer mesesGratis) {
        this.mesesGratis = mesesGratis;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return descripcion ;
    }

}
