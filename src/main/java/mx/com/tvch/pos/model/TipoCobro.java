/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class TipoCobro {

    private Integer tipoCobroId;

    private String descripcion;

    public TipoCobro(Integer tipoCobroId, String descripcion) {
        this.tipoCobroId = tipoCobroId;
        this.descripcion = descripcion;
    }

    public Integer getTipoCobroId() {
        return tipoCobroId;
    }

    public void setTipoCobroId(Integer tipoCobroId) {
        this.tipoCobroId = tipoCobroId;
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
