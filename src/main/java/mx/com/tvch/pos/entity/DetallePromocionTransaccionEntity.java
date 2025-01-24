/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class DetallePromocionTransaccionEntity {

    private Long detalleId;

    private Long transaccionId;

    private Long promocionId;

    private String descripcionPromocion;

    private Double costoPromocion;

    private Long tipoPromocionId;

    public Long getDetalleId() {
        return detalleId;
    }

    public void setDetalleId(Long detalleId) {
        this.detalleId = detalleId;
    }

    public Long getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(Long transaccionId) {
        this.transaccionId = transaccionId;
    }

    public Long getPromocionId() {
        return promocionId;
    }

    public void setPromocionId(Long promocionId) {
        this.promocionId = promocionId;
    }

    public String getDescripcionPromocion() {
        return descripcionPromocion;
    }

    public void setDescripcionPromocion(String descripcionPromocion) {
        this.descripcionPromocion = descripcionPromocion;
    }

    public Double getCostoPromocion() {
        return costoPromocion;
    }

    public void setCostoPromocion(Double costoPromocion) {
        this.costoPromocion = costoPromocion;
    }

    public Long getTipoPromocionId() {
        return tipoPromocionId;
    }

    public void setTipoPromocionId(Long tipoPromocionId) {
        this.tipoPromocionId = tipoPromocionId;
    }

}
