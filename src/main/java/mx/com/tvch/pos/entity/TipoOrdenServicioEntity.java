/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class TipoOrdenServicioEntity {

    private Long tipoOrdenId;

    private String descripcion;

    private Double costo;

    public Long getTipoOrdenId() {
        return tipoOrdenId;
    }

    public void setTipoOrdenId(Long tipoOrdenId) {
        this.tipoOrdenId = tipoOrdenId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

}
