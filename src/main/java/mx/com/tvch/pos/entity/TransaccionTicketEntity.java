/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class TransaccionTicketEntity {

    private Long transaccionId;

    private Long transaccionServerId;

    private String fechaTransaccion;

    private Integer tipoCobroId;

    private String descripcionTipoCobro;

    private Long contratoId;

    private Long contratoAnteriorId;

    private String fechaProximoPago;

    private Long aperturaCajaId;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private String calle;

    private String numeroCalle;

    private String colonia;

    private String servicio;

    private Double costoServicio;

    private String telefono;

    private Double monto;

    public Long getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(Long transaccionId) {
        this.transaccionId = transaccionId;
    }

    public Long getTransaccionServerId() {
        return transaccionServerId;
    }

    public void setTransaccionServerId(Long transaccionServerId) {
        this.transaccionServerId = transaccionServerId;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public Integer getTipoCobroId() {
        return tipoCobroId;
    }

    public void setTipoCobroId(Integer tipoCobroId) {
        this.tipoCobroId = tipoCobroId;
    }

    public String getDescripcionTipoCobro() {
        return descripcionTipoCobro;
    }

    public void setDescripcionTipoCobro(String descripcionTipoCobro) {
        this.descripcionTipoCobro = descripcionTipoCobro;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getContratoAnteriorId() {
        return contratoAnteriorId;
    }

    public void setContratoAnteriorId(Long contratoAnteriorId) {
        this.contratoAnteriorId = contratoAnteriorId;
    }

    public String getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(String fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public Long getAperturaCajaId() {
        return aperturaCajaId;
    }

    public void setAperturaCajaId(Long aperturaCajaId) {
        this.aperturaCajaId = aperturaCajaId;
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

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumeroCalle() {
        return numeroCalle;
    }

    public void setNumeroCalle(String numeroCalle) {
        this.numeroCalle = numeroCalle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public Double getCostoServicio() {
        return costoServicio;
    }

    public void setCostoServicio(Double costoServicio) {
        this.costoServicio = costoServicio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

}
