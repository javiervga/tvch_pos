/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model.client;

/**
 *
 * @author fvega
 */
public class UpdateContratoEstatusReconexionPosRequest {

    private Long contratoId;

    private Long usuarioId;

    private Integer generarOrdenReconexion;

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getGenerarOrdenReconexion() {
        return generarOrdenReconexion;
    }

    public void setGenerarOrdenReconexion(Integer generarOrdenReconexion) {
        this.generarOrdenReconexion = generarOrdenReconexion;
    }

}
