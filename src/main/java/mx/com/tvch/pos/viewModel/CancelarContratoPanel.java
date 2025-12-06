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
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CancelarContratoController;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.MotivoCancelacionEntity;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.model.TipoBusquedaCobro;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CancelarContratoPanel extends javax.swing.JPanel {

    private static CancelarContratoPanel cancelacionPanel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final CancelarContratoController controller;
    private final Utilerias util;
    private final Impresora impresora;

    private ContratoxSuscriptorDetalleEntity suscriptorSeleccionado;
    private List<DetallePagoServicio> listaDetallesPago;

    org.slf4j.Logger logger = LoggerFactory.getLogger(CancelarContratoPanel.class);

    public static CancelarContratoPanel getCobroPanel(PosFrame frame) {
        if (cancelacionPanel == null) {
            cancelacionPanel = new CancelarContratoPanel();
        }
        posFrame = frame;
        return cancelacionPanel;
    }

    /**
     * Creates new form CobroPanel
     */
    public CancelarContratoPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = CancelarContratoController.getCancelarContratoController();
        util = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        listaDetallesPago = new ArrayList<>();

        crearEventos();
        cargarComboMotivosCancelacion();
    }


    private void crearEventos() {
        

        ActionListener botonCobrarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (suscriptorSeleccionado != null && !listaDetallesPago.isEmpty()) {
                 
                    //if(!campoObservaciones.getText().isEmpty()){
                        if(!areaObservaciones.getText().isEmpty()){
                            try {
                            
                                MotivoCancelacionEntity motivoCancelacionEntity = (MotivoCancelacionEntity) comboMotivosCancelacion.getModel().getSelectedItem();
                            
                                Long transaccionId = controller.cobrarCancelacion(suscriptorSeleccionado, listaDetallesPago, motivoCancelacionEntity.getMotivoId(), areaObservaciones.getText());
                                try {
                                    impresora.imprimirTicketCancelacion(transaccionId, listaDetallesPago, suscriptorSeleccionado, sesion.getSucursal());
                                } catch (Exception ex) {
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    ex.printStackTrace(pw);
                                    logger.error("Fallo al imprimir ticket de transaccion: \n" + sw.toString());
                                    JOptionPane.showMessageDialog(cancelacionPanel, "El cobro se realizó correctamente pero ocurrió un error al imprimir su ticket. Si desea una rempresión vaya a sección de reimpresiones", "", JOptionPane.WARNING_MESSAGE);
                                }
                                System.out.println("transaccionId: " + transaccionId);
                                limpiarPantalla();
                                
                                posFrame.cambiarPantalla(cancelacionPanel, VentanaEnum.CONSULTA_CONTRATOS);
                            } catch (Exception ex) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                ex.printStackTrace(pw);
                                logger.error("Fallo al cobrar transaccion: \n" + sw.toString());
                                JOptionPane.showMessageDialog(cancelacionPanel, "Ocurrió un error al realizar el cobro, por favor reintente. Si el problema persiste consulte a soporte.", "", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JOptionPane.showMessageDialog(cancelacionPanel, "Observaciones deben tener un maximo de 100 caracteres", "", JOptionPane.WARNING_MESSAGE);
                        }
                    //}
                }else{
                    JOptionPane.showMessageDialog(cancelacionPanel, "Para realizar la cancelacion es necesario que capture sus observaciones", "", JOptionPane.WARNING_MESSAGE);
                }

            }
        };
        botonCobrar.addActionListener(botonCobrarActionListener);

        ActionListener botonReestablecerMontoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reestablecerMontoSugerido();
            }
        };
        botonRestablecerMonto.addActionListener(botonReestablecerMontoActionListener);

        ActionListener botonAplicarMontoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (suscriptorSeleccionado != null &&
                        listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_CANCELACION).findAny().isPresent()) {
                
                    Double montoIngresado = null;
                    try{
                        montoIngresado = Double.parseDouble(campoMontoCancelacion.getText());
                    }catch(Exception ex){
                    
                    }
 
                    if(montoIngresado != null && montoIngresado >= 0){

                        listaDetallesPago
                                .stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_CANCELACION)
                                .findFirst()
                                .get()
                                .setMonto(montoIngresado);
                            
                        actualizarTablaDetallesPago();
                        etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));
                        campoMontoCancelacion.setText("");

                    }else{
                        JOptionPane.showMessageDialog(cancelacionPanel, "Por favor, ingrese un monto numérico", "", JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        };
        botonAplicarMonto.addActionListener(botonAplicarMontoActionListener);


      

        ActionListener botonRegresarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPantalla();
                posFrame.cambiarPantalla(cancelacionPanel, VentanaEnum.CONSULTA_CONTRATOS);
            }
        };
        botonRegresar.addActionListener(botonRegresarActionListener);

      
  

    }
    
    private void reestablecerMontoSugerido(){
        if (suscriptorSeleccionado != null &&
                        listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_CANCELACION).findAny().isPresent()) {

                    Double montoPorDia = controller.calcularMontoPorDia(suscriptorSeleccionado);
                    Double montoTotalCancelacion = util.redondearMonto(controller.obtenerMontoCancelacion(suscriptorSeleccionado, montoPorDia));
                    
                    listaDetallesPago
                                .stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_CANCELACION)
                                .findFirst()
                                .get()
                                .setMonto(montoTotalCancelacion);
                    actualizarTablaDetallesPago();
                    etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));

                }
    }

    /**
     * 
     * @param contratosuscriptor 
     */
    private void cargarDatosSuscriptor(ContratoxSuscriptorDetalleEntity contratosuscriptor) {

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
        
        Double montoPorDia = util.redondearMonto(controller.calcularMontoPorDia(suscriptorSeleccionado));
        Double montoTotalCancelacion = util.redondearMonto(controller.obtenerMontoCancelacion(suscriptorSeleccionado, montoPorDia));

        DetallePagoServicio detalleMontoPago = new DetallePagoServicio();
        StringBuilder conceptoMontoPago = new StringBuilder();
        conceptoMontoPago.append("Pago Cancelacion ");
        conceptoMontoPago.append(" ");
        conceptoMontoPago.append(suscriptorSeleccionado.getServicio());
        conceptoMontoPago.append(" >> $");
        conceptoMontoPago.append(suscriptorSeleccionado.getCostoServicio());
        conceptoMontoPago.append(" por mes");
        conceptoMontoPago.append(" >> $");
        conceptoMontoPago.append(montoPorDia);
        conceptoMontoPago.append(" por día");
        detalleMontoPago.setConcepto(conceptoMontoPago.toString());
        
        detalleMontoPago.setMonto(montoTotalCancelacion);
        detalleMontoPago.setCadenaMonto("  $".concat(String.valueOf(montoTotalCancelacion)));
        detalleMontoPago.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_CANCELACION);
        listaDetallesPago.add(detalleMontoPago);

        actualizarTablaDetallesPago();
        etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));

    }

    private void actualizarTablaDetallesPago() {

        DefaultTableModel model = (DefaultTableModel) tablaDetallesPago.getModel();
        model.getDataVector().clear();
        for (DetallePagoServicio o : listaDetallesPago) {
            model.addRow(new Object[]{
                o.getConcepto(),
                o.getCadenaMonto()});
        }
        model.fireTableDataChanged();

    }

    private void limpiarDatosSuscriptor() {
        listaDetallesPago.clear();
        suscriptorSeleccionado = null;
        campoSuscriptor.setText("");
        campoContrato.setText("");
        campoFolioContrato.setText("");
        campoEstatus.setText("");
        campoFechaPago.setText("");
        campoServicioContratado.setText("");
        campoDomicilio.setText("");
        campoTelefono.setText("");
        DefaultTableModel model = (DefaultTableModel) tablaDetallesPago.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
        etiquetaImporte.setText("0.00");
    }


    private void limpiarPantalla() {
        limpiarDatosSuscriptor();

        etiquetaImporte.setText("0.00");
        areaObservaciones.setText("");
        limpiarDatosSuscriptor();
    }

    public void cargarDatosSesion() {
        
        cargarDatosSuscriptor(sesion.getContratoSeleccionado());

        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        
        tablaDetallesPago.getColumnModel().getColumn(0).setPreferredWidth(750);
        //tablaSuscriptores.gets

        //ImageIcon imagen = new ImageIcon("src/main/resources/logo_grande.jpg");
        //Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(/*etiquetaLogo.getWidth(), etiquetaLogo.getHeight()*/320, 130, Image.SCALE_DEFAULT));
        //etiquetaLogo.setIcon(icono);
        
        areaObservaciones.setLineWrap(true);
        areaObservaciones.setRows(4);

        etiquetaImporte.setText("0.00");
        
        reestablecerMontoSugerido();
    }


    
    private void cargarComboMotivosCancelacion() {

        try {

            List<MotivoCancelacionEntity> list = controller.consultarMotivosCancelacion();
            list.forEach(e -> comboMotivosCancelacion.addItem(e));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cancelacionPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }

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
        jLabel19 = new javax.swing.JLabel();
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
        panelPromociones = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        botonAplicarMonto = new javax.swing.JButton();
        botonRestablecerMonto = new javax.swing.JButton();
        campoMontoCancelacion = new javax.swing.JTextField();
        comboMotivosCancelacion = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaObservaciones = new javax.swing.JTextArea();
        panelInfoPago = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDetallesPago = new javax.swing.JTable();
        panelImportes = new javax.swing.JPanel();
        etiquetaPesos = new javax.swing.JLabel();
        etiquetaImporte = new javax.swing.JLabel();
        botonCobrar = new javax.swing.JButton();
        botonRegresar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

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

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel19.setText("Cancelaciones de Servicio");

        javax.swing.GroupLayout panelCabeceroLayout = new javax.swing.GroupLayout(panelCabecero);
        panelCabecero.setLayout(panelCabeceroLayout);
        panelCabeceroLayout.setHorizontalGroup(
            panelCabeceroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceroLayout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(207, 207, 207)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(etiquetaSucursal)
                .addGap(268, 268, 268)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
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
                    .addComponent(jLabel19))
                .addGap(0, 5, Short.MAX_VALUE))
        );

        panelInfoContrato.setBackground(new java.awt.Color(255, 255, 255));

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

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Fecha de Pago:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Teléfono:");

        campoFechaPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoFechaPago.setForeground(java.awt.Color.red);

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelInfoContratoLayout = new javax.swing.GroupLayout(panelInfoContrato);
        panelInfoContrato.setLayout(panelInfoContratoLayout);
        panelInfoContratoLayout.setHorizontalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoServicioContratado, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(campoSuscriptor))
                .addGap(18, 18, 18)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(campoFolioContrato, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(99, 99, 99)
                        .addComponent(jLabel14)
                        .addGap(55, 55, 55)
                        .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(55, Short.MAX_VALUE))))
        );
        panelInfoContratoLayout.setVerticalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31))
        );

        panelPromociones.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 51, 51));
        jLabel16.setText("Captura de Monto");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel17.setText("Ingrese monto a cobrar:");

        botonAplicarMonto.setBackground(new java.awt.Color(227, 126, 75));
        botonAplicarMonto.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        botonAplicarMonto.setForeground(new java.awt.Color(255, 255, 255));
        botonAplicarMonto.setText("Aplicar Monto");

        botonRestablecerMonto.setBackground(java.awt.Color.red);
        botonRestablecerMonto.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        botonRestablecerMonto.setForeground(new java.awt.Color(255, 255, 255));
        botonRestablecerMonto.setText("Reestablecer Monto Sugerido");
        botonRestablecerMonto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRestablecerMontoActionPerformed(evt);
            }
        });

        campoMontoCancelacion.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        comboMotivosCancelacion.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel15.setText("Seleccione Motivo:");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel18.setText("Observaciones Orden Cancelacion:");

        areaObservaciones.setColumns(20);
        areaObservaciones.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        areaObservaciones.setRows(5);
        jScrollPane1.setViewportView(areaObservaciones);

        javax.swing.GroupLayout panelPromocionesLayout = new javax.swing.GroupLayout(panelPromociones);
        panelPromociones.setLayout(panelPromocionesLayout);
        panelPromocionesLayout.setHorizontalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(campoMontoCancelacion)
                            .addComponent(comboMotivosCancelacion, 0, 300, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelPromocionesLayout.createSequentialGroup()
                                .addComponent(botonAplicarMonto)
                                .addGap(47, 47, 47)
                                .addComponent(botonRestablecerMonto))
                            .addGroup(panelPromocionesLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(35, 35, 35)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(300, 300, 300))))
        );
        panelPromocionesLayout.setVerticalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPromocionesLayout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(campoMontoCancelacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(botonAplicarMonto)
                        .addComponent(botonRestablecerMonto)))
                .addGap(34, 34, 34)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboMotivosCancelacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)))
                    .addComponent(jLabel18)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelInfoPago.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        tablaDetallesPago.setBackground(new java.awt.Color(227, 126, 75));
        tablaDetallesPago.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tablaDetallesPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Concepto", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaDetallesPago.setAutoscrolls(false);
        tablaDetallesPago.setMaximumSize(new java.awt.Dimension(195, 160));
        tablaDetallesPago.setMinimumSize(new java.awt.Dimension(195, 160));
        tablaDetallesPago.setRowHeight(40);
        tablaDetallesPago.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tablaDetallesPago);
        if (tablaDetallesPago.getColumnModel().getColumnCount() > 0) {
            tablaDetallesPago.getColumnModel().getColumn(0).setResizable(false);
            tablaDetallesPago.getColumnModel().getColumn(0).setPreferredWidth(120);
            tablaDetallesPago.getColumnModel().getColumn(1).setResizable(false);
        }

        panelImportes.setBackground(new java.awt.Color(255, 255, 255));

        etiquetaPesos.setFont(new java.awt.Font("Segoe UI", 0, 80)); // NOI18N
        etiquetaPesos.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaPesos.setText("$");

        etiquetaImporte.setFont(new java.awt.Font("Segoe UI", 0, 80)); // NOI18N
        etiquetaImporte.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaImporte.setText("0.00");

        botonCobrar.setBackground(new java.awt.Color(0, 153, 51));
        botonCobrar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonCobrar.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrar.setText("Cobrar");

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        javax.swing.GroupLayout panelImportesLayout = new javax.swing.GroupLayout(panelImportes);
        panelImportes.setLayout(panelImportesLayout);
        panelImportesLayout.setHorizontalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImportesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelImportesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(etiquetaPesos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelImportesLayout.setVerticalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelImportesLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaPesos, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonRegresar))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout panelInfoPagoLayout = new javax.swing.GroupLayout(panelInfoPago);
        panelInfoPago.setLayout(panelInfoPagoLayout);
        panelInfoPagoLayout.setHorizontalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1023, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(panelImportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelInfoPagoLayout.setVerticalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelImportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(143, 143, 143))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setForeground(java.awt.Color.red);
        jLabel1.setText("Monto Sugerido por sistema:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, 1487, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void botonRestablecerMontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRestablecerMontoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonRestablecerMontoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaObservaciones;
    private javax.swing.JButton botonAplicarMonto;
    private javax.swing.JButton botonCobrar;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JButton botonRestablecerMonto;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoDomicilio;
    private javax.swing.JTextField campoEstatus;
    private javax.swing.JTextField campoFechaPago;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoMontoCancelacion;
    private javax.swing.JTextField campoServicioContratado;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JComboBox<MotivoCancelacionEntity> comboMotivosCancelacion;
    private javax.swing.JLabel etiquetaImporte;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelImportes;
    private javax.swing.JPanel panelInfoContrato;
    private javax.swing.JPanel panelInfoPago;
    private javax.swing.JPanel panelPromociones;
    private javax.swing.JTable tablaDetallesPago;
    // End of variables declaration//GEN-END:variables
}
