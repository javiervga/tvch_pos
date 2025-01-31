/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import com.toedter.calendar.JDateChooser;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CorteCajaController;
import mx.com.tvch.pos.controller.ReimpresionesController;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.entity.TransaccionTicketEntity;
import mx.com.tvch.pos.model.DetalleCorte;
import mx.com.tvch.pos.model.TipoCobro;
import mx.com.tvch.pos.util.Calendario;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class ReimpresionesPanel extends javax.swing.JPanel {

    private static ReimpresionesPanel panel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final Utilerias utilerias;
    private final Impresora impresora;
    private final Calendario calendario;
    private final JDateChooser calendarioIni;
    private final JDateChooser calendarioFin;
    private final ReimpresionesController controller;
    private final Utilerias util;
    private List<TransaccionTicketEntity> listaTransacciones;
    private TransaccionTicketEntity transaccionSeleccionada;

    Logger logger = LoggerFactory.getLogger(CorteCajaPanel.class);

    public static ReimpresionesPanel getReimpresionesPanel(PosFrame frame) {
        if (panel == null) {
            panel = new ReimpresionesPanel();
        }
        posFrame = frame;
        return panel;
    }

    /**
     * Creates new form CorteCajaPanel
     */
    public ReimpresionesPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = ReimpresionesController.getReimpresionesController();
        utilerias = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        calendario = Calendario.getCalendario();
        calendarioIni = calendario.obtenerChooser();
        calendarioFin = calendario.obtenerChooser();
        util = Utilerias.getUtilerias();
        initComponents();
        crearEventos();
        cargarCOmboTiposCobro();
        
        Calendar calFechaMinima = Calendar.getInstance();
        calFechaMinima.setTime(new Date());
        calFechaMinima.add(Calendar.WEEK_OF_YEAR, -1);

        calendarioIni.setMinSelectableDate(calFechaMinima.getTime());
        calendarioFin.setMinSelectableDate(calFechaMinima.getTime());
        calendarioIni.setMaxSelectableDate(new Date());
        calendarioFin.setMaxSelectableDate(new Date());
        calendarioIni.setBounds(panelCalendarioIni.getX(), panelCalendarioIni.getY(), 180, 27);
        calendarioFin.setBounds(panelCalendarioFin.getX(), panelCalendarioFin.getY(), 180, 27);
        panelCalendarioIni.add(calendarioIni);
        panelCalendarioFin.add(calendarioFin);

    }

    private void crearEventos() {
        
        ActionListener botonImprimirListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(transaccionSeleccionada != null){
                    try {
                        controller.reimprimirTicket(transaccionSeleccionada);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        };
        botonImprimir.addActionListener(botonImprimirListener);;

        KeyListener enterTablaTransaccionesListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int selectedRow = tablaTransacciones.getSelectedRow();
                    if (selectedRow >= 0) {
                        Long transaccionId = (Long) tablaTransacciones.getModel().getValueAt(selectedRow, 0);
                        System.out.println("transaccion seleccionada: " + transaccionId);
                        if (!listaTransacciones.isEmpty()) {
                            if (listaTransacciones.stream().filter(t -> t.getTransaccionId() == transaccionId.longValue()).findAny().isPresent()) {
                                TransaccionTicketEntity entity = listaTransacciones.stream().filter(t -> t.getTransaccionId() == transaccionId.longValue()).findFirst().get();
                                cargarDatosTransaccion(entity);
                            }
                        }
                    }
                }
            }
        };
        tablaTransacciones.addKeyListener(enterTablaTransaccionesListener);

        MouseListener dobleClickTablaTransaccionesListener = new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    // your valueChanged overridden method
                    Long transaccionId = (Long) tablaTransacciones.getModel().getValueAt(row, 0);
                    System.out.println("transaccion seleccionada: " + transaccionId);
                    if (!listaTransacciones.isEmpty()) {
                        if (listaTransacciones.stream().filter(t -> t.getTransaccionId() == transaccionId.longValue()).findAny().isPresent()) {
                            TransaccionTicketEntity entity = listaTransacciones.stream().filter(t -> t.getTransaccionId() == transaccionId.longValue()).findFirst().get();
                            cargarDatosTransaccion(entity);
                        }
                    }
                    mouseEvent.consume();
                }
            }
        };
        tablaTransacciones.addMouseListener(dobleClickTablaTransaccionesListener);

        ActionListener botonBusquedaActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (calendarioIni.getCalendar().getTime() != null && calendarioFin.getCalendar().getTime() != null) {

                    if (calendarioFin.getCalendar().after(calendarioIni.getCalendar())
                            || calendarioFin.getCalendar().equals(calendarioIni.getCalendar())) {

                        try {

                            TipoCobro tipoCobro = (TipoCobro) comboTipoOperacion.getModel().getSelectedItem();
                            Date dateIni = util.ajustarFechaInicio(calendarioIni.getCalendar().getTime());
                            Date dateFin = util.ajustarFechaFin(calendarioFin.getCalendar().getTime());
                            String fechaInicio = util.convertirDateTime2String(dateIni, Constantes.FORMATO_FECHA_MYSQL);
                            String fechaFin = util.convertirDateTime2String(dateFin, Constantes.FORMATO_FECHA_MYSQL);

                            List<TransaccionTicketEntity> list = controller.consultarTransacciones(
                                    tipoCobro.getTipoCobroId(),
                                    fechaInicio,
                                    fechaFin);

                            DefaultTableModel model = (DefaultTableModel) tablaTransacciones.getModel();
                            model.getDataVector().clear();
                            if (!list.isEmpty()) {
                                listaTransacciones = list;
                                cargarTablaTransacciones(model, list);
                            } else {
                                JOptionPane.showMessageDialog(panel, "No se encontraron operaciones.", "", JOptionPane.WARNING_MESSAGE);
                            }

                            model.fireTableDataChanged();

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(panel, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(panel, "La fecha de inicio debe ser menor a la fecha fin, por favor verifique.", "", JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        };
        botonBusqueda.addActionListener(botonBusquedaActionListener);

        ActionListener botonRegresarActionListener = (ActionEvent e) -> {
            limpiarPantalla();
            posFrame.cambiarPantalla(panel, VentanaEnum.MENU);
        };
        botonRegresar.addActionListener(botonRegresarActionListener);

    }

    public void cargarDatosSesion() {
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());

        tablaTransacciones.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaTransacciones.getColumnModel().getColumn(1).setPreferredWidth(240);
        tablaTransacciones.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaTransacciones.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaTransacciones.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaTransacciones.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaTransacciones.getColumnModel().getColumn(6).setPreferredWidth(280);

        
        /*if(panelCalendarioIni.getComponents().length == 0)
            panelCalendarioIni.add(calendarioIni);
        if(panelCalendarioFin.getComponents().length == 0)
            panelCalendarioFin.add(calendarioFin);*/

        campoTransaccion.setEditable(false);
        campoAPerturaCaja.setEditable(false);
        campoContrato.setEditable(false);
        campoContratoAnterior.setEditable(false);
        campoFecha.setEditable(false);
        campoMonto.setEditable(false);
        campoServicio.setEditable(false);
        campoTipoCobro.setEditable(false);

    }

    private void cargarTablaTransacciones(DefaultTableModel model, List<TransaccionTicketEntity> list) {

        if (!list.isEmpty()) {
            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (TransaccionTicketEntity e : list) {

                StringBuilder suscriptor = new StringBuilder();
                suscriptor.append(e.getNombre());
                if (e.getApellidoPaterno() != null) {
                    suscriptor.append(" ").append(e.getApellidoPaterno());
                }
                if (e.getApellidoMaterno() != null) {
                    suscriptor.append(" ").append(e.getApellidoMaterno());
                }

                model.addRow(new Object[]{
                    e.getTransaccionId(),
                    suscriptor,
                    e.getContratoId(),
                    e.getContratoAnteriorId() != null ? String.valueOf(e.getContratoAnteriorId()) : "",
                    e.getFechaTransaccion(),
                    e.getMonto(),
                    e.getDescripcionTipoCobro()
                });
            }
        }

    }

    private void cargarCOmboTiposCobro() {

        List<TipoCobro> tiposCobro = new ArrayList<>();
        tiposCobro.add(new TipoCobro(2, "MENSUALIDAD SERVICIO"));
        tiposCobro.add(new TipoCobro(1, "ORDEN INSTALACION"));
        tiposCobro.add(new TipoCobro(3, "ORDEN DE SERVICIO"));
        tiposCobro.add(new TipoCobro(4, "ORDEN DE CAMBIO DE DOMICILIO"));
        //tiposCobro.add(new TipoCobro(5, "RECARGO EN MENSUALIDAD DE SERVICIO"));
        tiposCobro.forEach(t -> comboTipoOperacion.addItem(t));

    }

    private void cargarDatosTransaccion(TransaccionTicketEntity entity) {
        
        transaccionSeleccionada = entity;
        campoTransaccion.setText(String.valueOf(entity.getTransaccionId()));
        campoAPerturaCaja.setText(String.valueOf(entity.getAperturaCajaId()));
        campoContrato.setText(String.valueOf(entity.getContratoId()));
        if (entity.getContratoAnteriorId() != null) {
            campoContratoAnterior.setText(String.valueOf(entity.getContratoAnteriorId()));
        }
        campoFecha.setText(entity.getFechaTransaccion());
        campoMonto.setText(String.valueOf(entity.getMonto()));
        campoServicio.setText(entity.getServicio());
        campoTipoCobro.setText(entity.getDescripcionTipoCobro());

    }

    private void limpiarDatosTransaccion() {
        transaccionSeleccionada = null;
        campoTransaccion.setText("");
        campoAPerturaCaja.setText("");
        campoContrato.setText("");
        campoContratoAnterior.setText("");
        campoFecha.setText("");
        campoMonto.setText("");
        campoServicio.setText("");
        campoTipoCobro.setText("");
    }

    private void limpiarPantalla() {

        limpiarDatosTransaccion();
        DefaultTableModel model = (DefaultTableModel) tablaTransacciones.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
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
        jLabel10 = new javax.swing.JLabel();
        etiquetaNumeroCaja = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        etiquetaSucursal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        etiquetaUsuario = new javax.swing.JLabel();
        panelFiltros = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comboTipoOperacion = new javax.swing.JComboBox<>();
        panelCalendarioIni = new javax.swing.JPanel();
        botonBusqueda = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        panelCalendarioFin = new javax.swing.JPanel();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTransacciones = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        botonRegresar = new javax.swing.JButton();
        panelDatosTransaccion = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        campoTransaccion = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        campoAPerturaCaja = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        campoContrato = new javax.swing.JTextField();
        campoContratoAnterior = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        campoFecha = new javax.swing.JTextField();
        campoMonto = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        campoServicio = new javax.swing.JTextField();
        campoTipoCobro = new javax.swing.JTextField();
        botonImprimir = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1500, 800));
        setMinimumSize(new java.awt.Dimension(1500, 800));
        setPreferredSize(new java.awt.Dimension(1500, 800));

        panelCabecero.setBackground(new java.awt.Color(255, 255, 255));
        panelCabecero.setPreferredSize(new java.awt.Dimension(1500, 50));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("Caja Número:");

        etiquetaNumeroCaja.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaNumeroCaja.setText("0");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setText("Sucursal:");

        etiquetaSucursal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaSucursal.setText("TV");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("Usuario:");

        etiquetaUsuario.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaUsuario.setText("User");

        javax.swing.GroupLayout panelCabeceroLayout = new javax.swing.GroupLayout(panelCabecero);
        panelCabecero.setLayout(panelCabeceroLayout);
        panelCabeceroLayout.setHorizontalGroup(
            panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceroLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(426, 426, 426)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(etiquetaSucursal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(96, 96, 96))
        );
        panelCabeceroLayout.setVerticalGroup(
            panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(etiquetaUsuario))
                    .addGroup(panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(etiquetaSucursal))
                    .addGroup(panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(etiquetaNumeroCaja)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        panelFiltros.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Selecciona Tipo de Operación:");

        javax.swing.GroupLayout panelCalendarioIniLayout = new javax.swing.GroupLayout(panelCalendarioIni);
        panelCalendarioIni.setLayout(panelCalendarioIniLayout);
        panelCalendarioIniLayout.setHorizontalGroup(
            panelCalendarioIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 187, Short.MAX_VALUE)
        );
        panelCalendarioIniLayout.setVerticalGroup(
            panelCalendarioIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        botonBusqueda.setBackground(new java.awt.Color(227, 126, 75));
        botonBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonBusqueda.setForeground(new java.awt.Color(255, 255, 255));
        botonBusqueda.setText("Buscar");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("De:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("a:");

        javax.swing.GroupLayout panelCalendarioFinLayout = new javax.swing.GroupLayout(panelCalendarioFin);
        panelCalendarioFin.setLayout(panelCalendarioFinLayout);
        panelCalendarioFinLayout.setHorizontalGroup(
            panelCalendarioFinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 175, Short.MAX_VALUE)
        );
        panelCalendarioFinLayout.setVerticalGroup(
            panelCalendarioFinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(comboTipoOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(panelCalendarioIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(panelCalendarioFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(106, 106, 106)
                .addComponent(botonBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltrosLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelCalendarioIni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonBusqueda, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(comboTipoOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(panelCalendarioFin, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, 18))
        );

        panelTabla.setBackground(new java.awt.Color(255, 255, 255));

        tablaTransacciones.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaTransacciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Folio", "Suscriptor", "Contrato", "Contrato Anterior", "Fecha", "Monto", "Tipo Operacion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaTransacciones.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tablaTransacciones);
        if (tablaTransacciones.getColumnModel().getColumnCount() > 0) {
            tablaTransacciones.getColumnModel().getColumn(0).setResizable(false);
            tablaTransacciones.getColumnModel().getColumn(1).setResizable(false);
            tablaTransacciones.getColumnModel().getColumn(2).setResizable(false);
            tablaTransacciones.getColumnModel().getColumn(3).setResizable(false);
            tablaTransacciones.getColumnModel().getColumn(4).setResizable(false);
            tablaTransacciones.getColumnModel().getColumn(5).setResizable(false);
            tablaTransacciones.getColumnModel().getColumn(6).setResizable(false);
        }

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        panelDatosTransaccion.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Folio Transaccion:");

        campoTransaccion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Apertura Caja:");

        campoAPerturaCaja.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Contrato:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Contrato Anterior:");

        campoContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        campoContratoAnterior.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Fecha:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Monto:");

        campoFecha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        campoMonto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Servicio:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Tipo de Cobro:");

        campoServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        campoTipoCobro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonImprimir.setBackground(new java.awt.Color(0, 153, 51));
        botonImprimir.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonImprimir.setForeground(new java.awt.Color(255, 255, 255));
        botonImprimir.setText("Imprimir");

        javax.swing.GroupLayout panelDatosTransaccionLayout = new javax.swing.GroupLayout(panelDatosTransaccion);
        panelDatosTransaccion.setLayout(panelDatosTransaccionLayout);
        panelDatosTransaccionLayout.setHorizontalGroup(
            panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosTransaccionLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoAPerturaCaja, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addComponent(campoTransaccion))
                .addGap(18, 18, 18)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoContrato, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addComponent(campoContratoAnterior))
                .addGap(18, 18, 18)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(campoMonto))
                .addGap(18, 18, 18)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoServicio)
                    .addComponent(campoTipoCobro, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
                .addGap(54, 54, 54)
                .addComponent(botonImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosTransaccionLayout.setVerticalGroup(
            panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosTransaccionLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(campoTransaccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(campoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(campoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonImprimir))
                .addGap(35, 35, 35)
                .addGroup(panelDatosTransaccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(campoAPerturaCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(campoContratoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(campoMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(campoTipoCobro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCabecero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelDatosTransaccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelTabla, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatosTransaccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonBusqueda;
    private javax.swing.JButton botonImprimir;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoAPerturaCaja;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoContratoAnterior;
    private javax.swing.JTextField campoFecha;
    private javax.swing.JTextField campoMonto;
    private javax.swing.JTextField campoServicio;
    private javax.swing.JTextField campoTipoCobro;
    private javax.swing.JTextField campoTransaccion;
    private javax.swing.JComboBox<TipoCobro> comboTipoOperacion;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelCalendarioFin;
    private javax.swing.JPanel panelCalendarioIni;
    private javax.swing.JPanel panelDatosTransaccion;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JTable tablaTransacciones;
    // End of variables declaration//GEN-END:variables

}
