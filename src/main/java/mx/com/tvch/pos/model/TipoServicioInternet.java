/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class TipoServicioInternet {

    private Long tipoServicioInternetId;

    private String descripcion;

    public TipoServicioInternet(Long tipoServicioInternetId, String descripcion) {
        this.tipoServicioInternetId = tipoServicioInternetId;
        this.descripcion = descripcion;
    }

    public Long getTipoServicioInternetId() {
        return tipoServicioInternetId;
    }

    public void setTipoServicioInternetId(Long tipoServicioInternetId) {
        this.tipoServicioInternetId = tipoServicioInternetId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }

}
