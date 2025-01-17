/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CorteCajaController;
import mx.com.tvch.pos.model.CorteCaja;
import mx.com.tvch.pos.model.DetalleCorte;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CorteCajaPanel extends javax.swing.JPanel {

    private static CorteCajaPanel panel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final Utilerias utilerias;
    private final Impresora impresora;
    private final CorteCajaController controller;
    private List<DetalleCorte> detallesCorteConsultados;

    Logger logger = LoggerFactory.getLogger(CorteCajaPanel.class);

    public static CorteCajaPanel getCorteCajaPanel(PosFrame frame) {
        if (panel == null) {
            panel = new CorteCajaPanel();
        }
        posFrame = frame;
        return panel;
    }

    /**
     * Creates new form CorteCajaPanel
     */
    public CorteCajaPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = CorteCajaController.getCorteCajaController();
        utilerias = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        detallesCorteConsultados = null;

        crearEventos();

    }

    private void crearEventos() {

        /**
         *
         */
        ActionListener botonRealizarCorteActionListener = (ActionEvent e) -> {
            if (detallesCorteConsultados != null && !detallesCorteConsultados.isEmpty()) {

                Double montoIngresado = null;
                try {
                    montoIngresado = Double.valueOf(campoEfectivo.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Por favor ingrese un monto correcto", "", JOptionPane.WARNING_MESSAGE);
                }
                
                if(montoIngresado != null && montoIngresado > 0){
                    
                    try {
                        CorteCaja corteCaja = controller.realizarCorte(detallesCorteConsultados, montoIngresado);
                        impresora.imprimirTicketCorteCaja(corteCaja.getDetallesCorte(), corteCaja);
                        posFrame.cambiarPantalla(panel, VentanaEnum.MENU);
                        limpiarPantalla();
                        JOptionPane.showMessageDialog(panel, "Corte realizado exitosamente", "", JOptionPane.INFORMATION_MESSAGE);
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                    }
                    
                }

            }
        };
        botonRealizarCorte.addActionListener(botonRealizarCorteActionListener);

        /**
         * *
         *
         */
        ActionListener botonRegresarActionListener = (ActionEvent e) -> {
            limpiarPantalla();
            posFrame.cambiarPantalla(panel, VentanaEnum.MENU);
        };
        botonRegresar.addActionListener(botonRegresarActionListener);

        ActionListener botonCalcularMontosActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    if (detallesCorteConsultados == null) {
                        List<DetalleCorte> detallesCorte = controller.consultarInformacionCorte();
                        if (!detallesCorte.isEmpty()) {
                            detallesCorteConsultados = detallesCorte;
                            cargarTablaDetallesCorte();
                        }
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Ocurrió un error al validar informacion", "", JOptionPane.WARNING_MESSAGE);
                }

            }
        };
        botonCalcularMontos.addActionListener(botonCalcularMontosActionListener);

    }

    private void cargarTablaDetallesCorte() {
        DefaultTableModel model = (DefaultTableModel) tablaDetallesCorte.getModel();
        model.getDataVector().clear();
        for (DetalleCorte o : detallesCorteConsultados) {
            model.addRow(new Object[]{
                o.getConcepto(),
                o.getCantidad(),
                o.getMontoCadena()});
        }
        model.fireTableDataChanged();
    }

    private void limpiarPantalla() {
        detallesCorteConsultados = null;
        DefaultTableModel model = (DefaultTableModel) tablaDetallesCorte.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
        campoEfectivo.setText("");
    }

    public void cargarDatosSesion() {
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

        panelCabecero = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        etiquetaNumeroCaja = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        etiquetaSucursal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        etiquetaUsuario = new javax.swing.JLabel();
        etiquetaLogo = new javax.swing.JLabel();
        etiquetaCorte = new javax.swing.JLabel();
        panelInformacion = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDetallesCorte = new javax.swing.JTable();
        botonRealizarCorte = new javax.swing.JButton();
        botonCalcularMontos = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        campoEfectivo = new javax.swing.JTextField();
        panelFooter = new javax.swing.JPanel();
        botonRegresar = new javax.swing.JButton();

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

        etiquetaLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen_menu.png"))); // NOI18N

        etiquetaCorte.setFont(new java.awt.Font("Segoe UI", 0, 40)); // NOI18N
        etiquetaCorte.setForeground(new java.awt.Color(163, 73, 164));
        etiquetaCorte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaCorte.setText("Corte de Caja");

        panelInformacion.setBackground(new java.awt.Color(255, 255, 255));

        tablaDetallesCorte.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tablaDetallesCorte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Concepto", "Operaciones", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaDetallesCorte.setRowHeight(40);
        tablaDetallesCorte.setRowSelectionAllowed(false);
        tablaDetallesCorte.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tablaDetallesCorte);
        if (tablaDetallesCorte.getColumnModel().getColumnCount() > 0) {
            tablaDetallesCorte.getColumnModel().getColumn(0).setResizable(false);
            tablaDetallesCorte.getColumnModel().getColumn(1).setResizable(false);
            tablaDetallesCorte.getColumnModel().getColumn(1).setPreferredWidth(18);
            tablaDetallesCorte.getColumnModel().getColumn(2).setResizable(false);
        }

        botonRealizarCorte.setBackground(new java.awt.Color(0, 153, 51));
        botonRealizarCorte.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        botonRealizarCorte.setForeground(new java.awt.Color(255, 255, 255));
        botonRealizarCorte.setText("Realizar Corte");

        botonCalcularMontos.setBackground(new java.awt.Color(163, 73, 164));
        botonCalcularMontos.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        botonCalcularMontos.setForeground(new java.awt.Color(255, 255, 255));
        botonCalcularMontos.setText("Validar Operaciones");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Ingrese Efectivo:");

        campoEfectivo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        panelFooter.setBackground(new java.awt.Color(255, 255, 255));

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        javax.swing.GroupLayout panelFooterLayout = new javax.swing.GroupLayout(panelFooter);
        panelFooter.setLayout(panelFooterLayout);
        panelFooterLayout.setHorizontalGroup(
            panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        panelFooterLayout.setVerticalGroup(
            panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelInformacionLayout = new javax.swing.GroupLayout(panelInformacion);
        panelInformacion.setLayout(panelInformacionLayout);
        panelInformacionLayout.setHorizontalGroup(
            panelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInformacionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelInformacionLayout.createSequentialGroup()
                        .addComponent(botonCalcularMontos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(campoEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(botonRealizarCorte))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 936, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelInformacionLayout.setVerticalGroup(
            panelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInformacionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonCalcularMontos)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(campoEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonRealizarCorte))
                .addGap(50, 50, 50)
                .addComponent(panelFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCabecero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiquetaCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(536, 536, 536))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiquetaLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiquetaCorte)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonCalcularMontos;
    private javax.swing.JButton botonRealizarCorte;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoEfectivo;
    private javax.swing.JLabel etiquetaCorte;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelFooter;
    private javax.swing.JPanel panelInformacion;
    private javax.swing.JTable tablaDetallesCorte;
    // End of variables declaration//GEN-END:variables
}
