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
import java.text.ParseException;
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
import mx.com.tvch.pos.controller.CobroServicioController;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.entity.PromocionEntity;
import mx.com.tvch.pos.entity.TipoDescuentoEntity;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.model.TipoBusquedaCobro;
import mx.com.tvch.pos.model.client.Response;
import mx.com.tvch.pos.model.client.UpdateContratoResponse;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroServicioPanel extends javax.swing.JPanel {

    private static CobroServicioPanel cobroPanel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final CobroServicioController controller;
    private final Utilerias util;
    private final Impresora impresora;

    List<ContratoxSuscriptorEntity> suscriptoresConsultaList;
    private ContratoxSuscriptorEntity suscriptorSeleccionado;
    private List<DetallePagoServicio> listaDetallesPago;

    org.slf4j.Logger logger = LoggerFactory.getLogger(CobroServicioPanel.class);

    public static CobroServicioPanel getCobroPanel(PosFrame frame) {
        if (cobroPanel == null) {
            cobroPanel = new CobroServicioPanel();
        }
        posFrame = frame;
        return cobroPanel;
    }

    /**
     * Creates new form CobroPanel
     */
    public CobroServicioPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = CobroServicioController.getContratoxSuscriptorController();
        util = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        suscriptoresConsultaList = new ArrayList<>();
        listaDetallesPago = new ArrayList<>();

        cargarComboTiposDescuento();
        crearEventos();
        cargarComboTiposBusqueda();
        cargarComboEstatusSuscriptor();
        cargarComboMeses();
    }

    /*private void cobrarServicio() {

        if (suscriptorSeleccionado != null && !listaDetallesPago.isEmpty()) {
            try {
                Integer numeroMeses = (Integer) comboNumeroMeses.getModel().getSelectedItem();
                Long transaccionId = controller.cobrarServicio(suscriptorSeleccionado, listaDetallesPago, numeroMeses);
                try {
                    impresora.imprimirTicketServicio(transaccionId,listaDetallesPago, suscriptorSeleccionado, sesion.getSucursal(), numeroMeses);
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    logger.error("Fallo al imprimir ticket de transaccion: \n" + sw.toString());
                    JOptionPane.showMessageDialog(cobroPanel, "El cobro se realizó correctamente pero ocurrió un error al imprimir su ticket. Si desea una rempresión vaya a sección de reimpresiones", "", JOptionPane.WARNING_MESSAGE);
                }
                System.out.println("transaccionId: " + transaccionId);
                limpiarPantalla();
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                logger.error("Fallo al cobrar transaccion: \n" + sw.toString());
                JOptionPane.showMessageDialog(cobroPanel, "Ocurrió un error al realizar el cobro, por favor reintente. Si el problema persiste consulte a soporte.", "", JOptionPane.WARNING_MESSAGE);
            }
        }

    }*/

    private void crearEventos() {
        
        ActionListener comboPromocionesActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(suscriptorSeleccionado != null){
                    cargarDatosSuscriptor(suscriptorSeleccionado, false);
                }
            }
        };
        comboPromociones.addActionListener(comboPromocionesActionListener);
        
        ActionListener comboMesesActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(suscriptorSeleccionado != null){
                    cargarDatosSuscriptor(suscriptorSeleccionado, false);
                }
            }
        };
        comboNumeroMeses.addActionListener(comboMesesActionListener);

        ActionListener botonCobrarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (suscriptorSeleccionado != null && !listaDetallesPago.isEmpty()) {
                    try {
                        
                        Long transaccionId = null;
                        boolean seDebeGenerarOrden = false;
                        boolean seCanceloPago = false;
                        if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
                            
                            seDebeGenerarOrden = true;
                            StringBuilder sb = new StringBuilder();
                            sb.append("El contrato que esta cobrando se en cuentra En Corte:\n");
                            sb.append("¿Desea generar Orden de Reconexión? \n");
                            int input = JOptionPane.showConfirmDialog(null, sb.toString());
                            if (input == 0) {
                                seDebeGenerarOrden = true;
                            }else if (input == 1){
                                seDebeGenerarOrden = false;
                            }else{
                                seCanceloPago = true;
                            }
                            
                        }
                        
                        if(!seCanceloPago){
                            
                            //Integer numeroMeses = (Integer) comboNumeroMeses.getModel().getSelectedItem();
                            transaccionId = controller.cobrarServicio(suscriptorSeleccionado, listaDetallesPago/*, numeroMeses*/);
                            
                            try {
                                impresora.imprimirTicketServicio(transaccionId, listaDetallesPago, suscriptorSeleccionado, sesion.getSucursal()/*, numeroMeses*/);
                            } catch (Exception ex) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                ex.printStackTrace(pw);
                                logger.error("Fallo al imprimir ticket de transaccion: \n" + sw.toString());
                                JOptionPane.showMessageDialog(cobroPanel, "El cobro se realizó correctamente pero ocurrió un error al imprimir su ticket. Si desea una reimpresión vaya a sección de reimpresiones", "", JOptionPane.WARNING_MESSAGE);
                            }
                            System.out.println("transaccionId: " + transaccionId);
                            
                            if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
                                try{
                                    //actualizar en local el estatus del contrato y en server el estatus y la orden en caso de requerirse
                                    Response<UpdateContratoResponse> response = controller.actualizarContratoReconexion(suscriptorSeleccionado, seDebeGenerarOrden);
                                    switch (response.getCode()) {
                                        case Constantes.CODIGO_HTTP_OK:
                                            if(seDebeGenerarOrden)
                                                JOptionPane.showMessageDialog(cobroPanel, "El contrato se actualizo correctamente a estatus RECONEXION, \n "
                                                        + "Su orden de reconexión se generó correctamente, por favor verifique en su portal web", "", JOptionPane.INFORMATION_MESSAGE);
                                            else
                                                JOptionPane.showMessageDialog(cobroPanel, "El contrato se actualizo correctamente a estatus ACTIVO, por favor verifique", "", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        case Constantes.CODIGO_HTTP_OK_WARNING:
                                            JOptionPane.showMessageDialog(cobroPanel, response.getMessage(), "", JOptionPane.WARNING_MESSAGE);
                                            break;
                                        default:
                                            if(seDebeGenerarOrden){
                                                JOptionPane.showMessageDialog(cobroPanel, "Su cobro fue realizado exitosamente, sin embargo ocurrió un error de comunicación con el servidor al actualizar su contrato y generar su orden en línea,\n"
                                                        + "No fue posible validar que se haya generado la orden de reconexión solicitada, por favor revise, de ser necesario favor de generarla manualmente en el portal web", null, JOptionPane.WARNING_MESSAGE);
                                            }else{
                                                JOptionPane.showMessageDialog(cobroPanel, "Su cobro fue realizado exitosamente, sin embargo ocurrió un error de comunicación al actualizar su contrato en el servidor en línea, "
                                                        + "Su pago se sincronizará posteriormente y el contrato pasará a estatus RECONEXION, de ser necesario actualice a Activo en el portal web", null, JOptionPane.WARNING_MESSAGE);
                                            }   break;
                                    }
                                }catch(Exception ex){
                                    if(seDebeGenerarOrden){
                                        JOptionPane.showMessageDialog(cobroPanel, "Su cobro fue realizado exitosamente, sin embargo ocurrió un error de comunicación con el servidor al actualizar su contrato y generar su orden en línea,\n"
                                            + "No fue posible validar que se haya generado la orden de reconexión solicitada, por favor revise, de ser necesario favor de generarla manualmente en el portal web", null, JOptionPane.WARNING_MESSAGE);
                                    }else{
                                        JOptionPane.showMessageDialog(cobroPanel, "Su cobro fue realizado exitosamente, sin embargo ocurrió un error de comunicación al actualizar su contrato en el servidor en línea, "
                                            + "Su pago se sincronizará posteriormente y el contrato pasará a estatus RECONEXION, de ser necesario actualice a Activo en el portal web", null, JOptionPane.WARNING_MESSAGE);
                                    }
                                }
                            }
                            
                            limpiarPantalla();
                        }
       
                    } catch (Exception ex) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        logger.error("Fallo al cobrar transaccion: \n" + sw.toString());
                        JOptionPane.showMessageDialog(cobroPanel, "Ocurrió un error al realizar el cobro, por favor reintente. Si el problema persiste consulte a soporte.", "", JOptionPane.WARNING_MESSAGE);
                    }
                }

            }
        };
        botonCobrar.addActionListener(botonCobrarActionListener);

        ActionListener botonEliminarPromocionActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suscriptorSeleccionado != null) {

                    if (!listaDetallesPago.isEmpty()
                            && listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().isPresent()) {
                        listaDetallesPago = listaDetallesPago.stream().filter(d -> d.getTipoDetalle() != Constantes.TIPO_DETALLE_COBRO_PROMOCION).collect(Collectors.toList());
                        actualizarTablaDetallesPago();
                        etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));
                        etiquetaPromocionAplicada.setVisible(false);
                        suscriptorSeleccionado.setMesesGratis(0);
                    }

                }
            }
        };
        botonEliminarPromocion.addActionListener(botonEliminarPromocionActionListener);

        ActionListener botonAplicarPromocionActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suscriptorSeleccionado != null && comboPromociones.getModel().getSize() > 0) {

                    if (!listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().isPresent()) {

                        if (!listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().isPresent()) {

                            PromocionEntity entity = (PromocionEntity) comboPromociones.getModel().getSelectedItem();
                            Integer numeroMeses = (Integer) comboNumeroMeses.getModel().getSelectedItem();
                            
                            if(entity.getMesesPagados() == numeroMeses){
                                
                                String descFechaProximoPago = null;
                                try {
                                    descFechaProximoPago = util.obtenerNuevaFechaProximoPago(
                                        sesion.getDiaCorte(), 
                                        0, 
                                        suscriptorSeleccionado.getFechaProximoPago(), 
                                        Constantes.FORMATO_FECHA_TICKET, 
                                        numeroMeses+entity.getMesesGratis(),
                                        suscriptorSeleccionado.getEstatusContratoId());
                                } catch (Exception ex) {
                                    java.util.logging.Logger.getLogger(CobroServicioPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                DetallePagoServicio detallePromocion = new DetallePagoServicio();
                                StringBuilder promocion = new StringBuilder();
                                //promocion.append("Promoción - ");
                                promocion.append(entity.getDescripcion());
                                if(descFechaProximoPago != null){
                                    promocion.append(" >> Próximo pago hasta: ");
                                    promocion.append(descFechaProximoPago);
                                }
                                detallePromocion.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_PROMOCION);
                                detallePromocion.setConcepto(promocion.toString());
                                detallePromocion.setMonto(entity.getCostoPromocion()*numeroMeses);
                                Double costoTotalMenosPromocion = (suscriptorSeleccionado.getCostoServicio()-entity.getCostoPromocion())*numeroMeses;
                                detallePromocion.setCadenaMonto("- $".concat(String.valueOf(costoTotalMenosPromocion)));
                                detallePromocion.setPromocionId(entity.getPromocionId());
                                listaDetallesPago.add(detallePromocion);
                                actualizarTablaDetallesPago();
                                etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));
                                etiquetaPromocionAplicada.setVisible(true);
                                if(entity.getMesesGratis() != null)
                                    suscriptorSeleccionado.setMesesGratis(entity.getMesesGratis());
                            
                            }else{
                                JOptionPane.showMessageDialog(cobroPanel, "Para aplicar la promoción es necesario el pago de "+entity.getMesesPagados()+" meses", "", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(cobroPanel, "Ya existe un descuento aplicado en el cobro. Si desea aplicar una promoción es necesario eliminar el descuento.", "", JOptionPane.WARNING_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(cobroPanel, "Ya existe un descuento aplicado en el pago", "", JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        };
        botonAplicarPromocion.addActionListener(botonAplicarPromocionActionListener);

        ActionListener botonEliminarDescuentoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (suscriptorSeleccionado != null) {

                    if (!listaDetallesPago.isEmpty()
                            && listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().isPresent()) {
                        listaDetallesPago = listaDetallesPago.stream().filter(d -> d.getTipoDetalle() != Constantes.TIPO_DETALLE_COBRO_DESCUENTO).collect(Collectors.toList());
                        actualizarTablaDetallesPago();
                        etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));
                        campoDescuentoAplicado.setText("");
                        campoMotivoDescuento.setText("");
                        campoMontoDescuento.setText("");
                    }

                }

            }
        };
        botonEliminarDescuento.addActionListener(botonEliminarDescuentoActionListener);

        ActionListener botonApliCarDescuentoActionLister = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suscriptorSeleccionado != null && !campoMontoDescuento.getText().isEmpty() && !campoMotivoDescuento.getText().isEmpty()) {

                    if (!listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().isPresent()) {

                        if (!listaDetallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().isPresent()) {

                            Double importeDescuento = null;
                            boolean elImporteDescuentoEsNumerico = false;
                            try {
                                importeDescuento = Double.parseDouble(campoMontoDescuento.getText());
                                elImporteDescuentoEsNumerico = true;
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(cobroPanel, "Por favor capture un monto numérico de descuento", "", JOptionPane.WARNING_MESSAGE);
                            }
                            if (importeDescuento != null && elImporteDescuentoEsNumerico) {

                                TipoDescuentoEntity entity = (TipoDescuentoEntity) comboTipoDescuento.getModel().getSelectedItem();
                                DetallePagoServicio detalleDescuento = new DetallePagoServicio();
                                StringBuilder descuento = new StringBuilder();
                                descuento.append("Descuento - ");
                                descuento.append(entity.getDescripcion());
                                detalleDescuento.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_DESCUENTO);
                                detalleDescuento.setConcepto(descuento.toString());
                                detalleDescuento.setMonto(importeDescuento);
                                detalleDescuento.setCadenaMonto("- $".concat(String.valueOf(importeDescuento)));
                                detalleDescuento.setMotivoDescuento(campoMotivoDescuento.getText());
                                detalleDescuento.setTipoDescuentoId(entity.getIdTipoDescuento());
                                listaDetallesPago.add(detalleDescuento);
                                actualizarTablaDetallesPago();
                                etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));
                                campoDescuentoAplicado.setText(detalleDescuento.getMotivoDescuento());
                                campoMotivoDescuento.setText("");
                                campoMontoDescuento.setText("");

                            }

                        } else {
                            JOptionPane.showMessageDialog(cobroPanel, "Ya existe una promoción aplicada en el cobro. Si desea agregar un descuento es necesario eliminar la promoción.", "", JOptionPane.WARNING_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(cobroPanel, "Ya existe un descuento aplicado en el pago", "", JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        };
        botonAplicarDescuento.addActionListener(botonApliCarDescuentoActionLister);;

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
                                comboNumeroMeses.setSelectedIndex(0);
                                ContratoxSuscriptorEntity entity = suscriptoresConsultaList
                                        .stream().filter(cs -> cs.getContratoId() == contratoId.longValue()).findFirst().get();
                                cargarDatosSuscriptor(entity, true);
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
                            comboNumeroMeses.setSelectedIndex(0);
                            ContratoxSuscriptorEntity entity = suscriptoresConsultaList
                                    .stream().filter(cs -> cs.getContratoId() == contratoId.longValue()).findFirst().get();
                            cargarDatosSuscriptor(entity, true);
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

                        contrato = Long.parseLong(campoBusqueda.getText().trim());
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
            ContratoxSuscriptorEntity contratosuscriptor, boolean seRefrescanPromociones) {

        System.out.println("Seelccionado: " + contratosuscriptor.getContratoId());
        comboNumeroMeses.setEnabled(true);
        // primero borrar los datos de suscriptores que se hayan seleccionado antes
        limpiarDatosSuscriptor();
        suscriptorSeleccionado = contratosuscriptor;
        Integer numeroMeses = 1;
        Double montoTotalMeses = 0.0;
        if(contratosuscriptor.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
            comboNumeroMeses.setEnabled(false);
            numeroMeses = controller.obtenerMesesAtrasado(suscriptorSeleccionado);
            montoTotalMeses = controller.obtenerMontoAtrasado(suscriptorSeleccionado);
        }else if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTESIA){
            numeroMeses = 1;
            montoTotalMeses = 0.0;
        }else{
            numeroMeses = (Integer) comboNumeroMeses.getModel().getSelectedItem();
            montoTotalMeses = suscriptorSeleccionado.getCostoServicio()*numeroMeses;
        }
        suscriptorSeleccionado.setMesesPorPagar(numeroMeses);
        
        StringBuilder descripcionMes = new StringBuilder();
        if(numeroMeses == 1)
            descripcionMes.append("Un mes ");
        else
            descripcionMes.append(String.valueOf(numeroMeses).concat(" meses "));
        
        String descFechaProximoPago = null;
        try {
            descFechaProximoPago = util.obtenerNuevaFechaProximoPago(
                    sesion.getDiaCorte(), 
                    0, 
                    contratosuscriptor.getFechaProximoPago(), 
                    Constantes.FORMATO_FECHA_TICKET, 
                    numeroMeses,
                    contratosuscriptor.getEstatusContratoId());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(CobroServicioPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        DetallePagoServicio detalleMontoPago = new DetallePagoServicio();
        StringBuilder conceptoMontoPago = new StringBuilder();
        conceptoMontoPago.append("Pago Mensualidad ");
        conceptoMontoPago.append(descripcionMes);
        conceptoMontoPago.append(" ");
        conceptoMontoPago.append(suscriptorSeleccionado.getServicio());
        if(descFechaProximoPago != null)
            conceptoMontoPago.append(" >> Proximo pago hasta: ").append(descFechaProximoPago);
        detalleMontoPago.setConcepto(conceptoMontoPago.toString());
        
        
        /*if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
            montoTotalMeses = controller.obtenerMontoAtrasado(suscriptorSeleccionado);
        }else if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTESIA){
            montoTotalMeses = 0.0;
        }else{
            montoTotalMeses = suscriptorSeleccionado.getCostoServicio()*numeroMeses;
        }*/
        
        detalleMontoPago.setMonto(montoTotalMeses);
        detalleMontoPago.setCadenaMonto("  $".concat(String.valueOf(montoTotalMeses)));
        detalleMontoPago.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_SERVICIO);

        listaDetallesPago.add(detalleMontoPago);
        if (controller.seDebeGenerarRecargo(suscriptorSeleccionado)) {
            DetallePagoServicio detalleRecargo = new DetallePagoServicio();
            detalleRecargo.setConcepto("Recargo por pago tardío");
            detalleRecargo.setMonto(50.0);
            detalleRecargo.setCadenaMonto("  $50");
            detalleRecargo.setTipoDetalle(Constantes.TIPO_DETALLE_COBRO_RECARGO);
            listaDetallesPago.add(detalleRecargo);
        }

        actualizarTablaDetallesPago();
        etiquetaImporte.setText(controller.obtenerImporteActualizado(listaDetallesPago));

        if(seRefrescanPromociones){
            ///carcagr combo promociones
            comboPromociones.removeAllItems();
            cargarComboPromociones(suscriptorSeleccionado.getServicioId());
        }
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
        etiquetaPromocionAplicada.setVisible(false);
        campoDescuentoAplicado.setText("");
        
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

        suscriptoresConsultaList = controller.consultarSuscriptores(contrato, tipoBusquedaCobro, cadenaBusqueda);

        if (!suscriptoresConsultaList.isEmpty()) {

            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (ContratoxSuscriptorEntity c : suscriptoresConsultaList) {
                model.addRow(new Object[]{c.getContratoId(),
                    c.getFolioContrato()== null ? "" : c.getFolioContrato(),
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

    /**
     *
     */
    private void cargarComboPromociones(Long servicioId) {

        try {

            List<PromocionEntity> list = controller.consultarPromociones(servicioId);
            list.forEach(p -> comboPromociones.addItem(p));
            comboPromociones.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     *
     */
    private void cargarComboTiposDescuento() {

        try {

            List<TipoDescuentoEntity> list = controller.consultarTiposDescuento();
            list.forEach(td -> comboTipoDescuento.addItem(td));
            comboTipoDescuento.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }

    }
    
    private void cargarComboMeses(){
        
        for(int i=1; i<=12; i++)
            comboNumeroMeses.addItem(i);
        
    }

    private void limpiarPantalla() {
        limpiarDatosSuscriptor();

        DefaultTableModel model = (DefaultTableModel) tablaSuscriptores.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();

        campoMontoDescuento.setText("");
        campoMotivoDescuento.setText("");
        campoDescuentoAplicado.setText("");
        etiquetaImporte.setText("0.00");
        comboNumeroMeses.setSelectedIndex(0);
        comboNumeroMeses.setEnabled(true);
    }

    public void cargarDatosSesion() {

        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());

        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setViewportView(tablaSuscriptores);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //tablaSuscriptores.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaSuscriptores.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaSuscriptores.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaSuscriptores.getColumnModel().getColumn(2).setPreferredWidth(310);
        tablaSuscriptores.getColumnModel().getColumn(3).setPreferredWidth(190);
        tablaSuscriptores.getColumnModel().getColumn(4).setPreferredWidth(390);
        tablaSuscriptores.getColumnModel().getColumn(5).setPreferredWidth(130);
        
        tablaDetallesPago.getColumnModel().getColumn(0).setPreferredWidth(750);
        //tablaSuscriptores.gets

        ImageIcon imagen = new ImageIcon("src/main/resources/logo_grande.jpg");
        Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(/*etiquetaLogo.getWidth(), etiquetaLogo.getHeight()*/320, 130, Image.SCALE_DEFAULT));
        etiquetaLogo.setIcon(icono);

        etiquetaImporte.setText("0.00");
        etiquetaPromocionAplicada.setVisible(false);
        campoDescuentoAplicado.setEditable(false);
    }

    /**
     *
     */
    private void cargarComboEstatusSuscriptor() {

        try {

            List<EstatusSuscriptorEntity> list = controller.consultarEstatusSuscriptor();
            list = list.stream().filter(e -> e.getEstatusId() == Constantes.ESTATUS_SUSCRIPTOR_ACTIVO).collect(Collectors.toList());
            list.forEach(e -> comboEstatusSuscriptor.addItem(e));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
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
        panelPromociones = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        comboPromociones = new javax.swing.JComboBox<>();
        botonAplicarPromocion = new javax.swing.JButton();
        botonEliminarPromocion = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        comboNumeroMeses = new javax.swing.JComboBox<>();
        panelInfoPago = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDetallesPago = new javax.swing.JTable();
        panelImportes = new javax.swing.JPanel();
        etiquetaPesos = new javax.swing.JLabel();
        etiquetaImporte = new javax.swing.JLabel();
        etiquetaPromocionAplicada = new javax.swing.JLabel();
        panelDescuentos = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        comboTipoDescuento = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        campoMontoDescuento = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        campoMotivoDescuento = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        campoDescuentoAplicado = new javax.swing.JTextField();
        botonAplicarDescuento = new javax.swing.JButton();
        botonEliminarDescuento = new javax.swing.JButton();
        panelBotones = new javax.swing.JPanel();
        botonRegresar = new javax.swing.JButton();
        botonCobrar = new javax.swing.JButton();

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
        jLabel24.setText("Cobro de Mensualidades de Servicio");

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
        campoFechaPago.setForeground(java.awt.Color.red);

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

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
                        .addComponent(campoContrato, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPromociones.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 51, 51));
        jLabel16.setText("Promociones");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Seleccionar Promoción:");

        comboPromociones.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonAplicarPromocion.setBackground(new java.awt.Color(227, 126, 75));
        botonAplicarPromocion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonAplicarPromocion.setForeground(new java.awt.Color(255, 255, 255));
        botonAplicarPromocion.setText("Aplicar Promocion");

        botonEliminarPromocion.setBackground(java.awt.Color.red);
        botonEliminarPromocion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonEliminarPromocion.setForeground(new java.awt.Color(255, 255, 255));
        botonEliminarPromocion.setText("Eliminar Promocion");
        botonEliminarPromocion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarPromocionActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setForeground(java.awt.Color.red);
        jLabel23.setText("Meses a pagar:");

        comboNumeroMeses.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelPromocionesLayout = new javax.swing.GroupLayout(panelPromociones);
        panelPromociones.setLayout(panelPromocionesLayout);
        panelPromocionesLayout.setHorizontalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(botonAplicarPromocion)
                .addGap(18, 18, 18)
                .addComponent(botonEliminarPromocion)
                .addGap(63, 63, 63)
                .addComponent(jLabel23)
                .addGap(18, 18, 18)
                .addComponent(comboNumeroMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
        );
        panelPromocionesLayout.setVerticalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(botonAplicarPromocion)
                        .addComponent(botonEliminarPromocion)
                        .addComponent(jLabel23)
                        .addComponent(comboNumeroMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        etiquetaPesos.setFont(new java.awt.Font("Segoe UI", 0, 80)); // NOI18N
        etiquetaPesos.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaPesos.setText("$");

        etiquetaImporte.setFont(new java.awt.Font("Segoe UI", 0, 80)); // NOI18N
        etiquetaImporte.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaImporte.setText("0.00");

        etiquetaPromocionAplicada.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaPromocionAplicada.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaPromocionAplicada.setText("Promocion Aplicada!!");

        javax.swing.GroupLayout panelImportesLayout = new javax.swing.GroupLayout(panelImportes);
        panelImportes.setLayout(panelImportesLayout);
        panelImportesLayout.setHorizontalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImportesLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(etiquetaPesos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
            .addGroup(panelImportesLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(etiquetaPromocionAplicada)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelImportesLayout.setVerticalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImportesLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaPesos, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiquetaPromocionAplicada)
                .addContainerGap())
        );

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setText("Seleccione descuento:");

        comboTipoDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setText("Ingrese monto descuento:");

        campoMontoDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setText("Motivo Descuento:");

        campoMotivoDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Descuento aplicado:");

        campoDescuentoAplicado.setEditable(false);
        campoDescuentoAplicado.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoDescuentoAplicado.setEnabled(false);

        botonAplicarDescuento.setBackground(new java.awt.Color(227, 126, 75));
        botonAplicarDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonAplicarDescuento.setForeground(new java.awt.Color(255, 255, 255));
        botonAplicarDescuento.setText("Aplicar Descuento");

        botonEliminarDescuento.setBackground(java.awt.Color.red);
        botonEliminarDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonEliminarDescuento.setForeground(new java.awt.Color(255, 255, 255));
        botonEliminarDescuento.setText("EliminarDescuento");

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        botonCobrar.setBackground(new java.awt.Color(0, 153, 51));
        botonCobrar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonCobrar.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrar.setText("Cobrar");

        javax.swing.GroupLayout panelBotonesLayout = new javax.swing.GroupLayout(panelBotones);
        panelBotones.setLayout(panelBotonesLayout);
        panelBotonesLayout.setHorizontalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBotonesLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        panelBotonesLayout.setVerticalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonRegresar))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDescuentosLayout = new javax.swing.GroupLayout(panelDescuentos);
        panelDescuentos.setLayout(panelDescuentosLayout);
        panelDescuentosLayout.setHorizontalGroup(
            panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDescuentosLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDescuentosLayout.createSequentialGroup()
                        .addComponent(comboTipoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(campoMontoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(campoDescuentoAplicado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(campoMotivoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botonAplicarDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonEliminarDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(48, 48, 48)
                .addComponent(panelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        panelDescuentosLayout.setVerticalGroup(
            panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDescuentosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTipoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(campoMontoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(campoMotivoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonAplicarDescuento))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(campoDescuentoAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonEliminarDescuento)))
            .addGroup(panelDescuentosLayout.createSequentialGroup()
                .addComponent(panelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelInfoPagoLayout = new javax.swing.GroupLayout(panelInfoPago);
        panelInfoPago.setLayout(panelInfoPagoLayout);
        panelInfoPagoLayout.setHorizontalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInfoPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1023, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(panelImportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInfoPagoLayout.createSequentialGroup()
                .addComponent(panelDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelInfoPagoLayout.setVerticalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelImportes, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCabecero, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBusqueda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1500, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelInfoPago, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void botonEliminarPromocionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarPromocionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonEliminarPromocionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAplicarDescuento;
    private javax.swing.JButton botonAplicarPromocion;
    private javax.swing.JButton botonBusqueda;
    private javax.swing.JButton botonCobrar;
    private javax.swing.JButton botonEliminarDescuento;
    private javax.swing.JButton botonEliminarPromocion;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoBusqueda;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoDescuentoAplicado;
    private javax.swing.JTextField campoDomicilio;
    private javax.swing.JTextField campoEstatus;
    private javax.swing.JTextField campoFechaPago;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoMontoDescuento;
    private javax.swing.JTextField campoMotivoDescuento;
    private javax.swing.JTextField campoServicioContratado;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JComboBox<EstatusSuscriptorEntity> comboEstatusSuscriptor;
    private javax.swing.JComboBox<Integer> comboNumeroMeses;
    private javax.swing.JComboBox<PromocionEntity> comboPromociones;
    private javax.swing.JComboBox<TipoDescuentoEntity> comboTipoDescuento;
    private javax.swing.JComboBox<TipoBusquedaCobro> comboTiposBusqueda;
    private javax.swing.JLabel etiquetaImporte;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaPesos;
    private javax.swing.JLabel etiquetaPromocionAplicada;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelDescuentos;
    private javax.swing.JPanel panelImportes;
    private javax.swing.JPanel panelInfoContrato;
    private javax.swing.JPanel panelInfoPago;
    private javax.swing.JPanel panelPromociones;
    private javax.swing.JTable tablaDetallesPago;
    private javax.swing.JTable tablaSuscriptores;
    // End of variables declaration//GEN-END:variables
}
