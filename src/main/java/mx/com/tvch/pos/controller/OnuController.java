/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import mx.com.tvch.pos.dao.ContratoDao;
import mx.com.tvch.pos.dao.ContratoxSuscriptorDao;
import mx.com.tvch.pos.dao.OnuDao;
import mx.com.tvch.pos.entity.ContratoEntity;
import mx.com.tvch.pos.entity.ContratoJoinOnuEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.OnuEntity;
import mx.com.tvch.pos.model.EstatusOnu;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.TvchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class OnuController {
    
    private static OnuController controller;
    
    private final OnuDao onuDao;
    private final ContratoDao contratoDao;
    private final ContratoxSuscriptorDao contratoxSuscriptorDao;
    
    Logger logger = LoggerFactory.getLogger(CobroController.class);

    public static OnuController getOnuController() {
        if (controller == null) {
            controller = new OnuController();
        }
        return controller;
    }

    public OnuController() {
        onuDao = OnuDao.getOnuDao();
        contratoDao = ContratoDao.getContratoDao();
        contratoxSuscriptorDao = ContratoxSuscriptorDao.getContratoxSuscriptorDao();
    }
    
    /**
     * 
     * @param serieBuscada
     * @param estatusOnu
     * @return 
     */
    public List<ContratoJoinOnuEntity> consultarOnus(String serieBuscada , EstatusOnu estatusOnu) throws NoSuchElementException, Exception{
        
        List<ContratoJoinOnuEntity> onus = new ArrayList<>();
        
        if (estatusOnu.getEstatusId() == Constantes.ESTATUS_ONU_TODOS) {
            
            onus = onuDao.consultarOnus(serieBuscada, estatusOnu.getEstatusId());
            
        }else{
            
            onus = onuDao.consultarOnus(serieBuscada, null);
            
        }
        
        return onus;
        
    }
    
    /**
     * 
     * @param onuEntity
     * @param nuevaSerie 
     */
    public void actualizarSerieOnu(ContratoJoinOnuEntity onuEntity, String nuevaSerie){
        
        onuDao.actualizarOnu(onuEntity.getOnuId(), nuevaSerie, onuEntity.getEstatusOnuId());
        
    }
    
    /**
     * 
     * @param onuEntity
     * @throws Exception 
     */
    public void retirarOnu(OnuEntity onuEntity) throws Exception{
        
        List<ContratoEntity> contratos = contratoDao.consultarContratosPorOnu(onuEntity.getOnuId());
        
        if(contratos.isEmpty())
            throw new TvchException((""));
        
        onuDao.actualizarOnu(onuEntity.getOnuId(), onuEntity.getSerie(), Constantes.ESTATUS_ONU_DISPONIBLE);
        
    }
    
    /**
     * 
     * @param onuId
     * @param estatusId
     * @throws Exception 
     */
    public void cambiarEstatusOnu(Long onuId, Long estatusId) throws Exception{
        
        OnuEntity entity = onuDao.consultarOnu(onuId);
        entity.setEstatusId(estatusId);
        onuDao.actualizarOnu(entity.getOnuId(), entity.getSerie(), estatusId);
        
    }
    
    /**
     * 
     * @param onuId
     * @param contratoId
     * @throws Exception 
     */
    public void asignarOnuContrato(Long onuId, Long contratoId) throws Exception{
        
        OnuEntity entity = onuDao.consultarOnu(onuId);
        //entity.setEstatusId();
        onuDao.actualizarOnu(entity.getOnuId(), entity.getSerie(), Constantes.ESTATUS_ONU_ASIGNADA);
        contratoDao.actualizarOnu(contratoId, onuId);
        
    }
    
    /**
     * 
     * @param onuId
     * @throws Exception 
     */
    public void retirarOnuContrato(Long onuId) throws TvchException, Exception{
        
        OnuEntity entity = onuDao.consultarOnu(onuId);
        
        //obtener el contrato con la onu
        List<ContratoEntity> contratoEntitys = contratoDao.consultarContratosPorOnu(onuId);
        
        if(contratoEntitys.isEmpty())
            throw new TvchException("No se encontró el contrato asociado a la Onu indicada. Por favor contacte a Soporte Técnico");
        
        if(contratoEntitys.size() > 1)
            throw new TvchException("Se han encontrado múltiples contratos con la Onu seleccionada. Pr favor contacte a Soporte Técnico");
        
        onuDao.actualizarOnu(entity.getOnuId(), entity.getSerie(), Constantes.ESTATUS_ONU_DISPONIBLE);
        contratoDao.actualizarOnu(contratoEntitys.get(0).getId(), null);
        
    }
    
    /**
     * 
     * @param contratoId
     * @param esBusquedaPorFolio
     * @return
     * @throws Exception 
     */
    public ContratoxSuscriptorDetalleEntity consultarInformacionContratoSuscriptor(Long contratoId, boolean esBusquedaPorFolio) throws TvchException, Exception{
        
        if(contratoId == 0)
            throw new TvchException("El contrato seleccionado no tiene asociada una Onu");
        
        List<ContratoxSuscriptorDetalleEntity> entitys = contratoxSuscriptorDao.obtenerInformacionContratoSuscriptor(contratoId, esBusquedaPorFolio);
        if(entitys.isEmpty())
            throw new TvchException("Sin registros de Contrato");
        
        if(entitys.size() > 1)
            throw new TvchException("Múltiples registros del contrato solicitado, intente de nuevo. Si el problema persiste contacte a Soporte Técnico");
        
        return entitys.get(0);
        
    }
    
    
    
}
