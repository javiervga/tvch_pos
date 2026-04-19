/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.ContratoDao;
import mx.com.tvch.pos.dao.ContratoxSuscriptorDao;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDescuentoTransaccionDao;
import mx.com.tvch.pos.dao.DetallePromocionTransaccionDao;
import mx.com.tvch.pos.dao.DomicilioDao;
import mx.com.tvch.pos.dao.DomicilioPorContratoDao;
import mx.com.tvch.pos.dao.ServicioDao;
import mx.com.tvch.pos.dao.ServicioPorContratoDao;
import mx.com.tvch.pos.dao.SuscriptorDao;
import mx.com.tvch.pos.dao.TipoOrdenServicioDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.ContratoEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import mx.com.tvch.pos.entity.DetallePromocionTransaccionEntity;
import mx.com.tvch.pos.entity.DomicilioEntity;
import mx.com.tvch.pos.entity.DomicilioPorContratoEntity;
import mx.com.tvch.pos.entity.ServicioEntity;
import mx.com.tvch.pos.entity.ServicioPorContratoEntity;
import mx.com.tvch.pos.entity.SuscriptorEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.model.DetalleCobroTransaccion;
import mx.com.tvch.pos.model.DetalleDescuentoTransaccion;
import mx.com.tvch.pos.model.TransaccionTicket;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.model.DetallePromocionTransaccion;
import mx.com.tvch.pos.model.Transaccion;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class ReimpresionesController {

    private static ReimpresionesController controller;
    private final TransaccionDao transaccionDao;
    private final DetalleCobroTransaccionDao detalleCobroTransaccionDao;
    private final DetalleDescuentoTransaccionDao detalleDescuentoTransaccionDao;
    private final DetallePromocionTransaccionDao detallePromocionTransaccionDao;
    private final TipoOrdenServicioDao tipoOrdenServicioDao;
    private final ContratoDao contratoDao;
    private final ContratoxSuscriptorDao contratoxSuscriptorDao;
    private final SuscriptorDao suscriptorDao;
    private final DomicilioPorContratoDao domicilioPorContratoDao;
    private final DomicilioDao domicilioDao;
    private final ServicioPorContratoDao servicioPorContratoDao;
    private final ServicioDao servicioDao;
    private final Impresora impresora;
    private final Sesion sesion;

    Logger logger = LoggerFactory.getLogger(ReimpresionesController.class);

    public static ReimpresionesController getReimpresionesController() {
        if (controller == null) {
            controller = new ReimpresionesController();
        }
        return controller;
    }

    public ReimpresionesController() {
        transaccionDao = TransaccionDao.getTransaccionDao();
        detalleCobroTransaccionDao = DetalleCobroTransaccionDao.getDetalleCobroTransaccionDao();
        detalleDescuentoTransaccionDao = DetalleDescuentoTransaccionDao.getDetalleDescuentoTransaccionDao();
        detallePromocionTransaccionDao = DetallePromocionTransaccionDao.getDetallePromocionTransaccionDao();
        tipoOrdenServicioDao = TipoOrdenServicioDao.getTipoOrdenServicioDao();
        contratoDao = ContratoDao.getContratoDao();
        contratoxSuscriptorDao = ContratoxSuscriptorDao.getContratoxSuscriptorDao();
        suscriptorDao = SuscriptorDao.getSuscriptorDao();
        domicilioPorContratoDao = DomicilioPorContratoDao.getDomicilioPorContratoDao();
        domicilioDao = DomicilioDao.getDomicilioDao();
        servicioPorContratoDao = ServicioPorContratoDao.getServicioPorContratoDao();
        servicioDao = ServicioDao.getServicioDao();
        impresora = Impresora.getImpresora();
        sesion = Sesion.getSesion();
    }

    /**
     *
     * @param transaccion
     * @throws Exception
     */
    public void reimprimirTicket(Transaccion transaccion) throws Exception {

        try {
            
            boolean esCancelacion = false;
            for(DetalleCobroTransaccion d : transaccion.getDetallesCobro()){
                if(d.getTipoCobroId() == Constantes.TIPO_COBRO_CANCELACION_CONTRATO){
                    esCancelacion = true;
                    break;
                }
            }
            
            if(esCancelacion){
                impresora.reimprimirTicketCancelacion(transaccion);
            }else{
                impresora.reimprimirTicketServicio(transaccion);
            }
            
            /*if (transaccion.getTipoCobroId() == Constantes.TIPO_COBRO_SERVICIO) {
                List<DetallePagoServicio> detallesPago = obtenerDetallesPago(transaccion);
                impresora.reimprimirTicketServicio(transaccion, detallesPago, sesion.getSucursal());
            }else if (transaccion.getTipoCobroId() == Constantes.TIPO_COBRO_CANCELACION_CONTRATO) {
                List<DetallePagoServicio> detallesPago = obtenerDetallesPagoCancelacion(transaccion);
                impresora.reimprimirTicketCancelacion(transaccion, detallesPago, sesion.getSucursal());
            }else {
                //List<DetallePagoServicio> detallesPago = obtenerDetallesPago(transaccion);
                List<DetalleCobroTransaccionEntity> detallesTransaccion = detalleCobroTransaccionDao.obtenerDetallesCobroPorTransaccion(transaccion.getTransaccionId());
                if (!detallesTransaccion.isEmpty()) {

                    List<DetalleDescuentoTransaccionEntity> detallesDescuento = detalleDescuentoTransaccionDao
                            .obtenerDetallesDescuentoPorTransaccion(transaccion.getTransaccionId());
                    DetalleDescuentoTransaccionEntity detalleDescuento = null;
                    if (!detallesDescuento.isEmpty()) {
                        detalleDescuento = detallesDescuento.get(0);
                    }

                    List<DetallePromocionTransaccionEntity> detallesPromocion = detallePromocionTransaccionDao
                            .obtenerDetallesPromocionPorTransaccion(transaccion.getTransaccionId());
                    DetallePromocionTransaccionEntity detallePromocion = null;
                    if (!detallesPromocion.isEmpty()) {
                        detallePromocion = detallesPromocion.get(0);
                    }
                    
                    if (transaccion.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_INSTALACION)
                        impresora.reimprimirTicketOrdenInstalacion(transaccion, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                    else if (transaccion.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_SERVICIO)
                        impresora.reimprimirTicketOrdenServicio(transaccion, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                    else if (transaccion.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_CAMBIO_DOMICILIO)
                        impresora.reimprimirTicketOrdenCambioDomicilio(transaccion, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                }
            }*/  

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al consultar transacciones para reimpresion: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }
    }
    
    /**
     * 
     * @param entity
     * @return
     * @throws Exception 
     */
    private List<DetallePagoServicio> obtenerDetallesPagoCancelacion(TransaccionTicket entity) throws Exception {

        List<DetallePagoServicio> listaDetallesPago = new ArrayList<>();

        List<DetalleCobroTransaccionEntity> detallesTransaccion = detalleCobroTransaccionDao
                .obtenerDetallesCobroPorTransaccion(entity.getTransaccionId());
        if (!detallesTransaccion.isEmpty()) {

            for (DetalleCobroTransaccionEntity detalleCobro : detallesTransaccion) {
                DetallePagoServicio detalle = new DetallePagoServicio();
                detalle.setConcepto("Pago Mensualidad ");
                detalle.setMonto(detalleCobro.getMonto());
                detalle.setCadenaMonto("  $".concat(String.valueOf(detalleCobro.getMonto())));
                detalle.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_CANCELACION);
                detalle.setFechaProximoPago(entity.getFechaProximoPago());
                detalle.setNumeroMeses(detalleCobro.getNumeroMeses());
                listaDetallesPago.add(detalle);
            }

        } else {
            throw new Exception("No se encontró información de la transacción");
        }

        return listaDetallesPago;
    }
    
    /**
     * 
     * @param entity
     * @return
     * @throws Exception 
     */
    private List<DetallePagoServicio> obtenerDetallesPago(TransaccionTicket entity) throws Exception {

        List<DetallePagoServicio> listaDetallesPago = new ArrayList<>();

        List<DetalleCobroTransaccionEntity> detallesTransaccion = detalleCobroTransaccionDao.obtenerDetallesCobroPorTransaccion(entity.getTransaccionId());
        if (!detallesTransaccion.isEmpty()) {

            for (DetalleCobroTransaccionEntity detalleCobro : detallesTransaccion) {
                DetallePagoServicio detalle = new DetallePagoServicio();
                if (detalleCobro.getTipoCobroId() == Constantes.TIPO_COBRO_SERVICIO) {
                    detalle.setConcepto("Pago Mensualidad ");
                    detalle.setMonto(detalleCobro.getMonto());
                    detalle.setCadenaMonto("  $".concat(String.valueOf(detalleCobro.getMonto())));
                    detalle.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_SERVICIO);
                    detalle.setFechaProximoPago(entity.getFechaProximoPago());
                    detalle.setNumeroMeses(detalleCobro.getNumeroMeses());
                    listaDetallesPago.add(detalle);
                } else if (detalleCobro.getTipoCobroId() == Constantes.TIPO_COBRO_RECARGO_MENSUALIDAD) {
                    detalle.setConcepto("Recargo por pago tardío");
                    detalle.setMonto(50.0);
                    detalle.setCadenaMonto("  $50");
                    detalle.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_RECARGO);
                    listaDetallesPago.add(detalle);
                }
            }

            List<DetalleDescuentoTransaccionEntity> detallesDescuento = detalleDescuentoTransaccionDao
                    .obtenerDetallesDescuentoPorTransaccion(entity.getTransaccionId());
            if (!detallesDescuento.isEmpty()) {

                for (DetalleDescuentoTransaccionEntity detalle : detallesDescuento) {
                    DetallePagoServicio detalleDescuento = new DetallePagoServicio();
                    StringBuilder descuento = new StringBuilder();
                    descuento.append("Descuento - ");
                    descuento.append(detalle.getObservaciones());
                    detalleDescuento.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_DESCUENTO);
                    detalleDescuento.setConcepto(descuento.toString());
                    detalleDescuento.setMonto(detalle.getMonto());
                    detalleDescuento.setCadenaMonto("- $".concat(String.valueOf(detalle.getMonto())));
                    detalleDescuento.setMotivoDescuento(detalle.getObservaciones());
                    detalleDescuento.setTipoDescuentoId(detalle.getTipoDescuentoId());
                    listaDetallesPago.add(detalleDescuento);
                }
            }

            List<DetallePromocionTransaccionEntity> detallesPromocion = detallePromocionTransaccionDao
                    .obtenerDetallesPromocionPorTransaccion(entity.getTransaccionId());
            if (!detallesPromocion.isEmpty()) {

                for (DetallePromocionTransaccionEntity detallePromo : detallesPromocion) {
                    DetallePagoServicio detallePromocion = new DetallePagoServicio();
                    StringBuilder promocion = new StringBuilder();
                    promocion.append("Promoción - ");
                    promocion.append(detallePromo.getDescripcionPromocion());
                    detallePromocion.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_PROMOCION);
                    detallePromocion.setConcepto(promocion.toString());
                    detallePromocion.setMonto(detallePromo.getCostoPromocion());
                    detallePromocion.setCadenaMonto("- $".concat(String.valueOf(detallePromo.getCostoPromocion())));
                    detallePromocion.setPromocionId(detallePromo.getPromocionId());
                    listaDetallesPago.add(detallePromocion);
                }

            }
        } else {
            throw new Exception("No se encontró información de la transacción");
        }

        return listaDetallesPago;
    }

    /**
     *
     * @param tipoCobro
     * @param fechaInicio
     * @param fechaFin
     * @return
     * @throws Exception
     */
    public List<Transaccion> consultarTransacciones(Integer tipoCobro, String fechaInicio, String fechaFin) throws Exception {

        try {

            List<Transaccion> list = new ArrayList<>();
            List<Transaccion> listFiltradas = new ArrayList<>();
            
            //primero obtener todas las transaccionesConsultadas realizadas entre las fechas solicitadas
            List<TransaccionEntity> transaccionesConsultadas = transaccionDao.obtenerTransaccionesPorFecha(fechaInicio, fechaFin);
            
            
            if(!transaccionesConsultadas.isEmpty()){
            
                //despues los detalles
                for(TransaccionEntity t : transaccionesConsultadas){
                    
                    //obtener los detalles de la transaccion
                    List<DetalleCobroTransaccion> detallesTransaccion = new ArrayList<>();
                    List<DetalleCobroTransaccionEntity> detalleCobroTransaccionEntitys = detalleCobroTransaccionDao
                            .obtenerDetallesCobroPorTransaccion(t.getTransaccionId());
                    if(!detalleCobroTransaccionEntitys.isEmpty()){
                        for(DetalleCobroTransaccionEntity d : detalleCobroTransaccionEntitys){
                            DetalleCobroTransaccion detalleCobroTransaccion = new DetalleCobroTransaccion();
                            detalleCobroTransaccion.setDescripcionOrden(d.getDescripcionOrden());
                            detalleCobroTransaccion.setDetalleId(d.getDetalleId());
                            detalleCobroTransaccion.setDetalleServerId(d.getDetalleServerId());
                            detalleCobroTransaccion.setMonto(d.getMonto());
                            detalleCobroTransaccion.setNumeroMeses(d.getNumeroMeses());
                            if(d.getOrdenId() != null)
                                detalleCobroTransaccion.setOrdenId(d.getOrdenId());
                            detalleCobroTransaccion.setServicioId(d.getServicioId());
                            detalleCobroTransaccion.setTipoCobroId(d.getTipoCobroId());
                            detalleCobroTransaccion.setTransaccionId(d.getTransaccionId());
                            detallesTransaccion.add(detalleCobroTransaccion);
                        }
                    }              
                    
                    //obtener los detalles de descuento en la transaccion
                    DetalleDescuentoTransaccion detalleDescuento = null;
                    List<DetalleDescuentoTransaccionEntity> detalleDescuentoTransaccionEntitys = detalleDescuentoTransaccionDao
                            .obtenerDetallesDescuentoPorTransaccion(t.getTransaccionId());
                    if(!detalleDescuentoTransaccionEntitys.isEmpty()){
                        DetalleDescuentoTransaccionEntity d = detalleDescuentoTransaccionEntitys.get(0);
                        detalleDescuento = new DetalleDescuentoTransaccion();
                        detalleDescuento.setDetalleId(d.getDetalleId());
                        detalleDescuento.setMonto(d.getMonto());
                        detalleDescuento.setObservaciones(d.getObservaciones());
                        detalleDescuento.setTipoDescuentoId(d.getTipoDescuentoId());
                        detalleDescuento.setTransaccionId(d.getTransaccionId());
                    }
                    
                    //obtener los detalles de promocion en la transaccion
                    DetallePromocionTransaccion detallePromocion = null;
                    List<DetallePromocionTransaccionEntity> detallePromocionTransaccionEntitys = detallePromocionTransaccionDao
                            .obtenerDetallesPromocionPorTransaccion(t.getTransaccionId());
                    if(!detallePromocionTransaccionEntitys.isEmpty()){
                        DetallePromocionTransaccionEntity d = detallePromocionTransaccionEntitys.get(0);
                        detallePromocion = new DetallePromocionTransaccion();
                        detallePromocion.setCostoPromocion(d.getCostoPromocion());
                        detallePromocion.setDescripcionPromocion(d.getDescripcionPromocion());
                        detallePromocion.setDetalleId(d.getDetalleId());
                        detallePromocion.setPromocionId(d.getPromocionId());
                        detallePromocion.setTipoPromocionId(d.getTipoPromocionId());
                        detallePromocion.setTransaccionId(d.getTransaccionId());
                    }

                    //obtener el contrato x suscriptor para posteriormente obtener datos de suscriptor
                    List<ContratoxSuscriptorEntity> contratoxSuscriptorEntitys = contratoxSuscriptorDao
                            .obtenerIdsContratoSuscriptor(t.getContratoId());
                    ContratoxSuscriptorEntity contratoxSuscriptorEntity = null;
                    if(contratoxSuscriptorEntitys
                            .stream()
                            .filter(cxs -> cxs.getContratoId().longValue() == t.getContratoId().longValue())
                            .findAny()
                            .isPresent()){
                        contratoxSuscriptorEntity = contratoxSuscriptorEntitys
                            .stream()
                            .filter(cxs -> cxs.getContratoId().longValue() == t.getContratoId().longValue())
                            .findFirst()
                            .get();
                    }
                    
                    if(contratoxSuscriptorEntity == null)
                        throw new Exception("Error al consultar operacion, por favor reintente.");

                    //obtenemos el suscriptor
                    SuscriptorEntity suscriptorEntity = suscriptorDao
                            .consultarSuscriptor(contratoxSuscriptorEntity.getSuscriptorId());
                    
                    //obtenemos el contrato
                    ContratoEntity contratoEntity = contratoDao
                            .obtenerContrato(contratoxSuscriptorEntity.getContratoId());

                    //sacar la lista de domiclios x contrato y posteriormente el domicilio activo
                    List<DomicilioPorContratoEntity> domicilioPorContratoEntitys = domicilioPorContratoDao
                            .consultarDomiciliosPorContrato(t.getContratoId());

                    DomicilioEntity domicilioTicket = null;
                    for(DomicilioPorContratoEntity dxc : domicilioPorContratoEntitys){
                        DomicilioEntity entity = domicilioDao.consultarDomicilio(dxc.getIdDomicilio());
                        if(entity.getEstatus() == Constantes.ESTATUS_DOMICILIO_CONTRATO_ACTIVO){
                            domicilioTicket = entity;
                            break;
                        }
                    }
                    
                    //sacar la lista de servicios x contrato y posteriormente el servicio activo
                    List<ServicioPorContratoEntity> servicioPorContratoEntitys = servicioPorContratoDao
                            .consultarServiciosPorContrato(t.getContratoId());
                    
                    ServicioEntity servicioTicket = null;
                    for(ServicioPorContratoEntity sxc : servicioPorContratoEntitys){
                        if(sxc.getEstatus() == Constantes.ESTATUS_ACTIVO){
                            servicioTicket = servicioDao.consultarServicio(sxc.getIdServicio());
                            break;
                        }
                    }

                    if(suscriptorEntity != null && domicilioTicket != null 
                            && contratoEntity != null && servicioTicket != null){

                        Transaccion ticket = new Transaccion();
                        ticket.setActualFechaCorte(t.getActualFechaCorte());
                        if(suscriptorEntity.getApellidoMaterno() != null)
                            ticket.setApellidoMaterno(suscriptorEntity.getApellidoMaterno());
                        else
                            ticket.setApellidoMaterno("");
                        ticket.setApellidoPaterno(suscriptorEntity.getApellidoPaterno());
                        ticket.setAperturaCajaId(t.getAperturaCajaId());
                        ticket.setCalle(domicilioTicket.getCalle());
                        if(domicilioTicket.getColonia() != null)
                            ticket.setColonia(domicilioTicket.getColonia());
                        else
                            ticket.setColonia("");
                        ticket.setContratoId(t.getContratoId());
                        ticket.setDescuento(detalleDescuento); // puede ser null
                        if(detallesTransaccion != null && !detallesTransaccion.isEmpty())
                            ticket.setDetallesCobro(detallesTransaccion);
                        ticket.setFechaTransaccion(t.getFechaTransaccion());
                        ticket.setFolioContrato(String.valueOf(contratoEntity.getFolioContrato()));
                        ticket.setMonto(t.getMonto());
                        ticket.setNombre(suscriptorEntity.getNombre());
                        ticket.setNombreSucursal(sesion.getSucursal());
                        ticket.setNuevaFechaCorte(t.getNuevaFechaCorte());
                        if(domicilioTicket.getNumeroCalle() != null)
                            ticket.setNumeroCalle(domicilioTicket.getNumeroCalle());
                        else
                            ticket.setNumeroCalle("");
                        ticket.setObservaciones(t.getObservaciones());
                        ticket.setPeriodo(t.getPeriodo());
                        ticket.setPromocion(detallePromocion); //puede ser null
                        ticket.setServicio(servicioTicket.getNombre());
                        ticket.setTelefono(suscriptorEntity.getTelefono());
                        ticket.setTransaccionId(t.getTransaccionId());
                        if(t.getTransaccionServerId() != null)
                            ticket.setTransaccionServerId(t.getTransaccionServerId());
                        else
                            ticket.setTransaccionServerId(0L);

                        list.add(ticket);

                    }else{
                        throw new Exception("Ocurrió un error al consultar operaciones, por favor reintente. "
                                + "\n SI el problema persiste contacte a soporte");
                    }
                }
                
                if(tipoCobro == Constantes.TIPO_COBRO_SERVICIO){
                    // se devuelven todas
                    listFiltradas = list;
                }else if (tipoCobro == Constantes.TIPO_COBRO_ORDEN_INSTALACION){
                    for(Transaccion t : list){
                        for(DetalleCobroTransaccion d : t.getDetallesCobro()){
                            if(d.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_INSTALACION){
                                listFiltradas.add(t);
                                break;
                            }
                        }
                    }
                }else if (tipoCobro == Constantes.TIPO_COBRO_ORDEN_CAMBIO_DOMICILIO){
                    for(Transaccion t : list){
                        for(DetalleCobroTransaccion d : t.getDetallesCobro()){
                            if(d.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_CAMBIO_DOMICILIO){
                                listFiltradas.add(t);
                                break;
                            }
                        }
                    }
                }else if (tipoCobro == Constantes.TIPO_COBRO_ORDEN_SERVICIO){
                    for(Transaccion t : list){
                        for(DetalleCobroTransaccion d : t.getDetallesCobro()){
                            if(d.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_SERVICIO){
                                listFiltradas.add(t);
                                break;
                            }
                        }
                    }
                }
                
                //if(listFiltradas.isEmpty()){
                    //throw new Exception("No se encontraron resultados.");
                //}
            
            }//else{
                //throw new Exception("No se encontraron resultados.");
            //}
            
            return listFiltradas;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al consultar transacciones para reimpresion: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }
    }

}
