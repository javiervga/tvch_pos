/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.RegistroSuscriptorController;
import mx.com.tvch.pos.controller.ServicioController;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import mx.com.tvch.pos.model.Mes;
import mx.com.tvch.pos.model.TipoServicioInternet;
import mx.com.tvch.pos.entity.ServicioEntity;
import mx.com.tvch.pos.util.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class RegistroSuscriptorPanel extends javax.swing.JPanel {
    
    private static RegistroSuscriptorPanel panel;
    private static PosFrame posFrame;
    private static boolean seRegistraSuscriptor;

    private final Sesion sesion;
    private final RegistroSuscriptorController controller;
    private final ServicioController servicioController;
    private final Utilerias utilerias;
    private boolean esNuevoSuscriptor;
    private boolean esNuevoCOntrato;
    
    Logger logger = LoggerFactory.getLogger(RegistroSuscriptorPanel.class);
    
    public static  RegistroSuscriptorPanel getRegistroSuscriptorPanel(PosFrame frame){
        if(panel == null)
            panel = new RegistroSuscriptorPanel();
        posFrame = frame;
        return panel;
    }

    /**
     * Creates new form IngresoCajaPanel
     */
    public RegistroSuscriptorPanel() {
        initComponents();
        sesion = Sesion.getSesion();
        controller = RegistroSuscriptorController.getRegistroSuscriptorController();
        servicioController = ServicioController.getServicioController();
        utilerias = Utilerias.getUtilerias();
        crearEventos();
    }
    
    public void crearEventos() {

        ActionListener botonRegresarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPantalla();
                if(esNuevoSuscriptor){
                    posFrame.cambiarPantalla(panel, VentanaEnum.CONSULTA_CONTRATOS_NUEVO_CONTRATO);
                }else if(esNuevoCOntrato){
                    posFrame.cambiarPantalla(panel, VentanaEnum.CONSULTA_CONTRATOS_NUEVO_CONTRATO);
                }else{
                    posFrame.cambiarPantalla(panel, VentanaEnum.CONSULTA_CONTRATOS);
                }
            }
        };
        botonRegresar.addActionListener(botonRegresarActionListener);

        ActionListener botonAceptarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                try{

                    List<String> erroresEnInformacion = obtenerErroresInformacion();

                    if(erroresEnInformacion.isEmpty()){

                        boolean seConfirmaRegistro = false;
                        StringBuilder sb = new StringBuilder();
                        if(seRegistraSuscriptor){
                            sb.append("Se va a registrar al nuevo suscriptor ");
                            sb.append(campoNombre.getText()).append(" ");
                            sb.append(campoApellidoPaterno.getText()).append(" ");
                            sb.append(campoApellidoMaterno.getText()).append(" \n y se generará un nuevo contrato con número de folio ");
                            sb.append(campoFolioContrato.getText());
                            sb.append("\n¿Los datos son correctos?\n");
                        }else{
                            sb.append("Se va a registrar un nuevo contrato con el folio ");
                            sb.append(campoFolioContrato.getText()).append(" \n asociado al suscriptor ");
                            sb.append(campoNombre.getText()).append(" ");
                            sb.append(campoApellidoPaterno.getText()).append(" ");
                            sb.append(campoApellidoMaterno.getText());
                            sb.append("\n¿Los datos son correctos?\n");
                        }
                        int input = JOptionPane.showConfirmDialog(null, sb.toString());
                        if (input == 0) {
                            seConfirmaRegistro = true;
                        }

                        if(seConfirmaRegistro){
                            try {
                                controller.registrarInformacionContrato(
                                        seRegistraSuscriptor,
                                        campoNombre.getText().trim(),
                                        campoApellidoPaterno.getText().trim(),
                                        campoApellidoMaterno.getText().trim(),
                                        campoTelefono.getText().trim(),
                                        campoFolioContrato.getText().trim(),
                                        (ServicioEntity)comboServicios.getSelectedItem(),
                                        campoTvs.getText().trim(),
                                        (Mes)comboMeses.getSelectedItem(),
                                        (Integer)comboAnios.getSelectedItem(),
                                        (TipoServicioInternet)comboTiposInternet.getSelectedItem(),
                                        campoFolioPlaca.getText().trim(),
                                        campoColorPlaca.getText().trim(),
                                        campoOnu.getText().trim(),
                                        campoNap.getText().trim(),
                                        campoCalle.getText().trim(), 
                                        campoNumeroExt.getText().trim(), 
                                        campoColonia.getText().trim(), 
                                        campoCiudad.getText().trim(), 
                                        campoEntreCalle1.getText().trim(), 
                                        campoEntreCalle2.getText().trim(), 
                                        areaReferencia.getText().trim());

                                if(seRegistraSuscriptor)
                                    JOptionPane.showMessageDialog(panel, "Su nuevo suscriptor y contrato se han registrado exitosamente", "", JOptionPane.INFORMATION_MESSAGE);
                                else
                                    JOptionPane.showMessageDialog(panel, "Contrato registrado exitosamente", "", JOptionPane.INFORMATION_MESSAGE);

                                //validacion extra, si el campo del flio de la placa se fue vacio, quiere decir que se 
                                //registro con el numero de folio de contrato, asi que hace falta actualizarlo
                                if(campoFolioPlaca.getText() == null || campoFolioPlaca.getText().trim().isEmpty()){
                                    campoFolioPlaca.setText(campoFolioContrato.getText());
                                }
                                
                                //al final deshabilitar botones para q no intente generar un duplicado
                                deshabilitarCamposSuscriptor();
                                deshbilitarCamposContrato();
                                botonRegistrar.setEnabled(false);

                            } catch (Exception ex) {
                                if(seRegistraSuscriptor)
                                    logger.error("Error al intentar registrar nuevo suscriptor con contrato");
                                else
                                    logger.error("Error al intentar registrar nuevo contrato");
                                JOptionPane.showMessageDialog(panel, "Ocurrió un error al intentar el registro. Por favor póngase en contacto con soporte.", "", JOptionPane.ERROR_MESSAGE);
                            }


                        }
                    
                    
                    }else{

                        String cadenaErrores = "";
                        for(String error : erroresEnInformacion){
                            cadenaErrores = cadenaErrores.concat(error);
                        }

                        JOptionPane.showMessageDialog(panel, 
                                "La información capturada contiene los siguientes errores: \n"+erroresEnInformacion.toString()+" \n Por favor valide.",
                                "", JOptionPane.WARNING_MESSAGE);

                    }
                
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(panel, 
                            ex.getMessage(),
                            "", JOptionPane.ERROR);
                }

            }
        };
        botonRegistrar.addActionListener(botonAceptarActionListener);
        
        ActionListener botonActualizarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                List<String> erroresEnInformacion = obtenerErroresInformacionActualizacion();
                
                if(erroresEnInformacion.isEmpty()){
                    
                    boolean seConfirmaActualizacion = false;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Se va a actualizar el contrato ").append(campoFolioContrato.getText());
                    sb.append(" perteneciente al suscriptor ");
                    sb.append(campoNombre.getText()).append(" ");
                    sb.append(campoApellidoPaterno.getText()).append(" ");
                    sb.append(campoApellidoMaterno.getText());
                    sb.append("\n¿Los datos son correctos?\n");
              
                    int input = JOptionPane.showConfirmDialog(null, sb.toString());
                    if (input == 0) {
                        seConfirmaActualizacion = true;
                    }
                    
                    if(seConfirmaActualizacion){
                        try {
                            controller.actualizarInformacionContrato(
                                    sesion.getContratoSeleccionado(),
                                    campoNombre.getText().trim(),
                                    campoApellidoPaterno.getText().trim(),
                                    campoApellidoMaterno.getText().trim(),
                                    campoTelefono.getText().trim(),
                                    campoTvs.getText().trim(),
                                    (TipoServicioInternet)comboTiposInternet.getSelectedItem(),
                                    campoFolioPlaca.getText().trim(),
                                    campoColorPlaca.getText().trim(),
                                    campoOnu.getText().trim(),
                                    campoNap.getText().trim(),
                                    campoCalle.getText().trim(), 
                                    campoNumeroExt.getText().trim(), 
                                    campoColonia.getText().trim(), 
                                    campoCiudad.getText().trim(), 
                                    campoEntreCalle1.getText().trim(), 
                                    campoEntreCalle2.getText().trim(), 
                                    areaReferencia.getText().trim());
                            
                        } catch (Exception ex) {

                            logger.error("Error al intentar actualizar contrato");
                            JOptionPane.showMessageDialog(panel, "Ocurrió un error al actualizar información. Por favor póngase en contacto con soporte.", "", JOptionPane.ERROR_MESSAGE);
                        }             

                        JOptionPane.showMessageDialog(panel, "Información registrada exitosamente", "", JOptionPane.INFORMATION_MESSAGE);
                        
                        //validacion extra, si el campo del flio de la placa se fue vacio, quiere decir que se 
                        //registro con el numero de folio de contrato, asi que hace falta actualizarlo
                        if(campoFolioPlaca.getText() == null || campoFolioPlaca.getText().trim().isEmpty())
                            campoFolioPlaca.setText(campoFolioContrato.getText());
                        }
                    
                }else{
                    
                    String cadenaErrores = "";
                    for(String error : erroresEnInformacion){
                        cadenaErrores = cadenaErrores.concat(error);
                    }
                    
                    JOptionPane.showMessageDialog(panel, 
                            "La información capturada contiene los siguientes errores: \n"+erroresEnInformacion.toString()+" \n Por favor valide.",
                            "", JOptionPane.WARNING_MESSAGE);
                    
                }
                
            }
        };
        botonActualizar.addActionListener(botonActualizarActionListener);
        
        

    }
    
    /**
     * 
     * @return 
     */
    private List<String> obtenerErroresInformacion() throws Exception{
        
        List<String> errores = new ArrayList<>();
        
        if(sesion.getContratoSeleccionado() == null){
            if( campoNombre.getText().trim().isEmpty() || campoNombre.getText().length() > 40)
                errores.add("El Nombre del suscriptor debe tener una longitud de entre 1 y 40 caracteres \n ");
            if( campoApellidoPaterno.getText().trim().isEmpty() || campoApellidoPaterno.getText().length() > 40)
                errores.add("El Apellido Paterno del suscriptor debe tener una longitud de entre 1 y 40 caracteres  \n ");
            if( campoApellidoMaterno.getText().trim().isEmpty() || campoApellidoMaterno.getText().length() > 40)
                errores.add("El Apellido Materno del suscriptor debe tener una longitud de entre 1 y 40 caracteres  \n ");
            if( campoTelefono.getText().trim().isEmpty() || !campoTelefono.getText().matches("\\d+") || campoTelefono.getText().length() > 10)
                errores.add("Formato de teléfono inválido  \n ");
        }
        
        if( campoFolioContrato.getText().trim().isEmpty()){
            errores.add("El folio de contrato no puede ir vacío  \n ");
        }else if(!campoFolioContrato.getText().matches("\\d+")){
            errores.add("Formato de Folio de contrato inválido  \n ");
        }else{
            try{
                //validar si el folio del contrato ya existe
                if(controller.existeFolioCOntrato(Long.valueOf(campoFolioContrato.getText()))){
                    errores.add("Ya existe el contrato con folio ".concat(campoFolioContrato.getText()).concat(" \n"));
                }
            }catch(Exception e){
                throw new Exception("Ocurrión un error al validar folio de contrato. Por favor reintente, si el problema persiste contacte a soporte");
            }
        }
        
        
        
        if(comboServicios.getSelectedItem() == null)
            errores.add("No existe un servicio seleccionado  \n ");
        
        if( campoTvs.getText().trim().isEmpty()){
            errores.add("El número de TV´s no puede ir vacío  \n ");
        }else if(!campoTvs.getText().matches("\\d+")){
            errores.add("Número de TV´s inválido  \n ");
        }
        
        if(comboMeses.getSelectedItem() == null)
            errores.add("No existe un mes seleccionado  \n ");
        
        if(comboAnios.getSelectedItem() == null)
            errores.add("No existe un año seleccionado  \n ");
          
        if(comboTiposInternet.getSelectedItem() == null)
            errores.add("No existe tipo de internet seleccionado  \n ");
        
        if(!campoFolioPlaca.getText().trim().isEmpty()){
            if(!campoFolioPlaca.getText().matches("\\d+"))
                errores.add("Formato de Folio de Placa inválido  \n ");
        }
        
        if( campoColorPlaca.getText().trim().isEmpty() || campoColorPlaca.getText().length() > 20)
            errores.add("El color de placa debe tener una longitud de entre 1 y 20 caracteres  \n ");
        
        if( campoOnu.getText().trim().isEmpty() || campoOnu.getText().length() > 50)
            errores.add("La ONU debe tener una longitud de entre 1 y 50 caracteres  \n ");
        
        if( campoNap.getText().trim().isEmpty() || campoNap.getText().length() > 50)
            errores.add("La Nap debe tener una longitud de entre 1 y 50 caracteres  \n ");
        
        if( campoCalle.getText().trim().isEmpty() || campoCalle.getText().length() > 120)
            errores.add("La calle debe tener una longitud de entre 1 y 120 caracteres  \n ");
        
        if( campoNumeroExt.getText().trim().isEmpty() || campoNumeroExt.getText().length() > 25)
            errores.add("El número de calle debe tener una longitud de entre 1 y 25 caracteres  \n ");
        
        if( campoColonia.getText().trim().isEmpty() || campoColonia.getText().length() > 70)
            errores.add("La colonia debe tener una longitud de entre 1 y 70 caracteres  \n ");
        
        if( campoCiudad.getText().trim().isEmpty() || campoCiudad.getText().length() > 70)
            errores.add("La ciudad debe tener una longitud de entre 1 y 70 caracteres  \n ");
        
        if( campoEntreCalle1.getText().trim().isEmpty() || campoEntreCalle1.getText().length() > 120)
            errores.add("La primera calle de referencia debe tener una longitud de entre 1 y 120 caracteres  \n ");
        
        if( campoEntreCalle2.getText().trim().isEmpty() || campoEntreCalle2.getText().length() > 120)
            errores.add("La segunda calle de referencia debe tener una longitud de entre 1 y 120 caracteres \n ");
        
        if( areaReferencia.getText().trim().isEmpty() || areaReferencia.getText().length() > 300)
            errores.add("La referencia de domicilio debe tener una longitud de entre 1 y 300 caracteres \n ");
        
        //al final validar que sea un mes y año validos
        LocalDate ld = LocalDate.now();
        Integer anioSeleccionado = (Integer) comboAnios.getSelectedItem();
        if( anioSeleccionado < ld.getYear() ){
            errores.add("El año seleccionado no puede ser menor al año en curso \n ");
        }else if(ld.getYear() == anioSeleccionado){
            Mes mesSeleccionado = (Mes) comboMeses.getSelectedItem();
            if( mesSeleccionado.getNumero() < ld.getMonthValue() )
                errores.add("El mes de primer pago debe ser igual o mayor al mes en curso \n ");
        }
        
        return errores;
        
    }
    
    /**
     * 
     * @return 
     */
    private List<String> obtenerErroresInformacionActualizacion(){
        
        List<String> errores = new ArrayList<>();
        
        if( campoNombre.getText().trim().isEmpty() || campoNombre.getText().length() > 40)
            errores.add("El Nombre del suscriptor debe tener una longitud de entre 1 y 40 caracteres \n ");
        if( campoApellidoPaterno.getText().trim().isEmpty() || campoApellidoPaterno.getText().length() > 40)
            errores.add("El Apellido Paterno del suscriptor debe tener una longitud de entre 1 y 40 caracteres  \n ");
        if( campoApellidoMaterno.getText().trim().isEmpty() || campoApellidoMaterno.getText().length() > 40)
            errores.add("El Apellido Materno del suscriptor debe tener una longitud de entre 1 y 40 caracteres  \n ");
        if( campoTelefono.getText().trim().isEmpty() || !campoTelefono.getText().matches("\\d+") || campoTelefono.getText().length() > 10)
            errores.add("Formato de teléfono inválido  \n ");
        
        if( campoTvs.getText() != null){
            if( campoTvs.getText().trim().isEmpty()){
                errores.add("El número de TV´s no puede ir vacío  \n ");
            }else if(!campoTvs.getText().matches("\\d+")){
                errores.add("Número de TV´s inválido  \n ");
            }
        }
          
        if(comboTiposInternet.getSelectedItem() == null)
            errores.add("No existe tipo de internet seleccionado  \n ");
        
        if( campoFolioPlaca.getText() != null){
            if(!campoFolioPlaca.getText().trim().isEmpty()){
                if(!campoFolioPlaca.getText().matches("\\d+"))
                    errores.add("Formato de Folio de Placa inválido  \n ");
            }
        }
        
        if( campoColorPlaca.getText().trim().isEmpty() || campoColorPlaca.getText().length() > 20)
            errores.add("El color de placa debe tener una longitud de entre 1 y 20 caracteres  \n ");
        
        if( campoOnu.getText().trim().isEmpty() || campoOnu.getText().length() > 50)
            errores.add("La ONU debe tener una longitud de entre 1 y 50 caracteres  \n ");
        
        if( campoNap.getText().trim().isEmpty() || campoNap.getText().length() > 50)
            errores.add("La Nap debe tener una longitud de entre 1 y 50 caracteres  \n ");
        
        if( campoCalle.getText().trim().isEmpty() || campoCalle.getText().length() > 120)
            errores.add("La calle debe tener una longitud de entre 1 y 120 caracteres  \n ");
        
        if( campoNumeroExt.getText().trim().isEmpty() || campoNumeroExt.getText().length() > 25)
            errores.add("El número de calle debe tener una longitud de entre 1 y 25 caracteres  \n ");
        
        if( campoColonia.getText().trim().isEmpty() || campoColonia.getText().length() > 70)
            errores.add("La colonia debe tener una longitud de entre 1 y 70 caracteres  \n ");
        
        if( campoCiudad.getText().trim().isEmpty() || campoCiudad.getText().length() > 70)
            errores.add("La ciudad debe tener una longitud de entre 1 y 70 caracteres  \n ");
        
        if( campoEntreCalle1.getText().trim().isEmpty() || campoEntreCalle1.getText().length() > 120)
            errores.add("La primera calle de referencia debe tener una longitud de entre 1 y 120 caracteres  \n ");
        
        if( campoEntreCalle2.getText().trim().isEmpty() || campoEntreCalle2.getText().length() > 120)
            errores.add("La segunda calle de referencia debe tener una longitud de entre 1 y 120 caracteres \n ");
        
        if( areaReferencia.getText().trim().isEmpty() || areaReferencia.getText().length() > 300)
            errores.add("La referencia de domicilio debe tener una longitud de entre 1 y 300 caracteres \n ");
        
        return errores;
        
    }
    
    /**
     * 
     */
    private void limpiarPantalla(){
        campoNombre.setText("");
        campoApellidoMaterno.setText("");
        campoApellidoPaterno.setText("");
        campoTelefono.setText("");
        
        campoFolioContrato.setText("");
        campoTvs.setText("");
        campoFolioPlaca.setText("");
        campoColorPlaca.setText("");
        campoOnu.setText("");
        campoNap.setText("");
        campoCalle.setText("");
        campoNumeroExt.setText("");
        campoColonia.setText("");
        campoCiudad.setText("");
        campoEntreCalle1.setText("");
        campoEntreCalle2.setText("");
        areaReferencia.setText("");
        
        comboTiposInternet.removeAllItems();
        comboMeses.removeAllItems();
        comboAnios.removeAllItems();
        comboServicios.removeAllItems();
        
    }
    
    /**
     * 
     */
    public void cargarDatosSesionNuevoSuscriptor() {
        
        //barra de informacion
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        
        // banderas de control
        seRegistraSuscriptor = true;
        esNuevoSuscriptor = true;
        esNuevoCOntrato = false;
        
        //botones
        botonRegistrar.setEnabled(true);
        botonActualizar.setEnabled(false);
        
        //datos del suscriptor
        campoNombre.setEnabled(true);
        campoApellidoPaterno.setEnabled(true);
        campoApellidoMaterno.setEnabled(true);
        campoTelefono.setEnabled(true);
        
        //datos del contrato
        etiquetaMes.setText("Mes Primer Pago");
        etiquetaAnio.setText("Año Primer Pago");        
        campoFolioContrato.setEnabled(true);
        campoEstatus.setEnabled(false);
        campoEstatus.setText("NUEVO");
        comboServicios.setEnabled(true);
        campoTvs.setEnabled(true);
        etiquetaMes.setText("Mes Primer Pago");
        etiquetaAnio.setText("Año Primer Pago");
        comboMeses.setEnabled(true);
        comboAnios.setEnabled(true);
        comboTiposInternet.setEnabled(true);
        campoFolioPlaca.setEnabled(true);
        campoColorPlaca.setEnabled(true);
        campoOnu.setEnabled(true);
        campoNap.setEnabled(true);
        campoCalle.setEnabled(true);
        campoNumeroExt.setEnabled(true);
        campoColonia.setEnabled(true);
        campoCiudad.setEnabled(true);
        campoEntreCalle1.setEnabled(true);
        campoEntreCalle2.setEnabled(true);
        areaReferencia.setEnabled(true);
        areaReferencia.setLineWrap(true);
        areaReferencia.setRows(4);
        
        cargarComboTiposInternet();
        cargarComboMeses();
        cargarComboAnios();
        cargarComboServicios();
    }
    
    /**
     * 
     */
    public void cargarDatosSesionNuevoContrato() {
              
        //primero validar que si venga un registro en la sesion
        if(sesion.getContratoSeleccionado() == null){
            JOptionPane.showMessageDialog(panel, "Para generar un contrato nuevo, es necesario que seleccione un suscriptor de la lista.","", JOptionPane.ERROR_MESSAGE);
            posFrame.cambiarPantalla(panel, VentanaEnum.CONSULTA_CONTRATOS);
        }else{
            
            //barra de informacion
            etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
            etiquetaUsuario.setText(sesion.getUsuario());
            etiquetaSucursal.setText(sesion.getSucursal());
            
            //banderas de control
            esNuevoSuscriptor = false;
            esNuevoCOntrato = true;
            seRegistraSuscriptor = false;
            
            //botones
            botonRegistrar.setEnabled(true);
            botonActualizar.setEnabled(false);
            
            //informacion del suscriptor -> en este caso se pinta desde la sesion y se deshabulitan los campos
            campoNombre.setText(sesion.getContratoSeleccionado().getNombre());
            campoApellidoPaterno.setText(sesion.getContratoSeleccionado().getApellidoPaterno());
            campoApellidoMaterno.setText(sesion.getContratoSeleccionado().getApellidoMaterno());
            campoTelefono.setText(sesion.getContratoSeleccionado().getTelefono());
            campoNombre.setEnabled(false);
            campoApellidoPaterno.setEnabled(false);
            campoApellidoMaterno.setEnabled(false);
            campoTelefono.setEnabled(false);
            
            //informacion del contrato
            campoFolioContrato.setEnabled(true);
            campoEstatus.setEnabled(false);
            campoEstatus.setText("NUEVO");
            comboServicios.setEnabled(true);
            campoTvs.setEnabled(true);
            etiquetaMes.setText("Mes Primer Pago");
            etiquetaAnio.setText("Año Primer Pago");
            comboMeses.setEnabled(true);
            comboAnios.setEnabled(true);
            comboTiposInternet.setEnabled(true);
            campoFolioPlaca.setEnabled(true);
            campoColorPlaca.setEnabled(true);
            campoOnu.setEnabled(true);
            campoNap.setEnabled(true);
            campoCalle.setEnabled(true);
            campoNumeroExt.setEnabled(true);
            campoColonia.setEnabled(true);
            campoCiudad.setEnabled(true);
            campoEntreCalle1.setEnabled(true);
            campoEntreCalle2.setEnabled(true);
            areaReferencia.setEnabled(true);
            areaReferencia.setLineWrap(true);
            areaReferencia.setRows(4);
            

            cargarComboTiposInternet();
            cargarComboMeses();
            cargarComboAnios();
            cargarComboServicios();
             
        }
    }
    
    /**
     * 
     */
    public void cargarDatosSesionEdicionContrato() {
        
        //barra de informacion
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        
        //banderas de control
        esNuevoSuscriptor = false;
        esNuevoCOntrato = false;
        
        //botones
        botonRegistrar.setEnabled(false);
        botonActualizar.setEnabled(true);
        
        //informacion del suscriptor
        campoNombre.setEnabled(true);
        campoNombre.setText(sesion.getContratoSeleccionado().getNombre());
        campoApellidoPaterno.setEnabled(true);
        campoApellidoPaterno.setText(sesion.getContratoSeleccionado().getApellidoPaterno());
        campoApellidoMaterno.setEnabled(true);
        campoApellidoMaterno.setText(sesion.getContratoSeleccionado().getApellidoMaterno());
        campoTelefono.setEnabled(true);
        campoTelefono.setText(sesion.getContratoSeleccionado().getTelefono());
        
        //informacion del contrato 
        LocalDateTime fechaCorte = utilerias.dateToLocalDateTime(sesion.getContratoSeleccionado().getFechaProximoPago());
        ServicioEntity servicioSeleccionado = servicioController.consultarServicio(sesion.getContratoSeleccionado().getServicioId());
        Mes mesSeleccionado = utilerias.obtenerMes(fechaCorte.getMonthValue());
        
        campoFolioContrato.setText(String.valueOf(sesion.getContratoSeleccionado().getFolioContrato()));
        campoFolioContrato.setEnabled(false);
        
        comboServicios.setEnabled(false);
        if(servicioSeleccionado != null){
            //cargarComboServicios();
            //comboServicios.setSelectedItem(servicioSeleccionado);
            comboServicios.addItem(servicioSeleccionado);
        }
        
        
        campoTvs.setEnabled(true);
        campoTvs.setText(String.valueOf(sesion.getContratoSeleccionado().getTvsContratadas()));
        
        cargarComboMeses();
        etiquetaMes.setText("Mes Corte");
        comboMeses.setSelectedItem(mesSeleccionado);
        comboMeses.setEnabled(false);
        
        cargarComboAnios();
        etiquetaAnio.setText("Año Corte");
        comboAnios.setSelectedItem(fechaCorte.getYear());
        comboAnios.setEnabled(false);
        
        TipoServicioInternet tipo = null;
        if(sesion.getContratoSeleccionado().getTipoServicioInternet() == 1)
            tipo = new TipoServicioInternet(1L, "SMART");
        else
            tipo = new TipoServicioInternet(2L, "RADIOS MANAGER");
        cargarComboTiposInternet();
        if(sesion.getContratoSeleccionado().getTipoServicioInternet() == 1)
            comboTiposInternet.setSelectedIndex(0);
        else
            comboTiposInternet.setSelectedIndex(1);
        comboTiposInternet.setEnabled(true);
        
        campoEstatus.setEnabled(false);
        
        if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_NUEVO){
            campoEstatus.setText("NUEVO");
        }else if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_PENDIENTE_INSTALAR){
            campoEstatus.setText("PENDIENTE DE INSTALAR");
        }else if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_ACTIVO){
            campoEstatus.setText("ACTIVO");
        }else if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_CORTESIA){
            campoEstatus.setText("CORTESIA");
        }else if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_CORTE){
            campoEstatus.setText("CORTE");
        }else if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_RECONEXION){
            campoEstatus.setText("RECONEXION");
        }else if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO){
            campoEstatus.setText("CANCELADO PENDIENTE DE RETIRO");
        }else if(sesion.getContratoSeleccionado().getEstatusContratoId().longValue() == Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO){
            campoEstatus.setText("CANCELADO RETIRADO");
        }
        
        campoFolioPlaca.setEnabled(true);
        campoFolioPlaca.setText(String.valueOf(sesion.getContratoSeleccionado().getFolioPlaca()));
        
        campoColorPlaca.setEnabled(true);
        campoColorPlaca.setText(sesion.getContratoSeleccionado().getColorPlaca());
        
        campoOnu.setEnabled(true);
        campoOnu.setText(sesion.getContratoSeleccionado().getOnu());
        
        campoNap.setEnabled(true);
        campoNap.setText(sesion.getContratoSeleccionado().getNap());
        
        campoCalle.setEnabled(true);
        campoCalle.setText(sesion.getContratoSeleccionado().getCalle());
        
        campoNumeroExt.setEnabled(true);
        campoNumeroExt.setText(sesion.getContratoSeleccionado().getNumeroCalle());
        
        campoColonia.setEnabled(true);
        campoColonia.setText(sesion.getContratoSeleccionado().getColonia());
        
        campoCiudad.setEnabled(true);
        campoCiudad.setText(sesion.getContratoSeleccionado().getCiudad());
        
        campoEntreCalle1.setEnabled(true);
        campoEntreCalle1.setText(sesion.getContratoSeleccionado().getCalle1());
        
        campoEntreCalle2.setEnabled(true);
        campoEntreCalle2.setText(sesion.getContratoSeleccionado().getCalle2());
        
        areaReferencia.setEnabled(true);
        areaReferencia.setLineWrap(true);
        areaReferencia.setRows(4);
        areaReferencia.setText(sesion.getContratoSeleccionado().getReferencia());
    }
    
    /**
     * 
     */
    private void cargarComboServicios(){
        
        List<ServicioEntity> servicios = servicioController.obtenerServicios();
        servicios.forEach(s -> comboServicios.addItem(s));
    }   
    
    /**
     * 
     */
    private void cargarComboAnios(){
        
        Mes mesEnCurso = utilerias.obtenerMesEnCurso();

        List<Integer> anios = new ArrayList<>();
        LocalDate ld = LocalDate.now();
        Integer anio = ld.getYear();
        anios.add(anio);
        anios.add(anio+1);
        if(mesEnCurso.getNumero() == 12){
            anios.add(anio+2);
        }
        anios.forEach(a -> comboAnios.addItem(a));
        if(mesEnCurso.getNumero() == 12){
            comboAnios.setSelectedItem(anio+1);
        }
    }
    
    /**
     * 
     */
    private void cargarComboMeses(){
        
        Mes mesEnCurso = utilerias.obtenerMesEnCurso();
        
        List<Mes> meses = utilerias.obtenerMeses();
        meses.forEach(m -> comboMeses.addItem(m));
        
        if(mesEnCurso.getNumero() == 12){
            comboMeses.setSelectedIndex(0);
        }else{
            comboMeses.setSelectedItem(utilerias.obtenerMesEnCurso());
        }
    }

    /**
     * 
     */
    private void cargarComboTiposInternet(){
        List<TipoServicioInternet> list = new ArrayList<>();
        list.add(new TipoServicioInternet(1L, "SMART"));
        list.add(new TipoServicioInternet(2L, "RADIOS MANAGER"));
        list.forEach(ts -> comboTiposInternet.addItem(ts));
    }
    
    /**
     * 
     */
    private void deshabilitarCamposSuscriptor(){
        campoNombre.setEnabled(false);
        campoApellidoPaterno.setEnabled(false);
        campoApellidoMaterno.setEnabled(false);
        campoTelefono.setEnabled(false);
    }
    
    /**
     * 
     */
    private void deshbilitarCamposContrato(){
        campoEstatus.setEnabled(false);
        campoFolioContrato.setEnabled(false);
        comboServicios.setEnabled(false);
        campoTvs.setEnabled(false);
        comboMeses.setEnabled(false);
        comboAnios.setEnabled(false);
        comboTiposInternet.setEnabled(false);
        campoFolioPlaca.setEnabled(false);
        campoColorPlaca.setEnabled(false);
        campoOnu.setEnabled(false);
        campoNap.setEnabled(false);
        campoCalle.setEnabled(false);
        campoNumeroExt.setEnabled(false);
        campoColonia.setEnabled(false);
        campoCiudad.setEnabled(false);
        campoEntreCalle1.setEnabled(false);
        campoEntreCalle2.setEnabled(false);
        areaReferencia.setEnabled(false);
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonRegistrar = new javax.swing.JButton();
        botonRegresar = new javax.swing.JButton();
        panelIzquierdo = new javax.swing.JPanel();
        etiquetaLogo = new javax.swing.JLabel();
        panelHeader = new javax.swing.JPanel();
        etiquetaDescCaja = new javax.swing.JLabel();
        etiquetaNumeroCaja = new javax.swing.JLabel();
        etiquetaDescSucursal = new javax.swing.JLabel();
        etiquetaSucursal = new javax.swing.JLabel();
        etiquetaDescPanel = new javax.swing.JLabel();
        etiquetaDescUsuario = new javax.swing.JLabel();
        etiquetaUsuario = new javax.swing.JLabel();
        panelSuscriptor = new javax.swing.JPanel();
        etiquetaNuevoSuscriptor = new javax.swing.JLabel();
        etiquetaNombreSuscriptor = new javax.swing.JLabel();
        campoNombre = new javax.swing.JTextField();
        etiquetaApellidoPeterno = new javax.swing.JLabel();
        campoApellidoPaterno = new javax.swing.JTextField();
        etiquetaApellidoMaterno = new javax.swing.JLabel();
        campoApellidoMaterno = new javax.swing.JTextField();
        etiquetaTelefono = new javax.swing.JLabel();
        campoTelefono = new javax.swing.JTextField();
        panelContrato = new javax.swing.JPanel();
        etiquetaNuevoSuscriptor1 = new javax.swing.JLabel();
        panelDatosContrato1 = new javax.swing.JPanel();
        etiquetaFolioContrato = new javax.swing.JLabel();
        etiquetaServicio = new javax.swing.JLabel();
        comboServicios = new javax.swing.JComboBox<>();
        etiquetaTvs = new javax.swing.JLabel();
        campoTvs = new javax.swing.JTextField();
        etiquetaMes = new javax.swing.JLabel();
        comboMeses = new javax.swing.JComboBox<>();
        etiquetaAnio = new javax.swing.JLabel();
        comboAnios = new javax.swing.JComboBox<>();
        etiquetaAnio1 = new javax.swing.JLabel();
        comboTiposInternet = new javax.swing.JComboBox<>();
        etiquetaFolioPlaca = new javax.swing.JLabel();
        campoFolioPlaca = new javax.swing.JTextField();
        etiquetaAvisoFolioPlaca = new javax.swing.JLabel();
        campoFolioContrato = new javax.swing.JTextField();
        etiquetaColorPlaca = new javax.swing.JLabel();
        campoColorPlaca = new javax.swing.JTextField();
        etiquetaOnu = new javax.swing.JLabel();
        campoOnu = new javax.swing.JTextField();
        etiquetaNap = new javax.swing.JLabel();
        campoNap = new javax.swing.JTextField();
        etiquetaEstatus = new javax.swing.JLabel();
        campoEstatus = new javax.swing.JTextField();
        panelDatosContrato2 = new javax.swing.JPanel();
        etiquetaCalle = new javax.swing.JLabel();
        campoCalle = new javax.swing.JTextField();
        etiquetaCalle1 = new javax.swing.JLabel();
        campoNumeroExt = new javax.swing.JTextField();
        etiquetaCalle2 = new javax.swing.JLabel();
        campoColonia = new javax.swing.JTextField();
        etiquetaCalle3 = new javax.swing.JLabel();
        campoCiudad = new javax.swing.JTextField();
        etiquetaCalle4 = new javax.swing.JLabel();
        campoEntreCalle1 = new javax.swing.JTextField();
        etiquetaCalle5 = new javax.swing.JLabel();
        campoEntreCalle2 = new javax.swing.JTextField();
        etiquetaCalle6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaReferencia = new javax.swing.JTextArea();
        botonActualizar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1500, 800));
        setMinimumSize(new java.awt.Dimension(1500, 800));
        setPreferredSize(new java.awt.Dimension(1500, 800));

        botonRegistrar.setBackground(new java.awt.Color(163, 73, 164));
        botonRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegistrar.setText("Registrar Contrato");
        botonRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRegistrarActionPerformed(evt);
            }
        });

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        panelIzquierdo.setBackground(new java.awt.Color(255, 255, 255));
        panelIzquierdo.setMaximumSize(new java.awt.Dimension(300, 770));
        panelIzquierdo.setMinimumSize(new java.awt.Dimension(300, 770));
        panelIzquierdo.setPreferredSize(new java.awt.Dimension(300, 770));

        etiquetaLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen_menu.png"))); // NOI18N

        javax.swing.GroupLayout panelIzquierdoLayout = new javax.swing.GroupLayout(panelIzquierdo);
        panelIzquierdo.setLayout(panelIzquierdoLayout);
        panelIzquierdoLayout.setHorizontalGroup(
            panelIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIzquierdoLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(etiquetaLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        panelIzquierdoLayout.setVerticalGroup(
            panelIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIzquierdoLayout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addComponent(etiquetaLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(339, Short.MAX_VALUE))
        );

        panelHeader.setBackground(new java.awt.Color(255, 255, 255));
        panelHeader.setMaximumSize(new java.awt.Dimension(1500, 30));
        panelHeader.setMinimumSize(new java.awt.Dimension(1500, 30));
        panelHeader.setPreferredSize(new java.awt.Dimension(1500, 30));

        etiquetaDescCaja.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescCaja.setText("Caja Número:");

        etiquetaNumeroCaja.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaNumeroCaja.setText("0");

        etiquetaDescSucursal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescSucursal.setText("Sucursal:");

        etiquetaSucursal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaSucursal.setText("TV");

        etiquetaDescPanel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescPanel.setText("Registro/Actualización de Suscriptores");

        etiquetaDescUsuario.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescUsuario.setText("Usuario:");

        etiquetaUsuario.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaUsuario.setText("User");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(etiquetaDescCaja)
                .addGap(18, 18, 18)
                .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(155, 155, 155)
                .addComponent(etiquetaDescSucursal)
                .addGap(18, 18, 18)
                .addComponent(etiquetaSucursal)
                .addGap(141, 141, 141)
                .addComponent(etiquetaDescPanel)
                .addGap(208, 208, 208)
                .addComponent(etiquetaDescUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addGap(0, 5, Short.MAX_VALUE)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaDescCaja)
                    .addComponent(etiquetaNumeroCaja)))
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaDescSucursal)
                    .addComponent(etiquetaSucursal)
                    .addComponent(etiquetaDescPanel)
                    .addComponent(etiquetaDescUsuario)
                    .addComponent(etiquetaUsuario))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelSuscriptor.setBackground(new java.awt.Color(255, 255, 255));
        panelSuscriptor.setMaximumSize(new java.awt.Dimension(1198, 210));
        panelSuscriptor.setMinimumSize(new java.awt.Dimension(1198, 210));
        panelSuscriptor.setPreferredSize(new java.awt.Dimension(1198, 210));

        etiquetaNuevoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        etiquetaNuevoSuscriptor.setForeground(new java.awt.Color(163, 73, 164));
        etiquetaNuevoSuscriptor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaNuevoSuscriptor.setText("Datos del Suscriptor");

        etiquetaNombreSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaNombreSuscriptor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaNombreSuscriptor.setText("Nombre:");

        campoNombre.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoNombre.setToolTipText("Capture nombre del nuevo suscriptor");

        etiquetaApellidoPeterno.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaApellidoPeterno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaApellidoPeterno.setText("Apellido Paterno:");

        campoApellidoPaterno.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoApellidoPaterno.setToolTipText("Capture Apellido Paterno del Suscriptor");

        etiquetaApellidoMaterno.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaApellidoMaterno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaApellidoMaterno.setText("Apellido Materno:");

        campoApellidoMaterno.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoApellidoMaterno.setToolTipText("Capture Apellido Materno del nuevo suscriptor");

        etiquetaTelefono.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaTelefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaTelefono.setText("Teléfono:");

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoTelefono.setToolTipText("Capture teléfono del suscriptor");

        javax.swing.GroupLayout panelSuscriptorLayout = new javax.swing.GroupLayout(panelSuscriptor);
        panelSuscriptor.setLayout(panelSuscriptorLayout);
        panelSuscriptorLayout.setHorizontalGroup(
            panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSuscriptorLayout.createSequentialGroup()
                .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelSuscriptorLayout.createSequentialGroup()
                            .addComponent(etiquetaApellidoPeterno, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(campoApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelSuscriptorLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(etiquetaNuevoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(panelSuscriptorLayout.createSequentialGroup()
                                    .addComponent(etiquetaNombreSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(campoNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(panelSuscriptorLayout.createSequentialGroup()
                        .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(etiquetaTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(etiquetaApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(campoApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 355, Short.MAX_VALUE))
        );
        panelSuscriptorLayout.setVerticalGroup(
            panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSuscriptorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiquetaNuevoSuscriptor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaNombreSuscriptor)
                    .addComponent(campoNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaApellidoPeterno))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaApellidoMaterno))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSuscriptorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaTelefono))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        panelContrato.setBackground(new java.awt.Color(255, 255, 255));
        panelContrato.setMaximumSize(new java.awt.Dimension(1200, 440));
        panelContrato.setMinimumSize(new java.awt.Dimension(1200, 440));
        panelContrato.setPreferredSize(new java.awt.Dimension(1200, 440));

        etiquetaNuevoSuscriptor1.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        etiquetaNuevoSuscriptor1.setForeground(new java.awt.Color(163, 73, 164));
        etiquetaNuevoSuscriptor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaNuevoSuscriptor1.setText("Datos del Contrato");

        panelDatosContrato1.setBackground(new java.awt.Color(255, 255, 255));
        panelDatosContrato1.setMaximumSize(new java.awt.Dimension(500, 390));
        panelDatosContrato1.setMinimumSize(new java.awt.Dimension(500, 390));
        panelDatosContrato1.setPreferredSize(new java.awt.Dimension(500, 390));

        etiquetaFolioContrato.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaFolioContrato.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaFolioContrato.setText("Folio:");

        etiquetaServicio.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaServicio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaServicio.setText("Servicio:");

        comboServicios.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        comboServicios.setToolTipText("Seleccione el Servicio del Nuevo Contrato");

        etiquetaTvs.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaTvs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaTvs.setText("Tv´s:");

        campoTvs.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoTvs.setToolTipText("Capture el número de TV´s");

        etiquetaMes.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaMes.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaMes.setText("Mes Primer Pago:");

        comboMeses.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        comboMeses.setToolTipText("Seleccione el mes del primer pago");

        etiquetaAnio.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaAnio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaAnio.setText("Año Primer Pago:");

        comboAnios.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        comboAnios.setToolTipText("Seelccione el año del primer pago");

        etiquetaAnio1.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaAnio1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaAnio1.setText("Tipo Internet:");

        comboTiposInternet.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        comboTiposInternet.setToolTipText("Seleccione el Tipo de Internet");

        etiquetaFolioPlaca.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaFolioPlaca.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaFolioPlaca.setText("Folio Placa:");

        campoFolioPlaca.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoFolioPlaca.setToolTipText("Capture Folio de la Placa (Sólo cuando sea distina del contrato)");

        etiquetaAvisoFolioPlaca.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        etiquetaAvisoFolioPlaca.setForeground(java.awt.Color.red);
        etiquetaAvisoFolioPlaca.setText("*Ingrese sólo en caso de ser diferente al número de contrato");

        campoFolioContrato.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoFolioContrato.setToolTipText("Capture Folio del Nuevo Contrato");

        etiquetaColorPlaca.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaColorPlaca.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaColorPlaca.setText("Color Placa:");

        campoColorPlaca.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoColorPlaca.setToolTipText("Capture el color de la placa");

        etiquetaOnu.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaOnu.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaOnu.setText("Onu:");

        campoOnu.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoOnu.setToolTipText("Capture Datos de la Onu");

        etiquetaNap.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaNap.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaNap.setText("Nap:");

        campoNap.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoNap.setToolTipText("Capture el color de la placa");

        etiquetaEstatus.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaEstatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaEstatus.setText("Estatus:");

        campoEstatus.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoEstatus.setToolTipText("Capture Folio del Nuevo Contrato");

        javax.swing.GroupLayout panelDatosContrato1Layout = new javax.swing.GroupLayout(panelDatosContrato1);
        panelDatosContrato1.setLayout(panelDatosContrato1Layout);
        panelDatosContrato1Layout.setHorizontalGroup(
            panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(etiquetaFolioContrato)
                            .addComponent(etiquetaServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboServicios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                                .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(etiquetaTvs, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoTvs, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                        .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(etiquetaFolioPlaca)
                            .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(etiquetaAnio, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                .addComponent(etiquetaAnio1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(etiquetaColorPlaca)
                            .addComponent(etiquetaOnu))
                        .addGap(18, 18, 18)
                        .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboTiposInternet, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(etiquetaAvisoFolioPlaca)
                                    .addComponent(comboAnios, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoFolioPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(campoOnu, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                    .addComponent(campoColorPlaca, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(38, 38, 38)
                                .addComponent(etiquetaNap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoNap))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosContrato1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(etiquetaEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(etiquetaMes, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboMeses, 0, 322, Short.MAX_VALUE)
                            .addComponent(campoEstatus))))
                .addContainerGap())
        );
        panelDatosContrato1Layout.setVerticalGroup(
            panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosContrato1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaFolioContrato)
                    .addComponent(etiquetaTvs)
                    .addComponent(campoTvs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaServicio)
                    .addComponent(comboServicios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaEstatus)
                    .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaMes)
                    .addComponent(comboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboAnios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaAnio))
                .addGap(9, 9, 9)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(etiquetaAnio1)
                    .addComponent(comboTiposInternet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(etiquetaFolioPlaca)
                    .addComponent(campoFolioPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiquetaAvisoFolioPlaca)
                .addGap(4, 4, 4)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoNap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaNap)
                    .addComponent(campoColorPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaColorPlaca))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosContrato1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoOnu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaOnu))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDatosContrato2.setBackground(new java.awt.Color(255, 255, 255));
        panelDatosContrato2.setMaximumSize(new java.awt.Dimension(600, 390));
        panelDatosContrato2.setMinimumSize(new java.awt.Dimension(600, 390));
        panelDatosContrato2.setPreferredSize(new java.awt.Dimension(600, 390));

        etiquetaCalle.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaCalle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaCalle.setText("Calle:");

        campoCalle.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoCalle.setToolTipText("Capture Calle del Domicilio");

        etiquetaCalle1.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaCalle1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaCalle1.setText("Número Ext:");

        campoNumeroExt.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoNumeroExt.setToolTipText("Capture Número Exterior del Domicilio");

        etiquetaCalle2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaCalle2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaCalle2.setText("Colonia:");

        campoColonia.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoColonia.setToolTipText("Capture colonia del Domicilio");

        etiquetaCalle3.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaCalle3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaCalle3.setText("Ciudad:");

        campoCiudad.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoCiudad.setToolTipText("Capture Ciudad del Domicilio");

        etiquetaCalle4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaCalle4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaCalle4.setText("Entre Calle:");

        campoEntreCalle1.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoEntreCalle1.setToolTipText("Capture entre que calle se encuentra el domicilio");

        etiquetaCalle5.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaCalle5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaCalle5.setText("y Calle:");

        campoEntreCalle2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        campoEntreCalle2.setToolTipText("Capture entre que calle se encuentra el domicilio");

        etiquetaCalle6.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        etiquetaCalle6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaCalle6.setText("Referencia:");

        jScrollPane1.setVerifyInputWhenFocusTarget(false);

        areaReferencia.setColumns(20);
        areaReferencia.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        areaReferencia.setRows(5);
        areaReferencia.setToolTipText("Capture referencia del domicilio");
        jScrollPane1.setViewportView(areaReferencia);

        javax.swing.GroupLayout panelDatosContrato2Layout = new javax.swing.GroupLayout(panelDatosContrato2);
        panelDatosContrato2.setLayout(panelDatosContrato2Layout);
        panelDatosContrato2Layout.setHorizontalGroup(
            panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosContrato2Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(etiquetaCalle6, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaCalle1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaCalle)
                    .addComponent(etiquetaCalle2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaCalle3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaCalle4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaCalle5, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoEntreCalle1)
                    .addComponent(campoEntreCalle2)
                    .addComponent(jScrollPane1)
                    .addComponent(campoColonia)
                    .addComponent(campoCiudad)
                    .addGroup(panelDatosContrato2Layout.createSequentialGroup()
                        .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(campoCalle, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoNumeroExt, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(27, 27, 27))
        );
        panelDatosContrato2Layout.setVerticalGroup(
            panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosContrato2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaCalle)
                    .addComponent(campoCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaCalle1)
                    .addComponent(campoNumeroExt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaCalle2)
                    .addComponent(campoColonia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaCalle3)
                    .addComponent(campoCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaCalle4)
                    .addComponent(campoEntreCalle1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaCalle5)
                    .addComponent(campoEntreCalle2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosContrato2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(etiquetaCalle6)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelContratoLayout = new javax.swing.GroupLayout(panelContrato);
        panelContrato.setLayout(panelContratoLayout);
        panelContratoLayout.setHorizontalGroup(
            panelContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContratoLayout.createSequentialGroup()
                .addGroup(panelContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelContratoLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(panelDatosContrato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelDatosContrato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelContratoLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(etiquetaNuevoSuscriptor1)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        panelContratoLayout.setVerticalGroup(
            panelContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiquetaNuevoSuscriptor1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosContrato1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelDatosContrato2, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        botonActualizar.setBackground(new java.awt.Color(163, 73, 164));
        botonActualizar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonActualizar.setForeground(new java.awt.Color(255, 255, 255));
        botonActualizar.setText("Actualizar Contrato");
        botonActualizar.setActionCommand("");
        botonActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonActualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(panelSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(botonRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void botonRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRegistrarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonRegistrarActionPerformed

    private void botonActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonActualizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonActualizarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaReferencia;
    private javax.swing.JButton botonActualizar;
    private javax.swing.JButton botonRegistrar;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoApellidoMaterno;
    private javax.swing.JTextField campoApellidoPaterno;
    private javax.swing.JTextField campoCalle;
    private javax.swing.JTextField campoCiudad;
    private javax.swing.JTextField campoColonia;
    private javax.swing.JTextField campoColorPlaca;
    private javax.swing.JTextField campoEntreCalle1;
    private javax.swing.JTextField campoEntreCalle2;
    private javax.swing.JTextField campoEstatus;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoFolioPlaca;
    private javax.swing.JTextField campoNap;
    private javax.swing.JTextField campoNombre;
    private javax.swing.JTextField campoNumeroExt;
    private javax.swing.JTextField campoOnu;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JTextField campoTvs;
    private javax.swing.JComboBox<Integer> comboAnios;
    private javax.swing.JComboBox<Mes> comboMeses;
    private javax.swing.JComboBox<ServicioEntity> comboServicios;
    private javax.swing.JComboBox<TipoServicioInternet> comboTiposInternet;
    private javax.swing.JLabel etiquetaAnio;
    private javax.swing.JLabel etiquetaAnio1;
    private javax.swing.JLabel etiquetaApellidoMaterno;
    private javax.swing.JLabel etiquetaApellidoPeterno;
    private javax.swing.JLabel etiquetaAvisoFolioPlaca;
    private javax.swing.JLabel etiquetaCalle;
    private javax.swing.JLabel etiquetaCalle1;
    private javax.swing.JLabel etiquetaCalle2;
    private javax.swing.JLabel etiquetaCalle3;
    private javax.swing.JLabel etiquetaCalle4;
    private javax.swing.JLabel etiquetaCalle5;
    private javax.swing.JLabel etiquetaCalle6;
    private javax.swing.JLabel etiquetaColorPlaca;
    private javax.swing.JLabel etiquetaDescCaja;
    private javax.swing.JLabel etiquetaDescPanel;
    private javax.swing.JLabel etiquetaDescSucursal;
    private javax.swing.JLabel etiquetaDescUsuario;
    private javax.swing.JLabel etiquetaEstatus;
    private javax.swing.JLabel etiquetaFolioContrato;
    private javax.swing.JLabel etiquetaFolioPlaca;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaMes;
    private javax.swing.JLabel etiquetaNap;
    private javax.swing.JLabel etiquetaNombreSuscriptor;
    private javax.swing.JLabel etiquetaNuevoSuscriptor;
    private javax.swing.JLabel etiquetaNuevoSuscriptor1;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaOnu;
    private javax.swing.JLabel etiquetaServicio;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaTelefono;
    private javax.swing.JLabel etiquetaTvs;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelContrato;
    private javax.swing.JPanel panelDatosContrato1;
    private javax.swing.JPanel panelDatosContrato2;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelIzquierdo;
    private javax.swing.JPanel panelSuscriptor;
    // End of variables declaration//GEN-END:variables
}
