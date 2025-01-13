/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CobroOrdenController;
import mx.com.tvch.pos.entity.EstatusSuscriptorEntity;
import mx.com.tvch.pos.model.Orden;
import mx.com.tvch.pos.model.TipoBusquedaCobro;
import mx.com.tvch.pos.model.TipoOrden;
import mx.com.tvch.pos.model.client.PromocionOrdenInstalacion;
import mx.com.tvch.pos.model.client.Suscriptor;
import mx.com.tvch.pos.model.client.TipoDescuento;
import mx.com.tvch.pos.util.Calendario;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobroOrdenPanel extends javax.swing.JPanel {

    private static CobroOrdenPanel cobroPanel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final CobroOrdenController controller;
    private final Calendario calendario;
    private Suscriptor suscriptorSeleccionado;
    private Orden ordenSeleccionada;
    private final Impresora impresora;

    private List<Suscriptor> suscriptoresConsultaList;
    private final JCalendar calendarioIni;
    private final JCalendar calendarioFin;
    private final JDateChooser chooserIni;
    private final JDateChooser chooserFin;
    List<TipoOrden> listTiposOrden = new ArrayList<>();
    
    Logger logger = LoggerFactory.getLogger(CobroOrdenPanel.class);

    public static CobroOrdenPanel getCobroOrdenPanel(PosFrame frame) {
        if (cobroPanel == null) {
            cobroPanel = new CobroOrdenPanel();
        }
        posFrame = frame;
        return cobroPanel;
    }

    /**
     * Creates new form CobroOrdenPanel
     */
    public CobroOrdenPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = CobroOrdenController.getCobroOrdenController();
        suscriptoresConsultaList = new ArrayList<>();
        calendario = Calendario.getCalendario();
        calendarioIni = calendario.obtenerCalendario(350, 170);
        calendarioFin = calendario.obtenerCalendario(350, 170);
        chooserIni = calendario.obtenerChooser();
        chooserFin = calendario.obtenerChooser();
        impresora = Impresora.getImpresora();

        crearEventos();
        cargarComboTiposBusqueda();
        cargarComboTiposOrden(null);
        cargarComboEstatusSuscriptor();
    }

    /**
     *
     */
    private void crearEventos() {

        ActionListener pagarOrdenActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ordenSeleccionada != null) {
                    
                    try{
                        Long transaccionId = controller.cobrarOrden(ordenSeleccionada);
                        try{
                            impresora.imprimirTicketOrdenInstalacion(ordenSeleccionada, suscriptorSeleccionado, sesion.getSucursal());
                        }catch(Exception ex){
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            logger.error("Fallo al imprimir ticket de transaccion: \n" + sw.toString());
                            JOptionPane.showMessageDialog(cobroPanel, "El cobro se realizó correctamente pero ocurrió un error al imprimir su ticket. Si desea una rempresión vaya a sección de reimpresiones", "", JOptionPane.WARNING_MESSAGE);
                        }
                        System.out.println("transaccionId: "+transaccionId);
                        limpiarPantallaCobro();
                    }catch(Exception ex){
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        logger.error("Fallo al cobrar transaccion: \n" + sw.toString());
                        JOptionPane.showMessageDialog(cobroPanel, "Ocurrió un error al realizar el cobro, por favor reintente. Si el problema persiste consulte a soporte.", "", JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        };
        botonCobrar.addActionListener(pagarOrdenActionListener);

        ActionListener eliminarDescuentoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ordenSeleccionada != null && ordenSeleccionada.getTipoDescuentoId() != null) {
                    double importePagar = 0.0;
                    if (ordenSeleccionada.getPromocionId() != null) {
                        //hay una promocion aplicada
                        importePagar = ordenSeleccionada.getCostoPromocion();
                    } else {
                        //no hay promociones
                        importePagar = ordenSeleccionada.getCosto();
                    }

                    ordenSeleccionada.setImportePagar(importePagar);
                    ordenSeleccionada.setTipoDescuentoId(null);
                    ordenSeleccionada.setImporteDescuento(null);
                    ordenSeleccionada.setMotivoDescuento(null);
                    campoDescuentoAplicado.setText("");
                    etiquetaImporte.setText(String.valueOf(ordenSeleccionada.getImportePagar()));
                }
            }
        };
        BotonEliminarDescuento.addActionListener(eliminarDescuentoActionListener);

        ActionListener aplicarDescuentoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ordenSeleccionada != null) {
                    if (ordenSeleccionada.getPromocionId() == null /*ordenSeleccionada.getImportePagar() > 0*/) {
                        if (!campoMotivoDescuento.getText().isEmpty() && !campoImporteDescuento.getText().isEmpty()) {
                            if (campoMotivoDescuento.getText().length() <= 100) {
                                Double importeDescuento = null;
                                boolean elImporteDescuentoEsNumerico = false;
                                try {
                                    importeDescuento = Double.parseDouble(campoImporteDescuento.getText());
                                    elImporteDescuentoEsNumerico = true;
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(cobroPanel, "Por favor capture un monto numérico de descuento", "", JOptionPane.WARNING_MESSAGE);
                                }
                                if (importeDescuento != null && elImporteDescuentoEsNumerico) {
                                    if (importeDescuento <= ordenSeleccionada.getImportePagar()) {
                                        TipoDescuento tipoDescuento = (TipoDescuento) comboTiposDescuento.getModel().getSelectedItem();
                                        double importePagar = ordenSeleccionada.getImportePagar() - importeDescuento;
                                        ordenSeleccionada.setTipoDescuentoId(tipoDescuento.getId());
                                        ordenSeleccionada.setMotivoDescuento(campoMotivoDescuento.getText());
                                        ordenSeleccionada.setImporteDescuento(importeDescuento);
                                        ordenSeleccionada.setImportePagar(importePagar);
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("$").append(ordenSeleccionada.getImporteDescuento());
                                        sb.append(" - ").append(ordenSeleccionada.getMotivoDescuento());
                                        campoDescuentoAplicado.setText(sb.toString());
                                        campoMotivoDescuento.setText("");
                                        campoImporteDescuento.setText("");
                                        etiquetaImporte.setText(String.valueOf(ordenSeleccionada.getImportePagar()));
                                    } else {
                                        JOptionPane.showMessageDialog(cobroPanel, "El descuento no puede ser mayor al importe de $" + ordenSeleccionada.getImportePagar() + " por pagar", "", JOptionPane.WARNING_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(cobroPanel, "El tamaño maximo del motivo es de 100 caracteres, por favor acorte su texto.", "", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(cobroPanel, "Por favor capture importe y motivo de descuento", "", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(cobroPanel, "Ya existe una promoción aplicada en su pago, no está permitido agregar descuentos", "", JOptionPane.WARNING_MESSAGE);
                        campoImporteDescuento.setText("");
                        campoMotivoDescuento.setText("");
                        //JOptionPane.showMessageDialog(cobroPanel, "No es posible aplicar el descuento ya que el importe actual a pagar es $0.00", "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        };
        botonAplicarDescuento.addActionListener(aplicarDescuentoActionListener);

        ActionListener eliminarPromocionActioListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ordenSeleccionada != null && comboPromociones.getModel().getSize() > 0 && ordenSeleccionada.getPromocionId() != null) {

                    //double importePagar = 0.0;
                    //validar si existe algun descuento aplicado
                    //if (ordenSeleccionada.getTipoDescuentoId() != null) {
                    //hay descuento aplicado
                    //importePagar = ordenSeleccionada.getCosto() - ordenSeleccionada.getImporteDescuento();
                    //} else {
                    //no hay descuentos
                    //importePagar = ordenSeleccionada.getCosto();
                    //}
                    double importePagar = ordenSeleccionada.getCosto();
                    ordenSeleccionada.setDescripcionPromocion(null);
                    ordenSeleccionada.setCostoPromocion(null);
                    ordenSeleccionada.setPromocionId(null);
                    ordenSeleccionada.setImportePagar(importePagar);
                    etiquetaImporte.setText(String.valueOf(ordenSeleccionada.getImportePagar()));
                    etiquetaPromoActiva.setVisible(false);
                    campoFechaPago.setText(suscriptorSeleccionado.getFechaProximoPago());
                }
            }
        };
        BotonEliminarPromocion.addActionListener(eliminarPromocionActioListener);

        ActionListener aplicarPromocionActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (ordenSeleccionada.getTipoDescuentoId() == null) {
                    if (ordenSeleccionada != null && comboPromociones.getModel().getSize() > 0) {

                        PromocionOrdenInstalacion promocion = (PromocionOrdenInstalacion) comboPromociones.getModel().getSelectedItem();

                        double importePagar = promocion.getCostoPromocion();
                        ordenSeleccionada.setPromocionId(promocion.getId());
                        ordenSeleccionada.setCostoPromocion(promocion.getCostoPromocion());

                        //if (ordenSeleccionada.getTipoDescuentoId() == null) {
                        //no hay descuentos aplicados, entonces el costo de la orden es el de la promocion
                        //importePagar = promocion.getCostoPromocion();
                        //} else {
                        // ya hay algun descuento aplicado, se valida que el importe de la orden no quede en negativo
                        //if ((promocion.getCostoPromocion() - ordenSeleccionada.getImporteDescuento()) > 0) {
                        //importePagar = promocion.getCostoPromocion() - ordenSeleccionada.getImporteDescuento();
                        //} else {
                        //importePagar = 0.0;
                        //}
                        //}
                        if (promocion.getMesesGratis() != null && promocion.getMesesGratis() > 0) {
                            String nuevaFechaPago = controller.actualizarFechaProximoPago(suscriptorSeleccionado.getFechaProximoPago(), promocion);
                            campoFechaPago.setText(nuevaFechaPago);
                            ordenSeleccionada.setFechaProximoPago(nuevaFechaPago);
                        } else {
                            ordenSeleccionada.setFechaProximoPago(suscriptorSeleccionado.getFechaProximoPago());
                        }

                        ordenSeleccionada.setMesesGratisPromocion(promocion.getMesesGratis());
                        ordenSeleccionada.setDescripcionPromocion(promocion.getDescripcion());
                        ordenSeleccionada.setImportePagar(importePagar);
                        etiquetaImporte.setText(String.valueOf(ordenSeleccionada.getImportePagar()));
                        etiquetaPromoActiva.setVisible(true);

                    }
                } else {
                    JOptionPane.showMessageDialog(cobroPanel, "Ya existe un descuento agregado en su pago, si desea agregar aplicar una promoción debe eliminar el descuento", "", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        BotonAplicarPromocion.addActionListener(aplicarPromocionActionListener);

        ActionListener buscarOrdenActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suscriptorSeleccionado != null) {

                    //if (chooserIni.getDate() != null && chooserFin.getDate() != null) {
                    //if (chooserFin.getDate().equals(chooserIni.getDate()) || chooserFin.getDate().after(chooserIni.getDate())) {
                    try {
                        ordenSeleccionada = null;
                        etiquetaPromoActiva.setVisible(false);
                        TipoOrden tipoOrden = (TipoOrden) comboTiposOrden.getModel().getSelectedItem();
                        List<Orden> ordenes = controller.consultarOrdenes(suscriptorSeleccionado, tipoOrden);
                        if (!ordenes.isEmpty() && ordenes.size() == 1) {
                            //cargar tabla de ordenes
                            ordenSeleccionada = ordenes.get(0);
                            ordenSeleccionada.setServicioId(suscriptorSeleccionado.getServicioId());
                            ordenSeleccionada.setServicio(suscriptorSeleccionado.getServicio());
                            ordenSeleccionada.setFechaProximoPago(suscriptorSeleccionado.getFechaProximoPago());
                            DefaultTableModel model = (DefaultTableModel) tablaOrdenes.getModel();
                            model.getDataVector().clear();
                            cargarTablaOrdenes(model, ordenes);
                            //consultar promociones
                            switch (tipoOrden.getTipoOrdenId()) {
                                case Constantes.TIPO_ORDEN_INSTALACION:
                                    cargarComboTiposDescuento();
                                    cargarComboPromocionesOrdenInstalacion();
                                    etiquetaImporte.setText(String.valueOf(ordenSeleccionada.getCosto()));
                                    break;
                                case Constantes.TIPO_ORDEN_SERVICIO:
                                    //cargarComboPromocionesOrdenInstalacion();
                                    break;
                                case Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO:
                                    //cargarComboPromocionesOrdenInstalacion();
                                    break;
                                default:
                                    break;
                            }

                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
                    }
                    //} else {
                    //JOptionPane.showMessageDialog(cobroPanel, "Fecha fin debe ser mayor o igual a fecha inicio. Por favor verifique", "", JOptionPane.WARNING_MESSAGE);
                    //}
                    //}

                }
            }
        };
        botonBusquedaOrden.addActionListener(buscarOrdenActionListener);

        KeyListener enterTablaSuscriptoresListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int selectedRow = tablaSuscriptores.getSelectedRow();
                    if (tablaSuscriptores.getModel().getValueAt(selectedRow, 0).toString().contains("N/A")) {

                        JOptionPane.showMessageDialog(cobroPanel, "El suscriptor " + tablaSuscriptores.getModel().getValueAt(selectedRow, 2) + " no cuenta aún con un contrato registrado", "", JOptionPane.WARNING_MESSAGE);

                    } else {
                        Long contratoId = (Long) tablaSuscriptores.getModel().getValueAt(selectedRow, 0);
                        System.out.println("contrato seleccionado: " + contratoId);
                        if (!suscriptoresConsultaList.isEmpty()) {
                            if (suscriptoresConsultaList.stream().filter(s -> s.getContrato().longValue() == contratoId.longValue()).findAny().isPresent()) {
                                Suscriptor suscriptor = suscriptoresConsultaList.stream().filter(s -> s.getContrato().longValue() == contratoId.longValue()).findFirst().get();
                                cargarDatosSuscriptor(suscriptor);
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
                    if (tablaSuscriptores.getModel().getValueAt(row, 0).toString().contains("N/A")) {

                        JOptionPane.showMessageDialog(cobroPanel, "El suscriptor " + tablaSuscriptores.getModel().getValueAt(row, 2) + " no cuenta aún con un contrato registrado", "", JOptionPane.WARNING_MESSAGE);

                    } else {
                        Long contratoId = (Long) tablaSuscriptores.getModel().getValueAt(row, 0);
                        System.out.println("contrato seleccionado: " + contratoId);
                        if (!suscriptoresConsultaList.isEmpty()) {
                            if (suscriptoresConsultaList.stream().filter(s -> s.getContrato().longValue() == contratoId.longValue()).findAny().isPresent()) {
                                Suscriptor suscriptor = suscriptoresConsultaList.stream().filter(s -> s.getContrato().longValue() == contratoId.longValue()).findFirst().get();
                                //validar el estatus del contrato
                                if (suscriptor.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO
                                        && suscriptor.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CANCELADO_RETIRADO) {
                                    cargarDatosSuscriptor(suscriptor);
                                } else {
                                    JOptionPane.showMessageDialog(cobroPanel, "El suscriptor " + tablaSuscriptores.getModel().getValueAt(row, 2) + " ya ha cancelado el contrato", "", JOptionPane.WARNING_MESSAGE);
                                }
                            }
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

        ActionListener botonBusquedaActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!campoBusqueda.getText().isEmpty()) {
                    posFrame.mostrarLoading();
                    try {
                        TipoBusquedaCobro tipoBusquedaCobro = (TipoBusquedaCobro) comboTiposBusqueda.getModel().getSelectedItem();
                        EstatusSuscriptorEntity estatus = (EstatusSuscriptorEntity) comboEstatusSuscriptor.getModel().getSelectedItem();
                        suscriptoresConsultaList = controller.consultarSuscriptores(tipoBusquedaCobro, campoBusqueda.getText(), estatus);

                        DefaultTableModel model = (DefaultTableModel) tablaSuscriptores.getModel();
                        model.getDataVector().clear();
                        suscriptorSeleccionado = null;
                        cargarTablaSuscriptores(model, suscriptoresConsultaList);
                        comboPromociones.removeAllItems();
                        comboPromociones.setEnabled(false);

                    } catch (NoSuchElementException ex) {
                        JOptionPane.showMessageDialog(cobroPanel, "No se encontraron suscriptores con la informacion recibida", "", JOptionPane.WARNING_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
                    }
                    posFrame.ocultarLoading();
                } else {
                    JOptionPane.showMessageDialog(cobroPanel, "Por favor ingrese texto a buscar", "", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        botonBusquedaSuscriptor.addActionListener(botonBusquedaActionListener);

    }

    /**
     *
     * @param suscriptor
     */
    private void cargarDatosSuscriptor(Suscriptor suscriptor) {

        limpiarInformacionContrato();
        limpiarOrdenSeleccionada();
        cargarComboTiposOrden(suscriptor.getEstatusContratoId());

        suscriptorSeleccionado = suscriptor;

        campoContrato.setText(String.valueOf(suscriptor.getContrato()));
        if (suscriptor.getContratoAnterior() != null) {
            campoContratoAnterior.setText(String.valueOf(suscriptor.getContratoAnterior()));
        }
        campoDomicilio.setText(String.valueOf(suscriptor.getDomicilio()));
        campoEstatusContrato.setText(String.valueOf(suscriptor.getEstatusContrato()));
        campoServicio.setText(String.valueOf(suscriptor.getServicio()));
        StringBuilder nombre = new StringBuilder();
        nombre.append(suscriptor.getNombre());
        nombre.append(" ");
        nombre.append(suscriptor.getApellidoPaterno());
        nombre.append(" ");
        nombre.append(suscriptor.getApellidoMaterno());
        campoSuscriptor.setText(nombre.toString());
        campoTelefono.setText(String.valueOf(suscriptor.getTelefono()));
        campoFechaPago.setText(suscriptor.getFechaProximoPago());
        comboTiposOrden.setEnabled(true);

        //System.out.println("Seelccionado: "+suscriptor.getId());
    }

    /**
     *
     * @param model
     * @param list
     * @throws Exception
     */
    private void cargarTablaSuscriptores(DefaultTableModel model, List<Suscriptor> list) throws Exception {

        limpiarInformacionContrato();
        comboTiposOrden.setEnabled(false);
        if (!list.isEmpty()) {

            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (Suscriptor c : list) {
                model.addRow(new Object[]{c.getContrato() == null ? "N/A" : c.getContrato(),
                    c.getContratoAnterior() == null ? "N/A" : c.getContratoAnterior(),
                    c.getNombre().concat(" ").concat(c.getApellidoPaterno()).concat(" ").concat(c.getApellidoMaterno()),
                    c.getServicio() == null ? "N/A" : c.getServicio(),
                    c.getDomicilio() == null ? "Domicilio no registrado" : c.getDomicilio(),
                    c.getEstatusContrato() == null ? "N/A" : c.getEstatusContrato()});
            }
        } else {
            JOptionPane.showMessageDialog(cobroPanel, "No se encontraron suscriptores con la información solicitada", "", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     *
     * @param model
     * @param list
     * @throws Exception
     */
    private void cargarTablaOrdenes(DefaultTableModel model, List<Orden> list) throws Exception {

        if (!list.isEmpty()) {

            model.getDataVector().clear();
            model.fireTableDataChanged();
            for (Orden o : list) {
                model.addRow(new Object[]{
                    o.getId(),
                    o.getContratoId(),
                    o.getTipoOrden(),
                    o.getFechaRegistro(),
                    o.getCosto()});
            }
        } else {
            JOptionPane.showMessageDialog(cobroPanel, "No se encontraron ordenes asociadas al contrato seleccionado", "", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     *
     */
    private void cargarComboPromocionesOrdenInstalacion() {

        try {
            List<PromocionOrdenInstalacion> promociones = controller
                    .consultarPromocionesOrdenes(suscriptorSeleccionado.getSucursalId(), suscriptorSeleccionado.getServicioId());
            promociones.forEach(p -> comboPromociones.addItem(p));
            comboPromociones.setEnabled(true);

        } catch (Exception ex) {
            comboPromociones.removeAllItems();
            comboPromociones.setEnabled(false);
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     *
     */
    private void cargarComboTiposDescuento() {

        try {

            List<TipoDescuento> list = controller.consultarTiposDescuento();
            list.forEach(td -> comboTiposDescuento.addItem(td));
            comboTiposDescuento.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     *
     */
    private void cargarComboEstatusSuscriptor() {

        try {

            List<EstatusSuscriptorEntity> list = controller.consultarEstatusSuscriptor();
            list.forEach(e -> comboEstatusSuscriptor.addItem(e));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     *
     */
    private void cargarComboTiposOrden(Long estatusContratoId) {

        comboTiposOrden.removeAllItems();
        listTiposOrden.clear();

        if (estatusContratoId == null) {
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_INSTALACION, "Orden de Instalación"));
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO, "Orden de Cambio de Domicilio"));
        } else {
            if (estatusContratoId == Constantes.ESTATUS_CONTRATO_NUEVO) {
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_INSTALACION, "Orden de Instalación"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CORTESIA) {
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_INSTALACION, "Orden de Instalación"));
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO, "Orden de Cambio de Domicilio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_ACTIVO) {
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO, "Orden de Cambio de Domicilio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CORTE) {
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_PENDIENTE_INSTALAR) {
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_RECONEXION) {
                listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
            }
        }

        listTiposOrden.forEach(to -> comboTiposOrden.addItem(to));

    }

    /**
     *
     */
    private void cargarComboTiposBusqueda() {

        List<TipoBusquedaCobro> list = new ArrayList<>();
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_CONTRATO, "Por Contrato"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_CONTRATO_ANTERIOR, "Por Contrato Anterior"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_NOMBRE, "Por Nombre"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_APELLIDO_PATERNO, "Por Apellido Paterno"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_APELLIDO_MATERNO, "Por Apellido Materno"));
        list.add(new TipoBusquedaCobro(Constantes.TIPO_BUSQUEDA_DOMICILIO, "Por Domicilio"));
        list.forEach(tb -> comboTiposBusqueda.addItem(tb));

    }

    /**
     *
     */
    public void cargarDatosSesion() {

        //header
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());

        //logo
        ImageIcon imagen = new ImageIcon("src/main/resources/logo_grande.jpg");
        Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(/*etiquetaLogo.getWidth(), etiquetaLogo.getHeight()*/320, 130, Image.SCALE_DEFAULT));
        etiquetaLogo.setIcon(icono);

        //seccion suscriptores y contrato
        campoSuscriptor.setEditable(false);
        campoContrato.setEditable(false);
        campoContratoAnterior.setEditable(false);
        campoEstatusContrato.setEditable(false);
        campoServicio.setEditable(false);
        campoDomicilio.setEditable(false);
        campoTelefono.setEditable(false);
        campoSuscriptor.setEditable(false);
        campoFechaPago.setEditable(false);
        campoContrato.setEditable(false);
        campoContratoAnterior.setEditable(false);
        campoEstatusContrato.setEditable(false);
        campoServicio.setEditable(false);
        campoDomicilio.setEditable(false);
        campoTelefono.setEditable(false);
        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setViewportView(tablaSuscriptores);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //tablaSuscriptores.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaSuscriptores.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaSuscriptores.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaSuscriptores.getColumnModel().getColumn(2).setPreferredWidth(270);
        tablaSuscriptores.getColumnModel().getColumn(3).setPreferredWidth(190);
        tablaSuscriptores.getColumnModel().getColumn(4).setPreferredWidth(380);
        tablaSuscriptores.getColumnModel().getColumn(5).setPreferredWidth(130);

        //seccion promociones
        comboPromociones.setEnabled(false);

        //seccion descuentos
        campoMotivoDescuento.setText("");
        campoDescuentoAplicado.setText("");
        campoImporteDescuento.setText("");
        campoDescuentoAplicado.setEditable(false);
        comboTiposDescuento.setEnabled(false);

        //seccion importe
        etiquetaPromoActiva.setVisible(false);

        //chooserIni.setBounds(panelCalendarioIni.getX(), panelCalendarioIni.getY(),160,26);
        //chooserFin.setBounds(panelCalendarioFin.getX(), panelCalendarioFin.getY(),160,26);
        //panelCalendarioIni.add(chooserIni);
        //panelCalendarioFin.add(chooserFin);
    }

    private void limpiarOrdenSeleccionada() {

        ordenSeleccionada = null;
        comboPromociones.removeAllItems();
        etiquetaImporte.setText("0.00");
        etiquetaPromoActiva.setVisible(false);
        campoMotivoDescuento.setText("");
        campoDescuentoAplicado.setText("");
        campoImporteDescuento.setText("");
        comboTiposDescuento.setEnabled(false);
        comboPromociones.setEnabled(false);
        DefaultTableModel modelOrdenes = (DefaultTableModel) tablaOrdenes.getModel();
        modelOrdenes.getDataVector().clear();
        modelOrdenes.fireTableDataChanged();

    }

    private void limpiarInformacionContrato() {
        suscriptorSeleccionado = null;
        campoSuscriptor.setText("");
        campoContrato.setText("");
        campoContratoAnterior.setText("");
        campoEstatusContrato.setText("");
        campoServicio.setText("");
        campoDomicilio.setText("");
        campoTelefono.setText("");
        campoFechaPago.setText("");
    }

    private void limpiarPantalla() {
        campoBusqueda.setText("");
        DefaultTableModel modelSuscriptores = (DefaultTableModel) tablaSuscriptores.getModel();
        modelSuscriptores.getDataVector().clear();
        DefaultTableModel modelOrdenes = (DefaultTableModel) tablaOrdenes.getModel();
        modelOrdenes.getDataVector().clear();
        limpiarInformacionContrato();
        comboPromociones.removeAllItems();
        comboTiposDescuento.removeAllItems();
        borrarDescuento();
        suscriptorSeleccionado = null;
        ordenSeleccionada = null;
    }
    
    private void limpiarPantallaCobro() {
        
        limpiarInformacionContrato();
        limpiarOrdenSeleccionada();
        
        campoBusqueda.setText("");
        DefaultTableModel modelSuscriptores = (DefaultTableModel) tablaSuscriptores.getModel();
        modelSuscriptores.getDataVector().clear();
        modelSuscriptores.fireTableDataChanged();
        DefaultTableModel modelOrdenes = (DefaultTableModel) tablaOrdenes.getModel();
        modelOrdenes.getDataVector().clear();
        modelOrdenes.fireTableDataChanged();
        comboPromociones.removeAllItems();
        comboTiposDescuento.removeAllItems();
        borrarDescuento();
        suscriptorSeleccionado = null;
        ordenSeleccionada = null;
    }

    private void borrarDescuento() {
        campoMotivoDescuento.setText("");
        campoDescuentoAplicado.setText("0.00");
        campoImporteDescuento.setText("");
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
        panelBusqueda = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaSuscriptores = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        comboEstatusSuscriptor = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        campoBusqueda = new javax.swing.JTextField();
        botonBusquedaSuscriptor = new javax.swing.JButton();
        etiquetaLogo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        comboTiposBusqueda = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaOrdenes = new javax.swing.JTable();
        panelInfoContrato = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        campoSuscriptor = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        campoContrato = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        campoContratoAnterior = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        campoServicio = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        campoDomicilio = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        campoEstatusContrato = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        campoTelefono = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        campoFechaPago = new javax.swing.JTextField();
        panelInfoOrdenes = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        comboTiposOrden = new javax.swing.JComboBox<>();
        botonBusquedaOrden = new javax.swing.JButton();
        botonRegresar = new javax.swing.JButton();
        botonCobrar = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        comboTiposDescuento = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        campoMotivoDescuento = new javax.swing.JTextField();
        botonAplicarDescuento = new javax.swing.JButton();
        etiquetaImporte = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        campoDescuentoAplicado = new javax.swing.JTextField();
        BotonEliminarDescuento = new javax.swing.JButton();
        panelPromociones = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        comboPromociones = new javax.swing.JComboBox<>();
        BotonAplicarPromocion = new javax.swing.JButton();
        BotonEliminarPromocion = new javax.swing.JButton();
        etiquetaPromoActiva = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        campoImporteDescuento = new javax.swing.JTextField();

        setMaximumSize(new java.awt.Dimension(1500, 900));
        setMinimumSize(new java.awt.Dimension(1500, 900));
        setPreferredSize(new java.awt.Dimension(1500, 900));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(etiquetaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
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

        panelBusqueda.setBackground(new java.awt.Color(255, 255, 255));
        panelBusqueda.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(163, 73, 164), null, null));
        panelBusqueda.setMaximumSize(new java.awt.Dimension(1499, 300));
        panelBusqueda.setMinimumSize(new java.awt.Dimension(1499, 300));

        tablaSuscriptores.setBackground(new java.awt.Color(204, 204, 204));
        tablaSuscriptores.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaSuscriptores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Número de Contrato", "Número de Contrato Anterior", "Nombre Suscriptor", "Servicio Contratado", "Domicilio Contrato", "Estatus Contrato"
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
        tablaSuscriptores.setMaximumSize(new java.awt.Dimension(1438, 170));
        tablaSuscriptores.setMinimumSize(new java.awt.Dimension(1438, 170));
        tablaSuscriptores.setPreferredSize(new java.awt.Dimension(1438, 170));
        tablaSuscriptores.setRowHeight(22);
        tablaSuscriptores.setShowGrid(false);
        tablaSuscriptores.getTableHeader().setReorderingAllowed(false);
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

        comboEstatusSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Texto a buscar:");

        campoBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonBusquedaSuscriptor.setBackground(new java.awt.Color(227, 126, 75));
        botonBusquedaSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonBusquedaSuscriptor.setForeground(new java.awt.Color(255, 255, 255));
        botonBusquedaSuscriptor.setText("Buscar Suscriptor");
        botonBusquedaSuscriptor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBusquedaSuscriptorActionPerformed(evt);
            }
        });

        etiquetaLogo.setBackground(new java.awt.Color(255, 255, 255));
        etiquetaLogo.setInheritsPopupMenu(false);
        etiquetaLogo.setMaximumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setMinimumSize(new java.awt.Dimension(410, 88));
        etiquetaLogo.setPreferredSize(new java.awt.Dimension(410, 88));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Estatus Suscriptor:");

        comboTiposBusqueda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelBusquedaLayout = new javax.swing.GroupLayout(panelBusqueda);
        panelBusqueda.setLayout(panelBusquedaLayout);
        panelBusquedaLayout.setHorizontalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelBusquedaLayout.createSequentialGroup()
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelBusquedaLayout.createSequentialGroup()
                                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelBusquedaLayout.createSequentialGroup()
                                        .addComponent(comboTiposBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(comboEstatusSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(campoBusqueda))
                                .addGap(119, 119, 119)
                                .addComponent(botonBusquedaSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(101, 101, 101))
                            .addGroup(panelBusquedaLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)))
                        .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBusquedaLayout.setVerticalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(etiquetaLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelBusquedaLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboEstatusSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboTiposBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonBusquedaSuscriptor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(campoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        tablaOrdenes.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tablaOrdenes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Folio Orden", "Contrato", "Tipo Orden", "Fecha Registro", "Costo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaOrdenes.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tablaOrdenes);
        if (tablaOrdenes.getColumnModel().getColumnCount() > 0) {
            tablaOrdenes.getColumnModel().getColumn(0).setResizable(false);
            tablaOrdenes.getColumnModel().getColumn(1).setResizable(false);
            tablaOrdenes.getColumnModel().getColumn(2).setResizable(false);
            tablaOrdenes.getColumnModel().getColumn(3).setResizable(false);
            tablaOrdenes.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 0, 0));
        jLabel17.setText("Información del contrato");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Nombre del suscriptor:");

        campoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Número de contrato:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Contrato Sistema Anterior:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Servicio contratado:");

        campoServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Domicilio registrado:");

        campoDomicilio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Estatus:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("Teléfono:");

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setText("Fecha de Pago:");

        campoFechaPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelInfoContratoLayout = new javax.swing.GroupLayout(panelInfoContrato);
        panelInfoContrato.setLayout(panelInfoContratoLayout);
        panelInfoContratoLayout.setHorizontalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInfoContratoLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(campoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoContratoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoEstatusContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(112, Short.MAX_VALUE))
        );
        panelInfoContratoLayout.setVerticalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(campoContratoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(campoEstatusContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(campoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(campoTelefono))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        panelInfoOrdenes.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("Búsqueda de Orden");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Seleccionar Tipo de Orden:");

        comboTiposOrden.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        comboTiposOrden.setEnabled(false);

        botonBusquedaOrden.setBackground(new java.awt.Color(227, 126, 75));
        botonBusquedaOrden.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonBusquedaOrden.setForeground(new java.awt.Color(255, 255, 255));
        botonBusquedaOrden.setText("Buscar Ordenes");

        javax.swing.GroupLayout panelInfoOrdenesLayout = new javax.swing.GroupLayout(panelInfoOrdenes);
        panelInfoOrdenes.setLayout(panelInfoOrdenesLayout);
        panelInfoOrdenesLayout.setHorizontalGroup(
            panelInfoOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoOrdenesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelInfoOrdenesLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(comboTiposOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(botonBusquedaOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        panelInfoOrdenesLayout.setVerticalGroup(
            panelInfoOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoOrdenesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboTiposOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonBusquedaOrden))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        botonCobrar.setBackground(new java.awt.Color(0, 153, 51));
        botonCobrar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonCobrar.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrar.setText("Cobrar");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setText("Motivo del Descuento:");

        comboTiposDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        comboTiposDescuento.setEnabled(false);

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setText("Seleccionar Descuento:");

        campoMotivoDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonAplicarDescuento.setBackground(new java.awt.Color(227, 126, 75));
        botonAplicarDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonAplicarDescuento.setForeground(new java.awt.Color(255, 255, 255));
        botonAplicarDescuento.setText("Aplicar Descuento");

        etiquetaImporte.setFont(new java.awt.Font("Segoe UI", 0, 80)); // NOI18N
        etiquetaImporte.setForeground(java.awt.Color.red);
        etiquetaImporte.setText("0.00");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 80)); // NOI18N
        jLabel23.setForeground(java.awt.Color.red);
        jLabel23.setText("$");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("Descuento aplicado:");

        campoDescuentoAplicado.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoDescuentoAplicado.setText("0.00");

        BotonEliminarDescuento.setBackground(new java.awt.Color(255, 51, 51));
        BotonEliminarDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        BotonEliminarDescuento.setForeground(new java.awt.Color(255, 255, 255));
        BotonEliminarDescuento.setText("Eliminar Descuento");

        panelPromociones.setBackground(new java.awt.Color(255, 255, 255));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 0, 0));
        jLabel24.setText("Promociones");
        jLabel24.setToolTipText("");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel25.setText("Seleccionar promoción:");

        BotonAplicarPromocion.setBackground(new java.awt.Color(227, 126, 75));
        BotonAplicarPromocion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        BotonAplicarPromocion.setForeground(new java.awt.Color(255, 255, 255));
        BotonAplicarPromocion.setText("Aplicar Promocion");

        BotonEliminarPromocion.setBackground(java.awt.Color.red);
        BotonEliminarPromocion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        BotonEliminarPromocion.setForeground(new java.awt.Color(255, 255, 255));
        BotonEliminarPromocion.setText("Eliminar Promoción");

        javax.swing.GroupLayout panelPromocionesLayout = new javax.swing.GroupLayout(panelPromociones);
        panelPromociones.setLayout(panelPromocionesLayout);
        panelPromocionesLayout.setHorizontalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BotonAplicarPromocion)
                        .addGap(18, 18, 18)
                        .addComponent(BotonEliminarPromocion)))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        panelPromocionesLayout.setVerticalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonAplicarPromocion)
                    .addComponent(BotonEliminarPromocion))
                .addGap(12, 12, 12))
        );

        etiquetaPromoActiva.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaPromoActiva.setForeground(java.awt.Color.red);
        etiquetaPromoActiva.setText("Precio Promoción!!");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setText("Importe:");

        campoImporteDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel20)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(comboTiposDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(66, 66, 66)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(campoImporteDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(campoMotivoDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
                            .addComponent(campoDescuentoAplicado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BotonEliminarDescuento, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(botonAplicarDescuento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1057, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(botonCobrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(botonRegresar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(etiquetaPromoActiva, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelBusqueda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelInfoContrato, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCabecero, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1531, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelInfoOrdenes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelPromociones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelPromociones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelInfoOrdenes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(etiquetaPromoActiva, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboTiposDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoImporteDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoMotivoDescuento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoDescuentoAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(botonAplicarDescuento)
                                .addGap(12, 12, 12)
                                .addComponent(BotonEliminarDescuento))))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(botonRegresar)
                            .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(48, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void botonBusquedaSuscriptorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBusquedaSuscriptorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonBusquedaSuscriptorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonAplicarPromocion;
    private javax.swing.JButton BotonEliminarDescuento;
    private javax.swing.JButton BotonEliminarPromocion;
    private javax.swing.JButton botonAplicarDescuento;
    private javax.swing.JButton botonBusquedaOrden;
    private javax.swing.JButton botonBusquedaSuscriptor;
    private javax.swing.JButton botonCobrar;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoBusqueda;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoContratoAnterior;
    private javax.swing.JTextField campoDescuentoAplicado;
    private javax.swing.JTextField campoDomicilio;
    private javax.swing.JTextField campoEstatusContrato;
    private javax.swing.JTextField campoFechaPago;
    private javax.swing.JTextField campoImporteDescuento;
    private javax.swing.JTextField campoMotivoDescuento;
    private javax.swing.JTextField campoServicio;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JComboBox<EstatusSuscriptorEntity> comboEstatusSuscriptor;
    private javax.swing.JComboBox<PromocionOrdenInstalacion> comboPromociones;
    private javax.swing.JComboBox<TipoBusquedaCobro> comboTiposBusqueda;
    private javax.swing.JComboBox<TipoDescuento> comboTiposDescuento;
    private javax.swing.JComboBox<TipoOrden> comboTiposOrden;
    private javax.swing.JLabel etiquetaImporte;
    private javax.swing.JLabel etiquetaLogo;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaPromoActiva;
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
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelInfoContrato;
    private javax.swing.JPanel panelInfoOrdenes;
    private javax.swing.JPanel panelPromociones;
    private javax.swing.JTable tablaOrdenes;
    private javax.swing.JTable tablaSuscriptores;
    // End of variables declaration//GEN-END:variables
}
