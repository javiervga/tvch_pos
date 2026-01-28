/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.EstatusOperacionesController;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.model.OperacionPendiente;
import mx.com.tvch.pos.model.TipoOperacion;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class EstatusOperacionesPanel extends javax.swing.JPanel {
    
    private static EstatusOperacionesPanel panel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final EstatusOperacionesController controller;
    private final Utilerias utilerias;
    
    Logger logger = LoggerFactory.getLogger(EstatusOperacionesPanel.class);
    
    public static  EstatusOperacionesPanel getEstatusOperacionesPanel(PosFrame frame){
        if(panel == null)
            panel = new EstatusOperacionesPanel();
        posFrame = frame;
        return panel;
    }

    /**
     * Creates new form IngresoCajaPanel
     */
    public EstatusOperacionesPanel() {
        initComponents();
        sesion = Sesion.getSesion();
        controller = EstatusOperacionesController.getEstatusOperacionesController();
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
                
                if(comboTiposOperacion.getModel().getSize() > 0){
                    
                    try{
                    
                        TipoOperacion tipoOperacion = (TipoOperacion) comboTiposOperacion.getModel().getSelectedItem();
                    
                        List<OperacionPendiente> operaciones = controller.consultarOperacionesPendientes(tipoOperacion);
                        if(operaciones.isEmpty()){
                            JOptionPane.showMessageDialog(panel, "No quedan operaciones de tipo "+tipoOperacion.getDescripcion()+" por sincronizar", "", JOptionPane.INFORMATION_MESSAGE);
                            limpiarPantalla();
                        }else{
                            cargarTablaOperaciones(operaciones);
                        }
                    
                    }catch(Exception ex){
                        logger.error("Error al consultar operaciones pendients");
                        ex.printStackTrace();
                    }
                    
                }

            }
        };
        botonCOnsultar.addActionListener(botonAceptarActionListener);

    }
    
    private void cargarTablaOperaciones(List<OperacionPendiente> operaciones){
        
        DefaultTableModel model = (DefaultTableModel) tablaOperaciones.getModel();
                model.getDataVector().clear();
        if(!operaciones.isEmpty()){
            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (OperacionPendiente o : operaciones) {
                model.addRow(new Object[]{o.getTipo().getDescripcion(),
                    o.getFolio(),
                    o.getMonto(),
                    o.getDescripcion(),
                    o.getFecha(),
                    o.getEstatus()});
            }
            tablaOperaciones.setRowSelectionInterval(0, 0);
        }
        
    }
    
    private void limpiarPantalla(){
        
        DefaultTableModel model = (DefaultTableModel) tablaOperaciones.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
    }

    
    public void cargarDatosSesion() {
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        cargarComboTiposOperacion();
        //campoFondoFijo.setText("0");
        
        tablaOperaciones.getColumnModel().getColumn(0).setPreferredWidth(250);
        tablaOperaciones.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaOperaciones.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaOperaciones.getColumnModel().getColumn(3).setPreferredWidth(520);
        tablaOperaciones.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaOperaciones.getColumnModel().getColumn(5).setPreferredWidth(100);
    }
    
    private void cargarComboTiposOperacion(){
        
        List<TipoOperacion> tipos = new ArrayList<>();
        tipos.add(new TipoOperacion(Constantes.OPERACION_NUEVO_CONTRATO, "Nuevos Contratos"));
        tipos.add(new TipoOperacion(Constantes.OPERACION_COBRO_SERVICIO, "Cobros de mensualidades de servicio"));
        tipos.add(new TipoOperacion(Constantes.OPERACION_CANCELACION, "Cobros de cancelaciones"));
        tipos.add(new TipoOperacion(Constantes.OPERACION_CORTE, "Cortes de Caja"));
        tipos.add(new TipoOperacion(Constantes.OPERACION_APERTURA, "Aperturas de Caja"));
        tipos.add(new TipoOperacion(Constantes.OPERACION_INGRESO, "Ingresos a caja"));
        tipos.add(new TipoOperacion(Constantes.OPERACION_EGRESO, "Egresos de Caja"));
        tipos.add(new TipoOperacion(Constantes.OPERACION_EGRESO_EXTRAORDINARIO, "Egresos Extraordinarios"));
        tipos.forEach(t -> comboTiposOperacion.addItem(t));
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
        jLabel13 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        comboTiposOperacion = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaOperaciones = new javax.swing.JTable();
        botonCOnsultar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        botonRegresar = new javax.swing.JButton();

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

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("Estatus de Sincronización de Operaciones");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        comboTiposOperacion.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Seleccione Tipo De Operación:");

        tablaOperaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Operación", "Folio", "Monto", "Descripción", "Fecha", "Estatus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane1.setViewportView(tablaOperaciones);
        if (tablaOperaciones.getColumnModel().getColumnCount() > 0) {
            tablaOperaciones.getColumnModel().getColumn(0).setResizable(false);
            tablaOperaciones.getColumnModel().getColumn(1).setResizable(false);
            tablaOperaciones.getColumnModel().getColumn(2).setResizable(false);
            tablaOperaciones.getColumnModel().getColumn(3).setResizable(false);
            tablaOperaciones.getColumnModel().getColumn(4).setResizable(false);
            tablaOperaciones.getColumnModel().getColumn(5).setResizable(false);
        }

        botonCOnsultar.setBackground(new java.awt.Color(163, 73, 164));
        botonCOnsultar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonCOnsultar.setForeground(new java.awt.Color(255, 255, 255));
        botonCOnsultar.setText("Consultar");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(comboTiposOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(144, 144, 144)
                        .addComponent(botonCOnsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(150, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTiposOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(botonCOnsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(163, 73, 164));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Operaciones Pendientes de Sincronizar");

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(etiquetaNumeroCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(206, 206, 206)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(etiquetaSucursal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(121, 121, 121)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
            .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 1800, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(524, 524, 524)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
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
                    .addComponent(jLabel11)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addComponent(etiquetaLogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonCOnsultar;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JComboBox<TipoOperacion> comboTiposOperacion;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaOperaciones;
    // End of variables declaration//GEN-END:variables
}
