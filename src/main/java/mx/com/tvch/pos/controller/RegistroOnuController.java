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
import mx.com.tvch.pos.dao.OnuDao;
import mx.com.tvch.pos.dao.TipoIngresoDao;
import mx.com.tvch.pos.entity.OnuEntity;
import mx.com.tvch.pos.entity.TipoIngresoEntity;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.TvchException;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class RegistroOnuController {
    
    private static RegistroOnuController controller;
    
    private final OnuDao onuDao;
    private final Sesion sesion;
    private final Utilerias util;
    
    Logger logger = LoggerFactory.getLogger(RegistroOnuController.class);
    
    public static RegistroOnuController getRegistroOnuController(){
        if(controller == null)
            controller = new RegistroOnuController();
        return controller;
    }
    
    public RegistroOnuController(){
        onuDao = OnuDao.getOnuDao();
        sesion = Sesion.getSesion();
        util = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param numeroSerie
     * @throws mx.com.tvch.pos.util.TvchException
     * @throws Exception 
     */
    public void registrarOnu(String numeroSerie) throws TvchException, Exception {
        
        //primero validar que no exista Onu con el mismo nuemero de serie
        OnuEntity onuExistente = onuDao.consultarOnuPorSerie(numeroSerie);
        if(onuExistente != null)
            throw new TvchException("Ya existe una Onu con el número de Serie solicitado");
          
        OnuEntity entity = new OnuEntity();
        entity.setOnuId(util.generarIdLocal());
        entity.setSerie(numeroSerie.toUpperCase());
        entity.setEstatusId(Constantes.ESTATUS_ONU_DISPONIBLE);
        entity.setSucursalId(sesion.getSucursalId());
        entity.setUsuarioId(sesion.getUsuarioId());
        entity.setActualizacion(1);
        onuDao.registrarOnu(entity);
             
    }
    
}
