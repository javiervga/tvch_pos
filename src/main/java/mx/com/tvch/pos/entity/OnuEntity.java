/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

import java.util.Date;

/**
 *
 * @author fvega
 */
public class OnuEntity {

    private Long onuId;

    private Long onuServerId;

    private String serie;

    private Long estatusId;

    private Long sucursalId;

    private Date fechaRegistro;

    private Long usuarioId;

    private Integer actualizacion;

    public Long getOnuId() {
        return onuId;
    }

    public void setOnuId(Long onuId) {
        this.onuId = onuId;
    }

    public Long getOnuServerId() {
        return onuServerId;
    }

    public void setOnuServerId(Long onuServerId) {
        this.onuServerId = onuServerId;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Long getEstatusId() {
        return estatusId;
    }

    public void setEstatusId(Long estatusId) {
        this.estatusId = estatusId;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getActualizacion() {
        return actualizacion;
    }

    public void setActualizacion(Integer actualizacion) {
        this.actualizacion = actualizacion;
    }

}
