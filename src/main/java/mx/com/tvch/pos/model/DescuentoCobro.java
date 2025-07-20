/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class DescuentoCobro {

    private Long tipoDescuentoId;

    private String motivoDescuento;

    private Double montoDescuento;

    public Long getTipoDescuentoId() {
        return tipoDescuentoId;
    }

    public void setTipoDescuentoId(Long tipoDescuentoId) {
        this.tipoDescuentoId = tipoDescuentoId;
    }

    public String getMotivoDescuento() {
        return motivoDescuento;
    }

    public void setMotivoDescuento(String motivoDescuento) {
        this.motivoDescuento = motivoDescuento;
    }

    public Double getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(Double montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

}
