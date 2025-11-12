/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.util.ArrayList;
import java.util.List;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.AperturaCajaDao;
import mx.com.tvch.pos.dao.CancelacionDao;
import mx.com.tvch.pos.dao.CobroProvisionalDao;
import mx.com.tvch.pos.dao.CorteCajaDao;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDescuentoTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDiferenciaCorteCajaDao;
import mx.com.tvch.pos.dao.DetallePromocionTransaccionDao;
import mx.com.tvch.pos.dao.IngresoCajaDao;
import mx.com.tvch.pos.dao.SalidaCajaDao;
import mx.com.tvch.pos.dao.SalidaExtraordinariaDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.AperturaCajaEntity;
import mx.com.tvch.pos.mapper.PosMapper;
import mx.com.tvch.pos.model.OperacionPendiente;
import mx.com.tvch.pos.model.TipoOperacion;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class EstatusOperacionesController {
    
    private static EstatusOperacionesController controller;
    
    private final AperturaCajaDao aperturaCajaDao;
    private final CorteCajaDao corteCajaDao;
    private final TransaccionDao transaccionDao;
    private final SalidaCajaDao salidaCajaDao;
    private final IngresoCajaDao ingresoCajaDao;
    private final SalidaExtraordinariaDao salidaExtraordinariaDao;
    private final CancelacionDao cancelacionDao;
    private final CobroProvisionalDao cobroProvisionalDao;
    private final Sesion sesion;
    private final Utilerias util;
    private final PosMapper mapper;
    
    Logger logger = LoggerFactory.getLogger(CorteCajaController.class);

    public static EstatusOperacionesController getEstatusOperacionesController() {
        if (controller == null) {
            controller = new EstatusOperacionesController();
        }
        return controller;
    }

    public EstatusOperacionesController() {
        aperturaCajaDao = AperturaCajaDao.getAperturaCajaDao();
        corteCajaDao = CorteCajaDao.getCorteCajaDao();
        transaccionDao = TransaccionDao.getTransaccionDao();
        salidaCajaDao = SalidaCajaDao.getSalidaCajaDao();
        ingresoCajaDao = IngresoCajaDao.getIngresoCajaDao();
        salidaExtraordinariaDao = SalidaExtraordinariaDao.getSalidaExtraordinariaDao();
        cancelacionDao = CancelacionDao.getCancelacionDao();
        cobroProvisionalDao = CobroProvisionalDao.getCobroProvisionalDao();
        sesion = Sesion.getSesion();
        util = Utilerias.getUtilerias();
        mapper = PosMapper.getPosMapper();
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<OperacionPendiente> consultarOperacionesPendientes(TipoOperacion tipo) throws Exception{
        
        List<OperacionPendiente> list = new ArrayList<>();
        
        switch (tipo.getTipo()) {
            case Constantes.OPERACION_COBRO_SERVICIO:
                return mapper.transacciones2OperacionPendientes(transaccionDao.obtenerTransacciones(), tipo);
            case Constantes.OPERACION_CANCELACION:
                return mapper.cancelaciones2OperacionPendientes(cancelacionDao.obtenerCancelaciones(), tipo);
            case Constantes.OPERACION_COBRO_PROVISIONAL:
                return mapper.cobrosProvisionales2OperacionPendientes(cobroProvisionalDao.obtenerCobrosProvisionales(), tipo);
            case Constantes.OPERACION_CORTE:
                return mapper.cortes2OperacionPendientes(corteCajaDao.obtenerCortesCaja(), tipo);
            case Constantes.OPERACION_APERTURA:
                return mapper.aperturas2OperacionPendientes(aperturaCajaDao.obtenerAperturasCaja(), tipo);
            case Constantes.OPERACION_INGRESO:
                return mapper.ingresos2OperacionPendientes(ingresoCajaDao.obtenerIngresosCaja(), tipo);
            case Constantes.OPERACION_EGRESO:
                return mapper.salidas2OperacionPendientes(salidaCajaDao.obtenerSalidasCaja(), tipo);
            case Constantes.OPERACION_EGRESO_EXTRAORDINARIO:
                return mapper.salidasExtraordinarias2OperacionPendientes(salidaExtraordinariaDao.obtenerSalidasExtraordinarias(), tipo);
            default:
                throw new AssertionError();
        }
        
    } 
    
}
