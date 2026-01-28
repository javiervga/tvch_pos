/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import mx.com.tvch.pos.dao.ServicioDao;
import mx.com.tvch.pos.entity.ServicioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class ServicioController {
    
    private static ServicioController controller;
    
    private final ServicioDao dao;

    Logger logger = LoggerFactory.getLogger(ServicioController.class);

    public static ServicioController getServicioController() {
        if (controller == null) {
            controller = new ServicioController();
        }
        return controller;
    }

    public ServicioController() {
        dao = ServicioDao.getServicioDao();
    }
    
    /**
     * 
     * @return 
     */
    public List<ServicioEntity> obtenerServicios() {

        List<ServicioEntity> servicios = new ArrayList<>();
        try {
            servicios = dao.obtenerServicios();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ServicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return servicios; 

    }
    
    /**
     * 
     * @param id
     * @return 
     */
    public ServicioEntity consultarServicio(Long id) {

        ServicioEntity servicio = null;
        try {
            servicio = dao.consultarServicio(id);
        } catch (Exception ex) {
            logger.error("Error el llamar dao para obtener servicio con id"+id);
        }
        return servicio; 

    }
    
}
