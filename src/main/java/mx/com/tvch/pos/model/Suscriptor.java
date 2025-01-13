/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

/**
 *
 * @author fvega
 */
public class Suscriptor {

    private Long contratoId;

    private Long contratoAnteriorId;

    private String nombre;

    private String domicilio;

    private String servicio;

    public Suscriptor(Long contratoId, Long contratoAnteriorId, String nombre, String domicilio, String servicio) {
        this.contratoId = contratoId;
        this.contratoAnteriorId = contratoAnteriorId;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.servicio = servicio;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

}
