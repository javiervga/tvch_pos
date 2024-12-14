/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.entity;

/**
 *
 * @author fvega
 */
public class TipoSalidaEntity {
    
    private Long tipoSalidaId;
    
    private String descripcion;

    public Long getTipoSalidaId() {
        return tipoSalidaId;
    }

    public void setTipoSalidaId(Long tipoSalidaId) {
        this.tipoSalidaId = tipoSalidaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
}
