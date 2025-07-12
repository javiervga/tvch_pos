/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.CobroProvisionalDao;
import mx.com.tvch.pos.entity.CobroProvisionalEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroProvisionalController {
    
    private static CobroProvisionalController controller;

    private final CobroProvisionalDao dao;
    private final Sesion sesion;
    private final Utilerias util;

    Logger logger = LoggerFactory.getLogger(CobroProvisionalController.class);

    public static CobroProvisionalController getCobroProvisionalController(){
        if (controller == null) {
            controller = new CobroProvisionalController();
        }
        return controller;
    }

    public CobroProvisionalController() {
        dao = CobroProvisionalDao.getCobroProvisionalDao();
        sesion = Sesion.getSesion();
        util = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param suscriptorEntity
     * @param tipoOrden
     * @param tipoOrdenServicio
     * @param observaciones
     * @param monto
     * @return
     * @throws Exception 
     */
    public CobroProvisionalEntity registrarCobroProvisional(ContratoxSuscriptorEntity suscriptorEntity,
            String tipoOrden, String tipoOrdenServicio, String observaciones, double monto) throws Exception {
        
        StringBuilder domicilio = new StringBuilder();
        domicilio.append(suscriptorEntity.getCalle());
        if(suscriptorEntity.getNumeroCalle() != null)
            domicilio.append(" ").append(suscriptorEntity.getNumeroCalle());
        if(suscriptorEntity.getColonia() != null)
            domicilio.append(" ").append(suscriptorEntity.getColonia());
        
        StringBuilder suscriptor = new StringBuilder();
        suscriptor.append(suscriptorEntity.getNombre());
        if(suscriptorEntity.getApellidoPaterno() != null)
            suscriptor.append(" ").append(suscriptorEntity.getApellidoPaterno());
        if(suscriptorEntity.getApellidoMaterno() != null)
            suscriptor.append(" ").append(suscriptorEntity.getApellidoMaterno());
        
        CobroProvisionalEntity entity = new CobroProvisionalEntity();
        entity.setCobroId(util.generarIdLocal());
        entity.setContratoId(suscriptorEntity.getContratoId());
        if(suscriptorEntity.getFolioContrato() != null)
            entity.setFolioContrato(suscriptorEntity.getFolioContrato());
        entity.setSuscriptor(suscriptor.toString().toUpperCase());
        entity.setDomicilio(domicilio.toString().toUpperCase());
        entity.setServicio(suscriptorEntity.getServicio().toUpperCase());
        entity.setTelefono(suscriptorEntity.getTelefono());
        entity.setTipoOrden(tipoOrden.toUpperCase());
        if(tipoOrdenServicio != null)
            entity.setTipoOrdenServicio(tipoOrdenServicio.toUpperCase());
        entity.setObservaciones(observaciones.toUpperCase());
        entity.setMonto(monto);
        entity.setUsuarioId(sesion.getUsuarioId());
        entity.setCajaId(sesion.getCajaId());
        entity.setEstatusId(Constantes.ESTATUS_COBRO_PROVISIONAL_NUEVO);
        
        Long cobroId = dao.registrarCobroProvisional(entity);
        CobroProvisionalEntity cobro = dao.obtenerCobroProvisional(cobroId);
        
        return cobro;
    }
    
}
