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
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.ContratoDao;
import mx.com.tvch.pos.dao.ContratoxSuscriptorDao;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDescuentoTransaccionDao;
import mx.com.tvch.pos.dao.DetallePromocionTransaccionDao;
import mx.com.tvch.pos.dao.EstatusSuscriptorDao;
import mx.com.tvch.pos.dao.PromocionDao;
import mx.com.tvch.pos.dao.TipoDescuentoDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import mx.com.tvch.pos.entity.DetallePromocionTransaccionEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.PromocionEntity;
import mx.com.tvch.pos.entity.TipoDescuentoEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroServicioController {

    private static CobroServicioController controller;

    private final ContratoxSuscriptorDao dao;
    private final EstatusSuscriptorDao estatusSuscriptorDao;
    private final TipoDescuentoDao tipoDescuentoDao;
    private final PromocionDao promocionDao;
    private final ContratoDao contratoDao;
    private final TransaccionDao transaccionDao;
    private final DetalleCobroTransaccionDao detalleCobroTransaccionDao;
    private final DetallePromocionTransaccionDao detallePromocionTransaccionDao;
    private final DetalleDescuentoTransaccionDao detalleDescuentoTransaccionDao;
    private final Utilerias util;
    private final Sesion sesion;

    Logger logger = LoggerFactory.getLogger(CobroServicioController.class);

    public static CobroServicioController getContratoxSuscriptorController() {
        if (controller == null) {
            controller = new CobroServicioController();
        }
        return controller;
    }

    public CobroServicioController() {
        dao = ContratoxSuscriptorDao.getContratoxSuscriptorDao();
        estatusSuscriptorDao = EstatusSuscriptorDao.getEstatusSuscriptorDao();
        tipoDescuentoDao = TipoDescuentoDao.getTipoDescuentoDao();
        promocionDao = PromocionDao.getPromocionDao();
        contratoDao = ContratoDao.getContratoDao();
        transaccionDao = TransaccionDao.getTransaccionDao();
        detalleCobroTransaccionDao = DetalleCobroTransaccionDao.getDetalleCobroTransaccionDao();
        detalleDescuentoTransaccionDao = DetalleDescuentoTransaccionDao.getDetalleDescuentoTransaccionDao();
        detallePromocionTransaccionDao = DetallePromocionTransaccionDao.getDetallePromocionTransaccionDao();
        util = Utilerias.getUtilerias();
        sesion = Sesion.getSesion();
    }

    public Long cobrarServicio(ContratoxSuscriptorEntity suscriptor, List<DetallePagoServicio> detallesPago) throws Exception {

        Long transaccionId = null;

        try {

            Double importePagar = Double.valueOf(obtenerImporteActualizado(detallesPago));

            String nuevaFechaPagoTicket = "";
            String nuevaFechaPagoMySql = "";
            if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().isPresent()) {
                    nuevaFechaPagoMySql = util.obtenerNuevaFechaProximoPago(
                            sesion.getDiaCorte(),
                        detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findFirst().get().getMesesGratis(),
                        suscriptor.getFechaProximoPago(), 
                        Constantes.FORMATO_FECHA_MYSQL);
                    nuevaFechaPagoTicket = util.obtenerNuevaFechaProximoPago(
                            sesion.getDiaCorte(),
                        detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findFirst().get().getMesesGratis(),
                        suscriptor.getFechaProximoPago(), 
                        Constantes.FORMATO_FECHA_TICKET);

            } else {
                    nuevaFechaPagoMySql = util.obtenerNuevaFechaProximoPago(
                            sesion.getDiaCorte(), 
                            null, 
                            suscriptor.getFechaProximoPago(), 
                            Constantes.FORMATO_FECHA_MYSQL);
                    nuevaFechaPagoTicket = util.obtenerNuevaFechaProximoPago(
                            sesion.getDiaCorte(), 
                            null, 
                            suscriptor.getFechaProximoPago(), 
                            Constantes.FORMATO_FECHA_TICKET);

            }

            //registrar la transaccion
            TransaccionEntity transaccionEntity = new TransaccionEntity();
            transaccionEntity.setAperturaCajaId(sesion.getAperturaCajaId());
            transaccionEntity.setContratoId(suscriptor.getContratoId());
            transaccionEntity.setFechaTransaccion(util.obtenerFechaFormatoMysql());
            transaccionEntity.setMonto(importePagar);//monto a pagar ya con descuentos y promociones aplicadas si existieran
            transaccionEntity.setTransaccionId(util.generarIdLocal());
            transaccionId = transaccionEntity.getTransaccionId();
            transaccionDao.registrarTransaccion(transaccionEntity);

            //registrar el detalle de la transaccion
            DetallePagoServicio detalleCobro = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_SERVICIO).findFirst().get();
            //setear la nueva fecha de pago para despues se imprima en el ticket
            detalleCobro.setFechaProximoPago(nuevaFechaPagoTicket);
            DetalleCobroTransaccionEntity detalleCobroTransaccionEntity = new DetalleCobroTransaccionEntity();
            detalleCobroTransaccionEntity.setMonto(detalleCobro.getMonto());// monto sin descuentos ni promociones aplicadas
            detalleCobroTransaccionEntity.setServicioId(suscriptor.getServicioId());
            detalleCobroTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_SERVICIO);
            detalleCobroTransaccionEntity.setTransaccionId(transaccionId);
            Long detalleCobroId = detalleCobroTransaccionDao.registrarDetalleTransaccion(detalleCobroTransaccionEntity);

            //registrar el recargo del servicio
            if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_RECARGO).findAny().isPresent()) {
                DetallePagoServicio detalleRecargo = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_RECARGO).findFirst().get();
                DetalleCobroTransaccionEntity detalleRecargoTransaccionEntity = new DetalleCobroTransaccionEntity();
                detalleRecargoTransaccionEntity.setMonto(detalleRecargo.getMonto());// monto sin descuentos ni promociones aplicadas
                detalleRecargoTransaccionEntity.setServicioId(suscriptor.getServicioId());
                detalleRecargoTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_RECARGO_MENSUALIDAD);
                detalleRecargoTransaccionEntity.setTransaccionId(transaccionId);
                Long detalleRecargoId = detalleCobroTransaccionDao.registrarDetalleTransaccion(detalleRecargoTransaccionEntity);
            }

            //registrar el detalle de promocion
            if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().isPresent()) {
                DetallePagoServicio detallePromocion = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().get();
                DetallePromocionTransaccionEntity detallePromocionTransaccionEntity = new DetallePromocionTransaccionEntity();
                detallePromocionTransaccionEntity.setCostoPromocion(detallePromocion.getMonto());
                detallePromocionTransaccionEntity.setDescripcionPromocion(detallePromocion.getConcepto());
                detallePromocionTransaccionEntity.setPromocionId(detallePromocion.getPromocionId());
                detallePromocionTransaccionEntity.setTransaccionId(transaccionId);
                Long detallePromocionId = detallePromocionTransaccionDao.registrarDetallePromocion(detallePromocionTransaccionEntity);
            }

            //registrar el detalle de decuento
            if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().isPresent()) {
                DetallePagoServicio detalleDescuento = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().get();
                DetalleDescuentoTransaccionEntity detalleDescuentoTransaccionEntity = new DetalleDescuentoTransaccionEntity();
                detalleDescuentoTransaccionEntity.setMonto(detalleDescuento.getMonto());
                detalleDescuentoTransaccionEntity.setObservaciones(detalleDescuento.getMotivoDescuento());
                detalleDescuentoTransaccionEntity.setTipoDescuentoId(detalleDescuento.getTipoDescuentoId());
                detalleDescuentoTransaccionEntity.setTransaccionId(transaccionId);
                Long detalleDescuentoId = detalleDescuentoTransaccionDao.registrarDetalleDescuento(detalleDescuentoTransaccionEntity);
            }

            //actualizar la fecha de pago en el contrato
            contratoDao.actualizarFechaPagoContrato(suscriptor.getContratoId(), nuevaFechaPagoMySql);

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al realizar cobro: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }

        return transaccionId;

    }

    /**
     *
     * @return @throws Exception
     */
    public List<PromocionEntity> consultarPromociones(Long servicioId) throws Exception {

        try {

            List<PromocionEntity> list = promocionDao.obtenerPromocionesActivas(servicioId);

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

    public List<ContratoxSuscriptorEntity> consultarSuscriptores(Long contratoId, int tipoBusquedaCobro, String cadenaBusqueda) throws Exception {

        try {

            List<ContratoxSuscriptorEntity> list = dao.obtenerContratosSuscriptor(contratoId, tipoBusquedaCobro, cadenaBusqueda);
            //quitar los cancelados
            list = list
                    .stream()
                    .filter(s -> s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO && s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO)
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
    public boolean seDebeGenerarRecargo(ContratoxSuscriptorEntity suscriptorSeleccionado) {
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

    /**
     *
     * @param suscriptorSeleccionado
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

}
