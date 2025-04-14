/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model.client;

/**
 *
 * @author fvega
 */
public class Suscriptor {

    private Long id;

    private String sucursal;

    private Long sucursalId;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private String telefono;

    private String fechaRegistroSuscriptor;

    private Long estatusSuscriptorId;

    private String estatusSuscriptor;

    private Long estatusContratoId;

    private String estatusContrato;

    private Long contrato;

    private Long contratoAnterior;

    private String domicilio;

    private String servicio;

    private Long servicioId;
    
    private String fechaProximoPago;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaRegistroSuscriptor() {
        return fechaRegistroSuscriptor;
    }

    public void setFechaRegistroSuscriptor(String fechaRegistroSuscriptor) {
        this.fechaRegistroSuscriptor = fechaRegistroSuscriptor;
    }

    public Long getEstatusSuscriptorId() {
        return estatusSuscriptorId;
    }

    public void setEstatusSuscriptorId(Long estatusSuscriptorId) {
        this.estatusSuscriptorId = estatusSuscriptorId;
    }

    public String getEstatusSuscriptor() {
        return estatusSuscriptor;
    }

    public void setEstatusSuscriptor(String estatusSuscriptor) {
        this.estatusSuscriptor = estatusSuscriptor;
    }

    public Long getEstatusContratoId() {
        return estatusContratoId;
    }

    public void setEstatusContratoId(Long estatusContratoId) {
        this.estatusContratoId = estatusContratoId;
    }

    public String getEstatusContrato() {
        return estatusContrato;
    }

    public void setEstatusContrato(String estatusContrato) {
        this.estatusContrato = estatusContrato;
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

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public String getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(String fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

}
