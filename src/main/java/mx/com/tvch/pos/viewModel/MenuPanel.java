/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.AperturaCajaController;
import mx.com.tvch.pos.entity.AperturaCajaEntity;
import mx.com.tvch.pos.util.VentanaEnum;

/**
 *
 * @author fvega
 */
public class MenuPanel extends javax.swing.JPanel {
    
    private static MenuPanel menuPanel;
    private static PosFrame posFrame;
    
    private final Sesion sesion;
    private final AperturaCajaController aperturaCajaController;
    
    public static MenuPanel getMenuPanel(PosFrame frame){
        if(menuPanel == null)
            menuPanel = new MenuPanel();
        posFrame = frame;
        return menuPanel;
    }

    /**
     * Creates new form MenuPanel
     */
    public MenuPanel() {
        initComponents();
        crearEventos();
        sesion = Sesion.getSesion();
        aperturaCajaController = AperturaCajaController.getAperturaCajaController();
    }
    
    private void crearEventos(){
        
        ActionListener botonSalirActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                posFrame.cambiarPantalla(menuPanel, VentanaEnum.LOGIN);
            }
        };
        botonSalir.addActionListener(botonSalirActionListener);
        
        ActionListener botonReimpresionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    AperturaCajaEntity entity = aperturaCajaController.obtenerAperturaCajaActiva();
                    if(entity != null){
                        if(entity.getUsuarioId().longValue() == sesion.getUsuarioId().longValue()){
                            sesion.setAperturaCajaId(entity.getAperturaCajaId());
                            posFrame.cambiarPantalla(menuPanel, VentanaEnum.REIMPRESION);
                        }else{
                            JOptionPane.showMessageDialog(menuPanel, "Se encontro una apertura de caja realizada por un usuario diferente, para registrar una salida debe ingresar con el usuario que realizo la apertura","", JOptionPane.WARNING_MESSAGE);
                        }
                    }else
                        JOptionPane.showMessageDialog(menuPanel, "No se encontro una apertura de caja activa, para registrar un ingreso primero debe abrir caja","", JOptionPane.WARNING_MESSAGE);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(menuPanel, ex.getMessage(),"", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        botonReimpresion.addActionListener(botonReimpresionListener);
        
        ActionListener botonRegistrarIngresoListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    AperturaCajaEntity entity = aperturaCajaController.obtenerAperturaCajaActiva();
                    if(entity != null){
                        if(entity.getUsuarioId().longValue() == sesion.getUsuarioId().longValue()){
                            sesion.setAperturaCajaId(entity.getAperturaCajaId());
                            posFrame.cambiarPantalla(menuPanel, VentanaEnum.INGRESO);
                        }else{
                            JOptionPane.showMessageDialog(menuPanel, "Se encontro una apertura de caja realizada por un usuario diferente, para registrar una salida debe ingresar con el usuario que realizo la apertura","", JOptionPane.WARNING_MESSAGE);
                        }
                    }else
                        JOptionPane.showMessageDialog(menuPanel, "No se encontro una apertura de caja activa, para registrar un ingreso primero debe abrir caja","", JOptionPane.WARNING_MESSAGE);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(menuPanel, ex.getMessage(),"", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        botonRegistrarIngreso.addActionListener(botonRegistrarIngresoListener);
        
        ActionListener botonCorteCajaListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    AperturaCajaEntity entity = aperturaCajaController.obtenerAperturaCajaActiva();
                    if(entity != null){
                        if(entity.getUsuarioId().longValue() == sesion.getUsuarioId().longValue()){
                            sesion.setAperturaCajaId(entity.getAperturaCajaId());
                            posFrame.cambiarPantalla(menuPanel, VentanaEnum.CORTE);
                        }else{
                            JOptionPane.showMessageDialog(menuPanel, "Se encontro una apertura de caja realizada por un usuario diferente, para realizar un corte de caja debe ingresar con el usuario que realizo la apertura","", JOptionPane.WARNING_MESSAGE);
                        }
                    }else
                        JOptionPane.showMessageDialog(menuPanel, "No se encontro una apertura de caja activa, para realizar un corte de caja primero debe abrir caja","", JOptionPane.WARNING_MESSAGE);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(menuPanel, ex.getMessage(),"", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        botonCorteCaja.addActionListener(botonCorteCajaListener);
        
        ActionListener botonCobrarOrdenListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //posFrame.mostrarLoading();
                try{
                    AperturaCajaEntity entity = aperturaCajaController.obtenerAperturaCajaActiva();
                    if(entity != null){
                        if(entity.getUsuarioId().longValue() == sesion.getUsuarioId().longValue()){
                            sesion.setAperturaCajaId(entity.getAperturaCajaId());
                            posFrame.cambiarPantalla(menuPanel, VentanaEnum.COBRO_ORDEN);
                        }else{
                            JOptionPane.showMessageDialog(menuPanel, "Se encontro una apertura de caja realizada por un usuario diferente, para realizar un cobro debe ingresar con el usuario que realizo la apertura","", JOptionPane.WARNING_MESSAGE);
                        }
                    }else
                        JOptionPane.showMessageDialog(menuPanel, "No se encontro una apertura de caja activa, para realizar un cobro primero debe abrir caja","", JOptionPane.WARNING_MESSAGE);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(menuPanel, ex.getMessage(),"", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        botonCobrarOrden.addActionListener(botonCobrarOrdenListener);
        
        ActionListener botonCobrarServicioListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //posFrame.mostrarLoading();
                try{
                    AperturaCajaEntity entity = aperturaCajaController.obtenerAperturaCajaActiva();
                    if(entity != null){
                        if(entity.getUsuarioId().longValue() == sesion.getUsuarioId().longValue()){
                            sesion.setAperturaCajaId(entity.getAperturaCajaId());
                            posFrame.cambiarPantalla(menuPanel, VentanaEnum.COBRO_SERVICIO);
                        }else{
                            JOptionPane.showMessageDialog(menuPanel, "Se encontro una apertura de caja realizada por un usuario diferente, para realizar un cobro debe ingresar con el usuario que realizo la apertura","", JOptionPane.WARNING_MESSAGE);
                        }
                    }else
                        JOptionPane.showMessageDialog(menuPanel, "No se encontro una apertura de caja activa, para realizar un cobro primero debe abrir caja","", JOptionPane.WARNING_MESSAGE);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(menuPanel, ex.getMessage(),"", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        botonCobrarServicio.addActionListener(botonCobrarServicioListener);
        
        ActionListener botonAbrirCajaListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    AperturaCajaEntity entity = aperturaCajaController.obtenerAperturaCajaActiva();
                    if(entity == null)
                        posFrame.cambiarPantalla(menuPanel, VentanaEnum.APERTURA);
                    else
                        JOptionPane.showMessageDialog(menuPanel, "Ya existe una apertura de caja activa, para realizar una nueva apertura primero debe realizar corte de caja","", JOptionPane.WARNING_MESSAGE);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(menuPanel, ex.getMessage(),"", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        botonAbrirCaja.addActionListener(botonAbrirCajaListener);
        
        ActionListener botonRegistrarSalidaListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    AperturaCajaEntity entity = aperturaCajaController.obtenerAperturaCajaActiva();
                    if(entity != null){
                        if(entity.getUsuarioId().longValue() == sesion.getUsuarioId().longValue()){
                            sesion.setAperturaCajaId(entity.getAperturaCajaId());
                            posFrame.cambiarPantalla(menuPanel, VentanaEnum.SALIDA);
                        }else{
                            JOptionPane.showMessageDialog(menuPanel, "Se encontro una apertura de caja realizada por un usuario diferente, para registrar una salida debe ingresar con el usuario que realizo la apertura","", JOptionPane.WARNING_MESSAGE);
                        }
                    }else
                        JOptionPane.showMessageDialog(menuPanel, "No se encontro una apertura de caja activa, para realizar una salida primero debe abrir caja","", JOptionPane.WARNING_MESSAGE);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(menuPanel, ex.getMessage(),"", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        botonRegistrarSalida.addActionListener(botonRegistrarSalidaListener);
        
    }
    
    public void cargarDatosSesion(){
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        
        botonCobrarOtraSucursal.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        etiquetaNumeroCaja = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        etiquetaUsuario = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        etiquetaSucursal = new javax.swing.JLabel();
        panelSuperior = new javax.swing.JPanel();
        botonRegistrarSalida = new javax.swing.JButton();
        botonRegistrarIngreso = new javax.swing.JButton();
        panelCentral = new javax.swing.JPanel();
        botonReimpresion = new javax.swing.JButton();
        etiquetaLogo = new javax.swing.JLabel();
        botonCobrarServicio = new javax.swing.JButton();
        panelInferior = new javax.swing.JPanel();
        botonSalir = new javax.swing.JButton();
        botonCobrarOtraSucursal = new javax.swing.JButton();
        botonAbrirCaja = new javax.swing.JButton();
        botonCorteCaja = new javax.swing.JButton();
        botonCobrarOrden = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1500, 800));
        setMinimumSize(new java.awt.Dimension(1500, 800));
        setPreferredSize(new java.awt.Dimension(1500, 800));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Caja Número:");

        etiquetaNumeroCaja.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaNumeroCaja.setText("0");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Usuario:");

        etiquetaUsuario.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaUsuario.setText("User");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Sucursal:");

        etiquetaSucursal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaSucursal.setText("TV");

        panelSuperior.setBackground(new java.awt.Color(255, 255, 255));
        panelSuperior.setMaximumSize(new java.awt.Dimension(1500, 250));
        panelSuperior.setMinimumSize(new java.awt.Dimension(1500, 250));
        panelSuperior.setPreferredSize(new java.awt.Dimension(1500, 250));

        botonRegistrarSalida.setBackground(new java.awt.Color(163, 73, 164));
        botonRegistrarSalida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonRegistrarSalida.setForeground(new java.awt.Color(255, 255, 255));
        botonRegistrarSalida.setText("Registrar Salida ");
        botonRegistrarSalida.setPreferredSize(new java.awt.Dimension(75, 25));
        botonRegistrarSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRegistrarSalidaActionPerformed(evt);
            }
        });

        botonRegistrarIngreso.setBackground(new java.awt.Color(163, 73, 164));
        botonRegistrarIngreso.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonRegistrarIngreso.setForeground(new java.awt.Color(255, 255, 255));
        botonRegistrarIngreso.setText("Registrar Ingreso ");
        botonRegistrarIngreso.setPreferredSize(new java.awt.Dimension(75, 25));
        botonRegistrarIngreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRegistrarIngresoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSuperiorLayout = new javax.swing.GroupLayout(panelSuperior);
        panelSuperior.setLayout(panelSuperiorLayout);
        panelSuperiorLayout.setHorizontalGroup(
            panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSuperiorLayout.createSequentialGroup()
                .addContainerGap(456, Short.MAX_VALUE)
                .addComponent(botonRegistrarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(134, 134, 134)
                .addComponent(botonRegistrarIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(434, 434, 434))
        );
        panelSuperiorLayout.setVerticalGroup(
            panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSuperiorLayout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonRegistrarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonRegistrarIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        panelCentral.setBackground(new java.awt.Color(255, 255, 255));
        panelCentral.setMaximumSize(new java.awt.Dimension(1500, 250));
        panelCentral.setMinimumSize(new java.awt.Dimension(1500, 250));
        panelCentral.setPreferredSize(new java.awt.Dimension(1500, 250));

        botonReimpresion.setBackground(new java.awt.Color(163, 73, 164));
        botonReimpresion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonReimpresion.setForeground(new java.awt.Color(255, 255, 255));
        botonReimpresion.setText("Reimprimir Ticket");
        botonReimpresion.setPreferredSize(new java.awt.Dimension(75, 25));
        botonReimpresion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonReimpresionActionPerformed(evt);
            }
        });

        etiquetaLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen_menu.png"))); // NOI18N

        botonCobrarServicio.setBackground(new java.awt.Color(163, 73, 164));
        botonCobrarServicio.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonCobrarServicio.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrarServicio.setText("Cobrar Servicio");
        botonCobrarServicio.setPreferredSize(new java.awt.Dimension(75, 25));

        javax.swing.GroupLayout panelCentralLayout = new javax.swing.GroupLayout(panelCentral);
        panelCentral.setLayout(panelCentralLayout);
        panelCentralLayout.setHorizontalGroup(
            panelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCentralLayout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addComponent(botonCobrarServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(etiquetaLogo)
                .addGap(62, 62, 62)
                .addComponent(botonReimpresion, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
        );
        panelCentralLayout.setVerticalGroup(
            panelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCentralLayout.createSequentialGroup()
                .addGroup(panelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCentralLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(botonReimpresion, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelCentralLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(etiquetaLogo)))
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCentralLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(botonCobrarServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        panelInferior.setBackground(new java.awt.Color(255, 255, 255));
        panelInferior.setMaximumSize(new java.awt.Dimension(1500, 250));
        panelInferior.setMinimumSize(new java.awt.Dimension(1500, 250));
        panelInferior.setPreferredSize(new java.awt.Dimension(1500, 250));

        botonSalir.setBackground(new java.awt.Color(255, 51, 0));
        botonSalir.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonSalir.setForeground(new java.awt.Color(255, 255, 255));
        botonSalir.setText("Salir");
        botonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSalirActionPerformed(evt);
            }
        });

        botonCobrarOtraSucursal.setBackground(new java.awt.Color(227, 176, 75));
        botonCobrarOtraSucursal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonCobrarOtraSucursal.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrarOtraSucursal.setText("Cobrar diferente sucursal");
        botonCobrarOtraSucursal.setPreferredSize(new java.awt.Dimension(75, 25));

        botonAbrirCaja.setBackground(new java.awt.Color(163, 73, 164));
        botonAbrirCaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonAbrirCaja.setForeground(new java.awt.Color(255, 255, 255));
        botonAbrirCaja.setText("Abrir caja");
        botonAbrirCaja.setPreferredSize(new java.awt.Dimension(75, 25));
        botonAbrirCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbrirCajaActionPerformed(evt);
            }
        });

        botonCorteCaja.setBackground(new java.awt.Color(163, 73, 164));
        botonCorteCaja.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonCorteCaja.setForeground(new java.awt.Color(255, 255, 255));
        botonCorteCaja.setText("Hacer Corte");
        botonCorteCaja.setPreferredSize(new java.awt.Dimension(75, 25));

        botonCobrarOrden.setBackground(new java.awt.Color(227, 176, 75));
        botonCobrarOrden.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonCobrarOrden.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrarOrden.setText("Cobrar Ordenes");
        botonCobrarOrden.setPreferredSize(new java.awt.Dimension(75, 25));

        javax.swing.GroupLayout panelInferiorLayout = new javax.swing.GroupLayout(panelInferior);
        panelInferior.setLayout(panelInferiorLayout);
        panelInferiorLayout.setHorizontalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInferiorLayout.createSequentialGroup()
                .addContainerGap(198, Short.MAX_VALUE)
                .addComponent(botonAbrirCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(134, 134, 134)
                .addComponent(botonCorteCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132)
                .addComponent(botonCobrarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(botonCobrarOtraSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );
        panelInferiorLayout.setVerticalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonAbrirCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonCorteCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonCobrarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonCobrarOtraSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(488, 488, 488)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(etiquetaSucursal)
                        .addGap(457, 457, 457)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48))
                    .addComponent(panelSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCentral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelInferior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(etiquetaNumeroCaja)
                    .addComponent(jLabel3)
                    .addComponent(etiquetaUsuario)
                    .addComponent(jLabel2)
                    .addComponent(etiquetaSucursal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCentral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void botonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSalirActionPerformed
        // TODO add your handling code here:
        
        
    }//GEN-LAST:event_botonSalirActionPerformed

    private void botonAbrirCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbrirCajaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonAbrirCajaActionPerformed

    private void botonRegistrarSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRegistrarSalidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonRegistrarSalidaActionPerformed

    private void botonReimpresionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonReimpresionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonReimpresionActionPerformed

    private void botonRegistrarIngresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRegistrarIngresoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonRegistrarIngresoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAbrirCaja;
    private javax.swing.JButton botonCobrarOrden;
    private javax.swing.JButton botonCobrarOtraSucursal;
    private javax.swing.JButton botonCobrarServicio;
    private javax.swing.JButton botonCorteCaja;
    private javax.swing.JButton botonRegistrarIngreso;
    private javax.swing.JButton botonRegistrarSalida;
    private javax.swing.JButton botonReimpresion;
    private javax.swing.JButton botonSalir;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel panelCentral;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelSuperior;
    // End of variables declaration//GEN-END:variables
}
