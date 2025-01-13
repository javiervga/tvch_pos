/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model.client;

/**
 *
 * @author fvega
 */
public class PromocionOrdenInstalacion {

    private Long id;

    private String descripcion;

    private Long sucursalId;

    private String sucursal;

    private Long servicioId;

    private String servicio;

    private Double costoPromocion;

    private Integer tvsContratadas;

    private Integer mesesGratis;

    private Integer estatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
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

    public Double getCostoPromocion() {
        return costoPromocion;
    }

    public void setCostoPromocion(Double costoPromocion) {
        this.costoPromocion = costoPromocion;
    }

    public Integer getTvsContratadas() {
        return tvsContratadas;
    }

    public void setTvsContratadas(Integer tvsContratadas) {
        this.tvsContratadas = tvsContratadas;
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
        return descripcion;
    }

}
