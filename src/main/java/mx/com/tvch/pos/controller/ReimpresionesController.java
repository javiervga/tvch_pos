/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDescuentoTransaccionDao;
import mx.com.tvch.pos.dao.DetallePromocionTransaccionDao;
import mx.com.tvch.pos.dao.TipoOrdenServicioDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import mx.com.tvch.pos.entity.DetallePromocionTransaccionEntity;
import mx.com.tvch.pos.entity.TipoOrdenServicioEntity;
import mx.com.tvch.pos.entity.TransaccionTicketEntity;
import mx.com.tvch.pos.model.DetallePagoServicio;
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
        impresora = Impresora.getImpresora();
        sesion = Sesion.getSesion();
    }

    /**
     *
     * @param entity
     * @throws Exception
     */
    public void reimprimirTicket(TransaccionTicketEntity entity) throws Exception {

        try {

            /*
            if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_SERVICIO) {
                List<DetallePagoServicio> detallesPago = obtenerDetallesPago(entity);
                impresora.reimprimirTicketServicio(entity, detallesPago, sesion.getSucursal());
            } else if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_INSTALACION) {
                //List<DetallePagoServicio> detallesPago = obtenerDetallesPago(entity);
                List<DetalleCobroTransaccionEntity> detallesTransaccion = detalleCobroTransaccionDao.obtenerDetallesCobroPorTransaccion(entity.getTransaccionId());
                if (!detallesTransaccion.isEmpty()) {

                    List<DetalleDescuentoTransaccionEntity> detallesDescuento = detalleDescuentoTransaccionDao
                            .obtenerDetallesDescuentoPorTransaccion(entity.getTransaccionId());
                    DetalleDescuentoTransaccionEntity detalleDescuento = null;
                    if (!detallesDescuento.isEmpty()) {
                        detalleDescuento = detallesDescuento.get(0);
                    }

                    List<DetallePromocionTransaccionEntity> detallesPromocion = detallePromocionTransaccionDao
                            .obtenerDetallesPromocionPorTransaccion(entity.getTransaccionId());
                    DetallePromocionTransaccionEntity detallePromocion = null;
                    if (!detallesPromocion.isEmpty()) {
                        detallePromocion = detallesPromocion.get(0);
                    }

                    impresora.reimprimirTicketOrdenInstalacion(entity, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                }
            } else if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_SERVICIO) {
                //List<DetallePagoServicio> detallesPago = obtenerDetallesPago(entity);
                List<DetalleCobroTransaccionEntity> detallesTransaccion = detalleCobroTransaccionDao.obtenerDetallesCobroPorTransaccion(entity.getTransaccionId());
                if (!detallesTransaccion.isEmpty()) {

                    List<DetalleDescuentoTransaccionEntity> detallesDescuento = detalleDescuentoTransaccionDao
                            .obtenerDetallesDescuentoPorTransaccion(entity.getTransaccionId());
                    DetalleDescuentoTransaccionEntity detalleDescuento = null;
                    if (!detallesDescuento.isEmpty()) {
                        detalleDescuento = detallesDescuento.get(0);
                    }

                    List<DetallePromocionTransaccionEntity> detallesPromocion = detallePromocionTransaccionDao
                            .obtenerDetallesPromocionPorTransaccion(entity.getTransaccionId());
                    DetallePromocionTransaccionEntity detallePromocion = null;
                    if (!detallesPromocion.isEmpty()) {
                        detallePromocion = detallesPromocion.get(0);
                    }

                    impresora.reimprimirTicketOrdenServicio(entity, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                }
            } else if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_CAMBIO_DOMICILIO) {
                //List<DetallePagoServicio> detallesPago = obtenerDetallesPago(entity);
                List<DetalleCobroTransaccionEntity> detallesTransaccion = detalleCobroTransaccionDao.obtenerDetallesCobroPorTransaccion(entity.getTransaccionId());
                if (!detallesTransaccion.isEmpty()) {

                    List<DetalleDescuentoTransaccionEntity> detallesDescuento = detalleDescuentoTransaccionDao
                            .obtenerDetallesDescuentoPorTransaccion(entity.getTransaccionId());
                    DetalleDescuentoTransaccionEntity detalleDescuento = null;
                    if (!detallesDescuento.isEmpty()) {
                        detalleDescuento = detallesDescuento.get(0);
                    }

                    List<DetallePromocionTransaccionEntity> detallesPromocion = detallePromocionTransaccionDao
                            .obtenerDetallesPromocionPorTransaccion(entity.getTransaccionId());
                    DetallePromocionTransaccionEntity detallePromocion = null;
                    if (!detallesPromocion.isEmpty()) {
                        detallePromocion = detallesPromocion.get(0);
                    }

                    impresora.reimprimirTicketOrdenCambioDomicilio(entity, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                }
            }
            */
            
            if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_SERVICIO) {
                List<DetallePagoServicio> detallesPago = obtenerDetallesPago(entity);
                impresora.reimprimirTicketServicio(entity, detallesPago, sesion.getSucursal());
            } else {
                //List<DetallePagoServicio> detallesPago = obtenerDetallesPago(entity);
                List<DetalleCobroTransaccionEntity> detallesTransaccion = detalleCobroTransaccionDao.obtenerDetallesCobroPorTransaccion(entity.getTransaccionId());
                if (!detallesTransaccion.isEmpty()) {

                    List<DetalleDescuentoTransaccionEntity> detallesDescuento = detalleDescuentoTransaccionDao
                            .obtenerDetallesDescuentoPorTransaccion(entity.getTransaccionId());
                    DetalleDescuentoTransaccionEntity detalleDescuento = null;
                    if (!detallesDescuento.isEmpty()) {
                        detalleDescuento = detallesDescuento.get(0);
                    }

                    List<DetallePromocionTransaccionEntity> detallesPromocion = detallePromocionTransaccionDao
                            .obtenerDetallesPromocionPorTransaccion(entity.getTransaccionId());
                    DetallePromocionTransaccionEntity detallePromocion = null;
                    if (!detallesPromocion.isEmpty()) {
                        detallePromocion = detallesPromocion.get(0);
                    }
                    
                    if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_INSTALACION)
                        impresora.reimprimirTicketOrdenInstalacion(entity, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                    else if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_SERVICIO)
                        impresora.reimprimirTicketOrdenServicio(entity, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                    else if (entity.getTipoCobroId() == Constantes.TIPO_COBRO_ORDEN_CAMBIO_DOMICILIO)
                        impresora.reimprimirTicketOrdenCambioDomicilio(entity, sesion.getSucursal(), detallesTransaccion.get(0), detallePromocion, detalleDescuento);
                }
            }  

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al consultar transacciones para reimpresion: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }
    }

    private List<DetallePagoServicio> obtenerDetallesPago(TransaccionTicketEntity entity) throws Exception {

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
    public List<TransaccionTicketEntity> consultarTransacciones(Integer tipoCobro, String fechaInicio, String fechaFin) throws Exception {

        try {

            List<TransaccionTicketEntity> list = transaccionDao.obtenerTransaccionesxTipoCobro(tipoCobro, fechaInicio, fechaFin);
            return list;

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al consultar transacciones para reimpresion: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }
    }

}
