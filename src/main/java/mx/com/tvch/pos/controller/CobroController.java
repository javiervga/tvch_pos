/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import mx.com.tvch.pos.client.TvchApiClient;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.CambioServicioDao;
import mx.com.tvch.pos.dao.ContratoDao;
import mx.com.tvch.pos.dao.ContratoxSuscriptorDao;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDescuentoTransaccionDao;
import mx.com.tvch.pos.dao.DetallePromocionTransaccionDao;
import mx.com.tvch.pos.dao.DomicilioDao;
import mx.com.tvch.pos.dao.DomicilioPorContratoDao;
import mx.com.tvch.pos.dao.EstatusSuscriptorDao;
import mx.com.tvch.pos.dao.OrdenCambioDomicilioDao;
import mx.com.tvch.pos.dao.OrdenInstalacionDao;
import mx.com.tvch.pos.dao.OrdenServicioDao;
import mx.com.tvch.pos.dao.PromocionDao;
import mx.com.tvch.pos.dao.RecuperacionContratoDao;
import mx.com.tvch.pos.dao.ServicioPorContratoDao;
import mx.com.tvch.pos.dao.TipoDescuentoDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.CambioServicioEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import mx.com.tvch.pos.entity.DetallePromocionTransaccionEntity;
import mx.com.tvch.pos.entity.DomicilioEntity;
import mx.com.tvch.pos.entity.DomicilioPorContratoEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.OrdenCambioDomicilioEntity;
import mx.com.tvch.pos.entity.OrdenInstalacionEntity;
import mx.com.tvch.pos.entity.OrdenServicioEntity;
import mx.com.tvch.pos.entity.PromocionEntity;
import mx.com.tvch.pos.entity.RecuperacionContratoEntity;
import mx.com.tvch.pos.entity.ServicioPorContratoEntity;
import mx.com.tvch.pos.entity.TipoDescuentoEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.model.CobroServicio;
import mx.com.tvch.pos.model.OrdenAgregadaPago;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroController {

    private static CobroController controller;

    private final ContratoxSuscriptorDao dao;
    private final EstatusSuscriptorDao estatusSuscriptorDao;
    private final TipoDescuentoDao tipoDescuentoDao;
    private final PromocionDao promocionDao;
    private final ContratoDao contratoDao;
    private final TransaccionDao transaccionDao;
    private final DetalleCobroTransaccionDao detalleCobroTransaccionDao;
    private final DetallePromocionTransaccionDao detallePromocionTransaccionDao;
    private final DetalleDescuentoTransaccionDao detalleDescuentoTransaccionDao;
    private final OrdenInstalacionDao ordenInstalacionDao;
    private final OrdenServicioDao ordenServicioDao;
    private final OrdenCambioDomicilioDao ordenCambioDomicilioDao;
    private final DomicilioPorContratoDao domicilioPorContratoDao;
    private final ServicioPorContratoDao servicioPorContratoDao;
    private final CambioServicioDao cambioServicioDao;
    private final DomicilioDao domicilioDao;
    private final RecuperacionContratoDao recuperacionContratoDao;
    private final Utilerias util;
    private final Sesion sesion;
    private final TvchApiClient client;

    Logger logger = LoggerFactory.getLogger(CobroController.class);

    public static CobroController getCobroController() {
        if (controller == null) {
            controller = new CobroController();
        }
        return controller;
    }

    public CobroController() {
        dao = ContratoxSuscriptorDao.getContratoxSuscriptorDao();
        estatusSuscriptorDao = EstatusSuscriptorDao.getEstatusSuscriptorDao();
        tipoDescuentoDao = TipoDescuentoDao.getTipoDescuentoDao();
        promocionDao = PromocionDao.getPromocionDao();
        contratoDao = ContratoDao.getContratoDao();
        transaccionDao = TransaccionDao.getTransaccionDao();
        detalleCobroTransaccionDao = DetalleCobroTransaccionDao.getDetalleCobroTransaccionDao();
        detalleDescuentoTransaccionDao = DetalleDescuentoTransaccionDao.getDetalleDescuentoTransaccionDao();
        detallePromocionTransaccionDao = DetallePromocionTransaccionDao.getDetallePromocionTransaccionDao();
        recuperacionContratoDao = RecuperacionContratoDao.getRecuperacionContratoDao();
        ordenInstalacionDao = OrdenInstalacionDao.getOrdenInstalacionDao();
        ordenServicioDao = OrdenServicioDao.getOrdenServicioDao();
        ordenCambioDomicilioDao = OrdenCambioDomicilioDao.getOrdenCambioDomicilioDao();
        cambioServicioDao = CambioServicioDao.getCambioServicioDao();
        domicilioDao = DomicilioDao.getDomicilioDao();
        domicilioPorContratoDao = DomicilioPorContratoDao.getDomicilioPorContratoDao();
        servicioPorContratoDao = ServicioPorContratoDao.getServicioPorContratoDao();
        util = Utilerias.getUtilerias();
        sesion = Sesion.getSesion();
        client = TvchApiClient.getTvchApiClient();
    }
    
    /**
     * 
     * @param suscriptor
     * @throws Exception 
     */
    public void recuperarContrato(ContratoxSuscriptorDetalleEntity suscriptor) throws Exception{
        
        //primero actualizar la fecha de pago al mes en curso
        Date nuevaFechaCorte = util.obtenerFechaCorteDelMesEnCurso();
        String nuevaFechaPagoMySql = util.convertirDateTime2String(nuevaFechaCorte, Constantes.FORMATO_FECHA_MYSQL);
        contratoDao.actualizarFechaPagoContrato(suscriptor.getContratoId(), nuevaFechaPagoMySql);
        
        //despues se actualiza el estatus del contrato a activo
        contratoDao.actualizarEstatus(
                suscriptor.getContratoId(), 
                Constantes.ESTATUS_CONTRATO_ACTIVO, 
                Constantes.TIPO_ACTUALIZACION_CONTRATO_NO_ACTUALIZAR);
        
        //se genera un registro de la recuperacion
        RecuperacionContratoEntity entity = new RecuperacionContratoEntity();
        entity.setContratoId(suscriptor.getContratoId());
        entity.setCosto(0d);
        entity.setFechaRecuperacion(new Date());
        entity.setObservaciones("");
        entity.setRecuperacionId(util.generarIdLocal());
        entity.setSucursalId(sesion.getSucursalId());
        entity.setUsuarioId(sesion.getUsuarioId());
        entity.setNuevaFechaPago(nuevaFechaPagoMySql);
        recuperacionContratoDao.registrarRecuperacionContrato(entity);

        //se genera la orden de instalacion
        /*OrdenInstalacionEntity ordenInstalacionEntity = new OrdenInstalacionEntity();
        ordenInstalacionEntity.setContratoId(suscriptor.getContratoId());
        ordenInstalacionEntity.setCosto(0d);
        ordenInstalacionEntity.setDomicilioId(suscriptor.getDomicilioId());
        ordenInstalacionEntity.setEstatusId(Constantes.ESTATUS_ORDEN_PAGADA);
        ordenInstalacionEntity.setObservacionesRegistro("RECUPERACION DE CONTRATO");
        ordenInstalacionEntity.setOrdenId(util.generarIdLocal());
        ordenInstalacionEntity.setServicioId(suscriptor.getServicioId());
        ordenInstalacionEntity.setSuscriptorId(suscriptor.getSusucriptorId());
        ordenInstalacionEntity.setTvs(suscriptor.getTvsContratadas());
        ordenInstalacionEntity.setUsuarioId(sesion.getUsuarioId());
        ordenInstalacionDao.registrarOrdenInstalacion(ordenInstalacionEntity);*/
        
        OrdenServicioEntity ordenServicioEntity = new OrdenServicioEntity();
        ordenServicioEntity.setContratoId(suscriptor.getContratoId());
        ordenServicioEntity.setCosto(0d);
        ordenServicioEntity.setDomicilioId(suscriptor.getDomicilioId());
        ordenServicioEntity.setEstatusId(Constantes.ESTATUS_ORDEN_PAGADA);
        //ordenServicioEntity.setFechaRegistro(nuevaFechaCorte);
        ordenServicioEntity.setObservacionesRegistro("RECUPERACION DE CONTRATO");
        ordenServicioEntity.setOrdenId(util.generarIdLocal());
        ordenServicioEntity.setServicioId(suscriptor.getServicioId());
        ordenServicioEntity.setSuscriptorId(suscriptor.getSusucriptorId());
        ordenServicioEntity.setTipoOrdenServicioId(Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO);
        ordenServicioEntity.setUsuarioId(sesion.getUsuarioId());
        ordenServicioDao.registrarOrdenServicio(ordenServicioEntity);
            

        
    }
    
    /**
     * 
     * @param suscriptor
     * @param cobro
     * @param sePasaReconexion
     * @return
     * @throws Exception 
     */
    public Long cobrarServicio(ContratoxSuscriptorDetalleEntity suscriptor, CobroServicio cobro, boolean sePasaReconexion) throws Exception {

        Long transaccionId = null;
        Integer numeroMeses = cobro.getMesesPagados();

        try {

            String nuevaFechaPagoTicket = "";
            String nuevaFechaPagoMySql = "";
            if (cobro.getPromocion() != null) {
                nuevaFechaPagoMySql = util.convertirDateTime2String(cobro.getFechaProximoPago(), Constantes.FORMATO_FECHA_MYSQL);
                nuevaFechaPagoTicket = cobro.getFechaProximoPagoTicket();

            } else {
                nuevaFechaPagoMySql = util.convertirDateTime2String(cobro.getFechaProximoPago(), Constantes.FORMATO_FECHA_MYSQL);
                nuevaFechaPagoTicket = cobro.getFechaProximoPagoTicket();

            }

            //registrar la transaccion
            TransaccionEntity transaccionEntity = new TransaccionEntity();
            transaccionEntity.setAperturaCajaId(sesion.getAperturaCajaId());
            transaccionEntity.setContratoId(suscriptor.getContratoId());
            transaccionEntity.setFechaTransaccion(util.obtenerFechaFormatoMysql());
            transaccionEntity.setMonto(cobro.getMontoTotal());//monto a pagar ya con descuentos y promociones aplicadas si existieran
            transaccionEntity.setTransaccionId(util.generarIdLocal());
            transaccionEntity.setObservaciones(cobro.getObservaciones().toUpperCase());
            if(cobro.isSeCobraServicio())
                transaccionEntity.setPeriodo(cobro.getConcepto().replace("Pago", "").toUpperCase());
            else{
                int numeroOrdenes = cobro.getOrdenesPago().size();
                if(numeroMeses == 1){
                    transaccionEntity.setPeriodo("PAGO DE 1 ORDEN");
                }else{
                    transaccionEntity.setPeriodo("PAGO DE ".concat(String.valueOf(numeroOrdenes)).concat(" ORDENES"));
                }
                
            }
            transaccionEntity.setActualFechaCorte(util.convertirDateTime2String(suscriptor.getFechaProximoPago(), Constantes.FORMATO_FECHA_MYSQL));
            transaccionEntity.setNuevaFechaCorte(util.convertirDateTime2String(cobro.getFechaProximoPago(), Constantes.FORMATO_FECHA_MYSQL));
            
            transaccionId = transaccionEntity.getTransaccionId();
            transaccionDao.registrarTransaccion(transaccionEntity);

            //registrar los detalles de la transaccion
            
            //primero ver si trae cobro de mensualidad
            if(cobro.isSeCobraServicio()){
                DetalleCobroTransaccionEntity detalleCobroTransaccionEntity = new DetalleCobroTransaccionEntity();
                detalleCobroTransaccionEntity.setMonto(cobro.getMontoServicio()*cobro.getMesesPagados());// monto sin descuentos ni promociones aplicadas
                detalleCobroTransaccionEntity.setServicioId(suscriptor.getServicioId());
                detalleCobroTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_SERVICIO);
                detalleCobroTransaccionEntity.setTransaccionId(transaccionId);
                detalleCobroTransaccionEntity.setNumeroMeses(numeroMeses);
                Long detalleCobroId = detalleCobroTransaccionDao.registrarDetalleTransaccion(detalleCobroTransaccionEntity);

                //registrar el recargo del servicio
                if (cobro.isSeCobraRecargo()) {
                    DetalleCobroTransaccionEntity detalleRecargoTransaccionEntity = new DetalleCobroTransaccionEntity();
                    detalleRecargoTransaccionEntity.setMonto(cobro.getMontoRecargo());// monto sin descuentos ni promociones aplicadas
                    detalleRecargoTransaccionEntity.setServicioId(suscriptor.getServicioId());
                    detalleRecargoTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_RECARGO_MENSUALIDAD);
                    detalleRecargoTransaccionEntity.setTransaccionId(transaccionId);
                    detalleRecargoTransaccionEntity.setNumeroMeses(numeroMeses);
                    Long detalleRecargoId = detalleCobroTransaccionDao.registrarDetalleTransaccion(detalleRecargoTransaccionEntity);
                }
                
                //registrar el detalle de promocion
                if (cobro.getPromocion() != null) {
                    DetallePromocionTransaccionEntity detallePromocionTransaccionEntity = new DetallePromocionTransaccionEntity();
                    detallePromocionTransaccionEntity.setCostoPromocion(cobro.getPromocion().getCostoPromocion());
                    detallePromocionTransaccionEntity.setDescripcionPromocion(cobro.getPromocion().getDescripcion());
                    detallePromocionTransaccionEntity.setPromocionId(cobro.getPromocion().getPromocionId());
                    detallePromocionTransaccionEntity.setTransaccionId(transaccionId);
                    detallePromocionTransaccionEntity.setTipoPromocionId(Constantes.TIPO_PROMOCION_SERVICIO);
                    Long detallePromocionId = detallePromocionTransaccionDao.registrarDetallePromocion(detallePromocionTransaccionEntity);
                }
                
                //registrar el detalle de decuento
                if (cobro.getDescuento() != null) {
                    DetalleDescuentoTransaccionEntity detalleDescuentoTransaccionEntity = new DetalleDescuentoTransaccionEntity();
                    detalleDescuentoTransaccionEntity.setMonto(cobro.getDescuento().getMontoDescuento());
                    detalleDescuentoTransaccionEntity.setObservaciones(util.limpiarAcentos(cobro.getDescuento().getMotivoDescuento()).toUpperCase());
                    detalleDescuentoTransaccionEntity.setTipoDescuentoId(cobro.getDescuento().getTipoDescuentoId());
                    detalleDescuentoTransaccionEntity.setTransaccionId(transaccionId);
                    Long detalleDescuentoId = detalleDescuentoTransaccionDao.registrarDetalleDescuento(detalleDescuentoTransaccionEntity);
                }
                
                //actualizar la fecha de pago en el contrato
                contratoDao.actualizarFechaPagoContrato(suscriptor.getContratoId(), nuevaFechaPagoMySql);
                
                //validar el estatus del contrato
                if(cobro.isSeCobraServicio() && cobro.isSeCobraRecargo() 
                        && suscriptor.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
                    
                    //actualizar el estatus a reconexion
                    //y la bandera de actualizacion apagada, ya que el cambio de estatus en el server se hara cuando llegue la orden reconexion
                    if(sePasaReconexion)
                        contratoDao.actualizarEstatus(suscriptor.getContratoId(), 
                                Constantes.ESTATUS_CONTRATO_RECONEXION,
                                Constantes.TIPO_ACTUALIZACION_CONTRATO_NO_ACTUALIZAR);
                    else{
                        //actualizar contrato a activo
                        //unico caso donde se manda bandera de actualiacion en 2
                        //ya que al no generar y enviar orden de reconexion al server, el contrato no pasaria a activo
                        //de esta forma con el valor en 2 se envia el estatus a actualizar en la sincronizacion del cron
                        contratoDao.actualizarEstatus(
                                suscriptor.getContratoId(), 
                                Constantes.ESTATUS_CONTRATO_ACTIVO, 
                                Constantes.TIPO_ACTUALIZACION_CONTRATO_INFORMACION_Y_ESTATUS);
                    }
                        
                }else if(suscriptor.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_RECONEXION){
                    //se quita esta actualizacion ya que aunque pague, sino tiene orden reconexion se queda igual en reconexion
                    //para actualizar a activo se implemento desde la edicion de contrato
                    //actualizar el contrato a activo
                    //bandera apagada ya que el estatus cambiara en el server a activo cuando llegue la transaccion
                    //contratoDao.actualizarEstatus(suscriptor.getContratoId(), 
                     //       Constantes.ESTATUS_CONTRATO_ACTIVO, 
                       //     Constantes.TIPO_ACTUALIZACION_CONTRATO_NO_ACTUALIZAR);
                    
                }
                
            }

            if(cobro.getOrdenesPago() != null && !cobro.getOrdenesPago().isEmpty()){
                
                for(OrdenAgregadaPago orden : cobro.getOrdenesPago()){
                    
                    //ir registrado la orden y posteriormente generar los detalles
                    
                    //primero obtener el id de sucursal
                    Long ordenId = util.generarIdLocal();
                    
                    //generar el detalle de la transaccion
                    DetalleCobroTransaccionEntity detalleCobroTransaccionEntity = new DetalleCobroTransaccionEntity();
                    detalleCobroTransaccionEntity.setMonto(orden.getCosto());// monto sin descuentos ni promociones aplicadas
                    detalleCobroTransaccionEntity.setServicioId(orden.getServicioId());
                    if(orden.getTipoOrden() == Constantes.TIPO_ORDEN_INSTALACION){
                        detalleCobroTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_ORDEN_INSTALACION);
                    }else if(orden.getTipoOrden() == Constantes.TIPO_ORDEN_SERVICIO){
                        detalleCobroTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_ORDEN_SERVICIO);
                    }else if(orden.getTipoOrden() == Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO){
                        detalleCobroTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_ORDEN_CAMBIO_DOMICILIO);
                    }else{
                        detalleCobroTransaccionEntity.setTipoCobroId(orden.getTipoOrden());///no deberia caer nunca aqui
                    }
                    
                    detalleCobroTransaccionEntity.setTransaccionId(transaccionId);
                    detalleCobroTransaccionEntity.setOrdenId(ordenId);
                    
                    String descripcionOrden = "";
                    if(orden.getTipoOrden() == Constantes.TIPO_ORDEN_INSTALACION || 
                            orden.getTipoOrden() == Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO){
                        descripcionOrden = orden.getTipoOrdenDesc();
                    }else{
                        descripcionOrden = orden.getTipoOrdenServicioDesc();
                    }
                    
                    detalleCobroTransaccionEntity.setDescripcionOrden(util.limpiarAcentos(descripcionOrden).toUpperCase());
                    detalleCobroTransaccionEntity.setNumeroMeses(0);
                    detalleCobroTransaccionDao.registrarDetalleTransaccion(detalleCobroTransaccionEntity);
                    
                    //generar la orden correspondiente
                    if(orden.getTipoOrden() == Constantes.TIPO_ORDEN_INSTALACION){
                        
                        registrarOrdenInstalacion(orden, ordenId);
                        //actualizar el contrato a estatus activo despues de hacerle su orden
                        contratoDao.actualizarEstatus(orden.getContratoId(), 
                                Constantes.ESTATUS_CONTRATO_ACTIVO,
                                Constantes.TIPO_ACTUALIZACION_CONTRATO_NO_ACTUALIZAR);
                        
                    }else if(orden.getTipoOrden() == Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO){
                        
                        deshabilitarDomiciliosAnteriores(orden.getContratoId());
                        Long nuevoDOmicilioId = util.generarIdLocal();
                        registrarNuevoDomicilio(orden, nuevoDOmicilioId);
                        registrarOrdenCambioDomicilio(orden, ordenId, nuevoDOmicilioId);
                        
                      
                    }else if(orden.getTipoOrden() == Constantes.TIPO_ORDEN_SERVICIO){
                        
                        //primero registrar la orden
                        registrarOrdenServicio(orden, ordenId);
                        
                        //despues dependiendo de la orden ir actualizando informacion del contrato en bd
                        if(orden.getTipoOrdenServicio() == Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN){
                            registrarCambioServicio(orden, ordenId);
                            deshabilitarServiciosAnteriores(orden.getContratoId());
                            registrarNuevoServicioPorContrato(orden);
                        } else if(orden.getTipoOrdenServicio() == Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL){
                            //aca actualizar las tvs, la bandera de act del contrato se pone en 1 por default, esto es correcto
                            Integer nuevoNumeroTvs = orden.getTvs() + orden.getTvsAdicionales();
                            contratoDao.actualizarNumeroTvs(orden.getContratoId(), nuevoNumeroTvs);
                        }else if(orden.getTipoOrdenServicio() == Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO){
                            //aca actualizar el estatus del contrato a activo
                            //cuando lelgue al server se actualiza el estatus del contrato a activo, entonces no prender bandera actualizacion
                            contratoDao.actualizarEstatus(orden.getContratoId(), 
                                    Constantes.ESTATUS_CONTRATO_ACTIVO,
                                    Constantes.TIPO_ACTUALIZACION_CONTRATO_NO_ACTUALIZAR);
                        }else if(orden.getTipoOrdenServicio() == Constantes.TIPO_ORDEN_SERVICIO_RETIRO_EQUIPO_CANCELACION){
                            //aca actualizar el estatus del contrato
                            //en server se actualiza el estatus al llegar la cancelacion, no prender bandera
                            contratoDao.actualizarEstatus(orden.getContratoId(), 
                                    Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO,
                                    Constantes.TIPO_ACTUALIZACION_CONTRATO_NO_ACTUALIZAR);
                        }
                    }
                    
                    
                }
                
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al realizar cobro: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }

        return transaccionId;

    }
    
    private void registrarNuevoServicioPorContrato(OrdenAgregadaPago orden){
        
        ServicioPorContratoEntity entity = new ServicioPorContratoEntity();
        entity.setEstatus(1);
        entity.setIdContrato(orden.getContratoId());
        entity.setIdServicio(orden.getNuevoServicioId());
        servicioPorContratoDao.registrarServicioPorContrato(entity);
        
    }
    
    /**
     * 
     */
    private void deshabilitarServiciosAnteriores(Long contratoId){
        try {
            List<ServicioPorContratoEntity> list = servicioPorContratoDao.consultarServiciosPorContrato(contratoId);
            for(ServicioPorContratoEntity sxc : list){
                sxc.setEstatus(0);
                servicioPorContratoDao.actualizarEstatusServicioPorContrato(sxc);
            }
        } catch (Exception ex) {
            logger.error("Error al desactivar domicilios");
        }
    }
    
    /**
     * 
     */
    private void deshabilitarDomiciliosAnteriores(Long contratoId){
        try {
            List<DomicilioPorContratoEntity> list = domicilioPorContratoDao.consultarDomiciliosPorContrato(contratoId);
            for(DomicilioPorContratoEntity dxc : list){
                domicilioDao.actualizarEstatusDomicilio(dxc.getIdDomicilio(), Constantes.ESTATUS_INACTIVO);
            }
        } catch (Exception ex) {
            logger.error("Error al desactivar domicilios");
        }
    }
    
    /**
     * 
     * @param orden 
     */
    private void registrarNuevoDomicilio(OrdenAgregadaPago orden, Long domicilioId){
        DomicilioEntity entity = new DomicilioEntity();
        entity.setCalle(orden.getCalle().toUpperCase());
        entity.setCalle1(orden.getCalle1().toUpperCase());
        entity.setCalle2(orden.getCalle2().toUpperCase());
        entity.setCiudad(orden.getCiudad().toUpperCase());
        entity.setColonia(orden.getColonia().toUpperCase());
        entity.setEstatus(Constantes.ESTATUS_DOMICILIO_CONTRATO_ACTIVO);
        entity.setId(domicilioId);
        entity.setNumeroCalle(orden.getNumeroCalle().toUpperCase());
        entity.setReferencia(orden.getReferencia().toUpperCase());
        domicilioDao.registrarDomicilio(entity);
        
        DomicilioPorContratoEntity domicilioPorContratoEntity = new DomicilioPorContratoEntity();
        domicilioPorContratoEntity.setIdContrato(orden.getContratoId());
        domicilioPorContratoEntity.setIdDomicilio(entity.getId());
        domicilioPorContratoDao.registrarDomicilioPorContrato(domicilioPorContratoEntity);
        
    }
    
    /**
     * 
     * @param orden
     * @param ordenId 
     */
    private void registrarCambioServicio (OrdenAgregadaPago orden, Long ordenId){
        CambioServicioEntity entity = new CambioServicioEntity();
        entity.setCambioId(util.generarIdLocal());
        entity.setContratoId(orden.getContratoId());
        entity.setCosto(orden.getCosto());
        entity.setOrdenServicioId(ordenId);
        entity.setServicioActualId(orden.getServicioId());
        entity.setServicioNuevoId(orden.getNuevoServicioId());
        entity.setSucursalId(orden.getSucursalId());
        entity.setUsuarioId(orden.getUsuarioId());
        cambioServicioDao.registrarCambioServicio(entity);
    }
    
    /**
     * 
     * @param orden
     * @param ordenId 
     */
    private void registrarOrdenServicio (OrdenAgregadaPago orden, Long ordenId){
        OrdenServicioEntity entity = new OrdenServicioEntity();
        entity.setContratoId(orden.getContratoId());
        entity.setCosto(orden.getCosto());
        entity.setDomicilioId(orden.getDomicilioId());
        entity.setEstatusId(Constantes.ESTATUS_ORDEN_PAGADA);
        entity.setObservacionesRegistro(util.limpiarAcentos(orden.getObservaciones()).toUpperCase());
        entity.setOrdenId(ordenId);
        entity.setServicioId(orden.getServicioId());
        entity.setSuscriptorId(orden.getSuscriptorId());
        entity.setTipoOrdenServicioId(orden.getTipoOrdenServicio());
        entity.setUsuarioId(orden.getUsuarioId());
        ordenServicioDao.registrarOrdenServicio(entity);
    }
    
    /**
     * 
     * @param orden
     * @param ordenId 
     */
    private void registrarOrdenCambioDomicilio (OrdenAgregadaPago orden, Long ordenId, Long nuevoDomicilioId){
        OrdenCambioDomicilioEntity entity = new OrdenCambioDomicilioEntity();
        entity.setCalle(orden.getCalle());
        entity.setCalle1(orden.getCalle1());
        entity.setCalle2(orden.getCalle2());
        entity.setCiudad(orden.getCiudad());
        entity.setColonia(orden.getColonia());
        entity.setContratoId(orden.getContratoId());
        entity.setCosto(orden.getCosto());
        entity.setDomicilioId(orden.getDomicilioId());
        entity.setDomicilioNuevoId(nuevoDomicilioId);
        entity.setEstatusId(Constantes.ESTATUS_ORDEN_PAGADA);
        entity.setNumeroCalle(orden.getNumeroCalle());
        entity.setObservacionesRegistro(orden.getObservaciones());
        entity.setOrdenId(ordenId);
        entity.setReferencia(orden.getReferencia());
        entity.setServicioId(orden.getServicioId());
        entity.setSuscriptorId(orden.getSuscriptorId());
        entity.setUsuarioId(orden.getUsuarioId());
        ordenCambioDomicilioDao.registrarCambioDomicilio(entity);
    }
    
    /**
     * 
     * @param orden
     * @param ordenId 
     */
    private void registrarOrdenInstalacion (OrdenAgregadaPago orden, Long ordenId){
        OrdenInstalacionEntity entity = new OrdenInstalacionEntity();
        entity.setContratoId(orden.getContratoId());
        entity.setCosto(orden.getCosto());
        entity.setDomicilioId(orden.getDomicilioId());
        entity.setEstatusId(Constantes.ESTATUS_ORDEN_PAGADA);
        entity.setObservacionesRegistro(orden.getObservaciones());
        entity.setOrdenId(ordenId);
        entity.setServicioId(orden.getServicioId());
        entity.setSuscriptorId(orden.getSuscriptorId());
        entity.setTvs(orden.getTvs());
        entity.setUsuarioId(orden.getUsuarioId());
        ordenInstalacionDao.registrarOrdenInstalacion(entity);
    }

    /**
     *
     * @return @throws Exception
     */
    public List<PromocionEntity> consultarPromociones(Long servicioId) throws Exception {

        try {

            List<PromocionEntity> list = promocionDao.obtenerPromocionesActivas(servicioId, sesion.getSucursalId());

            if (list == null) {
                throw new NoSuchElementException(("No se encontraron tipos de descuentos registrados."));
            }

            return list;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar promociones: \n" + sw.toString());
            throw new Exception("Error al consultar promociones. Por favor vuelva a intentar, si el problema persiste contacte a soporte.");
        }

    }

    /**
     *
     * @return @throws Exception
     */
    public List<TipoDescuentoEntity> consultarTiposDescuento() throws Exception {

        try {

            List<TipoDescuentoEntity> list = tipoDescuentoDao.obtenerTiposDescuento();

            if (list == null) {
                throw new NoSuchElementException(("No se encontraron tipos de descuentos registrados."));
            }

            return list;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar tipos de descuento: \n" + sw.toString());
            throw new Exception("Error al consultar tipo de descuento. Por favor vuelva a intentar, si el problema persiste contacte a soporte.");
        }

    }

    public List<ContratoxSuscriptorDetalleEntity> consultarSuscriptores(Long contratoId, int tipoBusquedaCobro, String cadenaBusqueda, boolean seBuscanCancelados) throws Exception {

        try {

            List<ContratoxSuscriptorDetalleEntity> list = dao.obtenerContratosSuscriptor(contratoId, tipoBusquedaCobro, cadenaBusqueda);
            
            //validar y quitar los cancelados
            if(seBuscanCancelados){
                list = list
                    .stream()
                    .filter(s -> s.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO || s.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO)
                    .collect(Collectors.toList());
            }else{
                list = list
                    .stream()
                    .filter(s -> s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO && s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO)
                    .collect(Collectors.toList());
            }

            if (list == null) {
                throw new NoSuchElementException(("No se encontraron suscriptores registrados con la información solicitada."));
            }

            return list;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar suscriptores: \n" + sw.toString());
            throw new Exception("Error al consultar suscriptores. Por favor vuelva a intentar, si el problema persiste contacte a soporte.");
        }

    }

    public List<EstatusSuscriptorEntity> consultarEstatusSuscriptor() throws Exception {

        try {

            return estatusSuscriptorDao.obtenerEstatusSuscriptor();

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar estatus de suscriptor: \n" + sw.toString());
            throw new Exception("Error al consultar tipos de salida. Por favor contacte a soporte.");
        }

    }

    /**
     *
     * @return
     */
    /*public boolean seDebeGenerarRecargo(ContratoxSuscriptorDetalleEntity suscriptorSeleccionado) {
        boolean seGeneraRecargo = false;

        if (suscriptorSeleccionado.getFechaProximoPago() != null) {

            //primero poner fechas de pago y del dia a formato sin horas y minutos
            Calendar fechaPago = Calendar.getInstance();
            fechaPago.setTime(suscriptorSeleccionado.getFechaProximoPago());
            fechaPago.set(Calendar.HOUR_OF_DAY, 0);
            fechaPago.set(Calendar.MINUTE, 0);
            fechaPago.set(Calendar.SECOND, 0);
            fechaPago.set(Calendar.MILLISECOND, 0);
            Calendar fechaHoy = Calendar.getInstance();
            fechaHoy.setTime(new Date());
            fechaHoy.set(Calendar.HOUR_OF_DAY, 0);
            fechaHoy.set(Calendar.MINUTE, 0);
            fechaHoy.set(Calendar.SECOND, 0);
            fechaHoy.set(Calendar.MILLISECOND, 0);

            if (fechaHoy.after(fechaPago)) {
                seGeneraRecargo = true;
            }

        }

        return seGeneraRecargo;
    }*/

    /**
     *
     * @param suscriptorSeleccionado
     * @param detalles
     * @return
     */
    /*public String obtenerImporteActualizado(List<DetallePagoServicio> detalles) {

        String cadenaImporte = "0.00";

        Double importe = 0.0;
        Double importeRecargo = 0.0;
        for (DetallePagoServicio detalle : detalles) {
            if (null != detalle.getTipoDetalle()) {
                switch (detalle.getTipoDetalle()) {
                    case Constantes.TIPO_DETALLE_COBRO_SERVICIO:
                        importe = importe + detalle.getMonto();
                        break;
                    case Constantes.TIPO_DETALLE_COBRO_RECARGO:
                        importe = importe + detalle.getMonto();
                        importeRecargo = detalle.getMonto();
                        break;
                    case Constantes.TIPO_DETALLE_COBRO_PROMOCION:
                        importe = detalle.getMonto() + importeRecargo;
                        break;
                    case Constantes.TIPO_DETALLE_COBRO_DESCUENTO:
                        importe = importe - detalle.getMonto();
                        break;
                    default:
                        break;
                }
            }
        }

        if (importe < 0) {
            cadenaImporte = "0.00";
        } else {
            cadenaImporte = String.valueOf(importe);
        }

        return cadenaImporte;

    }*/
    
    /*public Double obtenerMontoPorCobrar(ContratoxSuscriptorDetalleEntity suscriptorSeleccionado){
        
        Double monto = 0.0;
                    
        Calendar fechaProximoPago = Calendar.getInstance();
        fechaProximoPago.setTime(suscriptorSeleccionado.getFechaProximoPago());
        int anioFechaProximoPago = fechaProximoPago.get(Calendar.YEAR);
        int mesFechaProximoPago = fechaProximoPago.get(Calendar.MONTH);
        int diaFechaProximoPago = fechaProximoPago.get(Calendar.DAY_OF_MONTH);
        fechaProximoPago.set(Calendar.HOUR_OF_DAY, 0);
        fechaProximoPago.set(Calendar.MINUTE, 0);
        fechaProximoPago.set(Calendar.SECOND, 0);
        fechaProximoPago.set(Calendar.MILLISECOND, 0);
            
        Calendar fechaHoy = Calendar.getInstance();
        fechaHoy.setTime(new Date());
        int mesHoy = fechaHoy.get(Calendar.MONTH);
        int anioHoy = fechaHoy.get(Calendar.YEAR);
        int diaHoy = fechaHoy.get(Calendar.DAY_OF_MONTH);
        fechaHoy.set(Calendar.HOUR_OF_DAY, 0);
        fechaHoy.set(Calendar.MINUTE, 0);
        fechaHoy.set(Calendar.SECOND, 0);
        fechaHoy.set(Calendar.MILLISECOND, 0);
        
 
        //si esta vencido es porque la fecha ya se paso
        int diferenciaAnios = anioHoy - anioFechaProximoPago;
        int diferenciaMeses = (diferenciaAnios*12) + mesHoy - mesFechaProximoPago;
        monto = (suscriptorSeleccionado.getCostoServicio() * (diferenciaMeses+1)) ;
       
        return monto;
    }*/
    
    /**
     *
     * @param suscriptorSeleccionado
     * @return
     */
    /*public Integer obtenerMesesAtrasado(ContratoxSuscriptorDetalleEntity suscriptorSeleccionado){
        
        Double monto = 0.0;
                    
        Calendar fechaProximoPago = Calendar.getInstance();
        fechaProximoPago.setTime(suscriptorSeleccionado.getFechaProximoPago());
        int anioFechaProximoPago = fechaProximoPago.get(Calendar.YEAR);
        int mesFechaProximoPago = fechaProximoPago.get(Calendar.MONTH);
        int diaFechaProximoPago = fechaProximoPago.get(Calendar.DAY_OF_MONTH);
        fechaProximoPago.set(Calendar.HOUR_OF_DAY, 0);
        fechaProximoPago.set(Calendar.MINUTE, 0);
        fechaProximoPago.set(Calendar.SECOND, 0);
        fechaProximoPago.set(Calendar.MILLISECOND, 0);
            
        Calendar fechaHoy = Calendar.getInstance();
        fechaHoy.setTime(new Date());
        int mesCancelacion = fechaHoy.get(Calendar.MONTH);
        int anioCancelacion = fechaHoy.get(Calendar.YEAR);
        int diaCancelacion = fechaHoy.get(Calendar.DAY_OF_MONTH);
        fechaHoy.set(Calendar.HOUR_OF_DAY, 0);
        fechaHoy.set(Calendar.MINUTE, 0);
        fechaHoy.set(Calendar.SECOND, 0);
        fechaHoy.set(Calendar.MILLISECOND, 0);
        
 
        //si esta vencido es porque la fecha ya se paso
        int diferenciaAnios = anioCancelacion - anioFechaProximoPago;
        int diferenciaMeses = (diferenciaAnios*12) + mesCancelacion - mesFechaProximoPago;
       
        return diferenciaMeses+1;
    }*/
    
    /**
     * 
     * @param suscriptorSeleccionado
     * @param seDebeGenerarorden
     * @return 
     * @throws Exception 
     */
    /*public Response<UpdateContratoResponse> actualizarContratoReconexion(ContratoxSuscriptorDetalleEntity suscriptorSeleccionado, boolean seDebeGenerarorden) throws Exception{
        
        try{
            
            //paso 1 -> actualizar el estatus del contrato en local
            if(seDebeGenerarorden)
                contratoDao.actualizarEstatus(suscriptorSeleccionado.getContratoId(), Constantes.ESTATUS_CONTRATO_RECONEXION);
            else
                contratoDao.actualizarEstatus(suscriptorSeleccionado.getContratoId(), Constantes.ESTATUS_CONTRATO_ACTIVO);
        
            //paso 2-> enviar peticion de actualizacion de estaus y posible generacion de orden a central
            UpdateContratoEstatusReconexionPosRequest reconexionPosRequest = new UpdateContratoEstatusReconexionPosRequest();
            reconexionPosRequest.setContratoId(suscriptorSeleccionado.getContratoId());
            reconexionPosRequest.setUsuarioId(sesion.getUsuarioId());
            if(seDebeGenerarorden)
                reconexionPosRequest.setGenerarOrdenReconexion(1);
            else
                reconexionPosRequest.setGenerarOrdenReconexion(0);
            Request<UpdateContratoEstatusReconexionPosRequest> request = new Request<>();
            request.setData(reconexionPosRequest);
            Response<UpdateContratoResponse> response = client.updateEstatusContratoReconexion(request);
            return response;
            
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al realizar cobro: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }
    
    }*/

}
