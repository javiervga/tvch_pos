/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mx.com.tvch.pos.model.Orden;
import mx.com.tvch.pos.model.TipoOrden;
import mx.com.tvch.pos.model.client.AuthResponse;
import mx.com.tvch.pos.model.client.ListOrdenesCambioDomicilioResponse;
import mx.com.tvch.pos.model.client.ListOrdenesInstalacionResponse;
import mx.com.tvch.pos.model.client.ListOrdenesServicioResponse;
import mx.com.tvch.pos.model.client.ListPromocionesOrdenInstalacionResponse;
import mx.com.tvch.pos.model.client.ListSuscriptoresResponse;
import mx.com.tvch.pos.model.client.ListTiposDescuentoResponse;
import mx.com.tvch.pos.model.client.OrdenCambioDomicilio;
import mx.com.tvch.pos.model.client.OrdenInstalacion;
import mx.com.tvch.pos.model.client.OrdenServicio;
import mx.com.tvch.pos.model.client.PromocionOrdenInstalacion;
import mx.com.tvch.pos.model.client.Suscriptor;
import mx.com.tvch.pos.model.client.TipoDescuento;
import mx.com.tvch.pos.model.client.UpdateOrdenCambioDomicilioResponse;
import mx.com.tvch.pos.model.client.UpdateOrdenInstalacionResponse;
import mx.com.tvch.pos.model.client.UpdateOrdenServicioResponse;

/**
 *
 * @author fvega
 */
public class PosMapper {
    
    private static PosMapper mapper;
    
    public static PosMapper getPosMapper(){
        if(mapper == null)
            mapper = new PosMapper();
        return mapper;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    private Map<String, Object> getMapper(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper
                .convertValue(object, new TypeReference<Map<String,Object>>(){});
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public UpdateOrdenCambioDomicilioResponse object2UpdateOrdenCambioDomicilioResponse(Object object){
        
        UpdateOrdenCambioDomicilioResponse response = new UpdateOrdenCambioDomicilioResponse();
        Map<String, Object> map = getMapper(object);
        
        double contratoId = (double) map.get("contratoId");
        response.setContratoId((long) contratoId);
        response.setCosto((Double) map.get("costo"));
        response.setEstatusOrden((String) map.get("estatusOrden"));
        double estatusOrdenId = (double) map.get("estatusOrdenId");
        response.setEstatusOrdenId((long) estatusOrdenId);
        if(map.get("fechaAgenda") != null)
            response.setFechaAgenda((String) map.get("fechaAgenda"));
        response.setFechaRegistro((String) map.get("fechaRegistro"));
        if(map.get("fechaCambioDomicilio") != null)
            response.setFechaCambioDomicilio((String) map.get("fechaCambioDomicilio"));
        if(map.get("observacionesAgenda") != null)
            response.setObservacionesAgenda((String) map.get("observacionesAgenda"));
        if(map.get("observacionesCambioDomicilio") != null)
            response.setObservacionesCambioDomicilio((String) map.get("observacionesCambioDomicilio"));
        double ordenCambioDomicilioId = (double) map.get("ordenCambioDomicilioId");
        response.setOrdenCambioDomicilioId((long) ordenCambioDomicilioId);
        response.setServicio((String) map.get("servicio"));
        double servicioId = (double) map.get("servicioId");
        response.setServicioId((long) servicioId);
        response.setSuscriptor((String) map.get("suscriptor"));
        double suscriptorId = (double) map.get("suscriptorId");
        response.setSuscriptorId((long) suscriptorId);
        response.setUsuario((String) map.get("usuario"));
        double usuarioId = (double) map.get("usuarioId");
        response.setUsuarioId((long) usuarioId);
        
        return response;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public UpdateOrdenServicioResponse object2UpdateOrdenServicioResponse(Object object){
        
        UpdateOrdenServicioResponse response = new UpdateOrdenServicioResponse();
        Map<String, Object> map = getMapper(object);
        
        double contratoId = (double) map.get("contratoId");
        response.setContratoId((long) contratoId);
        response.setEstatusOrden((String) map.get("estatusOrden"));
        double estatusOrdenId = (double) map.get("estatusOrdenId");
        response.setEstatusOrdenId((long) estatusOrdenId);
        if(map.get("fechaAgenda") != null)
            response.setFechaAgenda((String) map.get("fechaAgenda"));
        response.setFechaRegistro((String) map.get("fechaRegistro"));
        if(map.get("fechaServicio") != null)
            response.setFechaServicio((String) map.get("fechaServicio"));
        if(map.get("observacionesAgenda") != null)
            response.setObservacionesAgenda((String) map.get("observacionesAgenda"));
        if(map.get("observacionesServicio") != null)
            response.setObservacionesServicio((String) map.get("observacionesServicio"));
        double ordenServicioId = (double) map.get("ordenServicioId");
        response.setOrdenServicioId((long) ordenServicioId);
        response.setServicio((String) map.get("servicio"));
        double servicioId = (double) map.get("servicioId");
        response.setServicioId((long) servicioId);
        response.setSuscriptor((String) map.get("suscriptor"));
        double suscriptorId = (double) map.get("suscriptorId");
        response.setSuscriptorId((long) suscriptorId);
        response.setTipoOrdenServicio((String) map.get("tipoOrdenServicio"));
        double tipoOrdenServicioId = (double) map.get("tipoOrdenServicioId");
        response.setTipoOrdenServicioId((long) tipoOrdenServicioId);
        response.setUsuario((String) map.get("usuario"));
        double usuarioId = (double) map.get("usuarioId");
        response.setUsuarioId((long) usuarioId);
        
        return response;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public UpdateOrdenInstalacionResponse object2UpdateOrdenInstalacionResponse(Object object){
        
        UpdateOrdenInstalacionResponse response = new UpdateOrdenInstalacionResponse();
        Map<String, Object> map = getMapper(object);
        
        double ordenInstalacionId = (double) map.get("ordenInstalacionId");
        response.setOrdenInstalacionId((long) ordenInstalacionId);
        double contratoId = (double) map.get("contratoId");
        response.setContratoId((long) contratoId);
        response.setUsuario((String) map.get("usuario"));
        if(map.get("fechaAgenda") != null)
            response.setFechaAgenda((String) map.get("fechaAgenda"));
        if(map.get("observacionesAgenda") != null)
            response.setObservacionesAgenda((String) map.get("observacionesAgenda"));
        response.setFechaRegistro((String) map.get("fechaRegistro"));
        if(map.get("fechaInstalacion") != null)
            response.setFechaInstalacion((String) map.get("fechaInstalacion"));
        if(map.get("observacionesInstalacion") != null)
            response.setObservacionesInstalacion((String) map.get("observacionesInstalacion"));
        double estatusId = (double) map.get("estatusId");
        response.setEstatusId((long) estatusId);
        response.setEstatus((String) map.get("estatus"));
        if(map.get("vendedorId") != null){
            double vendedorId = (double) map.get("vendedorId");
            response.setVendedorId((long) vendedorId);
            response.setVendedor((String) map.get("vendedor"));
        }
        response.setCosto((Double) map.get("costo"));
        
        return response;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public ListTiposDescuentoResponse object2ListTiposDescuentoResponse(Object object){
        
        ListTiposDescuentoResponse response = new ListTiposDescuentoResponse();
        Map<String, Object> map = getMapper(object);
        
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
        List<TipoDescuento> tipoDescuento = new ArrayList<>();
        list.forEach(td -> tipoDescuento.add(map2TipoDescuento(td)));
        response.setList(tipoDescuento);
        
        return response;
    }
    
    /**
     * 
     * @param map
     * @return 
     */
    private TipoDescuento map2TipoDescuento(Map<String, Object> map){
        
        TipoDescuento tipoDescuento = new TipoDescuento();
        
        tipoDescuento.setDescripcion((String) map.get("descripcion"));
        double id = (double) map.get("id");
        tipoDescuento.setId((long) id);
        return tipoDescuento;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public ListPromocionesOrdenInstalacionResponse object2ListPromocionesOrdenesInstalacionResponse(Object object){
        
        ListPromocionesOrdenInstalacionResponse response = new ListPromocionesOrdenInstalacionResponse();
        Map<String, Object> map = getMapper(object);
        
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
        List<PromocionOrdenInstalacion> promocion = new ArrayList<>();
        list.forEach(p -> promocion.add(map2PromocionOrdenInstalacion(p)));
        response.setList(promocion);
        
        return response;
    }
    
    /**
     * 
     * @param map
     * @return 
     */
    private PromocionOrdenInstalacion map2PromocionOrdenInstalacion(Map<String, Object> map){
        
        PromocionOrdenInstalacion promocion = new PromocionOrdenInstalacion();
        
        promocion.setCostoPromocion((Double) map.get("costoPromocion"));
        promocion.setDescripcion((String) map.get("descripcion"));
        double estatus = (double) map.get("estatus");
        promocion.setEstatus((int) estatus);
        double id = (double) map.get("id");
        promocion.setId((long) id);
        if(map.get("mesesGratis") != null){
            double mesesGratis = (double) map.get("mesesGratis");
            promocion.setMesesGratis((int) mesesGratis);
        }
        promocion.setServicio((String) map.get("servicio"));
        double servicioId = (double) map.get("servicioId");
        promocion.setServicioId((long) servicioId);
        promocion.setSucursal((String) map.get("sucursal"));
        double sucursalId = (double) map.get("sucursalId");
        promocion.setSucursalId((long) sucursalId);
        if(map.get("tvsContratadas") != null){
            double tvsContratadas = (double) map.get("tvsContratadas");
            promocion.setTvsContratadas((int) tvsContratadas);
        }
        return promocion;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public ListOrdenesInstalacionResponse object2ListOrdenesInstalacionResponse(Object object){
        
        ListOrdenesInstalacionResponse response = new ListOrdenesInstalacionResponse();
        Map<String, Object> map = getMapper(object);
        
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
        List<OrdenInstalacion> ordenesInstalacion = new ArrayList<>();
        list.forEach(o -> ordenesInstalacion.add(map2OrdenInstalacion(o)));
        response.setList(ordenesInstalacion);
        
        return response;
    }
    
    /**
     * 
     * @param map
     * @return 
     */
    private OrdenInstalacion map2OrdenInstalacion(Map<String, Object> map){
        
        OrdenInstalacion ordenInstalacion = new OrdenInstalacion();
        
        double contratoId = (double) map.get("contratoId");
        ordenInstalacion.setContratoId((long) contratoId);
        ordenInstalacion.setCosto((Double) map.get("costo"));
        ordenInstalacion.setEstatus((String) map.get("estatus"));
        double estatusId = (double) map.get("estatusId");
        ordenInstalacion.setEstatusId((long) estatusId);
        if(map.get("fechaAgenda") != null)
            ordenInstalacion.setFechaAgenda((String) map.get("fechaAgenda"));
        if(map.get("fechaInstalacion") != null)
            ordenInstalacion.setFechaInstalacion((String) map.get("fechaInstalacion"));
        ordenInstalacion.setFechaRegistro((String) map.get("fechaRegistro"));
        if(map.get("observacionesAgenda") != null)
            ordenInstalacion.setObservacionesAgenda((String) map.get("observacionesAgenda"));
        if(map.get("observacionesInstalacion") != null)
            ordenInstalacion.setObservacionesInstalacion((String) map.get("observacionesInstalacion"));
        double ordenInstalacionId = (double) map.get("ordenInstalacionId");
        ordenInstalacion.setOrdenInstalacionId((long) ordenInstalacionId);
        ordenInstalacion.setSuscriptor((String) map.get("suscriptor"));
        ordenInstalacion.setUsuario((String) map.get("usuario"));
        if(map.get("vendedor") != null){
            ordenInstalacion.setVendedor((String) map.get("vendedor"));
            double vendedorId = (double) map.get("vendedorId");
            ordenInstalacion.setVendedorId((long) vendedorId);
        }
        return ordenInstalacion;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public ListOrdenesServicioResponse object2ListOrdenesServicioResponse(Object object){
        
        ListOrdenesServicioResponse response = new ListOrdenesServicioResponse();
        Map<String, Object> map = getMapper(object);
        
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
        List<OrdenServicio> ordenesServicio = new ArrayList<>();
        list.forEach(o -> ordenesServicio.add(map2OrdenServicio(o)));
        response.setList(ordenesServicio);
        
        return response;
    }
    
    /**
     * 
     * @param map
     * @return 
     */
    private OrdenServicio map2OrdenServicio(Map<String, Object> map){
        
        OrdenServicio ordenServicio = new OrdenServicio();
        
        double contratoId = (double) map.get("contratoId");
        ordenServicio.setContratoId((long) contratoId);
        ordenServicio.setCosto((Double) map.get("costo"));
        ordenServicio.setEstatus((String) map.get("estatus"));
        double estatusId = (double) map.get("estatusId");
        ordenServicio.setEstatusId((long) estatusId);
        if(map.get("fechaAgenda") != null)
            ordenServicio.setFechaAgenda((String) map.get("fechaAgenda"));
        ordenServicio.setFechaRegistro((String) map.get("fechaRegistro"));
        if(map.get("fechaServicio") != null)
            ordenServicio.setFechaServicio((String) map.get("fechaServicio"));
        if(map.get("observacionesAgenda") != null)
            ordenServicio.setObservacionesAgenda((String) map.get("observacionesAgenda"));
        if(map.get("observacionesServicio") != null)
            ordenServicio.setObservacionesServicio((String) map.get("observacionesServicio"));
        double ordenServicioId = (double) map.get("ordenServicioId");
        ordenServicio.setOrdenServicioId((long) ordenServicioId);
        ordenServicio.setServicio((String) map.get("servicio"));
        double servicioId = (double) map.get("servicioId");
        ordenServicio.setServicioId((long) servicioId);
        ordenServicio.setSuscriptor((String) map.get("suscriptor"));
        double suscriptorId = (double) map.get("suscriptorId");
        ordenServicio.setSuscriptorId((long) suscriptorId);
        ordenServicio.setTipoOrdenServicio((String) map.get("tipoOrdenServicio"));
        double tipoOrdenServicioId = (double) map.get("tipoOrdenServicioId");
        ordenServicio.setTipoOrdenServicioId((long) tipoOrdenServicioId);
        ordenServicio.setUsuario((String) map.get("usuario"));
        double usuarioId = (double) map.get("usuarioId");
        ordenServicio.setUsuarioId((long) usuarioId);
        return ordenServicio;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public ListOrdenesCambioDomicilioResponse object2ListOrdenesCambioDomicilioResponse(Object object){
        
        ListOrdenesCambioDomicilioResponse response = new ListOrdenesCambioDomicilioResponse();
        Map<String, Object> map = getMapper(object);
        
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
        List<OrdenCambioDomicilio> ordenesCambioDomicilio = new ArrayList<>();
        list.forEach(o -> ordenesCambioDomicilio.add(map2OrdenCambioDomicilio(o)));
        response.setList(ordenesCambioDomicilio);
        
        return response;
    }
    
    /**
     * 
     * @param map
     * @return 
     */
    private OrdenCambioDomicilio map2OrdenCambioDomicilio(Map<String, Object> map){
        
        OrdenCambioDomicilio ordenCambioDomicilio = new OrdenCambioDomicilio();
        
        double contratoId = (double) map.get("contratoId");
        ordenCambioDomicilio.setContratoId((long) contratoId);
        ordenCambioDomicilio.setCosto((Double) map.get("costo"));
        ordenCambioDomicilio.setEstatusOrden((String) map.get("estatusOrden"));
        double estatusOrdenId = (double) map.get("estatusOrdenId");
        ordenCambioDomicilio.setEstatusOrdenId((long) estatusOrdenId);
        if(map.get("fechaAgenda") != null)
            ordenCambioDomicilio.setFechaAgenda((String) map.get("fechaAgenda"));
        ordenCambioDomicilio.setFechaRegistro((String) map.get("fechaRegistro"));
        if(map.get("fechaCambioDomicilio") != null)
            ordenCambioDomicilio.setFechaCambioDomicilio((String) map.get("fechaCambioDomicilio"));
        if(map.get("observacionesAgenda") != null)
            ordenCambioDomicilio.setObservacionesAgenda((String) map.get("observacionesAgenda"));
        if(map.get("observacionesCambioDomicilio") != null)
            ordenCambioDomicilio.setObservacionesCambioDomicilio((String) map.get("observacionesCambioDomicilio"));
        double ordenCambioDomicilioId = (double) map.get("ordenCambioDomicilioId");
        ordenCambioDomicilio.setOrdenCambioDomicilioId((long) ordenCambioDomicilioId);
        ordenCambioDomicilio.setServicio((String) map.get("servicio"));
        double servicioId = (double) map.get("servicioId");
        ordenCambioDomicilio.setServicioId((long) servicioId);
        ordenCambioDomicilio.setSuscriptor((String) map.get("suscriptor"));
        double suscriptorId = (double) map.get("suscriptorId");
        ordenCambioDomicilio.setSuscriptorId((long) suscriptorId);
        ordenCambioDomicilio.setUsuario((String) map.get("usuario"));
        double usuarioId = (double) map.get("usuarioId");
        ordenCambioDomicilio.setUsuarioId((long) usuarioId);
        return ordenCambioDomicilio;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public ListSuscriptoresResponse object2ListSuscriptoresResponse(Object object){
        
        ListSuscriptoresResponse response = new ListSuscriptoresResponse();
        Map<String, Object> map = getMapper(object);
        
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("suscriptores");
        List<Suscriptor> suscriptores = new ArrayList<>();
        list.forEach(m -> suscriptores.add(map2Suscriptor(m)));
        response.setSuscriptores(suscriptores);
        
        return response;
    }
    
    /**
     * 
     * @param map
     * @return 
     */
    private Suscriptor map2Suscriptor(Map<String, Object> map){
        
        Suscriptor suscriptor = new Suscriptor();
        
        suscriptor.setApellidoMaterno((String) map.get("apellidoMaterno"));
        suscriptor.setApellidoPaterno((String) map.get("apellidoPaterno"));
        if(map.get("contrato") != null){
            double contrato = (double) map.get("contrato");
            suscriptor.setContrato((long) contrato);
        }
        if(map.get("contratoAnterior") != null){
            double contratoAnterior = (double) map.get("contratoAnterior");
            suscriptor.setContratoAnterior((long) contratoAnterior);
        }
        suscriptor.setDomicilio((String) map.get("domicilio"));
        if(map.get("estatusContrato") != null){
            suscriptor.setEstatusContrato((String) map.get("estatusContrato"));
        }
        if(map.get("estatusContratoId") != null){
            double estatusContratoId = (double) map.get("estatusContratoId");
            suscriptor.setEstatusContratoId((long) estatusContratoId);
        }
        suscriptor.setEstatusSuscriptor((String) map.get("estatusSuscriptor"));
        double estatusSuscriptorId = (double) map.get("estatusSuscriptorId");
        suscriptor.setEstatusSuscriptorId((long)estatusSuscriptorId);
        suscriptor.setFechaProximoPago((String) map.get("fechaProximoPago"));
        suscriptor.setFechaRegistroSuscriptor((String) map.get("fechaRegistroSuscriptor"));
        double id = (double) map.get("id");
        suscriptor.setId((long)id);
        suscriptor.setNombre((String) map.get("nombre"));
        suscriptor.setSucursal((String) map.get("sucursal"));
        double sucursalId = (double) map.get("sucursalId");
        suscriptor.setSucursalId((long)sucursalId);
        suscriptor.setTelefono((String) map.get("telefono"));
        if(map.get("servicioId") != null){
            double servicioId = (double) map.get("servicioId");
            suscriptor.setServicioId((long) servicioId);
        }
        if(map.get("servicio") != null){
            suscriptor.setServicio((String) map.get("servicio"));
        }
        return suscriptor;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public AuthResponse object2AuthResponse(Object object){
        
        AuthResponse response = new AuthResponse();
        Map<String, Object> map = getMapper(object);
        
        response.setPerfiles((List<String>) map.get("perfiles"));
        response.setToken((String) map.get("token"));
        response.setUsuario((String) map.get("usuario"));
        double d = (double) map.get("usuarioId");
        response.setUsuarioId((long) d);
        return response;
        
    }
    
    /**
     * 
     * @param list
     * @param tipoOrden
     * @return 
     */
    public List<Orden> ordenInstalacionList2Ordenes(List<OrdenInstalacion> list, TipoOrden tipoOrden){
        List<Orden> ordenes = new ArrayList<>();
        list.forEach(o -> ordenes.add(
                new Orden(o.getOrdenInstalacionId(), o.getContratoId(), tipoOrden.getTipoOrdenId(), 
                        tipoOrden.getDescripcion(), o.getCosto(), o.getFechaRegistro(), o.getCosto())));
        return ordenes;
    }
    
    /**
     * 
     * @param list
     * @param tipoOrden
     * @return 
     */
    public List<Orden> ordenCambioDomiclioList2Ordenes(List<OrdenCambioDomicilio> list, TipoOrden tipoOrden){
        List<Orden> ordenes = new ArrayList<>();
        list.forEach(o -> ordenes.add(
                new Orden(o.getOrdenCambioDomicilioId(), o.getContratoId(), tipoOrden.getTipoOrdenId(), 
                        tipoOrden.getDescripcion(), o.getCosto(), o.getFechaRegistro(), o.getCosto())));
        return ordenes;
    }
    
    /**
     * 
     * @param list
     * @param tipoOrden
     * @return 
     */
    public List<Orden> ordenServicioList2Ordenes(List<OrdenServicio> list, TipoOrden tipoOrden){
        List<Orden> ordenes = new ArrayList<>();
        for( OrdenServicio o : list){
            Orden orden = new Orden(o.getOrdenServicioId(), o.getContratoId(), tipoOrden.getTipoOrdenId(), 
                        tipoOrden.getDescripcion(), o.getCosto(), o.getFechaRegistro(), o.getCosto());
            orden.setConceptoOrdenServicio(o.getTipoOrdenServicio());
            ordenes.add(orden);
        }
        /*list.forEach(o -> ordenes.add(
                new Orden(o.getOrdenServicioId(), o.getContratoId(), tipoOrden.getTipoOrdenId(), 
                        tipoOrden.getDescripcion(), o.getCosto(), o.getFechaRegistro(), o.getCosto())));*/
        return ordenes;
    }
    
    
}
