/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class SucursalEntity {

    private Long sucursalId;

    private String nombre;

    private Long zonaId;

    private Integer diaCorte;

    private Integer estatus;

    private String telefono;

    private Integer usaWhats;
    
    private String telefonoSoporte;

    private String ticketLineaCiudadRfc;

    private Integer ticketSangriaCiudadRfc;

    private String ticketLineaCalle;

    private Integer ticketSangriaCalle;

    private String ticketLineaColonia;

    private Integer ticketSangriaColonia;

    private Integer ticketSangriaSucursal;

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

    public Long getZonaId() {
        return zonaId;
    }

    public void setZonaId(Long zonaId) {
        this.zonaId = zonaId;
    }

    public Integer getDiaCorte() {
        return diaCorte;
    }

    public void setDiaCorte(Integer diaCorte) {
        this.diaCorte = diaCorte;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getUsaWhats() {
        return usaWhats;
    }

    public void setUsaWhats(Integer usaWhats) {
        this.usaWhats = usaWhats;
    }

    public String getTelefonoSoporte() {
        return telefonoSoporte;
    }

    public void setTelefonoSoporte(String telefonoSoporte) {
        this.telefonoSoporte = telefonoSoporte;
    }

    public String getTicketLineaCiudadRfc() {
        return ticketLineaCiudadRfc;
    }

    public void setTicketLineaCiudadRfc(String ticketLineaCiudadRfc) {
        this.ticketLineaCiudadRfc = ticketLineaCiudadRfc;
    }

    public Integer getTicketSangriaCiudadRfc() {
        return ticketSangriaCiudadRfc;
    }

    public void setTicketSangriaCiudadRfc(Integer ticketSangriaCiudadRfc) {
        this.ticketSangriaCiudadRfc = ticketSangriaCiudadRfc;
    }

    public String getTicketLineaCalle() {
        return ticketLineaCalle;
    }

    public void setTicketLineaCalle(String ticketLineaCalle) {
        this.ticketLineaCalle = ticketLineaCalle;
    }

    public Integer getTicketSangriaCalle() {
        return ticketSangriaCalle;
    }

    public void setTicketSangriaCalle(Integer ticketSangriaCalle) {
        this.ticketSangriaCalle = ticketSangriaCalle;
    }

    public String getTicketLineaColonia() {
        return ticketLineaColonia;
    }

    public void setTicketLineaColonia(String ticketLineaColonia) {
        this.ticketLineaColonia = ticketLineaColonia;
    }

    public Integer getTicketSangriaColonia() {
        return ticketSangriaColonia;
    }

    public void setTicketSangriaColonia(Integer ticketSangriaColonia) {
        this.ticketSangriaColonia = ticketSangriaColonia;
    }

    public Integer getTicketSangriaSucursal() {
        return ticketSangriaSucursal;
    }

    public void setTicketSangriaSucursal(Integer ticketSangriaSucursal) {
        this.ticketSangriaSucursal = ticketSangriaSucursal;
    }
}
