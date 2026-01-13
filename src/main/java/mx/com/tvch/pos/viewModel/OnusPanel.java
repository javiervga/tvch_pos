/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.JComponent.TOOL_TIP_TEXT_KEY;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.OnuController;
import mx.com.tvch.pos.entity.ContratoEntity;
import mx.com.tvch.pos.entity.ContratoJoinOnuEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.model.EstatusOnu;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.TvchException;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class OnusPanel extends javax.swing.JPanel {

    private static OnusPanel onusPanel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final OnuController controller;
    private final Utilerias util;

    private List<EstatusOnu> listaEstatus;
    private List<ContratoJoinOnuEntity> onusConsultadas;
    private ContratoJoinOnuEntity onuSeleccionada;
    private ContratoxSuscriptorDetalleEntity contratoAsignadoSeleccionado;
    private ContratoxSuscriptorDetalleEntity contratoPorAsociarSeleccionado;

    org.slf4j.Logger logger = LoggerFactory.getLogger(OnusPanel.class);

    public static OnusPanel getOnusPanel(PosFrame frame) {
        if (onusPanel == null) {
            onusPanel = new OnusPanel();
        }
        posFrame = frame;
        return onusPanel;
    }

    /**
     * Creates new form CobroPanel
     */
    public OnusPanel() {
        initComponents();

        listaEstatus = new ArrayList<>();
        listaEstatus.add(new EstatusOnu(Constantes.ESTATUS_ONU_TODOS, "CONSULTAR TODO"));
        listaEstatus.add(new EstatusOnu(Constantes.ESTATUS_ONU_ASIGNADA, "ASIGNADA"));
        listaEstatus.add(new EstatusOnu(Constantes.ESTATUS_ONU_DISPONIBLE, "DISPONIBLE"));
        listaEstatus.add(new EstatusOnu(Constantes.ESTATUS_ONU_INSERVIBLE, "INSERVIBLE"));
        sesion = Sesion.getSesion();
        controller = OnuController.getOnuController();
        util = Utilerias.getUtilerias();
        onusConsultadas = new ArrayList<>();
        onuSeleccionada = null;
        contratoAsignadoSeleccionado = null;
        contratoPorAsociarSeleccionado = null;
        crearEventos();
    }

    private void crearEventos() {

        KeyListener enterTablaSuscriptoresListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int selectedRow = tablaOnus.getSelectedRow();
                    if (selectedRow >= 0) {
                        Long onuId = (Long) tablaOnus.getModel().getValueAt(selectedRow, 0);
                        System.out.println("onu seleccionada: " + onuId);
                        
                        if (!onusConsultadas.isEmpty()) {
                            if (onusConsultadas.stream()
                                    .filter(cs -> cs.getOnuId() == onuId.longValue()).findAny().isPresent()) {
                                ContratoJoinOnuEntity entity = onusConsultadas
                                        .stream().filter(cs -> cs.getOnuId() == onuId.longValue()).findFirst().get();
                                //onuSeleccionada = entity;

                                try {
            
                                    cargarDatosOnu(entity);
            
                                } catch (TvchException ex) {
                                    contratoAsignadoSeleccionado = null;
                                    JOptionPane.showMessageDialog(onusPanel, ex.getMessage(),"", JOptionPane.WARNING_MESSAGE);
                                } catch (Exception ex) {
                                    contratoAsignadoSeleccionado = null;
                                    JOptionPane.showMessageDialog(onusPanel, "Ocurrió un error al consultar información del contrato. "
                                                    + "\nPor favor reintente, si el problema persiste contacte a Soporte Técnico.","", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                }
            }
        };
        tablaOnus.addKeyListener(enterTablaSuscriptoresListener);

        MouseListener dobleClickTablaSuscriptoresListener = new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    // your valueChanged overridden method
                    
                    try{ 
                    
                        Long onuId = (Long) tablaOnus.getModel().getValueAt(row, 0);
                        System.out.println("onu seleccionada: " + onuId);
                        
                        if (!onusConsultadas.isEmpty()) {
                            if (onusConsultadas.stream()
                                    .filter(cs -> cs.getOnuId() == onuId.longValue()).findAny().isPresent()) {
                                ContratoJoinOnuEntity entity = onusConsultadas
                                        .stream().filter(cs -> cs.getOnuId() == onuId.longValue()).findFirst().get();
                                //onuSeleccionada = entity;

                                cargarDatosOnu(entity);
                            }
                        }
                    
                    }catch (TvchException ex) {
                        contratoAsignadoSeleccionado = null;
                        JOptionPane.showMessageDialog(onusPanel, ex.getMessage(),"", JOptionPane.WARNING_MESSAGE);
                    }catch(Exception ex){
                        contratoAsignadoSeleccionado = null;
                        logger.error("No se encontro onu en la posicion del mouse, excepcion controlada");
                    }
                    mouseEvent.consume();
                }
            }
        };
        tablaOnus.addMouseListener(dobleClickTablaSuscriptoresListener);

        ActionListener botonRegresarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPantalla();
                posFrame.cambiarPantalla(onusPanel, VentanaEnum.MENU);
            }
        };
        botonRegresar.addActionListener(botonRegresarActionListener);

        KeyListener keyListenerBuscarSuscriptor = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarOnus();
                    e.consume();
                }
            }
        };
        campoSerieOnuBusqueda.addKeyListener(keyListenerBuscarSuscriptor);
        
        ActionListener botonBusquedaActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(!campoSerieOnuBusqueda.getText().trim().isEmpty()){
                    buscarOnus();
                }else{
                    JOptionPane.showMessageDialog(onusPanel, "Por favor capture un número de serie.","", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonBuscarOnu.addActionListener(botonBusquedaActionListener);
        
        ActionListener botonActualizarSerieActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(!campoSerieOnuBusqueda.getText().trim().isEmpty()){
                    if(campoSerieOnuBusqueda.getText().trim().length() <= 50){
                        
                        
                        StringBuilder sbReconexion = new StringBuilder();
                        sbReconexion.append("El número de Serie de la Onu se actualizará \n");
                        sbReconexion.append("\nConfirme actualización por favor. ");

                        boolean seConfirmaActualizacion = false;
                        Object[] options = {"SI", "NO"};
                        int result = JOptionPane.showOptionDialog(
                            onusPanel,
                            sbReconexion.toString(),
                            "CONFIRMACION DE ACTUALIZACION DE SERIE",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1] 
                        );

                        if (result == 0) {
                            seConfirmaActualizacion = true;
                        }
                        
                        if(seConfirmaActualizacion){
                            controller.actualizarSerieOnu(onuSeleccionada, campoNumeroSerieOnu.getText().trim().toUpperCase());
                            campoSerieOnuBusqueda.setText(campoNumeroSerieOnu.getText().trim().toUpperCase());
                            limpiarCamposInfoOnu();
                            limpiarCamposAsociacion();
                            habilitarCamposAsociacion(false);
                            comboEstatusOnu.setSelectedIndex(0); 
                            onuSeleccionada = null;
                            contratoPorAsociarSeleccionado = null;
                            contratoAsignadoSeleccionado = null;
                            buscarOnus();
                        }
                    
                    }else{
                        JOptionPane.showMessageDialog(onusPanel, "El número de Serie de su Onu no puede exceder de 50 caracteres. "
                            ,"", JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(onusPanel, "El número de Serie de su Onu no puede ir vacío. "
                            + "\n Por favor capture un número de serie.","", JOptionPane.WARNING_MESSAGE);
                }
                
            }
        };
        botonActualizarSerie.addActionListener(botonActualizarSerieActionListener);
        
        /**
         * 
         */
        ActionListener botonNuevaOnuActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //posFrame.cambiarPantalla(onusPanel, VentanaEnum.REGISTRO_SUSCRIPTOR);
                
                //enviar a pagina de registro de nueva Onu
                
                
            }
        };
        botonNuevaOnu.addActionListener(botonNuevaOnuActionListener);
        
        /**
         * 
         */
        ActionListener botonRetirarOnuActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(onuSeleccionada != null){
                    
                    StringBuilder sbRetiro = new StringBuilder();
                        sbRetiro.append("La Onu se actualizará a estatus DISPONIBLE\n");
                        sbRetiro.append("y dejará de aparecer en la información de su contrato");
                        sbRetiro.append("\nConfirme actualización por favor. ");

                        boolean seConfirmaRetiro = false;
                        Object[] options = {"SI", "NO"};
                        int result = JOptionPane.showOptionDialog(onusPanel,
                            sbRetiro.toString(),
                            "CONFIRMACION DE RETIRO DE ONU DE CONTRATO",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1] 
                        );

                        if (result == 0) {
                            seConfirmaRetiro = true;
                        }
                        
                        if(seConfirmaRetiro){
                            try {
                                controller.retirarOnuContrato(onuSeleccionada.getOnuId());
                                campoSerieOnuBusqueda.setText(onuSeleccionada.getSerie());
                                onuSeleccionada = null;
                                buscarOnus();
                            } catch (TvchException ex) {
                                JOptionPane.showMessageDialog(onusPanel, ex.getMessage(),"", JOptionPane.WARNING_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(onusPanel, "Ocurrió un error al realizar el retiro de la Onu del Contrato. "
                                + "\nPor favor intente de nuevo, si el error persisre contacte a Soporte Técnico.","", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    
                }else{
                    JOptionPane.showMessageDialog(onusPanel, "Para retirar una Onu de un contrato existente, "
                            + "por favor seleccionelo de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonRetirarContrato.addActionListener(botonRetirarOnuActionListener);
        
        /**
         * 
         */
        ActionListener botonCambioEstatusActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(onuSeleccionada != null){
                    
                    try {
                        
                        StringBuilder sbReconexion = new StringBuilder();
                        if(onuSeleccionada.getEstatusOnuId() == Constantes.ESTATUS_ONU_DISPONIBLE)
                            sbReconexion.append("La Onu se actualizará a estatus INSERVIBLE\n");
                        else
                            sbReconexion.append("La Onu se actualizará a estatus DISPONIBLE\n");
                        sbReconexion.append("\nConfirme actualización por favor. ");

                        boolean seConfirmaActualizacion = false;
                        Object[] options = {"SI", "NO"};
                        int result = JOptionPane.showOptionDialog(
                            onusPanel,
                            sbReconexion.toString(),
                            "CONFIRMACION DE ACTUALIZACION DE ESTATUS",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1] 
                        );

                        if (result == 0) {
                            seConfirmaActualizacion = true;
                        }
                        
                        if(seConfirmaActualizacion){
                            if(onuSeleccionada.getEstatusOnuId() == Constantes.ESTATUS_ONU_DISPONIBLE){
                                //cambiar de disponible a inservible
                                controller.cambiarEstatusOnu(onuSeleccionada.getOnuId(), Constantes.ESTATUS_ONU_INSERVIBLE);

                            }else if(onuSeleccionada.getEstatusOnuId() == Constantes.ESTATUS_ONU_INSERVIBLE){
                                //cambiar de inservible a disponible
                                controller.cambiarEstatusOnu(onuSeleccionada.getOnuId(), Constantes.ESTATUS_ONU_DISPONIBLE);

                            }
                            campoSerieOnuBusqueda.setText(onuSeleccionada.getSerie());
                            onuSeleccionada = null;
                            buscarOnus();
                        }
 
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(onusPanel, "Ocurrió un error al actualizar Onu. \nPor favor reintente,  "
                            + "si el error persiste contacte a Soporte Técnico.","", JOptionPane.WARNING_MESSAGE);
                    }
                    
                }else{
                    JOptionPane.showMessageDialog(onusPanel, "Para retirar una Onu de un contrato existente, "
                            + "por favor seleccionelo de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonCambioEstatusOnu.addActionListener(botonCambioEstatusActionListener);
        
        /**
         * 
         */
        ActionListener botonAsociarContratoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(onuSeleccionada != null){
                    
                    if(onuSeleccionada.getEstatusOnuId() == Constantes.ESTATUS_ONU_ASIGNADA){
                        JOptionPane.showMessageDialog(onusPanel, "La Onu seleccionada ya ha sido asignado a un contrato, "
                            + "por favor seleccione una Onu disponible de la lista.","", JOptionPane.WARNING_MESSAGE);
                    }else if(onuSeleccionada.getEstatusOnuId() == Constantes.ESTATUS_ONU_DISPONIBLE){
                        
                        habilitarCamposAsociacion(true);
                        campoOnuSeleccionada.setText(onuSeleccionada.getSerie());
                        
                    }if(onuSeleccionada.getEstatusOnuId() == Constantes.ESTATUS_ONU_INSERVIBLE){
                        JOptionPane.showMessageDialog(onusPanel, "La Onu seleccionada no puede ser asociada a un contrato, "
                            + "por favor seleccione una Onu disponible de la lista.","", JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(onusPanel, "Para asociar una Onu a un contrato existente, "
                            + "por favor seleccione una Onu disponible de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonAsociarOnu.addActionListener(botonAsociarContratoActionListener);
        
        /**
         * 
         */
        ActionListener botonBuscarContratoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(onuSeleccionada != null){
                    
                    Long folioContrato = null;
                    boolean esFolioValido = false;
                    try{
                        folioContrato = Long.parseLong(campoContratoBuscar.getText());
                        if(folioContrato > 0)
                            esFolioValido = true;
                    }catch(NumberFormatException nfe){
                        
                    }
                    
                    if(esFolioValido){
                        
                        contratoPorAsociarSeleccionado = null;
                        try {
                            ContratoxSuscriptorDetalleEntity contrato = controller
                                    .consultarInformacionContratoSuscriptor(folioContrato, true);
                            
                            if(contrato.getOnuId() != null && contrato.getOnuId() > 0)
                                throw new TvchException("El contrato solicitado ya cuenta con una Onu asignada,"
                                        + "\nPara asignarle una nueva Onu es necesario retirar la onu actual.");
                            
                            contratoPorAsociarSeleccionado = contrato;
                            cargarDatosContrato();
                            
                        } catch (TvchException ex) {
                            JOptionPane.showMessageDialog(onusPanel, ex.getMessage(),"", JOptionPane.WARNING_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(onusPanel, "Ocurrió un error al consultar contrato."
                                    + "\n Por favor reintente, en caso de que persista el error llame a Soporte Técnico","", JOptionPane.ERROR_MESSAGE);
                        }
                    
                    }else{
                        limpiarCamposAsociacion();
                        JOptionPane.showMessageDialog(onusPanel, "Ingrese un folio válido ","", JOptionPane.WARNING_MESSAGE);
                    }
                    
                    
                    
                }else{
                    JOptionPane.showMessageDialog(onusPanel, "No es posible buscar el contrato, "
                            + "por favor seleccione una Onu disponible de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonBuscarContrato.addActionListener(botonBuscarContratoActionListener);
        
        /**
         * 
         */
        ActionListener botonGuardarAsociacionActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(contratoPorAsociarSeleccionado != null){
                    
                    try {
                        
                        StringBuilder sbReconexion = new StringBuilder();
                        sbReconexion.append("La Onu se actualizará a estatus ASIGNADA "
                                + "\n y se asociará al contrato ").append(contratoPorAsociarSeleccionado.getFolioContrato());
                        sbReconexion.append("\nConfirme actualización por favor. ");

                        boolean seConfirmaAsociacion = false;
                        Object[] options = {"SI", "NO"};
                        int result = JOptionPane.showOptionDialog(
                            onusPanel,
                            sbReconexion.toString(),
                            "CONFIRMACION DE ASOCIACION DE ONU",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1] 
                        );

                        if (result == 0) {
                            seConfirmaAsociacion = true;
                        }
                        
                        if(seConfirmaAsociacion){
                            
                            controller.asignarOnuContrato(onuSeleccionada.getOnuId(), contratoPorAsociarSeleccionado.getContratoId());
                            campoSerieOnuBusqueda.setText(onuSeleccionada.getSerie());
                            onuSeleccionada = null;
                            contratoPorAsociarSeleccionado = null;
                            buscarOnus();
                        }
 
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(onusPanel, "Ocurrió un error al actualizar Onu. \nPor favor reintente,  "
                            + "si el error persiste contacte a Soporte Técnico.","", JOptionPane.WARNING_MESSAGE);
                    }
                    
                }else{
                    JOptionPane.showMessageDialog(onusPanel, "Para retirar una Onu de un contrato existente, "
                            + "por favor seleccionelo de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonGuardarAsociacion.addActionListener(botonGuardarAsociacionActionListener);

    }

    /**
     * 
     */
    private void buscarOnus() {
        
        //primero limpiar
        limpiarCamposInfoOnu();
        limpiarCamposAsociacion();
        habilitarCamposAsociacion(false);
        botonCambioEstatusOnu.setEnabled(false);
        botonAsociarOnu.setEnabled(false);
        botonActualizarSerie.setEnabled(false);
        botonRetirarContrato.setEnabled(false);

        EstatusOnu estatusOnu = (EstatusOnu) comboEstatusOnu.getModel().getSelectedItem();

        try {
            //primero limpiar la tabla
            DefaultTableModel model = (DefaultTableModel) tablaOnus.getModel();
            model.getDataVector().clear();
            //model.fireTableStructureChanged();

            cargarTablaOnus(model, estatusOnu, campoSerieOnuBusqueda.getText().trim());
            

        } catch (NoSuchElementException ex) {
            JOptionPane.showMessageDialog(onusPanel, "Sin resultados", "", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            logger.error("Error al consultar onus");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(onusPanel, "Ocurrió un error al consultar Onus. Por favor contacte a Soporte", "", JOptionPane.ERROR_MESSAGE);
        }

    }
    
    /**
     *
     * @param model
     * @param contrato
     * @param tipoBusquedaCobro
     * @param cadenaBusqueda
     * @throws Exception
     */
    private void cargarTablaOnus(DefaultTableModel model, EstatusOnu estatusOnu, String cadenaBusqueda) throws Exception {

        onusConsultadas = controller.consultarOnus(cadenaBusqueda, estatusOnu);

        if (!onusConsultadas.isEmpty()) {

            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (ContratoJoinOnuEntity c : onusConsultadas) {
                
                String estatus = "";
                if(c.getEstatusOnuId() == Constantes.ESTATUS_ONU_ASIGNADA)
                    estatus = "ASIGNADA";
                else if(c.getEstatusOnuId() == Constantes.ESTATUS_ONU_DISPONIBLE)        
                    estatus = "DISPONIBLE";
                else
                    estatus = "INSERVIBLE";
                
                model.addRow(new Object[]{
                    c.getOnuId(),
                    c.getSerie(),
                    estatus,
                    c.getId()==null || c.getId() == 0?"SIN ASIGNAR A CONTRATO":c.getFolioContrato(),
                    c.getFechaRegistroOnu()});
            }
            tablaOnus.setRowSelectionInterval(0, 0);
        } else {
            
            model.getDataVector().clear();
            model.fireTableDataChanged();
            
            JOptionPane.showMessageDialog(onusPanel, "No se encontraron onus con el número de serie solicitado", "", JOptionPane.WARNING_MESSAGE);
        }
        
    }

    /**
     * 
     * @param onu 
     */
    private void cargarDatosOnu( 
            ContratoJoinOnuEntity onu) throws TvchException, Exception{

        System.out.println("Seleccionado: " + onu.getOnuId());

        // primero borrar los datos de suscriptores que se hayan seleccionado antes
        limpiarCamposInfoOnu();
        limpiarCamposAsociacion();
        onuSeleccionada = onu;
        contratoAsignadoSeleccionado = null;
        
        if(onu.getEstatusOnuId() == Constantes.ESTATUS_ONU_ASIGNADA){
            
            //se consulta el contrato asociado
            //obtener el detalle del contrato
            ContratoxSuscriptorDetalleEntity contrato = controller
                    .consultarInformacionContratoSuscriptor(onuSeleccionada.getId(), false);
            contratoAsignadoSeleccionado = contrato;
            campoContratoOnu.setText(String.valueOf(contrato.getFolioContrato()));
            
        }else{
            
            campoContratoOnu.setText("SIN CONTRATO");
            
        }
        
        campoNumeroSerieOnu.setText(onu.getSerie());
        campoIdOnu.setText(String.valueOf(onu.getOnuId()));
        campoEstatusOnu.setText(obtenerCadenaEstatusOnu(onu.getEstatusOnuId()));
        campoFechaRegistroOnu.setText(util.convertirDateTime2String(onu.getFechaRegistroOnu(), Constantes.FORMATO_FECHA_HORA_WEB_SERVICE));

        if(onu.getEstatusOnuId() == Constantes.ESTATUS_ONU_ASIGNADA){
            botonCambioEstatusOnu.setText("Habilitar Onu");
            botonCambioEstatusOnu.setEnabled(false);
            botonAsociarOnu.setEnabled(false);
            botonActualizarSerie.setEnabled(true);
            botonRetirarContrato.setEnabled(true);
        } else if(onu.getEstatusOnuId() == Constantes.ESTATUS_ONU_DISPONIBLE){
            botonCambioEstatusOnu.setText("Inhabilitar Onu");
            botonCambioEstatusOnu.setEnabled(true);
            botonAsociarOnu.setEnabled(true);
            botonActualizarSerie.setEnabled(true);
            botonRetirarContrato.setEnabled(false);
        }else { //estatus inservible
            botonCambioEstatusOnu.setText("Habilitar Onu");
            botonCambioEstatusOnu.setEnabled(true);
            botonAsociarOnu.setEnabled(false);
            botonActualizarSerie.setEnabled(false);
            botonRetirarContrato.setEnabled(false);
        }

    }
    
    /**
     * 
     */
    private void cargarDatosContrato(){
        
        if(contratoPorAsociarSeleccionado != null){
   
            //armar el nombre del suscriptor
            StringBuilder nombre = new StringBuilder();
            nombre.append(contratoPorAsociarSeleccionado.getNombre());
            if (contratoPorAsociarSeleccionado.getApellidoPaterno() != null) {
                nombre.append(" ").append(contratoPorAsociarSeleccionado.getApellidoPaterno());
            }
            if (contratoPorAsociarSeleccionado.getApellidoMaterno() != null) {
                nombre.append(" ").append(contratoPorAsociarSeleccionado.getApellidoMaterno());
            }

            //armar el domicilio
            StringBuilder domicilio = new StringBuilder();
            domicilio.append(contratoPorAsociarSeleccionado.getCalle()).append(" ").append(contratoPorAsociarSeleccionado.getNumeroCalle());
            domicilio.append(" ").append(contratoPorAsociarSeleccionado.getColonia());
            
            campoFolioContrato.setText(String.valueOf(contratoPorAsociarSeleccionado.getFolioContrato()));
            campoSuscriptor.setText(nombre.toString());
            campoDireccion.setText(domicilio.toString());
            campoEstatusContrato.setText(contratoPorAsociarSeleccionado.getEstatusContrato());
            
        }
        
    }
    
    /**
     * 
     */
    public void cargarDatosSesion() {

        //setear los datos del cabcero
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());

        //ajustar el tamaño de la tabla
        tablaOnus.getColumnModel().getColumn(0).setPreferredWidth(180);
        tablaOnus.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaOnus.getColumnModel().getColumn(2).setPreferredWidth(340);
        tablaOnus.getColumnModel().getColumn(3).setPreferredWidth(230);
        tablaOnus.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        botonCambioEstatusOnu.setEnabled(false);
        botonAsociarOnu.setEnabled(false);
        botonActualizarSerie.setEnabled(false);
        botonRetirarContrato.setEnabled(false);
        
        //inicializar todos los campos
        cargarComboEstatusOnu();
        limpiarPantalla();

    }
    
    /**
     * 
     */
    private void cargarComboEstatusOnu(){
        
        if(comboEstatusOnu.getModel().getSize() == 0){
            listaEstatus.forEach(e -> comboEstatusOnu.addItem(e));
        }
        
    }

    
    /**
     * 
     */
    private void limpiarPantalla() {
        
        campoSerieOnuBusqueda.setText("");
        comboEstatusOnu.setSelectedIndex(0);

        limpiarTablaOnus();
        limpiarCamposInfoOnu();
        limpiarCamposAsociacion();
        
        //deshabilitar los campos para asociar contrato
        habilitarCamposAsociacion(false);
        
        //al final setear a leyenda de habilitar para que este por default
        botonCambioEstatusOnu.setText("Habilitar Onu");
        
        contratoAsignadoSeleccionado = null;
        onuSeleccionada = null;
        contratoAsignadoSeleccionado = null;
        contratoPorAsociarSeleccionado = null;
        onusConsultadas.clear();
        
    }
    
    /**
     * 
     */
    private void limpiarTablaOnus(){
        DefaultTableModel model = (DefaultTableModel) tablaOnus.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
    }
    
    /**
     * 
     */
    private void limpiarCamposInfoOnu(){
        campoNumeroSerieOnu.setText("");
        campoIdOnu.setText("");
        campoEstatusOnu.setText("");
        campoContratoOnu.setText("");
        campoFechaRegistroOnu.setText("");
    }
    
    /**
     * 
     */
    private void limpiarCamposAsociacion() {
        campoOnuSeleccionada.setText("");
        campoContratoBuscar.setText("");
        campoFolioContrato.setText("");
        campoSuscriptor.setText("");
        campoDireccion.setText("");
        campoEstatusContrato.setText("");
        contratoPorAsociarSeleccionado = null;
    }
    
    /**
     * 
     * @param seHabilita 
     */
    private void habilitarCamposAsociacion(boolean seHabilita) {
        campoOnuSeleccionada.setEnabled(seHabilita);
        campoContratoBuscar.setEnabled(seHabilita);
        campoFolioContrato.setEnabled(seHabilita);
        campoSuscriptor.setEnabled(seHabilita);
        campoDireccion.setEnabled(seHabilita);
        campoEstatusContrato.setEnabled(seHabilita);
        botonBuscarContrato.setEnabled(seHabilita);
        botonGuardarAsociacion.setEnabled(seHabilita);
    }
    
    /**
     * 
     * @param estatusId
     * @return 
     */
    private String obtenerCadenaEstatusOnu(Long estatusId){
        
        String estatus = "";
        if(estatusId == Constantes.ESTATUS_ONU_ASIGNADA)
            estatus = "ASIGNADA";
        else if(estatusId == Constantes.ESTATUS_ONU_DISPONIBLE)        
            estatus = "DISPONIBLE";
        else
            estatus = "INSERVIBLE";

        return estatus;
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCabecero = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        etiquetaSucursal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        etiquetaUsuario = new javax.swing.JLabel();
        etiquetaNumeroCaja = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        panelBusqueda = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaOnus = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        etiquetaTextoBuscar = new javax.swing.JLabel();
        campoSerieOnuBusqueda = new javax.swing.JTextField();
        botonBuscarOnu = new javax.swing.JButton();
        etiquetaLogo = new javax.swing.JLabel();
        botonNuevaOnu = new javax.swing.JButton();
        etiquetaTextoBuscar1 = new javax.swing.JLabel();
        comboEstatusOnu = new javax.swing.JComboBox<>();
        panelInferior = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        campoContratoBuscar = new javax.swing.JTextField();
        botonBuscarContrato = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        campoFolioContrato = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        campoSuscriptor = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        campoEstatusContrato = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        campoDireccion = new javax.swing.JTextField();
        botonGuardarAsociacion = new javax.swing.JButton();
        campoOnuSeleccionada = new javax.swing.JTextField();
        panelInfoContrato = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        campoNumeroSerieOnu = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        campoIdOnu = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        campoEstatusOnu = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        campoFechaRegistroOnu = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        campoContratoOnu = new javax.swing.JTextField();
        botonRetirarContrato = new javax.swing.JButton();
        botonActualizarSerie = new javax.swing.JButton();
        botonCambioEstatusOnu = new javax.swing.JButton();
        botonAsociarOnu = new javax.swing.JButton();
        botonRegresar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1500, 800));
        setMinimumSize(new java.awt.Dimension(1500, 800));
        setPreferredSize(new java.awt.Dimension(1500, 800));

        panelCabecero.setBackground(new java.awt.Color(255, 255, 255));
        panelCabecero.setMaximumSize(new java.awt.Dimension(1500, 30));
        panelCabecero.setMinimumSize(new java.awt.Dimension(1500, 30));
        panelCabecero.setPreferredSize(new java.awt.Dimension(1500, 30));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setText("Sucursal:");

        etiquetaSucursal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaSucursal.setText("TV");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("Usuario:");

        etiquetaUsuario.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaUsuario.setText("User");

        etiquetaNumeroCaja.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaNumeroCaja.setText("0");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("Caja Número:");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel24.setText("Consulta y actualización de Onus");

        javax.swing.GroupLayout panelCabeceroLayout = new javax.swing.GroupLayout(panelCabecero);
        panelCabecero.setLayout(panelCabeceroLayout);
        panelCabeceroLayout.setHorizontalGroup(
            panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceroLayout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(220, 220, 220)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(etiquetaSucursal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(135, 135, 135)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84))
        );
        panelCabeceroLayout.setVerticalGroup(
            panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceroLayout.createSequentialGroup()
                .addGroup(panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(etiquetaNumeroCaja)
                    .addComponent(jLabel12)
                    .addComponent(etiquetaSucursal)
                    .addComponent(jLabel11)
                    .addComponent(etiquetaUsuario)
                    .addComponent(jLabel24))
                .addGap(0, 5, Short.MAX_VALUE))
        );

        panelBusqueda.setBackground(new java.awt.Color(255, 255, 255));
        panelBusqueda.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(163, 73, 164), null, null));
        panelBusqueda.setMaximumSize(new java.awt.Dimension(1500, 300));
        panelBusqueda.setMinimumSize(new java.awt.Dimension(1500, 300));
        panelBusqueda.setPreferredSize(new java.awt.Dimension(1500, 300));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setFocusTraversalPolicyProvider(true);
        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jScrollPane1.setMaximumSize(new java.awt.Dimension(16, 6));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(462, 195));

        tablaOnus.setBackground(new java.awt.Color(204, 204, 204));
        tablaOnus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaOnus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Sistema", "Número de  Serie", "Estatus", "Contrato asociado", "Fecha de Registro"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaOnus.setToolTipText("");
        tablaOnus.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaOnus.setFillsViewportHeight(true);
        tablaOnus.setMaximumSize(new java.awt.Dimension(1438, 1000));
        tablaOnus.setMinimumSize(new java.awt.Dimension(1438, 1000));
        tablaOnus.setPreferredSize(new java.awt.Dimension(1438, 1000));
        tablaOnus.setRowHeight(15);
        tablaOnus.setShowGrid(false);
        jScrollPane1.setViewportView(tablaOnus);
        if (tablaOnus.getColumnModel().getColumnCount() > 0) {
            tablaOnus.getColumnModel().getColumn(0).setResizable(false);
            tablaOnus.getColumnModel().getColumn(1).setResizable(false);
            tablaOnus.getColumnModel().getColumn(2).setResizable(false);
            tablaOnus.getColumnModel().getColumn(3).setResizable(false);
            tablaOnus.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Busqueda de Onu");

        etiquetaTextoBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaTextoBuscar.setText("Serie a buscar:");

        campoSerieOnuBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonBuscarOnu.setBackground(new java.awt.Color(227, 126, 75));
        botonBuscarOnu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonBuscarOnu.setForeground(new java.awt.Color(255, 255, 255));
        botonBuscarOnu.setText("Buscar Onu");

        etiquetaLogo.setBackground(new java.awt.Color(255, 255, 255));
        etiquetaLogo.setInheritsPopupMenu(false);
        etiquetaLogo.setMaximumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setMinimumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setPreferredSize(new java.awt.Dimension(410, 88));

        botonNuevaOnu.setBackground(new java.awt.Color(255, 102, 204));
        botonNuevaOnu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonNuevaOnu.setForeground(new java.awt.Color(255, 255, 255));
        botonNuevaOnu.setText("Registrar Nueva Onu");

        etiquetaTextoBuscar1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaTextoBuscar1.setText("Estatus de la Onu:");

        comboEstatusOnu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelBusquedaLayout = new javax.swing.GroupLayout(panelBusqueda);
        panelBusqueda.setLayout(panelBusquedaLayout);
        panelBusquedaLayout.setHorizontalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBusquedaLayout.createSequentialGroup()
                        .addComponent(etiquetaTextoBuscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboEstatusOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(etiquetaTextoBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(campoSerieOnuBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)
                        .addComponent(botonBuscarOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonNuevaOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1458, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiquetaLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );
        panelBusquedaLayout.setVerticalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaTextoBuscar1)
                    .addComponent(comboEstatusOnu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaTextoBuscar)
                    .addComponent(campoSerieOnuBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonBuscarOnu)
                    .addComponent(botonNuevaOnu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelInferior.setBackground(new java.awt.Color(255, 255, 255));
        panelInferior.setMaximumSize(new java.awt.Dimension(1500, 200));
        panelInferior.setMinimumSize(new java.awt.Dimension(1500, 200));
        panelInferior.setPreferredSize(new java.awt.Dimension(1500, 200));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 0, 0));
        jLabel23.setText("Asignación de Onu a Contrato");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Onu Seleccionada:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Capture el Folio de Contrato que se asociará:");

        campoContratoBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoContratoBuscar.setForeground(java.awt.Color.red);

        botonBuscarContrato.setBackground(java.awt.Color.orange);
        botonBuscarContrato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonBuscarContrato.setForeground(new java.awt.Color(255, 255, 255));
        botonBuscarContrato.setText("Buscar Contrato:");
        botonBuscarContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarContratoActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 0, 0));
        jLabel25.setText("Información del Contrato por asignar");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Folio Contrato:");

        campoFolioContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoFolioContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoFolioContratoActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Suscriptor:");

        campoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoSuscriptor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoSuscriptorActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("Estatus:");

        campoEstatusContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoEstatusContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoEstatusContratoActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Dirección:");

        campoDireccion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoDireccionActionPerformed(evt);
            }
        });

        botonGuardarAsociacion.setBackground(new java.awt.Color(0, 153, 153));
        botonGuardarAsociacion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonGuardarAsociacion.setForeground(new java.awt.Color(255, 255, 255));
        botonGuardarAsociacion.setText("Asociar Onu");
        botonGuardarAsociacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarAsociacionActionPerformed(evt);
            }
        });

        campoOnuSeleccionada.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoOnuSeleccionada.setForeground(java.awt.Color.red);

        javax.swing.GroupLayout panelInferiorLayout = new javax.swing.GroupLayout(panelInferior);
        panelInferior.setLayout(panelInferiorLayout);
        panelInferiorLayout.setHorizontalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInferiorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel23))
                    .addGroup(panelInferiorLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(campoOnuSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(campoContratoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(botonBuscarContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInferiorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel25)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoDireccion)
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoEstatusContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(botonGuardarAsociacion, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        panelInferiorLayout.setVerticalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(campoContratoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonBuscarContrato)
                    .addComponent(campoOnuSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(campoEstatusContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(campoDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonGuardarAsociacion))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelInfoContrato.setMaximumSize(new java.awt.Dimension(1500, 100));
        panelInfoContrato.setMinimumSize(new java.awt.Dimension(1500, 100));
        panelInfoContrato.setPreferredSize(new java.awt.Dimension(1500, 100));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("Información de la Onu");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Número de Serie");

        campoNumeroSerieOnu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoNumeroSerieOnu.setForeground(java.awt.Color.red);

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("ID Sistema:");

        campoIdOnu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoIdOnu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoIdOnuActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Estatus:");

        campoEstatusOnu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoEstatusOnu.setForeground(java.awt.Color.red);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Fecha de Registro:");

        campoFechaRegistroOnu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Contrato:");

        campoContratoOnu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoContratoOnu.setForeground(java.awt.Color.red);

        botonRetirarContrato.setBackground(java.awt.Color.red);
        botonRetirarContrato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonRetirarContrato.setForeground(new java.awt.Color(255, 255, 255));
        botonRetirarContrato.setText("Retirar del Contrato");
        botonRetirarContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRetirarContratoActionPerformed(evt);
            }
        });

        botonActualizarSerie.setBackground(java.awt.Color.gray);
        botonActualizarSerie.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonActualizarSerie.setForeground(new java.awt.Color(255, 255, 255));
        botonActualizarSerie.setText("Actualizar Número de Serie");

        botonCambioEstatusOnu.setBackground(java.awt.Color.blue);
        botonCambioEstatusOnu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonCambioEstatusOnu.setForeground(new java.awt.Color(255, 255, 255));
        botonCambioEstatusOnu.setText("Inhabilitar Onu");

        botonAsociarOnu.setBackground(new java.awt.Color(102, 153, 0));
        botonAsociarOnu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonAsociarOnu.setForeground(new java.awt.Color(255, 255, 255));
        botonAsociarOnu.setText("Asociar a contrato");
        botonAsociarOnu.setToolTipText("");

        javax.swing.GroupLayout panelInfoContratoLayout = new javax.swing.GroupLayout(panelInfoContrato);
        panelInfoContrato.setLayout(panelInfoContratoLayout);
        panelInfoContratoLayout.setHorizontalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(botonCambioEstatusOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(campoNumeroSerieOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(campoIdOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                        .addComponent(campoEstatusOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel13)
                                        .addGap(18, 18, 18)
                                        .addComponent(campoContratoOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(botonAsociarOnu, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGap(969, 969, 969)
                        .addComponent(botonActualizarSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(campoFechaRegistroOnu)
                    .addComponent(botonRetirarContrato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelInfoContratoLayout.setVerticalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoNumeroSerieOnu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel22)
                    .addComponent(campoIdOnu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(campoEstatusOnu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(campoFechaRegistroOnu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(campoContratoOnu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonRetirarContrato)
                    .addComponent(botonActualizarSerie)
                    .addComponent(botonCambioEstatusOnu)
                    .addComponent(botonAsociarOnu))
                .addContainerGap())
        );

        botonRegresar.setBackground(java.awt.Color.red);
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");
        botonRegresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRegresarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelInfoContrato, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1488, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelInferior, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1494, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void campoIdOnuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoIdOnuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoIdOnuActionPerformed

    private void botonRetirarContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRetirarContratoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonRetirarContratoActionPerformed

    private void botonRegresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRegresarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonRegresarActionPerformed

    private void botonBuscarContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarContratoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonBuscarContratoActionPerformed

    private void campoFolioContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoFolioContratoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoFolioContratoActionPerformed

    private void campoSuscriptorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoSuscriptorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoSuscriptorActionPerformed

    private void campoEstatusContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoEstatusContratoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoEstatusContratoActionPerformed

    private void campoDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoDireccionActionPerformed

    private void botonGuardarAsociacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarAsociacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonGuardarAsociacionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonActualizarSerie;
    private javax.swing.JButton botonAsociarOnu;
    private javax.swing.JButton botonBuscarContrato;
    private javax.swing.JButton botonBuscarOnu;
    private javax.swing.JButton botonCambioEstatusOnu;
    private javax.swing.JButton botonGuardarAsociacion;
    private javax.swing.JButton botonNuevaOnu;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JButton botonRetirarContrato;
    private javax.swing.JTextField campoContratoBuscar;
    private javax.swing.JTextField campoContratoOnu;
    private javax.swing.JTextField campoDireccion;
    private javax.swing.JTextField campoEstatusContrato;
    private javax.swing.JTextField campoEstatusOnu;
    private javax.swing.JTextField campoFechaRegistroOnu;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoIdOnu;
    private javax.swing.JTextField campoNumeroSerieOnu;
    private javax.swing.JTextField campoOnuSeleccionada;
    private javax.swing.JTextField campoSerieOnuBusqueda;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JComboBox<EstatusOnu> comboEstatusOnu;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaTextoBuscar;
    private javax.swing.JLabel etiquetaTextoBuscar1;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelInfoContrato;
    private javax.swing.JTable tablaOnus;
    // End of variables declaration//GEN-END:variables
}
