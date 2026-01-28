/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

import java.util.List;

/**
 *
 * @author fvega
 */
public class Transaccion {

    private Long transaccionId;

    private Long transaccionServerId;

    private Long aperturaCajaId;

    private Long contratoId;

    private Double monto;

    private String observaciones;

    private String periodo;

    private String fechaTransaccion;

    private String actualFechaCorte;

    private String nuevaFechaCorte;

    private List<DetalleCobroTransaccion> detallesCobro;

    private DetalleDescuentoTransaccion descuento;

    private DetallePromocionTransaccion promocion;

    private String folioContrato;

    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String calle;
    private String numeroCalle;
    private String colonia;
    private String nombreSucursal;
    private String servicio;
    private String telefono;

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

    public Long getAperturaCajaId() {
        return aperturaCajaId;
    }

    public void setAperturaCajaId(Long aperturaCajaId) {
        this.aperturaCajaId = aperturaCajaId;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getActualFechaCorte() {
        return actualFechaCorte;
    }

    public void setActualFechaCorte(String actualFechaCorte) {
        this.actualFechaCorte = actualFechaCorte;
    }

    public String getNuevaFechaCorte() {
        return nuevaFechaCorte;
    }

    public void setNuevaFechaCorte(String nuevaFechaCorte) {
        this.nuevaFechaCorte = nuevaFechaCorte;
    }

    public List<DetalleCobroTransaccion> getDetallesCobro() {
        return detallesCobro;
    }

    public void setDetallesCobro(List<DetalleCobroTransaccion> detallesCobro) {
        this.detallesCobro = detallesCobro;
    }

    public DetalleDescuentoTransaccion getDescuento() {
        return descuento;
    }

    public void setDescuento(DetalleDescuentoTransaccion descuento) {
        this.descuento = descuento;
    }

    public DetallePromocionTransaccion getPromocion() {
        return promocion;
    }

    public void setPromocion(DetallePromocionTransaccion promocion) {
        this.promocion = promocion;
    }

    public String getFolioContrato() {
        return folioContrato;
    }

    public void setFolioContrato(String folioContrato) {
        this.folioContrato = folioContrato;
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

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
