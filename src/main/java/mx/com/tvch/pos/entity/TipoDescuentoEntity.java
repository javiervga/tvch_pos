/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class TipoDescuentoEntity {

    private Long idTipoDescuento;

    private String descripcion;

    public Long getIdTipoDescuento() {
        return idTipoDescuento;
    }

    public void setIdTipoDescuento(Long idTipoDescuento) {
        this.idTipoDescuento = idTipoDescuento;
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
