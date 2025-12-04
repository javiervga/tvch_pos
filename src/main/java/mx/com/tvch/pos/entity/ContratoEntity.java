/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class ContratoEntity {

    private Long id;
    private Long serverId;
    private Long folioContrato;
    private Long estatus;
    private Integer tvs;
    private String fechaProximoPago;
    private Long tipoServicioId;
    private Long folioPlaca;
    private String colorPlaca;
    private String onu;
    private String fechaRegistro;
    private Integer primerDiaPago;
    private Integer primerMesPago;
    private Integer primerAnioPago;
    private String nap;
    private Long usuarioId;
    private Integer numeroCaja;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getFolioContrato() {
        return folioContrato;
    }

    public void setFolioContrato(Long folioContrato) {
        this.folioContrato = folioContrato;
    }

    public Long getEstatus() {
        return estatus;
    }

    public void setEstatus(Long estatus) {
        this.estatus = estatus;
    }

    public Integer getTvs() {
        return tvs;
    }

    public void setTvs(Integer tvs) {
        this.tvs = tvs;
    }

    public String getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(String fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public Long getTipoServicioId() {
        return tipoServicioId;
    }

    public void setTipoServicioId(Long tipoServicioId) {
        this.tipoServicioId = tipoServicioId;
    }

    public Long getFolioPlaca() {
        return folioPlaca;
    }

    public void setFolioPlaca(Long folioPlaca) {
        this.folioPlaca = folioPlaca;
    }

    public String getColorPlaca() {
        return colorPlaca;
    }

    public void setColorPlaca(String colorPlaca) {
        this.colorPlaca = colorPlaca;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getOnu() {
        return onu;
    }

    public void setOnu(String onu) {
        this.onu = onu;
    }

    public Integer getPrimerDiaPago() {
        return primerDiaPago;
    }

    public void setPrimerDiaPago(Integer primerDiaPago) {
        this.primerDiaPago = primerDiaPago;
    }

    public Integer getPrimerMesPago() {
        return primerMesPago;
    }

    public void setPrimerMesPago(Integer primerMesPago) {
        this.primerMesPago = primerMesPago;
    }

    public Integer getPrimerAnioPago() {
        return primerAnioPago;
    }

    public void setPrimerAnioPago(Integer primerAnioPago) {
        this.primerAnioPago = primerAnioPago;
    }

    public String getNap() {
        return nap;
    }

    public void setNap(String nap) {
        this.nap = nap;
    }

    public Integer getNumeroCaja() {
        return numeroCaja;
    }

    public void setNumeroCaja(Integer numeroCaja) {
        this.numeroCaja = numeroCaja;
    }

}
