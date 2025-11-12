/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.SalidaExtraordinariaDao;
import mx.com.tvch.pos.entity.SalidaExtraordinariaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class SalidaExtraordinariaController {

    private static SalidaExtraordinariaController controller;

    private final SalidaExtraordinariaDao dao;
    private final Sesion sesion;

    Logger logger = LoggerFactory.getLogger(SalidaExtraordinariaController.class);

    public static SalidaExtraordinariaController getSalidaCajaController() {
        if (controller == null) {
            controller = new SalidaExtraordinariaController();
        }
        return controller;
    }

    public SalidaExtraordinariaController() {
        dao = SalidaExtraordinariaDao.getSalidaExtraordinariaDao();
        sesion = Sesion.getSesion();
    }
    
    /**
     * 
     * @param salidaId
     * @return
     * @throws Exception 
     */
    public SalidaExtraordinariaEntity consultarSalida (Long salidaId) throws Exception {
        
        SalidaExtraordinariaEntity entity = dao.obtenerSalidasPorId(salidaId);
        return entity;
    }
    
    /**
     * 
     * @param montoSalida
     * @param observaciones
     * @return
     * @throws Exception 
     */
    public Long registrarSalidacaja(Double montoSalida, String observaciones) throws Exception {
                 
        return dao.registrarSalidaExtraordinaria(sesion, montoSalida, observaciones);
             
    }

}
