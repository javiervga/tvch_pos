/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import mx.com.tvch.pos.client.TvchApiClient;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.ContratoxSuscriptorDao;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDescuentoTransaccionDao;
import mx.com.tvch.pos.dao.DetallePromocionTransaccionDao;
import mx.com.tvch.pos.dao.EstatusSuscriptorDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import mx.com.tvch.pos.entity.DetallePromocionTransaccionEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.mapper.PosMapper;
import mx.com.tvch.pos.model.Orden;
import mx.com.tvch.pos.model.TipoBusquedaCobro;
import mx.com.tvch.pos.model.TipoOrden;
import mx.com.tvch.pos.model.client.ListOrdenesInstalacionPosRequest;
import mx.com.tvch.pos.model.client.ListOrdenesInstalacionResponse;
import mx.com.tvch.pos.model.client.ListPromocionesOrdenInstalacionRequest;
import mx.com.tvch.pos.model.client.ListPromocionesOrdenInstalacionResponse;
import mx.com.tvch.pos.model.client.ListSuscriptoresRequest;
import mx.com.tvch.pos.model.client.ListSuscriptoresResponse;
import mx.com.tvch.pos.model.client.ListTiposDescuentoResponse;
import mx.com.tvch.pos.model.client.PromocionOrdenInstalacion;
import mx.com.tvch.pos.model.client.Request;
import mx.com.tvch.pos.model.client.Response;
import mx.com.tvch.pos.model.client.Suscriptor;
import mx.com.tvch.pos.model.client.TipoDescuento;
import mx.com.tvch.pos.model.client.UpdateEstatusPagadaOrdenInstalacionRequest;
import mx.com.tvch.pos.model.client.UpdateOrdenInstalacionResponse;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroOrdenController {

    private static CobroOrdenController controller;

    private final Sesion sesion;
    private final TvchApiClient client;
    private final EstatusSuscriptorDao estatusSuscriptorDao;
    private final Utilerias util;
    private final PosMapper mapper;
    private final TransaccionDao transaccionDao;
    private final DetalleCobroTransaccionDao detalleCobroTransaccionDao;
    private final DetallePromocionTransaccionDao detallePromocionTransaccionDao;
    private final DetalleDescuentoTransaccionDao detalleDescuentoTransaccionDao;
    private final ContratoxSuscriptorDao contratoxSuscriptorDao;

    Logger logger = LoggerFactory.getLogger(CobroOrdenController.class);

    public static CobroOrdenController getCobroOrdenController() {
        if (controller == null) {
            controller = new CobroOrdenController();
        }
        return controller;
    }

    public CobroOrdenController() {
        sesion = Sesion.getSesion();
        client = TvchApiClient.getTvchApiClient();
        estatusSuscriptorDao = EstatusSuscriptorDao.getEstatusSuscriptorDao();
        util = Utilerias.getUtilerias();
        mapper = PosMapper.getPosMapper();
        transaccionDao = TransaccionDao.getTransaccionDao();
        detalleCobroTransaccionDao = DetalleCobroTransaccionDao.getDetalleCobroTransaccionDao();
        detallePromocionTransaccionDao = DetallePromocionTransaccionDao.getDetallePromocionTransaccionDao();
        detalleDescuentoTransaccionDao = DetalleDescuentoTransaccionDao.getDetalleDescuentoTransaccionDao();
        contratoxSuscriptorDao = ContratoxSuscriptorDao.getContratoxSuscriptorDao();
    }

    /**
     * 
     * @param orden
     * @return
     * @throws Exception 
     */
    public Long registrarTransaccion(Orden orden) throws Exception {
        
        Long transaccionId = null;

        //registrar la transaccion
        TransaccionEntity transaccionEntity = new TransaccionEntity();
        transaccionEntity.setAperturaCajaId(sesion.getAperturaCajaId());
        transaccionEntity.setContratoId(orden.getContratoId());
        transaccionEntity.setFechaTransaccion(util.obtenerFechaFormatoMysql());
        transaccionEntity.setMonto(orden.getImportePagar());//monto a pagar ya con descuentos y promociones aplicadas si existieran
        transaccionEntity.setTransaccionId(util.generarIdLocal());
        transaccionId = transaccionEntity.getTransaccionId();
        transaccionDao.registrarTransaccion(transaccionEntity);

        //registrar el detalle de la transaccion
        DetalleCobroTransaccionEntity detalleCobroTransaccionEntity = new DetalleCobroTransaccionEntity();
        detalleCobroTransaccionEntity.setMonto(orden.getCosto());// monto sin descuentos ni promociones aplicadas
        detalleCobroTransaccionEntity.setServicioId(orden.getServicioId());
        detalleCobroTransaccionEntity.setTipoCobroId(Constantes.TIPO_COBRO_ORDEN_INSTALACION);
        detalleCobroTransaccionEntity.setTransaccionId(transaccionId);
        detalleCobroTransaccionEntity.setOrdenId(orden.getId());
        Long detalleCobro = detalleCobroTransaccionDao.registrarDetalleTransaccion(detalleCobroTransaccionEntity);

        //registrar el detalle de promocion
        if (orden.getPromocionId() != null) {
            DetallePromocionTransaccionEntity detallePromocionTransaccionEntity = new DetallePromocionTransaccionEntity();
            detallePromocionTransaccionEntity.setCostoPromocion(orden.getCostoPromocion());
            detallePromocionTransaccionEntity.setDescripcionPromocion(orden.getDescripcionPromocion());
            detallePromocionTransaccionEntity.setPromocionId(orden.getPromocionId());
            detallePromocionTransaccionEntity.setTransaccionId(transaccionId);
            Long detallePromocion = detallePromocionTransaccionDao.registrarDetallePromocion(detallePromocionTransaccionEntity);
        }

        //registrar el detalle de decuento
        if (orden.getTipoDescuentoId() != null) {
            DetalleDescuentoTransaccionEntity detalleDescuentoTransaccionEntity = new DetalleDescuentoTransaccionEntity();
            detalleDescuentoTransaccionEntity.setMonto(orden.getImporteDescuento());
            detalleDescuentoTransaccionEntity.setObservaciones(orden.getMotivoDescuento());
            detalleDescuentoTransaccionEntity.setTipoDescuentoId(orden.getTipoDescuentoId());
            detalleDescuentoTransaccionEntity.setTransaccionId(transaccionId);
            Long detalleDescuento = detalleDescuentoTransaccionDao.registrarDetalleDescuento(detalleDescuentoTransaccionEntity);
        }
        
        return transaccionId;

    }
    
    /**
     * 
     * @param orden
     * @return 
     */
    /*private String obtenerNuevaFechaPagoContrato(Orden orden){
        String fecha = orden.getFechaProximoPago();
        
        //validar si esta aplicada alguna promo y si en ella se incluyen meses gratis
        if(orden.getPromocionId() != null){
            if(orden.getMesesGratisPromocion() != null && orden.getMesesGratisPromocion() > 0){
                
            }
        }
        
        return fecha;
    }*/

    /**
     *
     * @param orden
     * @throws Exception
     */
    public Long cobrarOrden(Orden orden) throws Exception {
        
        Long transaccionId = null;

        try {

            //paso 1 -> generar peticion de actualizacion de orden y enviarla a central
            switch (orden.getTipoOrdenId()) {

                case Constantes.TIPO_ORDEN_INSTALACION:
                    
                    //segundo actualizar estatus de contrato y orden de instalacion en server
                    UpdateEstatusPagadaOrdenInstalacionRequest instalacionRequest = new UpdateEstatusPagadaOrdenInstalacionRequest();
                    instalacionRequest.setOrdenInstalacionId(orden.getId());
                    instalacionRequest.setFechaProximoPago(util.obtenerNuevaFechaProximoPagoOrdenInstalacion(sesion.getDiaCorte(), orden.getMesesGratisPromocion()));
                    Request<UpdateEstatusPagadaOrdenInstalacionRequest> request = new Request<>();
                    request.setData(instalacionRequest);
                    Response<UpdateOrdenInstalacionResponse> response = client.updateEstatusPagoOrdenInstalacion(request);
                    switch (response.getCode()) {
                        case Constantes.CODIGO_HTTP_OK:
                            //registrar em bd local
                            transaccionId = registrarTransaccion(orden);
                            break;
                        case Constantes.CODIGO_HTTP_TVCH_ERROR:
                            throw new Exception(response.getMessage());
                        case Constantes.CODIGO_HTTP_SERVER_ERROR:
                            throw new Exception(response.getMessage());
                        case Constantes.CODIGO_HTTP_PERMISOS_ERROR:
                            throw new Exception("Su usuario no cuenta con los permisos para realizar el pago de la orden");
                        default:
                            throw new Exception(response.getMessage());
                    }

                default:
                    break;
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

    /**
     *
     * @return @throws Exception
     */
    public List<TipoDescuento> consultarTiposDescuento() throws Exception {

        try {

            Response<ListTiposDescuentoResponse> response = client.consultarTiposDescuento();
            switch (response.getCode()) {
                case Constantes.CODIGO_HTTP_OK:
                    return response.getData().getList();
                case Constantes.CODIGO_HTTP_NO_CONTENT:
                    throw new Exception("No se encontraron tipos de descuento registrados");
                case Constantes.CODIGO_HTTP_PERMISOS_ERROR:
                    throw new Exception("Su usuario no cuenta con los permisos para realizar la consulta de tipos de descuento");
                default:
                    throw new Exception(response.getMessage());
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al consultar tipos de descuento: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }

    }

    /**
     *
     * @param sucursalId
     * @param servicioId
     * @return
     * @throws Exception
     */
    public List<PromocionOrdenInstalacion> consultarPromocionesOrdenes(Long sucursalId, Long servicioId) throws Exception {

        try {

            ListPromocionesOrdenInstalacionRequest promocionesRequest = new ListPromocionesOrdenInstalacionRequest();
            promocionesRequest.setEstatus(Constantes.ESTATUS_ACTIVO);
            promocionesRequest.setServicioId(servicioId);
            promocionesRequest.setSucursalId(sucursalId);
            Request<ListPromocionesOrdenInstalacionRequest> request = new Request<>();
            request.setData(promocionesRequest);
            Response<ListPromocionesOrdenInstalacionResponse> response = client.consultarPromocionesOrdenesInstalacion(request);
            switch (response.getCode()) {
                case Constantes.CODIGO_HTTP_OK:
                    return response.getData().getList();
                case Constantes.CODIGO_HTTP_NO_CONTENT:
                    throw new Exception("No se encontraron promociones activas para el servicio requerido");
                case Constantes.CODIGO_HTTP_PERMISOS_ERROR:
                    throw new Exception("Su usuario no cuenta con los permisos para realizar la consulta de  para ordenes de instalación");
                default:
                    throw new Exception(response.getMessage());
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al consultar promociones de ordenes de instalacion: \n" + sw.toString());
            throw new Exception(ex.getMessage());
        }

    }

    /**
     *
     * @param suscriptor
     * @param tipoOrden
     * @return
     * @throws Exception
     */
    public List<Orden> consultarOrdenes(Suscriptor suscriptor, TipoOrden tipoOrden) throws Exception {

        switch (tipoOrden.getTipoOrdenId()) {
            case Constantes.TIPO_ORDEN_INSTALACION:
                ListOrdenesInstalacionPosRequest ordenesRequest = new ListOrdenesInstalacionPosRequest();
                ordenesRequest.setContratoId(suscriptor.getContrato());
                //ordenesRequest.setFechaInicio(util.convertirDateTime2String(fechaInicio, "dd/MM/yyyy"));
                //ordenesRequest.setFechaFin(util.convertirDateTime2String(fechaFin, "dd/MM/yyyy"));
                Request<ListOrdenesInstalacionPosRequest> request = new Request<>();
                request.setData(ordenesRequest);
                Response<ListOrdenesInstalacionResponse> responseOrdenes = client.consultarOrdenesInstalacion(request);
                switch (responseOrdenes.getCode()) {
                    case Constantes.CODIGO_HTTP_OK:
                        return mapper.ordenInstalacionList2Ordenes(responseOrdenes.getData().getList(), tipoOrden);
                    case Constantes.CODIGO_HTTP_NO_CONTENT:
                        throw new Exception("No se encontraron ordenes de instalacion para el contrato solicitado");
                    case Constantes.CODIGO_HTTP_PERMISOS_ERROR:
                        throw new Exception("Su usuario no cuenta con los permisos para realizar la consulta de ordenes de instalación");
                    default:
                        throw new Exception(responseOrdenes.getMessage());
                }
            default:
                throw new Exception();
        }

    }
   
    /**
     * 
     * @param fechaPago
     * @param promocion
     * @return 
     */
    public String actualizarFechaProximoPago(String fechaPago, PromocionOrdenInstalacion promocion){
        try{
            return util.obtenerNuevaFechaProximoPagoOrdenInstalacion(sesion.getDiaCorte(), promocion.getMesesGratis());
        }catch(Exception ex){
            return fechaPago;
        }
    }

    /**
     *
     * @param tipoBusqueda
     * @param cadenaBuscar
     * @param estatusSuscriptor
     * @return
     * @throws Exception
     */
    public List<Suscriptor> consultarSuscriptores(TipoBusquedaCobro tipoBusqueda, String cadenaBuscar, EstatusSuscriptorEntity estatusSuscriptor) throws NoSuchElementException, Exception {

        try {

            ListSuscriptoresRequest suscriptoresRequest = new ListSuscriptoresRequest();
            suscriptoresRequest.setSucursalId(sesion.getSucursalId());
            suscriptoresRequest.setEstatusSuscriptorId(estatusSuscriptor.getEstatusId());

            if (tipoBusqueda.getTipoCobroId() == Constantes.TIPO_BUSQUEDA_CONTRATO) {
                try {
                    Long contratoId = Long.parseLong(cadenaBuscar);
                    suscriptoresRequest.setContrato(contratoId);
                } catch (NumberFormatException ex) {
                    throw new Exception("Por favor, ingrese un número de contrato numérico");
                }
            } else if (tipoBusqueda.getTipoCobroId() == Constantes.TIPO_BUSQUEDA_CONTRATO_ANTERIOR) {
                try {
                    Long contratoAnteriorId = Long.parseLong(cadenaBuscar);
                    suscriptoresRequest.setContratoAnterior(contratoAnteriorId);
                } catch (NumberFormatException ex) {
                    throw new Exception("Por favor, ingrese un número de contrato numérico");
                }
            } else if (tipoBusqueda.getTipoCobroId() == Constantes.TIPO_BUSQUEDA_APELLIDO_MATERNO) {
                suscriptoresRequest.setApellidoMaterno(cadenaBuscar);
            } else if (tipoBusqueda.getTipoCobroId() == Constantes.TIPO_BUSQUEDA_APELLIDO_PATERNO) {
                suscriptoresRequest.setApellidoPaterno(cadenaBuscar);
            } else if (tipoBusqueda.getTipoCobroId() == Constantes.TIPO_BUSQUEDA_DOMICILIO) {
                suscriptoresRequest.setDomicilio(cadenaBuscar);
            } else if (tipoBusqueda.getTipoCobroId() == Constantes.TIPO_BUSQUEDA_NOMBRE) {
                suscriptoresRequest.setNombre(cadenaBuscar);
            }

            Request<ListSuscriptoresRequest> request = new Request<>();
            request.setData(suscriptoresRequest);
            Response<ListSuscriptoresResponse> response = client.consultarSuscriptores(request);

            switch (response.getCode()) {
                case Constantes.CODIGO_HTTP_OK:
                    //quitar los contratos cancelados
                    return response.getData().getSuscriptores()
                            .stream()
                            .filter(s -> s.getEstatusContratoId() == null ||
                                    (s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO && s.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO) )
                            .collect(Collectors.toList());
                case Constantes.CODIGO_HTTP_NO_CONTENT:
                    throw new NoSuchElementException("No se encontraron suscriptores con la informacion recibida");
                case Constantes.CODIGO_HTTP_TVCH_ERROR:
                    throw new Exception(response.getMessage());
                default:
                    throw new Exception("Ocurrió un error al intentar conectar con servidor. Por favor reintente");
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Fallo al consultar suscriptores: \n" + sw.toString());
            throw new Exception(ex.getMessage());
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

}
