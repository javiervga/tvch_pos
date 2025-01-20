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
import mx.com.tvch.pos.model.client.ListOrdenesInstalacionResponse;
import mx.com.tvch.pos.model.client.ListPromocionesOrdenInstalacionResponse;
import mx.com.tvch.pos.model.client.ListSuscriptoresResponse;
import mx.com.tvch.pos.model.client.ListTiposDescuentoResponse;
import mx.com.tvch.pos.model.client.OrdenInstalacion;
import mx.com.tvch.pos.model.client.PromocionOrdenInstalacion;
import mx.com.tvch.pos.model.client.Suscriptor;
import mx.com.tvch.pos.model.client.TipoDescuento;
import mx.com.tvch.pos.model.client.UpdateOrdenInstalacionResponse;

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
    
}
