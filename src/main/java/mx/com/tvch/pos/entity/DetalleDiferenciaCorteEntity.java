/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class DetalleDiferenciaCorteEntity {

    private Long diferenciaId;

    private Long diferenciaServerId;

    private Long corteCajaId;

    private Long tipoDiferenciaId;

    private Double monto;

    public Long getDiferenciaId() {
        return diferenciaId;
    }

    public void setDiferenciaId(Long diferenciaId) {
        this.diferenciaId = diferenciaId;
    }

    public Long getDiferenciaServerId() {
        return diferenciaServerId;
    }

    public void setDiferenciaServerId(Long diferenciaServerId) {
        this.diferenciaServerId = diferenciaServerId;
    }

    public Long getCorteCajaId() {
        return corteCajaId;
    }

    public void setCorteCajaId(Long corteCajaId) {
        this.corteCajaId = corteCajaId;
    }

    public Long getTipoDiferenciaId() {
        return tipoDiferenciaId;
    }

    public void setTipoDiferenciaId(Long tipoDiferenciaId) {
        this.tipoDiferenciaId = tipoDiferenciaId;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

}
