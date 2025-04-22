/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.config;

/**
 *
 * @author fvega
 */
public class Sesion {

    private static Sesion sesion;

    private Long usuarioId;

    private String usuario;

    private Integer numeroCaja;

    private Long cajaId;

    private String sucursal;

    private Long sucursalId;

    private Long aperturaCajaId;

    private String password;

    private Integer diaCorte;

    private String telefonoSucursal;

    private Integer usaWhats;
    
    private String telefonoSoporte;

    private String ticketLineaCiudadRfc;

    private Integer ticketSangriaCiudadRfc;

    private String ticketLineaCalle;

    private Integer ticketSangriaCalle;

    private String ticketLineaColonia;

    private Integer ticketSangriaColonia;

    private Integer ticketSangriaSucursal;

    public static Sesion getSesion() {
        if (sesion == null) {
            sesion = new Sesion();
        }
        return sesion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getNumeroCaja() {
        return numeroCaja;
    }

    public void setNumeroCaja(Integer numeroCaja) {
        this.numeroCaja = numeroCaja;
    }

    public Long getCajaId() {
        return cajaId;
    }

    public void setCajaId(Long cajaId) {
        this.cajaId = cajaId;
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

    public Long getAperturaCajaId() {
        return aperturaCajaId;
    }

    public void setAperturaCajaId(Long aperturaCajaId) {
        this.aperturaCajaId = aperturaCajaId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDiaCorte() {
        return diaCorte;
    }

    public void setDiaCorte(Integer diaCorte) {
        this.diaCorte = diaCorte;
    }

    public String getTelefonoSucursal() {
        return telefonoSucursal;
    }

    public void setTelefonoSucursal(String telefonoSucursal) {
        this.telefonoSucursal = telefonoSucursal;
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
