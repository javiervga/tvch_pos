/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class CorteCajaEntity {

    private Long corteCajaId;

    private Long corteCajaIdServer;

    private Long aperturaCajaId;

    private Long usuarioId;

    private Long sucursalId;

    private Double fondoFijo;

    private Integer cantidadCobros;

    private Double totalCobros;

    private Integer cantidadDescuentos;

    private Double totalDescuentos;

    private Integer cantidadSalidas;

    private Double totalSalidas;
    
    private Integer cantidadIngresos;
    
    private Double totalIngresos;

    private Integer promocionesAplicadas;

    private Double totalSolicitado;

    private Double totalEntregado;

    private String fechaCorte;

    public Long getCorteCajaId() {
        return corteCajaId;
    }

    public void setCorteCajaId(Long corteCajaId) {
        this.corteCajaId = corteCajaId;
    }

    public Long getCorteCajaIdServer() {
        return corteCajaIdServer;
    }

    public void setCorteCajaIdServer(Long corteCajaIdServer) {
        this.corteCajaIdServer = corteCajaIdServer;
    }

    public Long getAperturaCajaId() {
        return aperturaCajaId;
    }

    public void setAperturaCajaId(Long aperturaCajaId) {
        this.aperturaCajaId = aperturaCajaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Double getFondoFijo() {
        return fondoFijo;
    }

    public void setFondoFijo(Double fondoFijo) {
        this.fondoFijo = fondoFijo;
    }

    public Integer getCantidadCobros() {
        return cantidadCobros;
    }

    public void setCantidadCobros(Integer cantidadCobros) {
        this.cantidadCobros = cantidadCobros;
    }

    public Double getTotalCobros() {
        return totalCobros;
    }

    public void setTotalCobros(Double totalCobros) {
        this.totalCobros = totalCobros;
    }

    public Integer getCantidadDescuentos() {
        return cantidadDescuentos;
    }

    public void setCantidadDescuentos(Integer cantidadDescuentos) {
        this.cantidadDescuentos = cantidadDescuentos;
    }

    public Double getTotalDescuentos() {
        return totalDescuentos;
    }

    public void setTotalDescuentos(Double totalDescuentos) {
        this.totalDescuentos = totalDescuentos;
    }

    public Integer getCantidadSalidas() {
        return cantidadSalidas;
    }

    public void setCantidadSalidas(Integer cantidadSalidas) {
        this.cantidadSalidas = cantidadSalidas;
    }

    public Double getTotalSalidas() {
        return totalSalidas;
    }

    public void setTotalSalidas(Double totalSalidas) {
        this.totalSalidas = totalSalidas;
    }

    public Integer getCantidadIngresos() {
        return cantidadIngresos;
    }

    public void setCantidadIngresos(Integer cantidadIngresos) {
        this.cantidadIngresos = cantidadIngresos;
    }

    public Double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(Double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public Integer getPromocionesAplicadas() {
        return promocionesAplicadas;
    }

    public void setPromocionesAplicadas(Integer promocionesAplicadas) {
        this.promocionesAplicadas = promocionesAplicadas;
    }

    public Double getTotalSolicitado() {
        return totalSolicitado;
    }

    public void setTotalSolicitado(Double totalSolicitado) {
        this.totalSolicitado = totalSolicitado;
    }

    public Double getTotalEntregado() {
        return totalEntregado;
    }

    public void setTotalEntregado(Double totalEntregado) {
        this.totalEntregado = totalEntregado;
    }

    public String getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(String fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

}
