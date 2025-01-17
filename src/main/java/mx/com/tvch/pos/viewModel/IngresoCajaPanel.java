/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.swing.JOptionPane;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.IngresoCajaController;
import mx.com.tvch.pos.entity.TipoIngresoEntity;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class IngresoCajaPanel extends javax.swing.JPanel {
    
    private static IngresoCajaPanel panel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final IngresoCajaController controller;
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(IngresoCajaPanel.class);
    
    public static  IngresoCajaPanel getIngresoCajaPanel(PosFrame frame){
        if(panel == null)
            panel = new IngresoCajaPanel();
        posFrame = frame;
        return panel;
    }

    /**
     * Creates new form IngresoCajaPanel
     */
    public IngresoCajaPanel() {
        initComponents();
        sesion = Sesion.getSesion();
        controller = IngresoCajaController.getIngresoCajaController();
        utilerias = Utilerias.getUtilerias();
        crearEventos();
    }
    
    public void crearEventos() {

        ActionListener botonRegresarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPantalla();
                posFrame.cambiarPantalla(panel, VentanaEnum.MENU);
            }
        };
        botonRegresar.addActionListener(botonRegresarActionListener);

        ActionListener botonAceptarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("lenghth: "+areaObservaciones.getText().length());
                if (areaObservaciones.getText().length() <= 100) {

                    if (comboTipoIngreso.getModel().getSelectedItem() != null) {

                        if (utilerias.esMontoValido(campoMontoSalida.getText(), false)) {

                            try {
                                
                                TipoIngresoEntity tipoIngreso = (TipoIngresoEntity) comboTipoIngreso.getModel().getSelectedItem();
                                controller.registrarIngresocaja(Double.parseDouble(campoMontoSalida.getText()), 
                                        tipoIngreso.getId(), areaObservaciones.getText());
                                posFrame.cambiarPantalla(panel, VentanaEnum.MENU);
                                JOptionPane.showMessageDialog(panel, "Su ingreso se ha registrado exitosamente", "", JOptionPane.INFORMATION_MESSAGE);

                            } catch (Exception ex) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                ex.printStackTrace(pw);
                                logger.error("Error al registrar ingreso de caja: " + sw.toString());
                                JOptionPane.showMessageDialog(panel, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                            }

                        } else {
                            JOptionPane.showMessageDialog(panel, "Por favor ingrese un monto de ingreso válido.", "", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel, "Por favor, seleccione un tipo de ingreso válido", "", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Observaciones deben tener un maximo de 100 caracteres", "", JOptionPane.WARNING_MESSAGE);
                }

            }
        };
        botonAceptar.addActionListener(botonAceptarActionListener);

    }
    
    private void limpiarPantalla(){
        campoMontoSalida.setText("");
        areaObservaciones.setText("");
    }
    
    private void cargarComboTiposSalida() {

        try {
            List<TipoIngresoEntity> list = controller.consultarTiposIngreso();
            list.forEach(ts -> comboTipoIngreso.addItem(ts));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }

    }
    
    public void cargarDatosSesion() {
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());

        campoUsuario.setText(sesion.getUsuario());
        campoCaja.setText(sesion.getNumeroCaja().toString());
        campoSucursal.setText(sesion.getSucursal());
        campoUsuario.setEnabled(false);
        campoCaja.setEnabled(false);
        campoSucursal.setEnabled(false);
        areaObservaciones.setLineWrap(true);
        areaObservaciones.setRows(4);
        cargarComboTiposSalida();
        //campoFondoFijo.setText("0");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel10 = new javax.swing.JLabel();
        etiquetaNumeroCaja = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        etiquetaSucursal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        etiquetaUsuario = new javax.swing.JLabel();
        etiquetaLogo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        campoCaja = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        campoSucursal = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        comboTipoIngreso = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        campoMontoSalida = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaObservaciones = new javax.swing.JTextArea();
        botonAceptar = new javax.swing.JButton();
        botonRegresar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        campoUsuario = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1500, 800));
        setMinimumSize(new java.awt.Dimension(1500, 800));
        setPreferredSize(new java.awt.Dimension(1500, 800));

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

        etiquetaLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen_menu.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(163, 73, 164));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Registro de Ingreso");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Caja:");

        campoCaja.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        campoCaja.setText("Caja");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Sucursal:");

        campoSucursal.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        campoSucursal.setText("Sucursal");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Tipo de salida:");

        comboTipoIngreso.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Monto:");

        campoMontoSalida.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        campoMontoSalida.setText("0");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText(" Observaciones:");

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        areaObservaciones.setColumns(20);
        areaObservaciones.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        areaObservaciones.setRows(5);
        jScrollPane1.setViewportView(areaObservaciones);

        botonAceptar.setBackground(new java.awt.Color(163, 73, 164));
        botonAceptar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonAceptar.setForeground(new java.awt.Color(255, 255, 255));
        botonAceptar.setText("Aceptar");

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Usuario:");

        campoUsuario.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        campoUsuario.setText("Usuario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(560, 560, 560)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(etiquetaSucursal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 353, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59))
            .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 1800, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(botonAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoMontoSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboTipoIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoSucursal)
                            .addComponent(campoCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(288, 288, 288))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(etiquetaNumeroCaja)
                    .addComponent(jLabel12)
                    .addComponent(etiquetaSucursal)
                    .addComponent(etiquetaUsuario)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addComponent(etiquetaLogo)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoUsuario)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoCaja)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoSucursal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTipoIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoMontoSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(27, 27, 27))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaObservaciones;
    private javax.swing.JButton botonAceptar;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JLabel campoCaja;
    private javax.swing.JTextField campoMontoSalida;
    private javax.swing.JLabel campoSucursal;
    private javax.swing.JLabel campoUsuario;
    private javax.swing.JComboBox<TipoIngresoEntity> comboTipoIngreso;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
