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
        
        ActionListener botonCobrarListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //posFrame.cambiarPantalla(menuPanel, VentanaEnum.LOADING);
                posFrame.mostrarLoading();
                /*try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MenuPanel.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                //posFrame.ocultarLoading();
            }
        };
        botonCobrar.addActionListener(botonCobrarListener);
        
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
        botonAbrirCaja = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        botonCobrar = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        botonSalir = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        botonRegistrarSalida = new javax.swing.JButton();
        etiquetaLogo = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        etiquetaSucursal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1500, 900));
        setMinimumSize(new java.awt.Dimension(1500, 900));
        setPreferredSize(new java.awt.Dimension(1500, 900));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Caja Número:");

        etiquetaNumeroCaja.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaNumeroCaja.setText("0");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Usuario:");

        etiquetaUsuario.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaUsuario.setText("User");

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

        jButton2.setBackground(new java.awt.Color(163, 73, 164));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Hacer Corte");
        jButton2.setPreferredSize(new java.awt.Dimension(75, 25));

        botonCobrar.setBackground(new java.awt.Color(163, 73, 164));
        botonCobrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonCobrar.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrar.setText("Cobrar");
        botonCobrar.setPreferredSize(new java.awt.Dimension(75, 25));

        jButton4.setBackground(new java.awt.Color(163, 73, 164));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Búsqueda de suscriptor");
        jButton4.setPreferredSize(new java.awt.Dimension(75, 25));

        botonSalir.setBackground(new java.awt.Color(255, 51, 0));
        botonSalir.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        botonSalir.setForeground(new java.awt.Color(255, 255, 255));
        botonSalir.setText("Salir");
        botonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSalirActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(163, 73, 164));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Reimprimir Ticket");
        jButton6.setPreferredSize(new java.awt.Dimension(75, 25));

        jButton7.setBackground(new java.awt.Color(227, 176, 75));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Cobrar diferente sucursal");
        jButton7.setPreferredSize(new java.awt.Dimension(75, 25));

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

        etiquetaLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen_menu.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Sucursal:");

        etiquetaSucursal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaSucursal.setText("TV");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(etiquetaLogo)
                        .addGap(143, 143, 143)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(190, 190, 190)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(159, 159, 159)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(467, 467, 467)
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(etiquetaSucursal)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(397, 397, 397)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(botonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(162, 162, 162)
                                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(317, 317, 317)
                                .addComponent(botonAbrirCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(209, 209, 209)
                                .addComponent(botonRegistrarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(252, 252, 252)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(botonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(botonRegistrarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(etiquetaNumeroCaja)
                                    .addComponent(jLabel3)
                                    .addComponent(etiquetaUsuario)
                                    .addComponent(jLabel2)
                                    .addComponent(etiquetaSucursal))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etiquetaLogo)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(botonAbrirCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78))))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAbrirCaja;
    private javax.swing.JButton botonCobrar;
    private javax.swing.JButton botonRegistrarSalida;
    private javax.swing.JButton botonSalir;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
