/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model.client;

/**
 *
 * @author fvega
 */
public class ListSuscriptoresRequest {

    private Integer tipoBusqueda;

    private Long sucursalId;

    private Long estatusContratoId;

    private Long estatusSuscriptorId;

    private String valor;

    public Integer getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(Integer tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Long getEstatusContratoId() {
        return estatusContratoId;
    }

    public void setEstatusContratoId(Long estatusContratoId) {
        this.estatusContratoId = estatusContratoId;
    }

    public Long getEstatusSuscriptorId() {
        return estatusSuscriptorId;
    }

    public void setEstatusSuscriptorId(Long estatusSuscriptorId) {
        this.estatusSuscriptorId = estatusSuscriptorId;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
