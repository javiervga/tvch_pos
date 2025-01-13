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

    private Long sucursalId;

    private Long estatusContratoId;

    private Long estatusSuscriptorId;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private Long contrato;

    private Long contratoAnterior;

    private String domicilio;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public Long getContrato() {
        return contrato;
    }

    public void setContrato(Long contrato) {
        this.contrato = contrato;
    }

    public Long getContratoAnterior() {
        return contratoAnterior;
    }

    public void setContratoAnterior(Long contratoAnterior) {
        this.contratoAnterior = contratoAnterior;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

}
