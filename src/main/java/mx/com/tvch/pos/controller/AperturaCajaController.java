/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.AperturaCajaDao;
import mx.com.tvch.pos.dao.CajaDao;
import mx.com.tvch.pos.entity.AperturaCajaEntity;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.Utilerias;

/**
 *
 * @author fvega
 */
public class AperturaCajaController {
    
    private static AperturaCajaController controller;
    
    private final AperturaCajaDao dao;
    private final CajaDao cajaDao;
    private final Sesion sesion;
    private final Utilerias utilerias;
    private final Impresora impresora;
    
    public static AperturaCajaController getAperturaCajaController(){
        if(controller == null)
            controller = new AperturaCajaController();
        return controller;
    }
    
    public AperturaCajaController(){
        dao = AperturaCajaDao.getAperturaCajaDao();
        sesion = Sesion.getSesion();
        utilerias = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        cajaDao = CajaDao.getCajaDao();
    }
    
    public AperturaCajaEntity abrirCaja(Double fondoFijo) throws Exception {
        
        Long aperturaCajaId = utilerias.generarIdLocal();
        dao.registrarAperturaCaja(aperturaCajaId, sesion, fondoFijo);
        cajaDao.actualizarEstatusCaja(sesion.getCajaId(), Constantes.ESTATUS_CAJA_ACTIVA);
        return dao.obtenerAperturaCaja(aperturaCajaId);
        
    }
    
    public AperturaCajaEntity obtenerAperturaCajaActiva()throws Exception{
        
        return dao.obtenerAperturaCaja(null);
    }
    
    
}
