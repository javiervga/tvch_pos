/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.model;

import java.util.List;
import mx.com.tvch.pos.entity.CorteCajaEntity;
import mx.com.tvch.pos.entity.DetalleDiferenciaCorteEntity;

/**
 *
 * @author fvega
 */
public class CorteCaja {

    private List<DetalleCorte> detallesCorte;

    private CorteCajaEntity entity;

    private DetalleDiferenciaCorteEntity faltanteEntity;

    private DetalleDiferenciaCorteEntity sobranteEntity;

    private boolean esExitoso;

    private String mensaje;

    public List<DetalleCorte> getDetallesCorte() {
        return detallesCorte;
    }

    public void setDetallesCorte(List<DetalleCorte> detallesCorte) {
        this.detallesCorte = detallesCorte;
    }

    public DetalleDiferenciaCorteEntity getFaltanteEntity() {
        return faltanteEntity;
    }

    public void setFaltanteEntity(DetalleDiferenciaCorteEntity faltanteEntity) {
        this.faltanteEntity = faltanteEntity;
    }

    public DetalleDiferenciaCorteEntity getSobranteEntity() {
        return sobranteEntity;
    }

    public void setSobranteEntity(DetalleDiferenciaCorteEntity sobranteEntity) {
        this.sobranteEntity = sobranteEntity;
    }

    public CorteCajaEntity getEntity() {
        return entity;
    }

    public void setEntity(CorteCajaEntity entity) {
        this.entity = entity;
    }

    public boolean isEsExitoso() {
        return esExitoso;
    }

    public void setEsExitoso(boolean esExitoso) {
        this.esExitoso = esExitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
