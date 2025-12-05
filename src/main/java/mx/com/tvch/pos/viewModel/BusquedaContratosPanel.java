/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.Image;
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
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CobroServicioController;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.PromocionEntity;
import mx.com.tvch.pos.model.TipoBusquedaCobro;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class BusquedaContratosPanel extends javax.swing.JPanel {

    private static BusquedaContratosPanel contratosPanel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final CobroServicioController controller;
    private final Utilerias util;

    List<ContratoxSuscriptorDetalleEntity> suscriptoresConsultaList;
    private ContratoxSuscriptorDetalleEntity suscriptorSeleccionado;

    org.slf4j.Logger logger = LoggerFactory.getLogger(BusquedaContratosPanel.class);

    public static BusquedaContratosPanel getCobroPanel(PosFrame frame) {
        if (contratosPanel == null) {
            contratosPanel = new BusquedaContratosPanel();
        }
        posFrame = frame;
        return contratosPanel;
    }

    /**
     * Creates new form CobroPanel
     */
    public BusquedaContratosPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = CobroServicioController.getContratoxSuscriptorController();
        util = Utilerias.getUtilerias();
        suscriptoresConsultaList = new ArrayList<>();
        crearEventos();
        cargarComboTiposBusqueda();
        cargarComboEstatusSuscriptor();
    }

    private void crearEventos() {

        KeyListener enterTablaSuscriptoresListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int selectedRow = tablaSuscriptores.getSelectedRow();
                    if (selectedRow >= 0) {
                        Long contratoId = (Long) tablaSuscriptores.getModel().getValueAt(selectedRow, 0);
                        System.out.println("contrato seleccionado: " + contratoId);
                        if (!suscriptoresConsultaList.isEmpty()) {
                            if (suscriptoresConsultaList.stream()
                                    .filter(cs -> cs.getContratoId() == contratoId.longValue()).findAny().isPresent()) {
                                ContratoxSuscriptorDetalleEntity entity = suscriptoresConsultaList
                                        .stream().filter(cs -> cs.getContratoId() == contratoId.longValue()).findFirst().get();
                                suscriptorSeleccionado = entity;

                                cargarDatosSuscriptor(entity);
                            }
                        }
                    }
                }
            }
        };
        tablaSuscriptores.addKeyListener(enterTablaSuscriptoresListener);

        MouseListener dobleClickTablaSuscriptoresListener = new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    // your valueChanged overridden method
                    
                    try{ 
                    
                        Long contratoId = (Long) tablaSuscriptores.getModel().getValueAt(row, 0);
                        System.out.println("contrato seleccionado: " + contratoId);
                        if (!suscriptoresConsultaList.isEmpty()) {
                            if (suscriptoresConsultaList.stream()
                                    .filter(cs -> cs.getContratoId() == contratoId.longValue()).findAny().isPresent()) {
                                ContratoxSuscriptorDetalleEntity entity = suscriptoresConsultaList
                                        .stream().filter(cs -> cs.getContratoId() == contratoId.longValue()).findFirst().get();
                                suscriptorSeleccionado = entity;
                                cargarDatosSuscriptor(entity);
                            }
                        }
                    
                    }catch(Exception ex){
                        logger.error("No se encontro contrato en la posicion del mouse, excepcion controlada");
                    }
                    mouseEvent.consume();
                }
            }
        };
        tablaSuscriptores.addMouseListener(dobleClickTablaSuscriptoresListener);

        ActionListener botonRegresarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPantalla();
                posFrame.cambiarPantalla(contratosPanel, VentanaEnum.MENU);
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
                    buscarSuscriptor();
                    e.consume();
                }
            }
        };
        campoBusqueda.addKeyListener(keyListenerBuscarSuscriptor);
        
        ActionListener botonBusquedaActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarSuscriptor();
            }
        };
        botonBusqueda.addActionListener(botonBusquedaActionListener);
        
        /**
         * 
         */
        ActionListener botonRegistrarSuscriptorActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                posFrame.cambiarPantalla(contratosPanel, VentanaEnum.REGISTRO_SUSCRIPTOR);
            }
        };
        botonNuevoSuscriptor.addActionListener(botonRegistrarSuscriptorActionListener);
        
        /**
         * 
         */
        ActionListener botonRegistrarContratoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(suscriptorSeleccionado != null){
                    sesion.setContratoSeleccionado(suscriptorSeleccionado);
                    posFrame.cambiarPantalla(contratosPanel, VentanaEnum.REGISTRO_CONTRATO);
                }else{
                    JOptionPane.showMessageDialog(contratosPanel, "Para generar un contrato de un suscriptor existente, por favor seleccionelo de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonNuevoContrato.addActionListener(botonRegistrarContratoActionListener);
        
        /**
         * 
         */
        ActionListener botonEdicionContratoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(suscriptorSeleccionado != null){
                    sesion.setTipoBusquedaAlmacenada((TipoBusquedaCobro) comboTiposBusqueda.getSelectedItem());
                    sesion.setTextoBusquedaAlmacenada(campoBusqueda.getText());
                    sesion.setContratoSeleccionado(suscriptorSeleccionado);
                    posFrame.cambiarPantalla(contratosPanel, VentanaEnum.EDICION_CONTRATO);
                }else{
                    JOptionPane.showMessageDialog(contratosPanel, "Para editar un contrato o suscriptor existente, por favor seleccionelo de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonEditarContrato.addActionListener(botonEdicionContratoActionListener);
        
        /**
         * 
         */
        ActionListener botonCobroActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(suscriptorSeleccionado != null){
                    sesion.setTipoBusquedaAlmacenada((TipoBusquedaCobro) comboTiposBusqueda.getSelectedItem());
                    sesion.setTextoBusquedaAlmacenada(campoBusqueda.getText());
                    sesion.setContratoSeleccionado(suscriptorSeleccionado);
                    posFrame.cambiarPantalla(contratosPanel, VentanaEnum.COBROS);
                }else{
                    JOptionPane.showMessageDialog(contratosPanel, "Para editar un contrato o suscriptor existente, por favor seleccionelo de la lista.","", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonCobro.addActionListener(botonCobroActionListener);

    }

    /**
     * 
     */
    private void buscarSuscriptor() {

        if (!campoBusqueda.getText().isEmpty()) {

            TipoBusquedaCobro tipoBusquedaCobro = (TipoBusquedaCobro) comboTiposBusqueda.getModel().getSelectedItem();

            try {

                Long contrato = null;
                DefaultTableModel model = (DefaultTableModel) tablaSuscriptores.getModel();
                model.getDataVector().clear();
                //model.fireTableStructureChanged();

                if (tipoBusquedaCobro.getTipoCobroId() == Constantes.TIPO_BUSQUEDA_FOLIO_CONTRATO) {
                    try {

                        contrato = Long.parseLong(campoBusqueda.getText().trim());
                        cargarTablaSuscriptores(model, contrato, tipoBusquedaCobro.getTipoCobroId(), "", checkCancelados.isSelected());
                        limpiarDatosSuscriptor();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(contratosPanel, "Formato de contrato incorrecto. Por favor ingrese un contrato numérico", "", JOptionPane.WARNING_MESSAGE);
                    }
                } else {

                    if (!campoBusqueda.getText().trim().isEmpty()) {

                        cargarTablaSuscriptores(model, contrato, tipoBusquedaCobro.getTipoCobroId(), campoBusqueda.getText().trim().toUpperCase(), checkCancelados.isSelected());
                        limpiarDatosSuscriptor();

                    } else {
                        JOptionPane.showMessageDialog(contratosPanel, "Por favor ingrese ingrese un texto a buscar válido.", "", JOptionPane.WARNING_MESSAGE);
                    }

                }

            } catch (NoSuchElementException ex) {
                JOptionPane.showMessageDialog(contratosPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(contratosPanel, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(contratosPanel, "Por favor ingrese información para realizar la busqueda.", "", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * 
     * @param mesSeleccionado
     * @param anioSeleccionado
     * @param contratosuscriptor
     * @param seRefrescanPromociones 
     */
    private void cargarDatosSuscriptor( 
            ContratoxSuscriptorDetalleEntity contratosuscriptor) {

        System.out.println("Seleccionado: " + contratosuscriptor.getContratoId());
        
        // primero borrar los datos de suscriptores que se hayan seleccionado antes
        limpiarDatosSuscriptor();
        suscriptorSeleccionado = contratosuscriptor;

        StringBuilder nombre = new StringBuilder();
        nombre.append(suscriptorSeleccionado.getNombre());
        if (suscriptorSeleccionado.getApellidoPaterno() != null) {
            nombre.append(" ").append(suscriptorSeleccionado.getApellidoPaterno());
        }
        if (suscriptorSeleccionado.getApellidoMaterno() != null) {
            nombre.append(" ").append(suscriptorSeleccionado.getApellidoMaterno());
        }
        campoSuscriptor.setText(nombre.toString());
        campoContrato.setText(String.valueOf(suscriptorSeleccionado.getContratoId()));
        campoFolioContrato.setText(String.valueOf(suscriptorSeleccionado.getFolioContrato()));
        campoCostoServicio.setText(String.valueOf(suscriptorSeleccionado.getCostoServicio()));
        campoEstatus.setText(suscriptorSeleccionado.getEstatusContrato());
        if (contratosuscriptor.getFechaProximoPago() != null) {
            campoFechaPago.setText(util.convertirDateTime2String(contratosuscriptor.getFechaProximoPago(), "dd/MM/yyyy"));
        }
        campoServicioContratado.setText(suscriptorSeleccionado.getServicio());
        StringBuilder domicilio = new StringBuilder();
        domicilio.append(suscriptorSeleccionado.getCalle()).append(" ").append(suscriptorSeleccionado.getNumeroCalle());
        domicilio.append(" ").append(suscriptorSeleccionado.getColonia());
        campoDomicilio.setText(domicilio.toString());
        campoTelefono.setText(suscriptorSeleccionado.getTelefono());
        
        if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_ACTIVO ||
                suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE ||
                suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTESIA ||
                suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_NUEVO ||
                suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_PENDIENTE_INSTALAR){
            botonCobro.setEnabled(true);
            botonEditarContrato.setEnabled(true);
            botonNuevoContrato.setEnabled(true);
            botonRecuperar.setEnabled(false);
            botonCancelar.setEnabled(true);
        }else if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO ||
                suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO){
            botonCobro.setEnabled(false);
            botonEditarContrato.setEnabled(true);
            botonNuevoContrato.setEnabled(true);
            botonRecuperar.setEnabled(true);
            botonCancelar.setEnabled(false);
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
    private void cargarTablaSuscriptores(DefaultTableModel model, Long contrato, int tipoBusquedaCobro, String cadenaBusqueda, boolean seBuscanCancelados) throws Exception {

        suscriptoresConsultaList = controller.consultarSuscriptores(contrato, tipoBusquedaCobro, cadenaBusqueda, seBuscanCancelados);

        if (!suscriptoresConsultaList.isEmpty()) {

            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (ContratoxSuscriptorDetalleEntity c : suscriptoresConsultaList) {
                model.addRow(new Object[]{c.getContratoId(),
                    c.getFolioContrato()== null ? "" : c.getFolioContrato(),
                    c.getNombre().concat(" ").concat(c.getApellidoPaterno()).concat(" ").concat(c.getApellidoMaterno()),
                    c.getServicio(),
                    c.getCalle().concat(" ").concat(c.getNumeroCalle()).concat(" ").concat(c.getColonia()),
                    c.getEstatusContrato()});
            }
            tablaSuscriptores.setRowSelectionInterval(0, 0);
        } else {
            
            sesion.setContratoRegistrado(null);
            sesion.setContratoSeleccionado(null);
            sesion.setTextoBusquedaAlmacenada(null);
            sesion.setTipoBusquedaAlmacenada(null);
            model.getDataVector().clear();
            model.fireTableDataChanged();
            
            JOptionPane.showMessageDialog(contratosPanel, "No se encontraron suscriptores con la información solicitada", "", JOptionPane.WARNING_MESSAGE);
        }
        
    }

    /**
     * 
     */
    private void cargarComboEstatusSuscriptor() {
        try {

            List<EstatusSuscriptorEntity> list = controller.consultarEstatusSuscriptor();
            list = list.stream().filter(e -> e.getEstatusId() == Constantes.ESTATUS_SUSCRIPTOR_ACTIVO).collect(Collectors.toList());
            list.forEach(e -> comboEstatusSuscriptor.addItem(e));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(contratosPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 
     */
    private void cargarComboTiposBusqueda() {
        List<TipoBusquedaCobro> list = new ArrayList<>();
        //list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_CONTRATO, "Por Contrato"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_FOLIO_CONTRATO, "Por Contrato"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_NOMBRE, "Por Nombre"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_APELLIDO_PATERNO, "Por Apellido Paterno"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_APELLIDO_MATERNO, "Por Apellido Materno"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_DOMICILIO, "Por Domicilio"));
        list.forEach(tb -> comboTiposBusqueda.addItem(tb));

    }
    
    
    /**
     * 
     */
    public void cargarDatosSesion() {

        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());

        tablaSuscriptores.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaSuscriptores.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaSuscriptores.getColumnModel().getColumn(2).setPreferredWidth(310);
        tablaSuscriptores.getColumnModel().getColumn(3).setPreferredWidth(160);
        tablaSuscriptores.getColumnModel().getColumn(4).setPreferredWidth(380);
        tablaSuscriptores.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        ImageIcon imagen = new ImageIcon("src/main/resources/logo_grande.jpg");
        Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(/*etiquetaLogo.getWidth(), etiquetaLogo.getHeight()*/320, 130, Image.SCALE_DEFAULT));
        etiquetaLogo.setIcon(icono);
        
        //validar si hay datos para refrescar la pantalla
        if(suscriptorSeleccionado != null && sesion.getContratoSeleccionado() != null
                && suscriptorSeleccionado.getContratoId() == sesion.getContratoSeleccionado().getContratoId()
                && sesion.getTipoBusquedaAlmacenada() != null
                && sesion.getTextoBusquedaAlmacenada() != null ){
            
            //asegurarnos de q si es un cancelado se marque el check
            if(sesion.getContratoSeleccionado().getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO ||
                    sesion.getContratoSeleccionado().getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO){
                checkCancelados.setSelected(true);
            }
            
            campoBusqueda.setText(sesion.getTextoBusquedaAlmacenada());
            comboTiposBusqueda.setSelectedItem(sesion.getTipoBusquedaAlmacenada());
            refrescarBusqueda();
        }else if(sesion.getContratoRegistrado() != null){
            
            //asegurarnos de desnarcar el checj de la busqueda de cancelados para q no truene
            checkCancelados.setSelected(false);
            
            comboTiposBusqueda.setSelectedIndex(0);
            campoBusqueda.setText(String.valueOf(sesion.getContratoRegistrado()));
            buscarContratoRecienCreado();
        }
        
    }
    
    public void buscarContratoRecienCreado(){
        
        buscarSuscriptor();
        //suscriptorSeleccionado = suscriptoresConsultaList.stream().filter(s -> s.getFolioContrato().longValue() == sesion.getContratoRegistrado()).findFirst().get();
        suscriptorSeleccionado = suscriptoresConsultaList.get(0);
        cargarDatosSuscriptor(suscriptorSeleccionado);
        //por ultimo selecionar en la tabla el contrato que ya se cargo
        int searchColumnIndex = 0; // columna con el id de sistema

        // seleccionar en la tabla el contrato que fue editado
        for (int i = 0; i < tablaSuscriptores.getRowCount(); i++) {
            Object cellValue = tablaSuscriptores.getValueAt(i, searchColumnIndex);
            if (cellValue != null && cellValue.toString().equals(String.valueOf(suscriptorSeleccionado.getContratoId()))) {
                tablaSuscriptores.setRowSelectionInterval(i, i); // aca seleccionar el row 
                tablaSuscriptores.scrollRectToVisible(tablaSuscriptores.getCellRect(i, 0, true)); // scroll al row seleccionado
                break; // detener al encontrar el registro
            }
        }
        
        //al final quitar de la sesion el contrato recien creado
        sesion.setContratoRegistrado(null);
        sesion.setContratoSeleccionado(null);
    }
    
    /**
     * 
     */
    public void refrescarBusqueda(){
        
        buscarSuscriptor();
        
        if(suscriptoresConsultaList.stream().filter(s -> s.getContratoId().longValue() == sesion.getContratoSeleccionado().getContratoId().longValue()).findFirst().isPresent()){
            //entraca cuando regresa de la pantalla de editar a consulta con la busqueda original
            //volver a setear el contrato ya que en el paso anterior se borra con el flujo original
            suscriptorSeleccionado = suscriptoresConsultaList.stream().filter(s -> s.getContratoId().longValue() == sesion.getContratoSeleccionado().getContratoId().longValue()).findFirst().get();
            cargarDatosSuscriptor(suscriptorSeleccionado);
  
        }else{
            // entra aca cuando regresa a la consulta y antes cambio los valores de la busqueda original y no encuentra el contrato
            if(!suscriptoresConsultaList.isEmpty()){
                suscriptorSeleccionado = suscriptoresConsultaList.get(0);
            }    
        }
        
        if(suscriptorSeleccionado != null){
            //por ultimo selecionar en la tabla el contrato que ya se cargo
            int searchColumnIndex = 0; // columna con el id de sistema
            // seleccionar en la tabla el contrato que fue editado
            for (int i = 0; i < tablaSuscriptores.getRowCount(); i++) {
                Object cellValue = tablaSuscriptores.getValueAt(i, searchColumnIndex);
                if (cellValue != null && cellValue.toString().equals(String.valueOf(suscriptorSeleccionado.getContratoId()))) {
                    tablaSuscriptores.setRowSelectionInterval(i, i); // aca seleccionar el row 
                    tablaSuscriptores.scrollRectToVisible(tablaSuscriptores.getCellRect(i, 0, true)); // scroll al row seleccionado
                    break; // detener al encontrar el registro
                }
            }
        }
        
    }
    
    /**
     * 
     */
    private void limpiarPantalla() {
        limpiarDatosSuscriptor();
        
        campoBusqueda.setText("");
        comboTiposBusqueda.setSelectedIndex(0);

        DefaultTableModel model = (DefaultTableModel) tablaSuscriptores.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
        sesion.setContratoSeleccionado(null);
        sesion.setTextoBusquedaAlmacenada(null);
        sesion.setTipoBusquedaAlmacenada(null);
        sesion.setContratoRegistrado(null);
        
    }
    
    /**
     * 
     */
    private void limpiarDatosSuscriptor() {
        suscriptorSeleccionado = null;
        campoSuscriptor.setText("");
        campoContrato.setText("");
        campoFolioContrato.setText("");
        campoEstatus.setText("");
        campoFechaPago.setText("");
        campoServicioContratado.setText("");
        campoDomicilio.setText("");
        campoTelefono.setText("");
        campoCostoServicio.setText("");

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
        tablaSuscriptores = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        etiquetaTipoBusqueda = new javax.swing.JLabel();
        comboTiposBusqueda = new javax.swing.JComboBox<>();
        etiquetaTextoBuscar = new javax.swing.JLabel();
        campoBusqueda = new javax.swing.JTextField();
        botonBusqueda = new javax.swing.JButton();
        etiquetaLogo = new javax.swing.JLabel();
        comboEstatusSuscriptor = new javax.swing.JComboBox<>();
        etiquetaEstatusSuscriptor = new javax.swing.JLabel();
        botonNuevoSuscriptor = new javax.swing.JButton();
        checkCancelados = new javax.swing.JCheckBox();
        panelInferior = new javax.swing.JPanel();
        botonRegresar = new javax.swing.JButton();
        panelInfoContrato = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        campoSuscriptor = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        campoContrato = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        campoFolioContrato = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        campoServicioContratado = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        campoDomicilio = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        campoEstatus = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        campoFechaPago = new javax.swing.JTextField();
        campoTelefono = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        campoCostoServicio = new javax.swing.JTextField();
        botonCobro = new javax.swing.JButton();
        botonCancelar = new javax.swing.JButton();
        botonRecuperar = new javax.swing.JButton();
        botonNuevoContrato = new javax.swing.JButton();
        botonEditarContrato = new javax.swing.JButton();

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
        jLabel24.setText("Consulta de Contratos");

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
                .addComponent(jLabel24)
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

        tablaSuscriptores.setBackground(new java.awt.Color(204, 204, 204));
        tablaSuscriptores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Sistema", "Número de  Contrato", "Nombre", "Servicio Contratado", "Domicilio Contrato", "Estatus Contrato"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaSuscriptores.setToolTipText("");
        tablaSuscriptores.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaSuscriptores.setFillsViewportHeight(true);
        tablaSuscriptores.setMaximumSize(new java.awt.Dimension(1438, 2200));
        tablaSuscriptores.setMinimumSize(new java.awt.Dimension(1438, 2200));
        tablaSuscriptores.setPreferredSize(new java.awt.Dimension(1438, 2200));
        tablaSuscriptores.setRowHeight(15);
        tablaSuscriptores.setShowGrid(false);
        jScrollPane1.setViewportView(tablaSuscriptores);
        if (tablaSuscriptores.getColumnModel().getColumnCount() > 0) {
            tablaSuscriptores.getColumnModel().getColumn(0).setResizable(false);
            tablaSuscriptores.getColumnModel().getColumn(1).setResizable(false);
            tablaSuscriptores.getColumnModel().getColumn(2).setResizable(false);
            tablaSuscriptores.getColumnModel().getColumn(3).setResizable(false);
            tablaSuscriptores.getColumnModel().getColumn(4).setResizable(false);
            tablaSuscriptores.getColumnModel().getColumn(5).setResizable(false);
        }

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Busqueda de Suscriptor");

        etiquetaTipoBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaTipoBusqueda.setText("Tipo de Búsqueda:");

        comboTiposBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        comboTiposBusqueda.setFocusCycleRoot(true);

        etiquetaTextoBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaTextoBuscar.setText("Texto a buscar:");

        campoBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonBusqueda.setBackground(new java.awt.Color(227, 126, 75));
        botonBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonBusqueda.setForeground(new java.awt.Color(255, 255, 255));
        botonBusqueda.setText("Buscar Suscriptor");

        etiquetaLogo.setBackground(new java.awt.Color(255, 255, 255));
        etiquetaLogo.setInheritsPopupMenu(false);
        etiquetaLogo.setMaximumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setMinimumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setPreferredSize(new java.awt.Dimension(410, 88));

        comboEstatusSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        etiquetaEstatusSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaEstatusSuscriptor.setText("Estatus Suscriptor:");

        botonNuevoSuscriptor.setBackground(java.awt.Color.magenta);
        botonNuevoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonNuevoSuscriptor.setForeground(new java.awt.Color(255, 255, 255));
        botonNuevoSuscriptor.setText("Registrar Nuevo Suscriptor");

        checkCancelados.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        checkCancelados.setText("Buscar en cancelados");

        javax.swing.GroupLayout panelBusquedaLayout = new javax.swing.GroupLayout(panelBusqueda);
        panelBusqueda.setLayout(panelBusquedaLayout);
        panelBusquedaLayout.setHorizontalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBusquedaLayout.createSequentialGroup()
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(etiquetaTextoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(etiquetaTipoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelBusquedaLayout.createSequentialGroup()
                                .addComponent(comboTiposBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(etiquetaEstatusSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(comboEstatusSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(campoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelBusquedaLayout.createSequentialGroup()
                                .addComponent(botonBusqueda)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(botonNuevoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelBusquedaLayout.createSequentialGroup()
                                .addComponent(checkCancelados)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1458, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiquetaLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );
        panelBusquedaLayout.setVerticalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaTipoBusqueda)
                    .addComponent(comboTiposBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaEstatusSuscriptor)
                    .addComponent(comboEstatusSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkCancelados))
                .addGap(3, 3, 3)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaTextoBuscar)
                    .addComponent(botonBusqueda)
                    .addComponent(botonNuevoSuscriptor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelInferior.setBackground(new java.awt.Color(255, 255, 255));
        panelInferior.setMaximumSize(new java.awt.Dimension(1500, 200));
        panelInferior.setMinimumSize(new java.awt.Dimension(1500, 200));
        panelInferior.setPreferredSize(new java.awt.Dimension(1500, 200));

        botonRegresar.setBackground(java.awt.Color.red);
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        panelInfoContrato.setMaximumSize(new java.awt.Dimension(1500, 100));
        panelInfoContrato.setMinimumSize(new java.awt.Dimension(1500, 100));
        panelInfoContrato.setPreferredSize(new java.awt.Dimension(1500, 100));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("Información del Contrato");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Nombre del Suscriptor:");

        campoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("ID Sistema:");

        campoContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoContratoActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Número de Contrato:");

        campoFolioContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoFolioContrato.setForeground(java.awt.Color.red);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Servicio Contratado:");
        jLabel7.setToolTipText("");

        campoServicioContratado.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Domicilio Registrado");

        campoDomicilio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Estatus:");

        campoEstatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoEstatus.setForeground(java.awt.Color.red);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Fecha de Corte:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Teléfono:");

        campoFechaPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoFechaPago.setForeground(java.awt.Color.red);

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Costo:");

        campoCostoServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelInfoContratoLayout = new javax.swing.GroupLayout(panelInfoContrato);
        panelInfoContrato.setLayout(panelInfoContratoLayout);
        panelInfoContratoLayout.setHorizontalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(campoCostoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoEstatus)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(97, 97, 97))))
        );
        panelInfoContratoLayout.setVerticalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel22)
                    .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(campoCostoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        botonCobro.setBackground(java.awt.Color.green);
        botonCobro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonCobro.setForeground(new java.awt.Color(255, 255, 255));
        botonCobro.setText("Realizar Cobro");

        botonCancelar.setBackground(new java.awt.Color(255, 102, 102));
        botonCancelar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonCancelar.setForeground(new java.awt.Color(255, 255, 255));
        botonCancelar.setText("Cancelar Contrato");

        botonRecuperar.setBackground(new java.awt.Color(102, 102, 255));
        botonRecuperar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonRecuperar.setForeground(new java.awt.Color(255, 255, 255));
        botonRecuperar.setText("Recuperar Contrato");

        botonNuevoContrato.setBackground(java.awt.Color.orange);
        botonNuevoContrato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonNuevoContrato.setForeground(new java.awt.Color(255, 255, 255));
        botonNuevoContrato.setText("Generar Nuevo Contrato ");
        botonNuevoContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonNuevoContratoActionPerformed(evt);
            }
        });

        botonEditarContrato.setBackground(new java.awt.Color(255, 153, 204));
        botonEditarContrato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonEditarContrato.setForeground(new java.awt.Color(255, 255, 255));
        botonEditarContrato.setText("Editar Contrato");

        javax.swing.GroupLayout panelInferiorLayout = new javax.swing.GroupLayout(panelInferior);
        panelInferior.setLayout(panelInferiorLayout);
        panelInferiorLayout.setHorizontalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInferiorLayout.createSequentialGroup()
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInferiorLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInferiorLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(botonCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonEditarContrato)
                        .addGap(18, 18, 18)
                        .addComponent(botonNuevoContrato)
                        .addGap(18, 18, 18)
                        .addComponent(botonRecuperar)
                        .addGap(18, 18, 18)
                        .addComponent(botonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelInferiorLayout.setVerticalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInferiorLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonCobro)
                    .addComponent(botonCancelar)
                    .addComponent(botonRecuperar)
                    .addComponent(botonNuevoContrato)
                    .addComponent(botonEditarContrato))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCabecero, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBusqueda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void campoContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoContratoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoContratoActionPerformed

    private void botonNuevoContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonNuevoContratoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonNuevoContratoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonBusqueda;
    private javax.swing.JButton botonCancelar;
    private javax.swing.JButton botonCobro;
    private javax.swing.JButton botonEditarContrato;
    private javax.swing.JButton botonNuevoContrato;
    private javax.swing.JButton botonNuevoSuscriptor;
    private javax.swing.JButton botonRecuperar;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoBusqueda;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoCostoServicio;
    private javax.swing.JTextField campoDomicilio;
    private javax.swing.JTextField campoEstatus;
    private javax.swing.JTextField campoFechaPago;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoServicioContratado;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JCheckBox checkCancelados;
    private javax.swing.JComboBox<EstatusSuscriptorEntity> comboEstatusSuscriptor;
    private javax.swing.JComboBox<TipoBusquedaCobro> comboTiposBusqueda;
    private javax.swing.JLabel etiquetaEstatusSuscriptor;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaTextoBuscar;
    private javax.swing.JLabel etiquetaTipoBusqueda;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
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
    private javax.swing.JTable tablaSuscriptores;
    // End of variables declaration//GEN-END:variables
}
