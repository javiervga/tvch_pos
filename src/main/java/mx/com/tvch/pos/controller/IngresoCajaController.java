/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.NoSuchElementException;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.IngresoCajaDao;
import mx.com.tvch.pos.dao.TipoIngresoDao;
import mx.com.tvch.pos.entity.TipoIngresoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class IngresoCajaController {
    
    private static IngresoCajaController controller;
    
    private final TipoIngresoDao tipoIngresoDao;
    private final IngresoCajaDao dao;
    private final Sesion sesion;
    
    Logger logger = LoggerFactory.getLogger(IngresoCajaController.class);
    
    public static IngresoCajaController getIngresoCajaController(){
        if(controller == null)
            controller = new IngresoCajaController();
        return controller;
    }
    
    public IngresoCajaController(){
        tipoIngresoDao = TipoIngresoDao.getTipoIngresoDao();
        dao = IngresoCajaDao.getIngresoCajaDao();
        sesion = Sesion.getSesion();
    }
    
    public List<TipoIngresoEntity> consultarTiposIngreso() throws Exception {

        try {

            List<TipoIngresoEntity> list = tipoIngresoDao.obtenerTiposIngreso();
            if (list == null) {
                throw new NoSuchElementException(("No se encontraron Tipos de Ingreso registrados en su equipo. Por favor, contacte a soporte"));
            }

            return list;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar tipos de ingreso: \n" + sw.toString());
            throw new Exception("Error al consultar tipos de ingreso. Por favor contacte a soporte.");
        }

    }
    
    public void registrarIngresocaja(Double montoSalida, Long tipoSalidaId, String observaciones) throws Exception {
                 
        dao.registrarIngresoCaja(sesion, montoSalida, tipoSalidaId, observaciones);
             
    }
    
}
