/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class TipoOrden {

    private Integer tipoOrdenId;

    private String descripcion;

    public TipoOrden(Integer _tipoOrdenId, String _descripcion) {
        this.tipoOrdenId = _tipoOrdenId;
        this.descripcion = _descripcion;
    }

    public Integer getTipoOrdenId() {
        return tipoOrdenId;
    }

    public void setTipoOrdenId(Integer tipoOrdenId) {
        this.tipoOrdenId = tipoOrdenId;
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
