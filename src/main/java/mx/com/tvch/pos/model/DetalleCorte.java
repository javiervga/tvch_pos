/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class DetalleCorte {
    
    private Integer tipoDetalle;
    
    private String concepto;
    
    private Integer cantidad;
    
    private Double monto;
    
    private String montoCadena;

    public Integer getTipoDetalle() {
        return tipoDetalle;
    }

    public void setTipoDetalle(Integer tipoDetalle) {
        this.tipoDetalle = tipoDetalle;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getMontoCadena() {
        return montoCadena;
    }

    public void setMontoCadena(String montoCadena) {
        this.montoCadena = montoCadena;
    }
    
}
