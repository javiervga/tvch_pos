/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import mx.com.tvch.pos.client.TvchApiClient;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.CancelacionDao;
import mx.com.tvch.pos.dao.ContratoDao;
import mx.com.tvch.pos.dao.ContratoxSuscriptorDao;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.EstatusSuscriptorDao;
import mx.com.tvch.pos.dao.MotivoCancelacionDao;
import mx.com.tvch.pos.dao.OrdenServicioDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.CancelacionEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.MotivoCancelacionEntity;
import mx.com.tvch.pos.entity.OrdenServicioEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.model.client.OrdenServicio;
import mx.com.tvch.pos.model.client.Request;
import mx.com.tvch.pos.model.client.UpdateContratoEstatusCanceladoPosRequest;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CancelarContratoController {
    
    private static CancelarContratoController controller;
    private final EstatusSuscriptorDao estatusSuscriptorDao;
    private final ContratoxSuscriptorDao dao;
    private final ContratoDao contratoDao;
    private final TransaccionDao transaccionDao;
    private final DetalleCobroTransaccionDao detalleCobroTransaccionDao;
    private final CancelacionDao cancelacionDao;
    private final MotivoCancelacionDao motivoCancelacionDao;
    private final Sesion sesion;
    private final Utilerias util;
    private final TvchApiClient client;
    
    Logger logger = LoggerFactory.getLogger(CancelarContratoController.class);
    
    public static CancelarContratoController getCancelarContratoController(){
        if(controller == null)
            controller = new CancelarContratoController();
        return controller;
    }
    
    public CancelarContratoController(){
        estatusSuscriptorDao = EstatusSuscriptorDao.getEstatusSuscriptorDao();
        dao = ContratoxSuscriptorDao.getContratoxSuscriptorDao();
        transaccionDao = TransaccionDao.getTransaccionDao();
        detalleCobroTransaccionDao = DetalleCobroTransaccionDao.getDetalleCobroTransaccionDao();
        contratoDao = ContratoDao.getContratoDao();
        cancelacionDao = CancelacionDao.getCancelacionDao();
        motivoCancelacionDao = MotivoCancelacionDao.getMotivoCancelacionDao();
        sesion = Sesion.getSesion();
        util = Utilerias.getUtilerias();
        client = TvchApiClient.getTvchApiClient();
    }
    
    /**
     * 
     * @param suscriptor
     * @param detallesPago
     * @return 
     */
    public Long cobrarCancelacion(
            ContratoxSuscriptorDetalleEntity suscriptor, 
            List<DetallePagoServicio> detallesPago, 
            Long motivoCancelacionId,
            String observaciones) throws Exception{
        
        Long transaccionId = null;
        
        try{
            
            CancelacionEntity cancelacionEntity = new CancelacionEntity();
            cancelacionEntity.setCancelacionId(util.generarIdLocal());
            cancelacionEntity.setContratoId(suscriptor.getContratoId());
            cancelacionEntity.setFechaCancelacion(util.convertirDateTime2String(new Date(), Constantes.FORMATO_FECHA_MYSQL));
            cancelacionEntity.setMotivoId(motivoCancelacionId);
            cancelacionEntity.setServicioId(suscriptor.getServicioId());
            cancelacionEntity.setUsuarioId(sesion.getUsuarioId());
            cancelacionEntity.setObservaciones(observaciones);
            cancelacionEntity.setSucursalId(sesion.getSucursalId());
            cancelacionDao.registrarCancelacion(cancelacionEntity);
            
            Double importePagar = Double.valueOf(obtenerImporteActualizado(detallesPago));
            
            //registrar la transaccion
            TransaccionEntity transaccionEntity = new TransaccionEntity();
            transaccionEntity.setAperturaCajaId(sesion.getAperturaCajaId());
            transaccionEntity.setContratoId(suscriptor.getContratoId());
            transaccionEntity.setFechaTransaccion(util.obtenerFechaFormatoMysql());
            transaccionEntity.setMonto(importePagar);
            transaccionEntity.setTransaccionId(util.generarIdLocal());
            transaccionEntity.setActualFechaCorte(util.convertirDateTime2String(suscriptor.getFechaProximoPago(), Constantes.FORMATO_FECHA_MYSQL));
            transaccionEntity.setNuevaFechaCorte(util.convertirDateTime2String(suscriptor.getFechaProximoPago(), Constantes.FORMATO_FECHA_MYSQL));
            transaccionEntity.setObservaciones("CANCELACION DE CONTRATO");
            transaccionId = transaccionEntity.getTransaccionId();
            transaccionDao.registrarTransaccion(transaccionEntity);
            
            //registrar el detalle de la transaccion
            DetallePagoServicio detalleCobro = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_CANCELACION).findFirst().get();
            DetalleCobroTransaccionEntity detalleCobroTransaccionEntity = new DetalleCobroTransaccionEntity();
            detalleCobroTransaccionEntity.setMonto(detalleCobro.getMonto());
            detalleCobroTransaccionEntity.setServicioId(suscriptor.getServicioId());
            detalleCobroTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_CANCELACION_CONTRATO);
            detalleCobroTransaccionEntity.setTransaccionId(transaccionId);
            detalleCobroTransaccionEntity.setNumeroMeses(0);
            Long detalleCobroId = detalleCobroTransaccionDao.registrarDetalleTransaccion(detalleCobroTransaccionEntity);
            
            contratoDao.actualizarEstatus(suscriptor.getContratoId(), Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO);
            
            try{
                UpdateContratoEstatusCanceladoPosRequest updateContratoEstatusCanceladoPosRequest = new UpdateContratoEstatusCanceladoPosRequest();
                updateContratoEstatusCanceladoPosRequest.setContratoId(suscriptor.getContratoId());
                updateContratoEstatusCanceladoPosRequest.setUsuarioId(sesion.getUsuarioId());
                Request<UpdateContratoEstatusCanceladoPosRequest> request = new Request<>();
                request.setData(updateContratoEstatusCanceladoPosRequest);
                client.updateEstatusContrato(request);
            }catch(Exception exception){
                
            }
            
            //por ultimo registrar orden de servicio de returo por cancelacion
            OrdenServicioDao ordenServicioDao = OrdenServicioDao.getOrdenServicioDao();
            OrdenServicioEntity entity = new OrdenServicioEntity();
            entity.setContratoId(suscriptor.getContratoId());
            entity.setCosto(importePagar);
            entity.setDomicilioId(suscriptor.getDomicilioId());
            entity.setEstatusId(Constantes.ESTATUS_ORDEN_PAGADA);
            entity.setObservacionesRegistro(util.limpiarAcentos(observaciones).toUpperCase());
            entity.setOrdenId(util.generarIdLocal());
            entity.setServicioId(suscriptor.getServicioId());
            entity.setSuscriptorId(suscriptor.getSusucriptorId());
            entity.setTipoOrdenServicioId(Constantes.TIPO_ORDEN_SERVICIO_RETIRO_EQUIPO_CANCELACION);
            entity.setUsuarioId(sesion.getUsuarioId());
            ordenServicioDao.registrarOrdenServicio(entity);
            
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al realizar cobro de cancelacion: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }
        return transaccionId;
    }
    
    /**
     * 
     * @param contratoId
     * @param tipoBusquedaCobro
     * @param cadenaBusqueda
     * @return
     * @throws Exception 
     */
    public List<ContratoxSuscriptorDetalleEntity> consultarSuscriptores(Long contratoId, int tipoBusquedaCobro, String cadenaBusqueda) throws Exception {

        try {

            List<ContratoxSuscriptorDetalleEntity> list = dao.obtenerContratosSuscriptor(contratoId, tipoBusquedaCobro, cadenaBusqueda);
            //quitar los cancelados
            list = list
                    .stream()
                    .filter(s -> s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO && 
                            s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO)
                    .collect(Collectors.toList());

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
    
    /**
     * 
     * @return
     * @throws Exception 
     */
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
     * @param detalles
     * @return 
     */
    public String obtenerImporteActualizado(List<DetallePagoServicio> detalles) {

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
                    case Constantes.TIPO_DETALLE_COBRO_CANCELACION:
                        importe = detalle.getMonto();
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

    }
    
    public boolean seDebeGenerarRecargo(ContratoxSuscriptorDetalleEntity suscriptorSeleccionado) {
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
    }
    
    public Double calcularMontoPorDia(ContratoxSuscriptorDetalleEntity suscriptorSeleccionado){
        return suscriptorSeleccionado.getCostoServicio() / 30;
    }
    
    /**
     * 
     * @param suscriptorSeleccionado
     * @return 
     */
    public Double obtenerMontoCancelacion(ContratoxSuscriptorDetalleEntity suscriptorSeleccionado, Double montoPorDia){
        
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
        
        if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_ACTIVO){
            
            //validaciones a realizarse
            //si el mes en que se esta cancelando es menor al mes de la fecha pago no se cobra cancelacion
            //si el mes de cancelacion es igual al de la fecha de pago en contrato, se le cobran los dias proporcionales
            // si el mes es menor, se le cobra el servicio completo x el numero de meses

            if(fechaProximoPago.after(fechaHoy) || fechaProximoPago.equals(fechaHoy)){
                //como la fecha de proximo pago es posterior o igual, se valida el dia
                //si se eintenta cancelar entre el dia 1 y el dia de corte se paga lo proporcional
                //si se eintenta cancelar el mes anterior no se cobra
                if(diaCancelacion >= 1 && diaCancelacion <= diaFechaProximoPago){
                    if(mesFechaProximoPago > mesCancelacion){
                        monto = 0.0;
                    }else{
                        monto = montoPorDia * diaCancelacion;
                    }
                }else{
                    monto = 0.0;
                }
            }else {
                //si la fecha de pago ya se paso, se le cobra el numero de meses que debe
                int diferenciaAnios = anioCancelacion - anioFechaProximoPago;
                int diferenciaMeses = (diferenciaAnios*12) + mesCancelacion - mesFechaProximoPago;
                monto = (suscriptorSeleccionado.getCostoServicio() * diferenciaMeses) + (montoPorDia * diaCancelacion);
            }  
            
        }else if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTESIA ||
                suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_PENDIENTE_INSTALAR){
            monto = 0.0;
        }else if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE ||
                suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_RECONEXION){
            //si esta vencido es porque la fecha ya se paso
            int diferenciaAnios = anioCancelacion - anioFechaProximoPago;
            int diferenciaMeses = (diferenciaAnios*12) + mesCancelacion - mesFechaProximoPago;
            monto = (suscriptorSeleccionado.getCostoServicio() * diferenciaMeses) + (montoPorDia * diaCancelacion);
        }
        
        return monto;
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<MotivoCancelacionEntity> consultarMotivosCancelacion() throws Exception {

        try {

            List<MotivoCancelacionEntity> list = motivoCancelacionDao.obtenerMotivosCancelacion();

            if (list == null) {
                throw new NoSuchElementException(("No se encontraron motivos de cancelacion registrados."));
            }

            return list;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar motivos de cancelacion: \n" + sw.toString());
            throw new Exception("Error al consultar motivos de cancelacion. Por favor vuelva a intentar, si el problema persiste contacte a soporte.");
        }

    }
    
}
