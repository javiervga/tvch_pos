/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import mx.com.tvch.pos.config.DbConfig;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.util.VentanaEnum;
import static mx.com.tvch.pos.util.VentanaEnum.APERTURA;
import static mx.com.tvch.pos.util.VentanaEnum.COBRO_SERVICIO;

/**
 *
 * @author fvega
 */
public class PosFrame extends javax.swing.JFrame {

    private final LoginPanel loginPanel;
    private final MenuPanel menuPanel;
    private final LoadingPanel loadingPanel;
    private final AperturaCajaPanel aperturaCajaPanel;
    private final SalidaCajaPanel salidaCajaPanel;
    private final CobroServicioPanel cobroServicioPanel;
    private final CobroOrdenPanel cobroOrdenPanel;
    private final DbConfig dbConfig;
    private Sesion sesion;

    boolean estaLoadingActivo = false;

    /**
     * Creates new form PosFrame
     */
    public PosFrame() {
        initComponents();

        loginPanel = LoginPanel.getLoginPanel(this);
        menuPanel = MenuPanel.getMenuPanel(this);
        aperturaCajaPanel = AperturaCajaPanel.getAperturaCajaPanel(this);
        salidaCajaPanel = SalidaCajaPanel.getSalidaCajaPanel(this);
        cobroServicioPanel = CobroServicioPanel.getCobroPanel(this);
        cobroOrdenPanel = CobroOrdenPanel.getCobroOrdenPanel(this);
        loadingPanel = LoadingPanel.getLoadingPanel();
        dbConfig = DbConfig.getdDbConfig();
        sesion = Sesion.getSesion();

        //loginPanel.setVisible(true);
        //menuPanel.setVisible(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.add(loginPanel, BorderLayout.CENTER);
        //this.add(menuPanel, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        
        if(dbConfig.existeConectorMySql()){  
            crearEventos();
        }else{
            JOptionPane.showMessageDialog(rootPane, "No se ha detecto Driver MySql instalado en el equipo. "
                    + "\n antes de continuar, verifique su instalación de la Base de Datos");
        }

        System.out.println("hashcode: " + this.hashCode());
    }
    
    private void crearEventos(){
        
    }

    public void cargarMenuPrincipal() {

        this.remove(loginPanel);
        this.revalidate();
        this.repaint();
        //this.pack();

        //this.setResizable(false);
        //this.setLayout(new BorderLayout());
        this.add(menuPanel);
        this.revalidate();
        this.repaint();

    }

    public void cambiarPantalla(JPanel panel, VentanaEnum nuevaVentana) {

        this.remove(panel);

        switch (nuevaVentana) {
            case LOADING:
                this.add(loadingPanel);
                break;
            case MENU:
                this.add(menuPanel);
                menuPanel.cargarDatosSesion();
                break;
            case COBRO_SERVICIO:
                this.add(cobroServicioPanel);
                cobroServicioPanel.cargarDatosSesion();
                break;
            case COBRO_ORDEN:
                this.add(cobroOrdenPanel);
                cobroOrdenPanel.cargarDatosSesion();
                break;
            case APERTURA:
                this.add(aperturaCajaPanel);
                aperturaCajaPanel.cargarDatosSesion();
                break;
            case SALIDA:
                this.add(salidaCajaPanel);
                salidaCajaPanel.cargarDatosSesion();
                break;
            case CORTE:
                break;
            case BUSQUEDA:
                break;
            case REIMPRESION:
                break;
            case LOGIN:
                sesion = null;
                loginPanel.limpiarPantalla();
                this.add(loginPanel);
                break;
            default:
                sesion = null;
                loginPanel.limpiarPantalla();
                this.add(loginPanel);
                break;
        }

        this.revalidate();
        this.repaint();

    }

    public void mostrarLoading() {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                estaLoadingActivo = true;
                add(loadingPanel, 0);
                revalidate();
                repaint();
                //}this.setEnabled(false);
                //this.paintAll(this.getGraphics());
            }
        });

        

    }

    public void ocultarLoading() {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (estaLoadingActivo) {
                    remove(loadingPanel);
                    revalidate();
                    repaint();
                    //this.setEnabled(true);
                    estaLoadingActivo = false;
                }
            }
        });

        

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tv Cable Hidalguense - Punto de Venta ");
        setMaximumSize(new java.awt.Dimension(1500, 900));
        setMinimumSize(new java.awt.Dimension(1500, 900));
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1500, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PosFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PosFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
