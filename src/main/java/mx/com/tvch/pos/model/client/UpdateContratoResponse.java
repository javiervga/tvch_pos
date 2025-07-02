/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model.client;

/**
 *
 * @author fvega
 */
public class UpdateContratoResponse {

    private Long contratoId;

    private Long folioContrato;

    private String estatus;

    private String fechaRegistro;

    private String usuario;

    private Integer tvsContratadas;

    private String fechaProximoPago;

    private String tipoServicioInternet;

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getFolioContrato() {
        return folioContrato;
    }

    public void setFolioContrato(Long folioContrato) {
        this.folioContrato = folioContrato;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getTvsContratadas() {
        return tvsContratadas;
    }

    public void setTvsContratadas(Integer tvsContratadas) {
        this.tvsContratadas = tvsContratadas;
    }

    public String getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(String fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public String getTipoServicioInternet() {
        return tipoServicioInternet;
    }

    public void setTipoServicioInternet(String tipoServicioInternet) {
        this.tipoServicioInternet = tipoServicioInternet;
    }

}
