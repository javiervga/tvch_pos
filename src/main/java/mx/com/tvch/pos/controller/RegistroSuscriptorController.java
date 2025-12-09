/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.util.Date;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.ContratoDao;
import mx.com.tvch.pos.dao.ContratoxSuscriptorDao;
import mx.com.tvch.pos.dao.DomicilioDao;
import mx.com.tvch.pos.dao.DomicilioPorContratoDao;
import mx.com.tvch.pos.dao.ServicioPorContratoDao;
import mx.com.tvch.pos.dao.SuscriptorDao;
import mx.com.tvch.pos.entity.ContratoEntity;
import mx.com.tvch.pos.entity.ContratoPorSuscriptorEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.DomicilioEntity;
import mx.com.tvch.pos.entity.DomicilioPorContratoEntity;
import mx.com.tvch.pos.entity.ServicioEntity;
import mx.com.tvch.pos.entity.ServicioPorContratoEntity;
import mx.com.tvch.pos.entity.SuscriptorEntity;
import mx.com.tvch.pos.model.Mes;
import mx.com.tvch.pos.model.TipoServicioInternet;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.viewModel.EstatusContrato;
import mx.com.tvch.pos.viewModel.RegistroSuscriptorPanel;

/**
 *
 * @author fvega
 */
public class RegistroSuscriptorController {

    private static RegistroSuscriptorController controller;

    private final ContratoDao contratoDao;
    private final SuscriptorDao suscriptorDao;
    private final ContratoxSuscriptorDao contratoxSuscriptorDao;
    private final DomicilioDao domicilioDao;
    private final DomicilioPorContratoDao domicilioPorContratoDao;
    private final ServicioPorContratoDao servicioPorContratoDao;
    private final Sesion sesion;
    private final Utilerias utilerias;

    public static RegistroSuscriptorController getRegistroSuscriptorController() {
        if (controller == null) {
            controller = new RegistroSuscriptorController();
        }
        return controller;
    }

    public RegistroSuscriptorController() {
        suscriptorDao = SuscriptorDao.getSuscriptorDao();
        contratoDao = ContratoDao.getContratoDao();
        contratoxSuscriptorDao = ContratoxSuscriptorDao.getContratoxSuscriptorDao();
        domicilioDao = DomicilioDao.getDomicilioDao();
        domicilioPorContratoDao = DomicilioPorContratoDao.getDomicilioPorContratoDao();
        servicioPorContratoDao = ServicioPorContratoDao.getServicioPorContratoDao();
        sesion = Sesion.getSesion();
        utilerias = Utilerias.getUtilerias();
    }
    
    /**
     * 
     * @param folio
     * @return 
     */
    public boolean existeFolioCOntrato(Long folio) throws Exception{
        
        return contratoDao.existeContrato(folio);
    }
    
    /**
     * 
     * @param entity
     * @param nombre
     * @param apellidoPaterno
     * @param apellidoMaterno
     * @param telefono
     * @param tvs
     * @param tipoServicioInternet
     * @param folioPlaca
     * @param colorPlaca
     * @param onu
     * @param nap
     * @param calle
     * @param numeroCalle
     * @param colonia
     * @param ciudad
     * @param calle1
     * @param calle2
     * @param referencia 
     */
    public void actualizarInformacionContrato(
            ContratoxSuscriptorDetalleEntity entity,
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String telefono,
            String tvs,
            TipoServicioInternet tipoServicioInternet,
            String folioPlaca,
            String colorPlaca,
            String onu,
            String nap,
            String calle,
            String numeroCalle,
            String colonia,
            String ciudad,
            String calle1,
            String calle2,
            String referencia){
        
        // actualizar suscriptor
        SuscriptorEntity suscriptorEntity = new SuscriptorEntity();
        suscriptorEntity.setId(entity.getSusucriptorId());
        suscriptorEntity.setNombre(utilerias.limpiarAcentos(nombre.toUpperCase()));
        suscriptorEntity.setApellidoPaterno(utilerias.limpiarAcentos(apellidoPaterno.toUpperCase()));
        suscriptorEntity.setApellidoMaterno(utilerias.limpiarAcentos(apellidoMaterno.toUpperCase()));
        suscriptorEntity.setTelefono(telefono);
        suscriptorEntity.setEstatus(entity.getEstatusSuscriptorId());

        // actualizar contrato
        ContratoEntity contratoEntity = new ContratoEntity();
        contratoEntity.setId(entity.getContratoId());
        contratoEntity.setFolioContrato(entity.getFolioContrato());
        contratoEntity.setEstatus(entity.getEstatusContratoId());
        if(tvs != null)
            contratoEntity.setTvs(Integer.valueOf(tvs));
        //contratoEntity.setFechaProximoPago(entity.getFechaProximoPago());  //no se actualiza
        contratoEntity.setTipoServicioId(tipoServicioInternet.getTipoServicioInternetId());
        contratoEntity.setColorPlaca(utilerias.limpiarAcentos(colorPlaca.toUpperCase()));
        
        //si la placa viene vacía, entonces dejarla igual que el folio del contrato ya que no puede ser null en la BD
        if(folioPlaca != null && !folioPlaca.trim().isEmpty())
            contratoEntity.setFolioPlaca(Long.valueOf(folioPlaca));
        else{
            contratoEntity.setFolioPlaca(Long.valueOf(contratoEntity.getFolioContrato()));
        }
        
        if(onu != null)
            contratoEntity.setOnu(utilerias.limpiarAcentos(onu.toUpperCase()));
        if(nap != null)
            contratoEntity.setNap(utilerias.limpiarAcentos(nap.toUpperCase()));

        // actualizar domicilio
        DomicilioEntity domicilioEntity = new DomicilioEntity();
        domicilioEntity.setId(entity.getDomicilioId());
        domicilioEntity.setColonia(utilerias.limpiarAcentos(colonia.toUpperCase()));
        domicilioEntity.setCalle(utilerias.limpiarAcentos(calle.toUpperCase()));
        domicilioEntity.setNumeroCalle(numeroCalle);
        domicilioEntity.setCiudad(utilerias.limpiarAcentos(ciudad.toUpperCase()));
        domicilioEntity.setCalle1(utilerias.limpiarAcentos(calle1.toUpperCase()));
        domicilioEntity.setCalle2(utilerias.limpiarAcentos(calle2.toUpperCase()));
        domicilioEntity.setReferencia(utilerias.limpiarAcentos(referencia.toUpperCase()));
        domicilioEntity.setEstatus(entity.getEstatusDomicilioId());

        suscriptorDao.actualizarSuscriptor(suscriptorEntity);
        contratoDao.actualizarContrato(contratoEntity);
        domicilioDao.actualizarDomicilio(domicilioEntity);
        
    }

    /**
     * 
     * @param seRegistraSuscriptor
     * @param nombre
     * @param apellidoPaterno
     * @param apellidoMaterno
     * @param telefono
     * @param folioContrato
     * @param servicioEntity
     * @param estatus
     * @param tvs
     * @param mesPago
     * @param anioPago
     * @param tipoServicioInternet
     * @param folioPlaca
     * @param colorPlaca
     * @param onu
     * @param nap
     * @param calle
     * @param numeroCalle
     * @param colonia
     * @param ciudad
     * @param calle1
     * @param calle2
     * @param referencia
     * @throws Exception 
     */
    public void registrarInformacionContrato(
            boolean seRegistraSuscriptor,
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String telefono,
            String folioContrato,
            ServicioEntity servicioEntity,
            EstatusContrato estatus,
            String tvs,
            Mes mesPago,
            Integer anioPago,
            TipoServicioInternet tipoServicioInternet,
            String folioPlaca,
            String colorPlaca,
            String onu,
            String nap,
            String calle,
            String numeroCalle,
            String colonia,
            String ciudad,
            String calle1,
            String calle2,
            String referencia) throws Exception {

        Date fechaPrimerPago = utilerias.obtenerFechaPago(mesPago, anioPago);
        String fechaPagoMySql = utilerias.convertirDateTime2String(fechaPrimerPago, Constantes.FORMATO_FECHA_MYSQL);

        SuscriptorEntity suscriptorEntity = new SuscriptorEntity();
        if (seRegistraSuscriptor) {
            suscriptorEntity.setId(utilerias.generarIdSucursal(sesion.getSucursalId()));
            suscriptorEntity.setNombre(utilerias.limpiarAcentos(nombre.toUpperCase()));
            suscriptorEntity.setApellidoPaterno(utilerias.limpiarAcentos(apellidoPaterno.toUpperCase()));
            suscriptorEntity.setApellidoMaterno(utilerias.limpiarAcentos(apellidoMaterno.toUpperCase()));
            suscriptorEntity.setTelefono(telefono);
            suscriptorEntity.setUsuarioId(sesion.getUsuarioId());
            suscriptorEntity.setEstatus(Constantes.ESTATUS_SUSCRIPTOR_ACTIVO);
        }

        ContratoEntity contratoEntity = new ContratoEntity();
        contratoEntity.setId(utilerias.generarIdSucursal(sesion.getSucursalId()));
        contratoEntity.setFolioContrato(Long.parseLong(folioContrato));
        contratoEntity.setEstatus(estatus.getId());
        contratoEntity.setTvs(Integer.parseInt(tvs));
        contratoEntity.setFechaProximoPago(fechaPagoMySql);
        contratoEntity.setTipoServicioId(tipoServicioInternet.getTipoServicioInternetId());
        contratoEntity.setColorPlaca(utilerias.limpiarAcentos(colorPlaca.toUpperCase()));
        if(folioPlaca != null && !folioPlaca.trim().isEmpty())
            contratoEntity.setFolioPlaca(Long.parseLong(folioPlaca));
        else
            contratoEntity.setFolioPlaca(Long.parseLong(folioContrato));
        contratoEntity.setUsuarioId(sesion.getUsuarioId());
        if(onu != null)
            contratoEntity.setOnu(utilerias.limpiarAcentos(onu.toUpperCase()));
        contratoEntity.setPrimerDiaPago(sesion.getDiaCorte());
        contratoEntity.setPrimerMesPago(mesPago.getNumero());
        contratoEntity.setPrimerAnioPago(anioPago);
        if(nap != null)
            contratoEntity.setNap(utilerias.limpiarAcentos(nap.toUpperCase()));
        contratoEntity.setNumeroCaja(sesion.getNumeroCaja());

        ContratoPorSuscriptorEntity entity = new ContratoPorSuscriptorEntity();
        entity.setIdContrato(contratoEntity.getId());
        if(seRegistraSuscriptor)
            entity.setIdSuscriptor(suscriptorEntity.getId());
        else
            entity.setIdSuscriptor(sesion.getContratoSeleccionado().getSusucriptorId());

        DomicilioEntity domicilioEntity = new DomicilioEntity();
        domicilioEntity.setId(utilerias.generarIdSucursal(sesion.getSucursalId()));
        domicilioEntity.setColonia(utilerias.limpiarAcentos(colonia.toUpperCase()));
        domicilioEntity.setCalle(utilerias.limpiarAcentos(calle.toUpperCase()));
        domicilioEntity.setNumeroCalle(numeroCalle);
        domicilioEntity.setCiudad(utilerias.limpiarAcentos(ciudad.toUpperCase()));
        domicilioEntity.setCalle1(utilerias.limpiarAcentos(calle1.toUpperCase()));
        domicilioEntity.setCalle2(utilerias.limpiarAcentos(calle2.toUpperCase()));
        domicilioEntity.setReferencia(utilerias.limpiarAcentos(referencia.toUpperCase()));
        domicilioEntity.setEstatus(Constantes.ESTATUS_DOMICILIO_CONTRATO_ACTIVO);

        DomicilioPorContratoEntity domicilioPorContratoEntity = new DomicilioPorContratoEntity();
        domicilioPorContratoEntity.setIdContrato(contratoEntity.getId());
        domicilioPorContratoEntity.setIdDomicilio(domicilioEntity.getId());
        
        ServicioPorContratoEntity servicioPorContratoEntity = new ServicioPorContratoEntity();
        servicioPorContratoEntity.setIdContrato(contratoEntity.getId());
        servicioPorContratoEntity.setIdServicio(servicioEntity.getServicioId());
        servicioPorContratoEntity.setEstatus(Constantes.ESTATUS_SERVICIO_CONTRATO_ACTIVO);
        

        if (seRegistraSuscriptor) {
            suscriptorDao.registrarSuscriptor(suscriptorEntity);
        }
        contratoDao.registrarContrato(contratoEntity);
        contratoxSuscriptorDao.registrarCOntratoxSuscriptor(entity);
        domicilioDao.registrarDomicilio(domicilioEntity);
        domicilioPorContratoDao.registrarDomicilioPorContrato(domicilioPorContratoEntity);
        servicioPorContratoDao.registrarServicioPorContrato(servicioPorContratoEntity);
        
        sesion.setContratoRegistrado(contratoEntity.getFolioContrato());
        sesion.setTipoBusquedaAlmacenada(null);
        sesion.setTextoBusquedaAlmacenada(null);

    }


}
