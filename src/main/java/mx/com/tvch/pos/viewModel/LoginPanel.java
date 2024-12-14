/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.LoginController;
import mx.com.tvch.pos.entity.CajaEntity;
import mx.com.tvch.pos.entity.SucursalEntity;
import mx.com.tvch.pos.entity.UsuarioEntity;
import mx.com.tvch.pos.util.VentanaEnum;

/**
 *
 * @author fvega
 */
public class LoginPanel extends javax.swing.JPanel {

    private static LoginPanel loginPanel;
    private static PosFrame posFrame;

    private final LoginController controller;
    private final Sesion sesion;

    public static LoginPanel getLoginPanel(PosFrame frame) {
        if (loginPanel == null) {
            loginPanel = new LoginPanel();
        }
        posFrame = frame;
        return loginPanel;
    }

    /**
     * Creates new form LoginPanel
     */
    public LoginPanel() {
        controller = LoginController.getLoginController();
        sesion = Sesion.getSesion();
        initComponents();
        crearEventos();
    }

    private void crearEventos() {

        ActionListener actionAccesarListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarUsuario();
            }
        };
        accesarButton.addActionListener(actionAccesarListener);

        KeyListener listenerClickBotonAceptar = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    autenticarUsuario();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        };
        accesarButton.addKeyListener(listenerClickBotonAceptar);

    }

    public void autenticarUsuario() {

        if (!campousuario.getText().isEmpty() && campoPassword.getPassword().length > 0) {

            posFrame.mostrarLoading();
            try {
                UsuarioEntity usuarioEntity = controller.autenticarUsuario(campousuario.getText(), String.valueOf(campoPassword.getPassword()));
                CajaEntity cajaEntity = controller.consultarCaja();
                SucursalEntity sucursalEntity = controller.consultarSucursal();
                generarSesion(usuarioEntity, cajaEntity, sucursalEntity);
                posFrame.cambiarPantalla(loginPanel, VentanaEnum.MENU);
                //posFrame.cargarMenuPrincipal();
            } catch (Exception ex) {
                campousuario.setText("");
                campoPassword.setText("");
                JOptionPane.showMessageDialog(loginPanel, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            }
            posFrame.ocultarLoading();

        } else {

        }

    }

    public void limpiarPantalla() {
        campoPassword.setText("");
        campousuario.setText("");
    }

    private void generarSesion(UsuarioEntity usuarioEntity, CajaEntity cajaEntity, SucursalEntity sucursalEntity) {

        sesion.setCajaId(cajaEntity.getCajaId());
        sesion.setNumeroCaja(cajaEntity.getNumero());
        sesion.setSucursal(sucursalEntity.getNombre());
        sesion.setSucursalId(sucursalEntity.getSucursalId());
        sesion.setUsuario(usuarioEntity.getUsuario());
        sesion.setUsuarioId(usuarioEntity.getUsuarioId());
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
        jLabel2 = new javax.swing.JLabel();
        campousuario = new javax.swing.JTextField();
        accesarButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        campoPassword = new javax.swing.JPasswordField();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1500, 900));
        setMinimumSize(new java.awt.Dimension(1500, 900));
        setPreferredSize(new java.awt.Dimension(1500, 900));

        jLabel1.setText("Usuario");

        jLabel2.setText("Contraseña");

        accesarButton.setBackground(new java.awt.Color(255, 102, 255));
        accesarButton.setForeground(new java.awt.Color(255, 255, 255));
        accesarButton.setText("Accesar");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo_grande.jpg"))); // NOI18N

        campoPassword.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 1130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(accesarButton, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(campousuario, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(campoPassword))
                .addContainerGap(187, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(208, 208, 208)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campousuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(accesarButton)
                .addContainerGap(545, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accesarButton;
    private javax.swing.JPasswordField campoPassword;
    private javax.swing.JTextField campousuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
