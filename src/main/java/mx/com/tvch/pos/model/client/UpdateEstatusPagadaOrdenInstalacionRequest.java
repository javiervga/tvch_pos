/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model.client;

/**
 *
 * @author fvega
 */
public class UpdateEstatusPagadaOrdenInstalacionRequest {

    private Long ordenInstalacionId;

    private String fechaProximoPago;

    public Long getOrdenInstalacionId() {
        return ordenInstalacionId;
    }

    public void setOrdenInstalacionId(Long ordenInstalacionId) {
        this.ordenInstalacionId = ordenInstalacionId;
    }

    public String getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(String fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

}
