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
import mx.com.tvch.pos.dao.SalidaCajaDao;
import mx.com.tvch.pos.dao.TipoSalidaDao;
import mx.com.tvch.pos.entity.TipoSalidaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class SalidaCajaController {

    private static SalidaCajaController controller;

    private final TipoSalidaDao tipoSalidaDao;
    private final SalidaCajaDao dao;
    private final Sesion sesion;

    Logger logger = LoggerFactory.getLogger(SalidaCajaController.class);

    public static SalidaCajaController getSalidaCajaController() {
        if (controller == null) {
            controller = new SalidaCajaController();
        }
        return controller;
    }

    public SalidaCajaController() {
        tipoSalidaDao = TipoSalidaDao.getTipoSalidaDao();
        dao = SalidaCajaDao.getSalidaCajaDao();
        sesion = Sesion.getSesion();
    }

    public List<TipoSalidaEntity> consultarTiposSalida() throws Exception {

        try {

            List<TipoSalidaEntity> list = tipoSalidaDao.obtenerTiposSalida();
            if (list == null) {
                throw new NoSuchElementException(("No se encontraron Tipos de Salida registrados en su equipo. Por favor, contacte a soporte"));
            }

            return list;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar tipos de salida: \n" + sw.toString());
            throw new Exception("Error al consultar tipos de salida. Por favor contacte a soporte.");
        }

    }
    
    public void registrarSalidacaja(Double montoSalida, Long tipoSalidaId, String observaciones) throws Exception {
                 
        dao.registrarSalidaCaja(sesion, montoSalida, tipoSalidaId, observaciones);
             
    }

}
