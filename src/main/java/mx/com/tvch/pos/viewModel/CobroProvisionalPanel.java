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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CobroProvisionalController;
import mx.com.tvch.pos.controller.CobroServicioController;
import mx.com.tvch.pos.entity.CobroProvisionalEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.model.TipoOrdenServicio;
import mx.com.tvch.pos.model.TipoBusquedaCobro;
import mx.com.tvch.pos.model.TipoOrden;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroProvisionalPanel extends javax.swing.JPanel {

    private static CobroProvisionalPanel cobroPanel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final CobroProvisionalController controller;
    private final Utilerias util;
    private final Impresora impresora;
    private TipoOrden tipoOrdenSeleccionado;
    private final CobroServicioController cobroServicioController;

    List<ContratoxSuscriptorEntity> suscriptoresConsultaList;
    private ContratoxSuscriptorEntity suscriptorSeleccionado;
    List<TipoOrden> listTiposOrden = new ArrayList<>();
    List<TipoOrdenServicio> listTiposOrdenServicio = new ArrayList<>();

    org.slf4j.Logger logger = LoggerFactory.getLogger(CobroProvisionalPanel.class);

    public static CobroProvisionalPanel getCobroPanel(PosFrame frame) {
        if (cobroPanel == null) {
            cobroPanel = new CobroProvisionalPanel();
        }
        posFrame = frame;
        return cobroPanel;
    }

    /**
     * Creates new form CobroPanel
     */
    public CobroProvisionalPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = CobroProvisionalController.getCobroProvisionalController();
        util = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        suscriptoresConsultaList = new ArrayList<>();
        cobroServicioController = CobroServicioController.getContratoxSuscriptorController();

        crearEventos();
        cargarComboTiposBusqueda();
        cargarComboEstatusSuscriptor();
    }

    private void crearEventos() {

        campoMonto.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                /*if (Integer.parseInt(campoMonto.getText()) <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number bigger than 0", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }*/
                etiquetaImporte.setText(campoMonto.getText());
            }
        });

        ActionListener comboTiposOrdenListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                TipoOrden tipoOrden = (TipoOrden) comboTiposOrden.getModel().getSelectedItem();
                if (tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO) {
                    comboTiposOrdenServicio.setEnabled(true);
                    cargarComboTiposOrdenServicio();
                } else {
                    comboTiposOrdenServicio.removeAllItems();
                    comboTiposOrdenServicio.setEnabled(false);
                }

            }
        };
        comboTiposOrden.addActionListener(comboTiposOrdenListener);

        ActionListener botonCobrarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean esMontoValido = false;

                if (suscriptorSeleccionado != null) {
                    if (!campoMonto.getText().isEmpty() && !campoObservaciones.getText().isEmpty()) {
                        Double monto = null;

                        try {
                            monto = Double.valueOf(campoMonto.getText());
                            if (monto >= 0) {
                                esMontoValido = true;
                            }
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(cobroPanel, "Por favor, ingrese un monto válido", "", JOptionPane.WARNING_MESSAGE);
                        }
                        if (esMontoValido) {

                            TipoOrdenServicio tipoOrdenServicio = null;
                            TipoOrden tipoOrden = (TipoOrden) comboTiposOrden.getModel().getSelectedItem();
                            if (tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO) {
                                tipoOrdenServicio = (TipoOrdenServicio) comboTiposOrdenServicio.getModel().getSelectedItem();
                            }

                            boolean seAceptaCobro = false;
                            boolean seCanceloCobro = false;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Se cobrará ").append(tipoOrden).append(" por un monto de $").append(monto).append("\n");
                            sb.append("¿Desea realizar el cobro? \n");
                            int input = JOptionPane.showConfirmDialog(null, sb.toString());
                            if (input == 0) {
                                seAceptaCobro = true;
                            }/* else if (input == 1) {
                                seAceptaCobro = false;
                            } else {
                                seCanceloCobro = true;
                            }*/

                            if (seAceptaCobro) {

                                CobroProvisionalEntity cobro = null;
                                try {
                                    if (tipoOrdenServicio != null) {
                                        cobro = controller
                                                .registrarCobroProvisional(
                                                        suscriptorSeleccionado,
                                                        tipoOrden.getDescripcion(),
                                                        tipoOrdenServicio.getDescripcion(),
                                                        campoObservaciones.getText(), monto);
                                    } else {
                                        cobro = controller
                                                .registrarCobroProvisional(
                                                        suscriptorSeleccionado,
                                                        tipoOrden.getDescripcion(),
                                                        null,
                                                        campoObservaciones.getText(), monto);
                                    }
                                } catch (Exception ex) {
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    ex.printStackTrace(pw);
                                    logger.error("Fallo al registrar cobro provisional: \n" + sw.toString());
                                    JOptionPane.showMessageDialog(cobroPanel, "Ocurrió un error al registrar su cobro, por favor reintente", "", JOptionPane.ERROR_MESSAGE);
                                }

                                if (cobro != null) {

                                    try {
                                        impresora.imprimirTicketCobroProvisional(sesion, cobro);
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(cobroPanel, "Su pago se realizó correctamente, pero ocurrió un error al imprimir su ticket. \nPor favor revise su impresora.", "", JOptionPane.WARNING_MESSAGE);
                                    }

                                    limpiarPantalla();
                                }

                            }

                        }else{
                            campoMonto.setText("0.0");
                        }
                    } else {
                        if (campoMonto.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(cobroPanel, "Por favor, ingrese un monto", "", JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(cobroPanel, "Por favor, ingrese sus observaciones", "", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }

            }
        };
        botonCobrar.addActionListener(botonCobrarActionListener);

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
                                ContratoxSuscriptorEntity entity = suscriptoresConsultaList
                                        .stream().filter(cs -> cs.getContratoId() == contratoId.longValue()).findFirst().get();
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
                    Long contratoId = (Long) tablaSuscriptores.getModel().getValueAt(row, 0);
                    System.out.println("contrato seleccionado: " + contratoId);
                    if (!suscriptoresConsultaList.isEmpty()) {
                        if (suscriptoresConsultaList.stream()
                                .filter(cs -> cs.getContratoId() == contratoId.longValue()).findAny().isPresent()) {
                            ContratoxSuscriptorEntity entity = suscriptoresConsultaList
                                    .stream().filter(cs -> cs.getContratoId() == contratoId.longValue()).findFirst().get();
                            cargarDatosSuscriptor(entity);
                        }
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
                posFrame.cambiarPantalla(cobroPanel, VentanaEnum.MENU);
            }
        };
        botonRegresar.addActionListener(botonRegresarActionListener);

        KeyListener keyListenerBuscarSuscriptor = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarSuscriptor();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
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

    }

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

                        contrato = Long.valueOf(campoBusqueda.getText().trim());
                        cargarTablaSuscriptores(model, contrato, tipoBusquedaCobro.getTipoCobroId(), "");
                        limpiarDatosSuscriptor();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(cobroPanel, "Formato de contrato incorrecto. Por favor ingrese un contrato numérico", "", JOptionPane.WARNING_MESSAGE);
                    }
                } else {

                    if (!campoBusqueda.getText().trim().isEmpty()) {

                        cargarTablaSuscriptores(model, contrato, tipoBusquedaCobro.getTipoCobroId(), campoBusqueda.getText().trim().toUpperCase());
                        limpiarDatosSuscriptor();

                    } else {
                        JOptionPane.showMessageDialog(cobroPanel, "Por favor ingrese ingrese un texto a buscar válido.", "", JOptionPane.WARNING_MESSAGE);
                    }

                }

            } catch (NoSuchElementException ex) {
                JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(cobroPanel, "Por favor ingrese información para realizar la busqueda.", "", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void cargarDatosSuscriptor(
            ContratoxSuscriptorEntity contratosuscriptor) {

        System.out.println("Seelccionado: " + contratosuscriptor.getContratoId());
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

    }

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
        etiquetaImporte.setText("0.00");

    }

    /**
     *
     * @param model
     * @param contrato
     * @param tipoBusquedaCobro
     * @param cadenaBusqueda
     * @throws Exception
     */
    private void cargarTablaSuscriptores(DefaultTableModel model, Long contrato, int tipoBusquedaCobro, String cadenaBusqueda) throws Exception {

        suscriptoresConsultaList = cobroServicioController.consultarSuscriptores(contrato, tipoBusquedaCobro, cadenaBusqueda);

        if (!suscriptoresConsultaList.isEmpty()) {

            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (ContratoxSuscriptorEntity c : suscriptoresConsultaList) {
                model.addRow(new Object[]{c.getContratoId(),
                    c.getFolioContrato() == null ? "" : c.getFolioContrato(),
                    c.getNombre().concat(" ").concat(c.getApellidoPaterno()).concat(" ").concat(c.getApellidoMaterno()),
                    c.getServicio(),
                    c.getCalle().concat(" ").concat(c.getNumeroCalle()).concat(" ").concat(c.getColonia()),
                    c.getEstatusContrato()});
            }
            tablaSuscriptores.setRowSelectionInterval(0, 0);
        } else {
            JOptionPane.showMessageDialog(cobroPanel, "No se encontraron suscriptores con la información solicitada", "", JOptionPane.WARNING_MESSAGE);
        }

    }

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

    private void limpiarPantalla() {
        limpiarDatosSuscriptor();

        DefaultTableModel model = (DefaultTableModel) tablaSuscriptores.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();

        etiquetaImporte.setText("0.00");
        campoMonto.setText("0.0");
        comboTiposOrdenServicio.setEnabled(false);
        campoObservaciones.setText("");
    }

    public void cargarDatosSesion() {

        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        tablaSuscriptores.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaSuscriptores.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaSuscriptores.getColumnModel().getColumn(2).setPreferredWidth(310);
        tablaSuscriptores.getColumnModel().getColumn(3).setPreferredWidth(190);
        tablaSuscriptores.getColumnModel().getColumn(4).setPreferredWidth(390);
        tablaSuscriptores.getColumnModel().getColumn(5).setPreferredWidth(130);

        ImageIcon imagen = new ImageIcon("src/main/resources/logo_grande.jpg");
        Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(/*etiquetaLogo.getWidth(), etiquetaLogo.getHeight()*/320, 130, Image.SCALE_DEFAULT));
        etiquetaLogo.setIcon(icono);

        campoObservaciones.setText("");
        etiquetaImporte.setText("0.00");
        campoMonto.setText("0.0");
        comboTiposOrdenServicio.setEnabled(false);
        cargarComboTiposOrden();
    }

    /**
     *
     */
    private void cargarComboEstatusSuscriptor() {

        try {

            List<EstatusSuscriptorEntity> list = cobroServicioController.consultarEstatusSuscriptor();
            list = list.stream().filter(e -> e.getEstatusId() == Constantes.ESTATUS_SUSCRIPTOR_ACTIVO).collect(Collectors.toList());
            list.forEach(e -> comboEstatusSuscriptor.addItem(e));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void cargarComboTiposOrdenServicio() {

        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "INSTALACION DE TELEVISION ADICIONAL"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "CAMBIO DE CABLE POR ACOMETIDA"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "CAMBIO DE CABLEADO"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "REUBICACION DE MODEM"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "CAMBIO DE UBICACION DE TV"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "VISITA TECNICA"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "CAMBIO DE PLAN"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_RETIRO_EQUIPO_CANCELACION, "RETIRO DE EQUIPO POR CANCELACION"));
        listTiposOrdenServicio.add(new TipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO, "RECONEXION DE SERVICIO"));
        listTiposOrdenServicio.forEach(to -> comboTiposOrdenServicio.addItem(to));

    }

    private void cargarComboTiposOrden() {

        //listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_INSTALACION, "Orden de Instalación"));
        listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
        listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO, "Orden de Cambio de Domicilio"));
        listTiposOrden.forEach(to -> comboTiposOrden.addItem(to));

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
        jLabel2 = new javax.swing.JLabel();
        comboTiposBusqueda = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        campoBusqueda = new javax.swing.JTextField();
        botonBusqueda = new javax.swing.JButton();
        etiquetaLogo = new javax.swing.JLabel();
        comboEstatusSuscriptor = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
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
        panelInfoPago = new javax.swing.JPanel();
        panelImportes = new javax.swing.JPanel();
        etiquetaPesos = new javax.swing.JLabel();
        etiquetaImporte = new javax.swing.JLabel();
        botonRegresar = new javax.swing.JButton();
        botonCobrar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        comboTiposOrden = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        comboTiposOrdenServicio = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        campoMonto = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        campoObservaciones = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(204, 204, 204));
        setMaximumSize(new java.awt.Dimension(1500, 950));
        setMinimumSize(new java.awt.Dimension(1500, 950));
        setPreferredSize(new java.awt.Dimension(1500, 950));

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
        jLabel24.setText("Cobro Provisional de Ordenes");

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
        panelBusqueda.setMaximumSize(new java.awt.Dimension(1499, 300));
        panelBusqueda.setMinimumSize(new java.awt.Dimension(1499, 300));
        panelBusqueda.setPreferredSize(new java.awt.Dimension(1499, 300));

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

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Tipo de Búsqueda:");

        comboTiposBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        comboTiposBusqueda.setFocusCycleRoot(true);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Texto a buscar:");

        campoBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonBusqueda.setBackground(new java.awt.Color(227, 126, 75));
        botonBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonBusqueda.setForeground(new java.awt.Color(255, 255, 255));
        botonBusqueda.setText("Buscar Suscriptor");

        etiquetaLogo.setBackground(new java.awt.Color(255, 255, 255));
        etiquetaLogo.setInheritsPopupMenu(false);
        etiquetaLogo.setMaximumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setMinimumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setPreferredSize(new java.awt.Dimension(410, 88));

        comboEstatusSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Estatus Suscriptor:");

        javax.swing.GroupLayout panelBusquedaLayout = new javax.swing.GroupLayout(panelBusqueda);
        panelBusqueda.setLayout(panelBusquedaLayout);
        panelBusquedaLayout.setHorizontalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBusquedaLayout.createSequentialGroup()
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelBusquedaLayout.createSequentialGroup()
                                .addComponent(comboTiposBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(comboEstatusSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(campoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(78, 78, 78)
                        .addComponent(botonBusqueda))
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
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonBusqueda)
                    .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(comboTiposBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(comboEstatusSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 17, Short.MAX_VALUE))
            .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("Información del Contrato");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Nombre del Suscriptor:");

        campoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("ID Sistema:");

        campoContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(java.awt.Color.red);
        jLabel6.setText("Número de Contrato:");

        campoFolioContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

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

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Fecha de Pago:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Teléfono:");

        campoFechaPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelImportesLayout = new javax.swing.GroupLayout(panelImportes);
        panelImportes.setLayout(panelImportesLayout);
        panelImportesLayout.setHorizontalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );
        panelImportesLayout.setVerticalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 137, Short.MAX_VALUE)
        );

        etiquetaPesos.setFont(new java.awt.Font("Segoe UI", 0, 90)); // NOI18N
        etiquetaPesos.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaPesos.setText("$");

        etiquetaImporte.setFont(new java.awt.Font("Segoe UI", 0, 90)); // NOI18N
        etiquetaImporte.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaImporte.setText("0.00");

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        botonCobrar.setBackground(new java.awt.Color(0, 153, 51));
        botonCobrar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonCobrar.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrar.setText("Cobrar");

        javax.swing.GroupLayout panelInfoPagoLayout = new javax.swing.GroupLayout(panelInfoPago);
        panelInfoPago.setLayout(panelInfoPagoLayout);
        panelInfoPagoLayout.setHorizontalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                        .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                        .addComponent(etiquetaPesos, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(443, 443, 443)
                .addComponent(panelImportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelInfoPagoLayout.setVerticalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaPesos)
                    .addComponent(etiquetaImporte))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonRegresar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(botonCobrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addComponent(panelImportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        comboTiposOrden.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Selecione Tipo de Orden:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("Tipo Servicio:");

        comboTiposOrdenServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Ingrese Monto:");

        campoMonto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoMonto.setText("0.0");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setText("Agregue Observaciones:");

        campoObservaciones.setColumns(20);
        campoObservaciones.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        campoObservaciones.setLineWrap(true);
        campoObservaciones.setRows(5);
        jScrollPane2.setViewportView(campoObservaciones);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(comboTiposOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(comboTiposOrdenServicio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTiposOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(comboTiposOrdenServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(campoMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(211, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelInfoContratoLayout = new javax.swing.GroupLayout(panelInfoContrato);
        panelInfoContrato.setLayout(panelInfoContratoLayout);
        panelInfoContratoLayout.setHorizontalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoSuscriptor)
                    .addComponent(campoServicioContratado, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(campoContrato)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))))
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        panelInfoContratoLayout.setVerticalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22)
                        .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5)
                    .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCabecero, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBusqueda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1500, Short.MAX_VALUE)
                    .addComponent(panelInfoContrato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonBusqueda;
    private javax.swing.JButton botonCobrar;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoBusqueda;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoDomicilio;
    private javax.swing.JTextField campoEstatus;
    private javax.swing.JTextField campoFechaPago;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoMonto;
    private javax.swing.JTextArea campoObservaciones;
    private javax.swing.JTextField campoServicioContratado;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JComboBox<EstatusSuscriptorEntity> comboEstatusSuscriptor;
    private javax.swing.JComboBox<TipoBusquedaCobro> comboTiposBusqueda;
    private javax.swing.JComboBox<TipoOrden> comboTiposOrden;
    private javax.swing.JComboBox<mx.com.tvch.pos.model.TipoOrdenServicio> comboTiposOrdenServicio;
    private javax.swing.JLabel etiquetaImporte;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaPesos;
    private javax.swing.JLabel etiquetaSucursal;
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
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelImportes;
    private javax.swing.JPanel panelInfoContrato;
    private javax.swing.JPanel panelInfoPago;
    private javax.swing.JTable tablaSuscriptores;
    // End of variables declaration//GEN-END:variables
}
