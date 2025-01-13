/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model.client;

/**
 *
 * @author fvega
 */
public class OrdenInstalacion {

    private Long ordenInstalacionId;

    private Long contratoId;

    private String usuario;

    private String fechaAgenda;

    private String observacionesAgenda;

    private String fechaRegistro;

    private String fechaInstalacion;

    private String observacionesInstalacion;

    private Long estatusId;

    private String estatus;

    private Long vendedorId;

    private String vendedor;

    private String suscriptor;

    private Double costo;

    public Long getOrdenInstalacionId() {
        return ordenInstalacionId;
    }

    public void setOrdenInstalacionId(Long ordenInstalacionId) {
        this.ordenInstalacionId = ordenInstalacionId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFechaAgenda() {
        return fechaAgenda;
    }

    public void setFechaAgenda(String fechaAgenda) {
        this.fechaAgenda = fechaAgenda;
    }

    public String getObservacionesAgenda() {
        return observacionesAgenda;
    }

    public void setObservacionesAgenda(String observacionesAgenda) {
        this.observacionesAgenda = observacionesAgenda;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaInstalacion() {
        return fechaInstalacion;
    }

    public void setFechaInstalacion(String fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }

    public String getObservacionesInstalacion() {
        return observacionesInstalacion;
    }

    public void setObservacionesInstalacion(String observacionesInstalacion) {
        this.observacionesInstalacion = observacionesInstalacion;
    }

    public Long getEstatusId() {
        return estatusId;
    }

    public void setEstatusId(Long estatusId) {
        this.estatusId = estatusId;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(String suscriptor) {
        this.suscriptor = suscriptor;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

}
