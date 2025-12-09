/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CobroController;
import mx.com.tvch.pos.controller.ServicioController;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.PromocionEntity;
import mx.com.tvch.pos.entity.ServicioEntity;
import mx.com.tvch.pos.entity.TipoDescuentoEntity;
import mx.com.tvch.pos.model.CobroServicio;
import mx.com.tvch.pos.model.DescuentoCobro;
import mx.com.tvch.pos.model.Mes;
import mx.com.tvch.pos.model.OrdenAgregadaPago;
import mx.com.tvch.pos.model.PromocionCobro;
import mx.com.tvch.pos.model.TipoOrden;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Impresora;
import mx.com.tvch.pos.util.TvchException;
import mx.com.tvch.pos.util.Utilerias;
import mx.com.tvch.pos.util.VentanaEnum;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CobrosPanel extends javax.swing.JPanel {

    private static CobrosPanel cobroPanel;
    private static PosFrame posFrame;

    private final Sesion sesion;
    private final CobroController controller;
    private final ServicioController servicioController;
    private final Utilerias util;
    private final Impresora impresora;

    //List<ContratoxSuscriptorDetalleEntity> suscriptoresConsultaList;
    private ContratoxSuscriptorDetalleEntity suscriptorSeleccionado;
    //private List<DetallePagoServicio> listaDetallesPago;
    private CobroServicio cobroCapturado;
    private PromocionEntity promocionSeleccionada;
    private Mes mesGuardado;
    private int anioGuardado;
    List<TipoOrden> listTiposOrden = new ArrayList<>();
    List<TipoOrden> listTiposOrdenServicio = new ArrayList<>();
    List<OrdenAgregadaPago> listOrdenesAgregadas = new ArrayList<>();

    org.slf4j.Logger logger = LoggerFactory.getLogger(CobrosPanel.class);

    public static CobrosPanel getCobroPanel(PosFrame frame) {
        if (cobroPanel == null) {
            cobroPanel = new CobrosPanel();
        }
        posFrame = frame;
        return cobroPanel;
    }

    /**
     * Creates new form CobroPanel
     */
    public CobrosPanel() {
        initComponents();

        sesion = Sesion.getSesion();
        controller = CobroController.getCobroController();
        servicioController = ServicioController.getServicioController();
        util = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        //suscriptoresConsultaList = new ArrayList<>();
        cargarComboTiposDescuento();
        crearEventos();
        
    }

    private void crearEventos() {
        
        /**
         * 
         */
        campoMonto.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                etiquetaImporte.setText(campoMonto.getText());
            }
        });
       
        /**
         * 
         */
        ActionListener botonRestablecerMontoSugeridoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(suscriptorSeleccionado != null){
                    setCamposPagoDefault();
                    Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                    int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
                    if(comboPromociones.getModel().getSize() > 0)
                        comboPromociones.setSelectedIndex(0);
                    actualizarInformacionPago(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, true);
                }
            }
        };
        botonReestablecerMonto.addActionListener(botonRestablecerMontoSugeridoActionListener);
        
        /**
         * 
         */
        ActionListener botonCalcularPagoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(suscriptorSeleccionado != null){
                    //primero guardar el indice del mes seleccionado                    
                    Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                    int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem(); 
                    if(util.esFechaPagoValida(
                            suscriptorSeleccionado, mesSeleccionado, anioSeleccionado)){
                        if(comboPromociones.getModel().getSize() > 0)
                            comboPromociones.setSelectedIndex(0);
                        actualizarInformacionPago(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
                    }else{
                        comboMeses.setSelectedItem(mesGuardado);
                        comboAnios.setSelectedItem(anioGuardado);
                        JOptionPane.showMessageDialog(cobroPanel, "La fecha de próximo pago debe ser de al menos un mes posterior al mes en curso \n. Por favor seleccione una fecha válida", "", JOptionPane.WARNING_MESSAGE);  
                    }
                }
            }
        };
        botonCalculaMonto.addActionListener(botonCalcularPagoActionListener);

        /**
         * 
         */
        ActionListener botonCobrarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if (suscriptorSeleccionado != null && cobroCapturado != null ) {
                    
                    Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                    int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem(); 
                    if(util.esFechaPagoValida(
                            suscriptorSeleccionado, mesSeleccionado, anioSeleccionado)){
                    
                        try {

                            //Primero realizar las validaciones necesarias
                            if(!checkServicio.isSelected() &&
                                    listOrdenesAgregadas.isEmpty())
                                throw new TvchException("Por favor, agregue información de cobro");

                            //que existan observaciones
                            if(campoObservaciones.getText().isEmpty())
                                throw new TvchException("Por favor, ingrese sus observaciones.");

                            //que las observaciones no pasen de los 200 caracteres
                            if(campoObservaciones.getText().length() > 200){
                                throw new TvchException("Por favor, ajuste sus observaciones a un máximo de 200 caracteres.");
                            }

                            //que el monto capturado se aun numero valido
                            Double montoPorCobrar = null;
                            try{
                                montoPorCobrar = Double.parseDouble(campoMonto.getText());
                            }catch(NumberFormatException nfe){
                                throw new TvchException("Por favor, ingrese un monto a cobrar válido");
                            }

                            //que el monto no sea menor a cero
                            if(montoPorCobrar < 0)
                                throw new TvchException("El monto a cobrar no puede ser menor a cero");

                            //que haya una promocion capturada, en ese caso se obtienen los datos 
                            if(promocionSeleccionada != null){
                                PromocionCobro promocion = new PromocionCobro();
                                promocion.setMesesGratis(promocionSeleccionada.getMesesGratis());
                                promocion.setCostoPromocion(promocionSeleccionada.getCostoPromocion());
                                promocion.setPromocionId(promocionSeleccionada.getPromocionId());
                                promocion.setDescripcion(promocionSeleccionada.getDescripcion());
                                cobroCapturado.setPromocion(promocion);
                                //cobroCapturado.setMontoTotal(promocionSeleccionada.getCostoPromocion());
                            }

                            //que en caso de que no exista promo y el monto a cobrar sea menor que el sugerido se 
                            //hayan capturado los datos del descuento 
                            //aqui mismo se obtienen los datos del descuento
                            if(montoPorCobrar < cobroCapturado.getMontoSugerido() && promocionSeleccionada == null){
                                if(campoMotivoDescuento.getText().isEmpty()){
                                    throw new TvchException("El monto capturado para ser cobrado es menor al sugerido por el sistema. \n"
                                        + "Por favor capture Tipo y Motivo de descuento antes de realizar el cobro");
                                }else{
                                    if(campoMotivoDescuento.getText().length() > 100){
                                        throw new TvchException("Por favor, ajuste su motivo de descuento a un m+aximo de 100 caracteres.");
                                    }else{
                                        double montoDescuento = cobroCapturado.getMontoSugerido() - montoPorCobrar;
                                        TipoDescuentoEntity tipoDescuento = (TipoDescuentoEntity) comboTipoDescuento.getSelectedItem();
                                        DescuentoCobro descuento = new DescuentoCobro();
                                        descuento.setMontoDescuento(montoDescuento);
                                        descuento.setMotivoDescuento(campoMotivoDescuento.getText());
                                        descuento.setTipoDescuentoId(tipoDescuento.getIdTipoDescuento());
                                        cobroCapturado.setDescuento(descuento);
                                    }             
                                }                          
                            }

                            //despues de las validaciones se obtienen el resto de los campos necesarios para hacer el pago
                            cobroCapturado.setMontoTotal(montoPorCobrar);
                            cobroCapturado.setCadenaMonto("$ ".concat(String.valueOf(montoPorCobrar)));
                            cobroCapturado.setObservaciones(campoObservaciones.getText());

                            Long transaccionId = null;
                     
                            boolean seAceptoPago = false;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Se realizará el cobro al contrato: ").append(suscriptorSeleccionado.getFolioContrato());
                            sb.append(" por un monto de ").append(cobroCapturado.getCadenaMonto());
                            sb.append("\n¿Los datos son correctos? \n");
                            int input = JOptionPane.showConfirmDialog(null, sb.toString());
                            if (input == 0) {
                                seAceptoPago = true;
                            }

                            if(seAceptoPago){

                                transaccionId = controller.cobrarServicio(suscriptorSeleccionado, cobroCapturado);
                                System.out.println("transaccionId: " + transaccionId);
                                
                                if(cobroCapturado.isSeCobraServicio() && cobroCapturado.isSeCobraRecargo() 
                                    && suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
                                    JOptionPane.showMessageDialog(cobroPanel, "El cobro se realizó correctamente y el estatus de su contrato "
                                            + "ha sido actualizado a RECONEXION \n"
                                            + "Por favor verifique.", "", JOptionPane.WARNING_MESSAGE);
                                }

                                try {
                                    impresora.imprimirTicketServicio(transaccionId, cobroCapturado, suscriptorSeleccionado, sesion.getSucursal()/*, numeroMeses*/);
                                } catch (Exception ex) {
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    ex.printStackTrace(pw);
                                    logger.error("Fallo al imprimir ticket de transaccion: \n" + sw.toString());
                                    JOptionPane.showMessageDialog(cobroPanel, "El cobro se realizó correctamente pero ocurrió un error al imprimir su ticket. Si desea una reimpresión vaya a sección de reimpresiones", "", JOptionPane.WARNING_MESSAGE);
                                }

                                limpiarPantalla();              
                                posFrame.cambiarPantalla(cobroPanel, VentanaEnum.CONSULTA_CONTRATOS);

                            }

                            

                        } catch (TvchException ex) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            logger.error("Error controlado al cobrar transaccion: \n" + sw.toString());
                            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
                        }catch (Exception ex) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            logger.error("Fallo al cobrar transaccion: \n" + sw.toString());
                            JOptionPane.showMessageDialog(cobroPanel, "Ocurrió un error al realizar el cobro, por favor reintente. Si el problema persiste consulte a soporte.", "", JOptionPane.WARNING_MESSAGE);
                        }
                    
                    }else{
                        comboMeses.setSelectedItem(mesGuardado);
                        comboAnios.setSelectedItem(anioGuardado);
                        JOptionPane.showMessageDialog(cobroPanel, "La fecha de próximo pago debe ser de al menos un mes posterior al mes en curso \n. Por favor seleccione una fecha válida", "", JOptionPane.WARNING_MESSAGE);  
                    }
                }

            }
        };
        botonCobrar.addActionListener(botonCobrarActionListener);

        /**
         * 
         */
        ActionListener botonEliminarPromocionActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suscriptorSeleccionado != null && promocionSeleccionada != null && cobroCapturado != null) {
                    eliminarPromocion();
                    JOptionPane.showMessageDialog(cobroPanel, "Promoción eliminada exitosamente.", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        botonEliminarPromocion.addActionListener(botonEliminarPromocionActionListener);

        /**
         * 
         */
        ActionListener botonAplicarPromocionActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (suscriptorSeleccionado != null && comboPromociones.getModel().getSize() > 0 ) {
                    
                    if(suscriptorSeleccionado.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CORTE &&
                            suscriptorSeleccionado.getEstatusContratoId() != Constantes.ESTATUS_CONTRATO_CORTESIA){               
                        PromocionEntity promocion = (PromocionEntity) comboPromociones.getModel().getSelectedItem();
                        
                        if(promocion.getMesesPagados() > 0){                  
                            setCamposPagoPromocion(promocion);
                            Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                            int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
                            promocionSeleccionada = promocion;
                            actualizarInformacionPago(promocion, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
                            JOptionPane.showMessageDialog(cobroPanel, "Promoción agregada exitosamente.", "", JOptionPane.INFORMATION_MESSAGE);
                            campoMonto.setEnabled(false);
                            comboMeses.setEnabled(false);
                            comboAnios.setEnabled(false);
                            comboTipoDescuento.setEnabled(false);
                            campoMotivoDescuento.setEnabled(false);
                            botonCalculaMonto.setEnabled(false);
                        }else{
                            JOptionPane.showMessageDialog(cobroPanel, "La promoción no está correctamente configurada. Por favor, contacte a soporte.", "", JOptionPane.WARNING_MESSAGE);
                        }
                            
                    }else{
                        JOptionPane.showMessageDialog(cobroPanel, "No es posible aplicar la promoción a contratos con estatus En corte o Cortesia.", "", JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        };
        botonAplicarPromocion.addActionListener(botonAplicarPromocionActionListener);

        /**
         * 
         */
        ActionListener botonRegresarActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPantalla();
                posFrame.cambiarPantalla(cobroPanel, VentanaEnum.CONSULTA_CONTRATOS);
            }
        };
        botonRegresar.addActionListener(botonRegresarActionListener);
        
        /**
         * 
         */
        ActionListener comboTiposOrdenActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                try{
                    if(comboTiposOrden.getItemCount() > 0){
                        TipoOrden tipoOrden = (TipoOrden) comboTiposOrden.getSelectedItem();
                        if(tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO){
                            campoCalle.setEnabled(true);
                            campoNumeroCalle.setEnabled(true);
                            campoColonia.setEnabled(true);
                            campoCiudad.setEnabled(true);
                            campoCalle1.setEnabled(true);
                            campoCalle2.setEnabled(true);
                            areaReferencia.setEnabled(true);
                            campoTvsExtra.setEnabled(false);
                            comboNuevoServicio.setEnabled(false);
                        }else{
                            campoCalle.setEnabled(false);
                            campoNumeroCalle.setEnabled(false);
                            campoColonia.setEnabled(false);
                            campoCiudad.setEnabled(false);
                            campoCalle1.setEnabled(false);
                            campoCalle2.setEnabled(false);
                            areaReferencia.setEnabled(false);
                        }
                        cargarComboTiposOrdenServicio(tipoOrden, suscriptorSeleccionado.getEstatusContratoId());
                    }
                }catch(Exception ex){
                    //se agrega este try por error detectado aun no replicado
                    //al parecer se corrige validando el count, ya no ha salido pero se deja 
                    logger.warn("Error en Action del como de tipos de orden");
                }
            }
        };
        comboTiposOrden.addActionListener(comboTiposOrdenActionListener);
        
        /**
         * 
         */
        ActionListener comboTiposOrdenServicioActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(comboTiposOrdenServicio.getItemCount() > 0){
                    TipoOrden tipoOrden = (TipoOrden) comboTiposOrdenServicio.getSelectedItem();
                    if(tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL){
                        campoTvsExtra.setEnabled(true);
                        comboNuevoServicio.setEnabled(false);
                    }else if(tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN){
                        comboNuevoServicio.setEnabled(true);
                        campoTvsExtra.setEnabled(false);
                    }else{
                        comboNuevoServicio.setEnabled(false);
                        campoTvsExtra.setEnabled(false);
                    }
                }
                
            }
        };
        comboTiposOrdenServicio.addActionListener(comboTiposOrdenServicioActionListener);
        
         /**
         * 
         */
        ActionListener botonAgregarOrdenActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboTiposOrden.getItemCount() > 0){
                    if(listOrdenesAgregadas.size() < 3){
                        if(campoObservacionesOrden.getText() != null && !campoObservacionesOrden.getText().isEmpty() &&
                                campoObservacionesOrden.getText().length() < 150){
                            
                            if(campoCostoOrden.getText() != null && !campoCostoOrden.getText().isEmpty()){
                                
                                boolean esCostoOrdenValido = false;
                                double costoOrden = 0;
                                try{
                                    costoOrden = Double.parseDouble(campoCostoOrden.getText());
                                    if(costoOrden >= 0)
                                        esCostoOrdenValido = true;
                                }catch(Exception ex){
                                    
                                }
                                
                                if(esCostoOrdenValido){
                                    TipoOrden tipoOrden = (TipoOrden) comboTiposOrden.getSelectedItem();
                                    agregarOrden(tipoOrden, costoOrden);
                                    Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                                    int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
                                    if(promocionSeleccionada != null)
                                        actualizarInformacionPago(promocionSeleccionada, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
                                    else
                                        actualizarInformacionPago(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
                                }else{
                                    JOptionPane.showMessageDialog(cobroPanel, 
                                        "Formato de costo de la orden inválido. Por favor agregue un número mayor o igual a cero", "", JOptionPane.WARNING_MESSAGE);
                                }
 
                            }else{
                                JOptionPane.showMessageDialog(cobroPanel, 
                                "Para agregar la orden es necesario que ingrese su costo", "", JOptionPane.WARNING_MESSAGE);
                            }
                            
                        }else{
                            JOptionPane.showMessageDialog(cobroPanel, 
                                "Para agregar la orden debe ingresar observaciones y estas no deben ser mayores a 150 caracteres. \n"
                                        + "Por favor verifique.", "", JOptionPane.WARNING_MESSAGE);
                        }
                    }else{
                        JOptionPane.showMessageDialog(cobroPanel, 
                        "Ha alcanzado el máximo de órdenes por transacción - Máx(3)", "", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
            }
        };
        botonAgregarOrden.addActionListener(botonAgregarOrdenActionListener);
        
         /**
         * 
         */
        ActionListener botonEliminarOrdenActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!listOrdenesAgregadas.isEmpty()){
                    
                    try{
                    
                        //int selectedRow = tablaOrdenes.getSelectedRow();
                        //if (selectedRow >= 0) {
                            //if(listOrdenesAgregadas.size() == 1){
                                listOrdenesAgregadas.clear();
                            //}else{
                                //Integer numeroOrden = (Integer) tablaOrdenes.getModel().getValueAt(selectedRow, 0);
                                //listOrdenesAgregadas.stream().filter(o -> o.getNumeroOrden() != numeroOrden).collect(Collectors.toList());
                            //}
                            
                            refrescarTablaOrdenesAgregadas();
                            Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                            int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
                            
                            if(promocionSeleccionada != null)
                                actualizarInformacionPago(promocionSeleccionada, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
                            else
                                actualizarInformacionPago(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
                        //}
                    
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(cobroPanel, 
                        "No se detectó alguna orden seleccionada", "", JOptionPane.WARNING_MESSAGE);
                    }
                    
                }
                
            }
        };
        botonLimpiarOrdenes.addActionListener(botonEliminarOrdenActionListener);
        
        
        /**
         * 
         */
        ItemListener checkServicioActionListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getSource() == checkServicio) {
                    if(cobroCapturado != null){
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            //cobroCapturado.setSeCobraServicio(true);
                            campoMonto.setEnabled(true);
                            comboMeses.setEnabled(true);
                            comboAnios.setEnabled(true);
                            botonCalculaMonto.setEnabled(true);
                            comboTipoDescuento.setEnabled(true);
                            campoMotivoDescuento.setEnabled(true);
                            comboPromociones.setEnabled(true);
                            botonAplicarPromocion.setEnabled(true);
                            botonEliminarPromocion.setEnabled(true);
                            
                        } else {
                            //cobroCapturado.setSeCobraServicio(false);
                            campoMonto.setEnabled(false);
                            comboMeses.setEnabled(false);
                            comboAnios.setEnabled(false);
                            botonCalculaMonto.setEnabled(false);
                            comboTipoDescuento.setEnabled(false);
                            campoMotivoDescuento.setEnabled(false);
                            comboPromociones.setEnabled(false);
                            botonAplicarPromocion.setEnabled(false);
                            botonEliminarPromocion.setEnabled(false);
                        }
                        eliminarPromocion();
                        Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                        int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
                        actualizarInformacionPago(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
                    }
                }
            }
        };
        checkServicio.addItemListener(checkServicioActionListener);

    }
    
    /**
     * 
     */
    private void eliminarPromocion(){
        
        setCamposPagoDefault();
        Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
        int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
        if(comboPromociones.getModel().getSize() > 0)
            comboPromociones.setSelectedIndex(0);
        promocionSeleccionada = null;
        actualizarInformacionPago(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, true);
        if(checkServicio.isSelected()){
            campoMonto.setEnabled(true);
            comboMeses.setEnabled(true);
            comboAnios.setEnabled(true);
            comboTipoDescuento.setEnabled(true);
            campoMotivoDescuento.setEnabled(true);
            botonCalculaMonto.setEnabled(true);
        }
        
    }
    
    /**
     * 
     * @param tipoOrden 
     */
    private void agregarOrden(TipoOrden tipoOrden, double costo){
        
        OrdenAgregadaPago ordenAgregadaPago = new OrdenAgregadaPago();
        boolean seAgregaOrden = true;
                
        if(listOrdenesAgregadas.isEmpty()){
            ordenAgregadaPago.setNumeroOrden(1);
        }else{
            ordenAgregadaPago.setNumeroOrden(listOrdenesAgregadas.size()+1);
        }
        
        ordenAgregadaPago.setContratoId(suscriptorSeleccionado.getContratoId());
        ordenAgregadaPago.setDomicilioId(suscriptorSeleccionado.getDomicilioId());
        ordenAgregadaPago.setSuscriptorId(suscriptorSeleccionado.getSusucriptorId());
        ordenAgregadaPago.setUsuarioId(sesion.getUsuarioId());
        ordenAgregadaPago.setTipoOrden(tipoOrden.getTipoOrdenId());
        ordenAgregadaPago.setTipoOrdenDesc(tipoOrden.getDescripcion());
        ordenAgregadaPago.setObservaciones(campoObservacionesOrden.getText());
        ordenAgregadaPago.setCosto(costo);
        ordenAgregadaPago.setServicioId(suscriptorSeleccionado.getServicioId());
        ordenAgregadaPago.setTvs(suscriptorSeleccionado.getTvsContratadas());
        ordenAgregadaPago.setSucursalId(sesion.getSucursalId());
        
        if(tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO ){
            if(comboTiposOrdenServicio.getItemCount() > 0){
                TipoOrden tipoOrdenServicio = (TipoOrden) comboTiposOrdenServicio.getSelectedItem();
                ordenAgregadaPago.setTipoOrdenServicioDesc(tipoOrdenServicio.getDescripcion());
                if(tipoOrdenServicio.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL){
                    try{
                        int tvs = Integer.parseInt(campoTvsExtra.getText());
                        ordenAgregadaPago.setTipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL);
                        ordenAgregadaPago.setTvsAdicionales(tvs);
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(cobroPanel, 
                        "Formato de tv extra inválido. Por favor ingrese un número válido", "", JOptionPane.WARNING_MESSAGE);
                        seAgregaOrden = false;
                    }
                }else if(tipoOrdenServicio.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN){
                    if(comboNuevoServicio.getItemCount() > 0){
                        ServicioEntity servicioEntity = (ServicioEntity) comboNuevoServicio.getSelectedItem();
                        ordenAgregadaPago.setTipoOrdenServicio(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN);
                        ordenAgregadaPago.setNuevoServicioId(servicioEntity.getServicioId());
                    }else{
                        JOptionPane.showMessageDialog(cobroPanel, 
                        "No se encontraton otros paquetes registrados en sistema. \n"
                                + "Por favor reinicie sistema, de persistir el problema contacte a soporte", "", JOptionPane.WARNING_MESSAGE);
                        seAgregaOrden = false;
                    }
                }else {
                    ordenAgregadaPago.setTipoOrdenServicio(tipoOrdenServicio.getTipoOrdenId());
                }
            }else{
                JOptionPane.showMessageDialog(cobroPanel, 
                        "No fue posible obtener el tipo de orden de servicio. \n "
                                + "Por favor reinice sistema, de persistir el problema contacte a soporte", "", JOptionPane.WARNING_MESSAGE);
                seAgregaOrden = false;
            }
        }else if(tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO ){
            //validar el tamaño de los campos
            List<String> errores = new ArrayList<>();
            
            if( campoCalle.getText().trim().isEmpty() || campoCalle.getText().length() > 120)
                errores.add("La calle debe tener una longitud de entre 1 y 120 caracteres  \n ");
            if( campoNumeroCalle.getText().trim().isEmpty() || campoNumeroCalle.getText().length() > 25)
                errores.add("El número de calle debe tener una longitud de entre 1 y 25 caracteres  \n ");
            if( campoColonia.getText().trim().isEmpty() || campoColonia.getText().length() > 70)
                errores.add("La colonia debe tener una longitud de entre 1 y 70 caracteres  \n ");
            if( campoCiudad.getText().trim().isEmpty() || campoCiudad.getText().length() > 70)
                errores.add("La ciudad debe tener una longitud de entre 1 y 70 caracteres  \n ");
            if( campoCalle1.getText().trim().isEmpty() || campoCalle1.getText().length() > 120)
                errores.add("La primera calle de referencia debe tener una longitud de entre 1 y 120 caracteres  \n ");
            if( campoCalle2.getText().trim().isEmpty() || campoCalle2.getText().length() > 120)
                errores.add("La segunda calle de referencia debe tener una longitud de entre 1 y 120 caracteres \n ");
            if( areaReferencia.getText().trim().isEmpty() || areaReferencia.getText().length() > 300)
                errores.add("La referencia de domicilio debe tener una longitud de entre 1 y 300 caracteres \n ");
            
            if(errores.isEmpty()){
                
                ordenAgregadaPago.setCalle(campoCalle.getText());
                ordenAgregadaPago.setNumeroCalle(campoNumeroCalle.getText());
                ordenAgregadaPago.setColonia(campoColonia.getText());
                ordenAgregadaPago.setCiudad(campoCiudad.getText());
                ordenAgregadaPago.setCalle1(campoCalle1.getText());
                ordenAgregadaPago.setCalle2(campoCalle2.getText());
                ordenAgregadaPago.setReferencia(areaReferencia.getText());
            
            }else{
                String cadenaErrores = "";
                for(String error : errores){
                    cadenaErrores = cadenaErrores.concat(error);
                }

                JOptionPane.showMessageDialog(cobroPanel, 
                        "La información de su orden de cambio de domicilio contiene los siguientes errores: \n"+cadenaErrores+" \n Por favor valide.",
                        "", JOptionPane.WARNING_MESSAGE);
                
                seAgregaOrden = false;
            }
            
            
            
        }
        
        //NOTA -> las observaciones y el costo las captura el cajero en la misma tabla
        
        if(seAgregaOrden){
            listOrdenesAgregadas.add(ordenAgregadaPago);
            refrescarTablaOrdenesAgregadas();
            
            //limpiar todos los campos editables de las ordenes
            campoObservacionesOrden.setText("");
            campoCostoOrden.setText("");
            campoCalle.setText("");
            campoCalle.setEnabled(false);
            campoNumeroCalle.setText("");
            campoNumeroCalle.setEnabled(false);
            campoColonia.setText("");
            campoColonia.setEnabled(false);
            campoCiudad.setText("");
            campoCiudad.setEnabled(false);
            campoCalle1.setText("");
            campoCalle1.setEnabled(false);
            campoCalle2.setText("");
            campoCalle2.setEnabled(false);
            areaReferencia.setText("");
            areaReferencia.setEnabled(false);
            
            if(comboTiposOrden.getItemCount() > 0)
                comboTiposOrden.setSelectedIndex(0);
            if(comboTiposOrdenServicio.getItemCount() > 0)
                comboTiposOrdenServicio.setSelectedIndex(0);
            
        }
        
    }
    
    /**
     * 
     */
    private void refrescarTablaOrdenesAgregadas(){
        
        DefaultTableModel model = (DefaultTableModel) tablaOrdenes.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
        
        if(!listOrdenesAgregadas.isEmpty()){
            for (OrdenAgregadaPago o : listOrdenesAgregadas) {
                String descripcionOrden = o.getTipoOrdenDesc();
                if(o.getTipoOrdenServicioDesc() != null && !o.getTipoOrdenServicioDesc().isEmpty())
                    descripcionOrden = o.getTipoOrdenServicioDesc();
            
                model.addRow(new Object[]{
                    o.getNumeroOrden(),
                    descripcionOrden,
                    o.getObservaciones(),
                    o.getCosto()});
            }
            tablaOrdenes.setRowSelectionInterval(0, 0);
        }
        
    }

    /**
     * 
     * @return 
     */
    private double obtenerMontoOrdenes(){
        double monto = 0;
        
        if(!listOrdenesAgregadas.isEmpty()){
            monto = listOrdenesAgregadas.stream().mapToDouble(OrdenAgregadaPago::getCosto).sum();
        }
        
        return monto;
    }
    
    /**
     * 
     * @param mesSeleccionado
     * @param anioSeleccionado
     * @param contratosuscriptor
     * @param seRefrescanPromociones 
     */
    private void actualizarInformacionPago( PromocionEntity promocion, Mes mesSeleccionado, int anioSeleccionado,
            ContratoxSuscriptorDetalleEntity contratosuscriptor, boolean seRefrescanPromociones) {

        System.out.println("Seleccionado: " + contratosuscriptor.getContratoId());
        
        // primero borrar los datos de suscriptores que se hayan seleccionado antes
        etiquetaPromocionAplicada.setVisible(false);
        //limpiarDatosSuscriptor();
        suscriptorSeleccionado = contratosuscriptor;
        promocionSeleccionada = promocion;
        mesGuardado = mesSeleccionado;
        anioGuardado = anioSeleccionado;
        cobroCapturado = null;
        //boolean seDebeMostrarAdvertenciaPromocionNoAplicada = false;  
        
        Mes mesPagado = util.obtenerMesAnterior(mesSeleccionado);
        int anioPagado = anioSeleccionado;
        if(mesPagado.getNumero() == 12)
            anioPagado = anioPagado - 1;
      
        Integer numeroMeses = 0;
        if(checkServicio.isSelected())
            numeroMeses = util.calcularMesesPagados(mesSeleccionado, anioSeleccionado, suscriptorSeleccionado.getFechaProximoPago());
        
        Double montoTotalMeses = 0.0;
        
        //validar si se cobra el servicio
        if(checkServicio.isSelected()){
            if(contratosuscriptor.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
                //comboMeses.setEnabled(false);
                montoTotalMeses = suscriptorSeleccionado.getCostoServicio() * numeroMeses;
            }else if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTESIA){
                montoTotalMeses = 0.0;
            }else{
                if(promocion != null){
                    //validar que se cumplan con los meses que se deben pagar para aplicar la promocion
                    montoTotalMeses = promocion.getCostoPromocion();
                    etiquetaPromocionAplicada.setVisible(true);
   
                }else{
                    montoTotalMeses = suscriptorSeleccionado.getCostoServicio() * numeroMeses;
                } 
            }
            
            montoTotalMeses = montoTotalMeses + obtenerMontoOrdenes();
            
        }else{
            montoTotalMeses = obtenerMontoOrdenes();
        }

        campoMontoSugerido.setText(String.valueOf(montoTotalMeses));
        campoMonto.setText(String.valueOf(montoTotalMeses));
        etiquetaImporte.setText(String.valueOf(montoTotalMeses));
        
        if(checkServicio.isSelected()){
            
            suscriptorSeleccionado.setMesesPorPagar(numeroMeses);
            if(listOrdenesAgregadas.isEmpty()){
                etiquetaDescPago1.setText("Usted está recibiendo el siguiente pago:");
                if(numeroMeses == 1){
                    etiquetaDescPago2.setText(util.obtenerDescripcionPagoUnMes(mesSeleccionado, anioSeleccionado));
                }else{
                    etiquetaDescPago2.setText(util.obtenerDescripcionVariosMeses(mesPagado, anioPagado, suscriptorSeleccionado.getFechaProximoPago()));
                }
                etiquetaDescPago3.setText("Meses a cobrar: ".concat(String.valueOf(numeroMeses)));
            }else{
                etiquetaDescPago1.setText("Usted está recibiendo los siguientes pagos:");
                if(numeroMeses == 1){
                    etiquetaDescPago2.setText(util.obtenerDescripcionPagoUnMes(mesSeleccionado, anioSeleccionado));
                }else{
                    etiquetaDescPago2.setText(util.obtenerDescripcionVariosMeses(mesPagado, anioPagado, suscriptorSeleccionado.getFechaProximoPago()));
                }
                etiquetaDescPago3.setText("Meses a cobrar: ".concat(String.valueOf(numeroMeses))
                        .concat(", Ordenes a cobrar:").concat(String.valueOf(listOrdenesAgregadas.size())));
            }
            
        }else{
            
            if(listOrdenesAgregadas.isEmpty()){
                etiquetaDescPago1.setText("Sin pagos por cobrar");
                etiquetaDescPago2.setText("");
                etiquetaDescPago3.setText("");
            }else{
                etiquetaDescPago1.setText("Usted está recibiendo el siguiente pago:");
                etiquetaDescPago2.setText(" >> "+listOrdenesAgregadas.size()+" orden(es)");
                etiquetaDescPago3.setText("Meses a cobrar: 0");
            }
            
            suscriptorSeleccionado.setMesesPorPagar(0);
            
        }

        if(seRefrescanPromociones){
            comboPromociones.removeAllItems();
            cargarComboPromociones(suscriptorSeleccionado.getServicioId());
        }  
        
        cobroCapturado = new CobroServicio();
        if(!listOrdenesAgregadas.isEmpty())
            cobroCapturado.setOrdenesPago(listOrdenesAgregadas);
        cobroCapturado.setCadenaMonto("$ ".concat(String.valueOf(montoTotalMeses)));
        if(checkServicio.isSelected()){
            cobroCapturado.setSeCobraServicio(true);
            cobroCapturado.setConcepto(etiquetaDescPago2.getText().replace(">>", "Pago ")); 
        }else{
            cobroCapturado.setSeCobraServicio(false);
            cobroCapturado.setConcepto("Pago de Ordenes");
        }
        cobroCapturado.setDescuento(null);
        if(checkServicio.isSelected()){
            //se toman las fechas de los combos
            cobroCapturado.setFechaProximoPagoTicket(util.obtenerCadenaFechaPago(mesSeleccionado, anioSeleccionado));
            cobroCapturado.setFechaProximoPago(util.obtenerFechaPago(mesSeleccionado, anioSeleccionado));
        }else{
            //se toma la fecha del suscriptor (que no ha cambiado)
            cobroCapturado.setFechaProximoPagoTicket(util.convertirDateTime2String(suscriptorSeleccionado.getFechaProximoPago(), Constantes.FORMATO_FECHA_WEB_SERVICE));
            cobroCapturado.setFechaProximoPago(suscriptorSeleccionado.getFechaProximoPago());
        }
        
        cobroCapturado.setMesesPagados(numeroMeses);
        if(checkServicio.isSelected()){
            if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
                montoTotalMeses = montoTotalMeses + 50;
                campoMontoSugerido.setText(String.valueOf(montoTotalMeses));
                campoMonto.setText(String.valueOf(montoTotalMeses));
                etiquetaImporte.setText(String.valueOf(montoTotalMeses));
                cobroCapturado.setMontoRecargo(50.0);
                cobroCapturado.setSeCobraRecargo(true);
                etiquetaRecargoPagoTardio.setVisible(true);
            }else{
                etiquetaRecargoPagoTardio.setVisible(false);
            }
        }else{
            etiquetaRecargoPagoTardio.setVisible(false);
        }
        cobroCapturado.setMontoServicio(suscriptorSeleccionado.getCostoServicio());
        cobroCapturado.setMontoTotal(montoTotalMeses);
        cobroCapturado.setMontoSugerido(montoTotalMeses);
        //cobroCapturado.setObservaciones();  --> obtenerlo al presionar cobrar
        if(promocionSeleccionada != null){
            PromocionCobro promo = new PromocionCobro();
            promo.setMesesGratis(promocionSeleccionada.getMesesGratis());
            promo.setCostoPromocion(promocionSeleccionada.getCostoPromocion());
            promo.setPromocionId(promocionSeleccionada.getPromocionId());
            promo.setDescripcion(promocionSeleccionada.getDescripcion());
            cobroCapturado.setPromocion(promo);
            
            if(promocionSeleccionada.getMesesGratis() > 0){
                int mesesParaCobrarPromo = numeroMeses - promocionSeleccionada.getMesesGratis();
                StringBuilder cadenaMesesPromo = new StringBuilder();
                cadenaMesesPromo.append("Meses a Cobrar: ").append(mesesParaCobrarPromo).append("/");
                if(!listOrdenesAgregadas.isEmpty())
                    cadenaMesesPromo.append("Ordenes: ").append(listOrdenesAgregadas.size()).append("/");
                cadenaMesesPromo.append("Meses Gratis: ").append(promocionSeleccionada.getMesesGratis());
                etiquetaDescPago3.setText(cadenaMesesPromo.toString());

            }/*else{
                if(listOrdenesAgregadas.isEmpty()){
                    etiquetaDescPago3.setText("Meses a cobrar: ".concat(String.valueOf(numeroMeses)));
                }else{
                    etiquetaDescPago3.setText("Meses a cobrar: ".concat(String.valueOf(numeroMeses))
                        .concat(", Ordenes a cobrar:").concat(String.valueOf(listOrdenesAgregadas.size())));
                }
            }*/   // no borrar, primero probar que funcione asi
  
        }else{
            cobroCapturado.setPromocion(null);
            //etiquetaDescPago3.setText("Meses a cobrar: ".concat(String.valueOf(numeroMeses)));
        }
        

    }

    /**
     * 
     */
    private void setCamposPagoDefault(){   

        Calendar fechaCorte = Calendar.getInstance();
        fechaCorte.setTime(suscriptorSeleccionado.getFechaProximoPago());
        
        Calendar fechaEnCurso = Calendar.getInstance();
        fechaEnCurso.setTime(new Date());
        
        int mesCorte = fechaCorte.get(Calendar.MONTH);
        int anioCorte = fechaCorte.get(Calendar.YEAR);
        // si la fecha en curso es mayor a la actual quiere decir que el contrato lleva varios meses sin pagar
        //se suman esos meses para obligar a que el pago se realice pro lo menos hasta el mes en curso
        if(fechaEnCurso.after(fechaCorte)){
            int diferenciaMeses = util.obtenerDiferenciaMeses(fechaCorte, fechaEnCurso);
            fechaCorte.add(Calendar.MONTH, diferenciaMeses);
            mesCorte = fechaCorte.get(Calendar.MONTH);
            anioCorte = fechaCorte.get(Calendar.YEAR);
        }

        if(mesCorte < 11)
            comboMeses.setSelectedIndex(mesCorte+1);
        else{
            comboMeses.setSelectedIndex(0);
            anioCorte = anioCorte + 1;
        }
            
        comboAnios.setSelectedItem(anioCorte);
        etiquetaPromocionAplicada.setVisible(false);
        
        etiquetaImporte.setText("0.00");
        campoMonto.setText("0.00");
        if(!checkServicio.isSelected()){
            campoMonto.setEnabled(false);
            comboMeses.setEnabled(false);
            comboAnios.setEnabled(false);
        }
    }
    
    /**
     * 
     * @param promocion 
     */
    private void setCamposPagoPromocion(PromocionEntity promocion){   
        //posicionarse en el mes en curso
        Calendar fechaPromocion = Calendar.getInstance();
        fechaPromocion.setTime(suscriptorSeleccionado.getFechaProximoPago());
        //int mesCorte = fechaPromocion.get(Calendar.MONTH);
        int mesesPromo = promocion.getMesesPagados() + promocion.getMesesGratis();
        fechaPromocion.add(Calendar.MONTH, mesesPromo);
        int mesPromocion = fechaPromocion.get(Calendar.MONTH);
        comboMeses.setSelectedIndex(mesPromocion);
        comboAnios.setSelectedItem(fechaPromocion.get(Calendar.YEAR));
        
        etiquetaPromocionAplicada.setVisible(false);
        
        etiquetaImporte.setText("0.00");
        campoMonto.setText("0.00");
        
        campoMonto.setEnabled(false);
        comboMeses.setEnabled(false);
        comboAnios.setEnabled(false);
    }
    
    private void cargarComboAnios(Date fechaCorte){
        
        //List<Integer> anios = util.obtenerAniosPorMostrar(fechaCorte);
        //anios.forEach(a -> comboAnios.addItem(a));
        
        Mes mesEnCurso = util.obtenerMesEnCurso();

        List<Integer> anios = new ArrayList<>();
        LocalDate ld = LocalDate.now();
        Integer anio = ld.getYear();
        anios.add(anio);
        anios.add(anio+1);
        if(mesEnCurso.getNumero() == 12){
            anios.add(anio+2);
        }
        anios.forEach(a -> comboAnios.addItem(a));
        if(mesEnCurso.getNumero() == 12){
            comboAnios.setSelectedItem(anio+1);
        }
        
    }
    
    private void cargarComboTiposDescuento() {
        try {

            List<TipoDescuentoEntity> list = controller.consultarTiposDescuento();
            list.forEach(td -> comboTipoDescuento.addItem(td));
            comboTipoDescuento.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void cargarComboPromociones(Long servicioId) {
        try {

            List<PromocionEntity> list = controller.consultarPromociones(servicioId);
            list.forEach(p -> comboPromociones.addItem(p));
            if(checkServicio.isSelected())
                comboPromociones.setEnabled(true);
            else
                comboPromociones.setEnabled(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cobroPanel, ex.getMessage(), "", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void cargarComboMeses(){
        
        List<Mes> meses = new ArrayList<>();
        meses.add(new Mes(1,"ENERO"));
        meses.add(new Mes(2,"FEBRERO"));
        meses.add(new Mes(3,"MARZO"));
        meses.add(new Mes(4,"ABRIL"));
        meses.add(new Mes(5,"MAYO"));
        meses.add(new Mes(6,"JUNIO"));
        meses.add(new Mes(7,"JULIO"));
        meses.add(new Mes(8,"AGOSTO"));
        meses.add(new Mes(9,"SEPTIEMBRE"));
        meses.add(new Mes(10,"OCTUBRE"));
        meses.add(new Mes(11,"NOVIEMBRE"));
        meses.add(new Mes(12,"DICIEMBRE"));
        meses.forEach(m -> comboMeses.addItem(m));
        
        //posicionarse en el mes en curso
        Calendar fechaEnCurso = Calendar.getInstance();
        int mesEnCurso = fechaEnCurso.get(Calendar.MONTH);
        
        if(mesEnCurso == 11){
            comboMeses.setSelectedIndex(0);
        }else{
            comboMeses.setSelectedIndex(mesEnCurso+1);
        }
        
    }
    
    /**
     * 
     * @param estatusContratoId 
     */
    private void cargarComboTiposOrden(Long estatusContratoId) {

        comboTiposOrden.removeAllItems();
        listTiposOrden.clear();
        
        if (estatusContratoId == Constantes.ESTATUS_CONTRATO_NUEVO) {  // ya no aplica
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_INSTALACION, "Orden de Instalación"));  
        } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CORTESIA) {
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO, "Orden de Cambio de Domicilio"));
        } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_ACTIVO) {
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO, "Orden de Cambio de Domicilio"));
        } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CORTE) {
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_CAMBIO_DOMICILIO, "Orden de Cambio de Domicilio"));
        } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_PENDIENTE_INSTALAR) {
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_INSTALACION, "Orden de Instalación"));
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio")); 
        } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_RECONEXION) {
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
        } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO) {
            listTiposOrden.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO, "Orden de Servicio"));
        }
        
        listTiposOrden.forEach(to -> comboTiposOrden.addItem(to));

    }
    
    /**
     * 
     * @param tipoOrden 
     */
    private void cargarComboTiposOrdenServicio(TipoOrden tipoOrden, Long estatusContratoId) {

        comboTiposOrdenServicio.removeAllItems();
        listTiposOrdenServicio.clear();
                
        if (tipoOrden.getTipoOrdenId() == Constantes.TIPO_ORDEN_SERVICIO) {
            comboTiposOrdenServicio.setEnabled(true);
            
            if (estatusContratoId == Constantes.ESTATUS_CONTRATO_NUEVO) {
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "Cambio de Cable por acometida"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "TV Adicional"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "Cambio de Cableado"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "Reubicación de Módem"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "Cambio de ubicación de TV"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "Cambio de Plan"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CORTESIA) {
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "Cambio de Cable por acometida"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "TV Adicional"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "Cambio de Cableado"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "Reubicación de Módem"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "Cambio de ubicación de TV"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "Cambio de Plan"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO, "Reconexión de Servicio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_ACTIVO) {
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "Cambio de Cable por acometida"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "TV Adicional"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "Cambio de Cableado"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "Reubicación de Módem"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "Cambio de ubicación de TV"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "Cambio de Plan"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO, "Reconexión de Servicio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CORTE) {
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "Cambio de Cable por acometida"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "TV Adicional"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "Cambio de Cableado"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "Reubicación de Módem"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "Cambio de ubicación de TV"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "Cambio de Plan"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO, "Reconexión de servicio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_PENDIENTE_INSTALAR) {
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "Cambio de Cable por acometida"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "TV Adicional"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "Cambio de Cableado"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "Reubicación de Módem"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "Cambio de ubicación de TV"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "Cambio de Plan"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_RECONEXION) {
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "Cambio de Cable por acometida"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "TV Adicional"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "Cambio de Cableado"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "Reubicación de Módem"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "Cambio de ubicación de TV"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "Cambio de Plan"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO, "Reconexión de Servicio"));
            } else if (estatusContratoId == Constantes.ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO) {
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
                listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_RETIRO_EQUIPO_CANCELACION, "Retiro de equipo por cancelación"));
            }
            
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLE_ACOMETIDA, "Cambio de Cable por acometida"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_TV_ADICIONAL, "TV Adicional"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_CABLEADO, "Cambio de Cableado"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_REUBICACION_MODEM, "Reubicación de Módem"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_UBICACION_TV, "Cambio de ubicación de TV"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_VISITA_TECNICA, "Visita Técnica"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_CAMBIO_PLAN, "Cambio de Plan"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_RETIRO_EQUIPO_CANCELACION, "Retiro de equipo por cancelación"));
//            listTiposOrdenServicio.add(new TipoOrden(Constantes.TIPO_ORDEN_SERVICIO_RECONEXION_SERVICIO, "Reconexión de servicio"));
        }else{
            comboTiposOrdenServicio.setEnabled(false);
        }
        
        listTiposOrdenServicio.forEach(to -> comboTiposOrdenServicio.addItem(to));

    }
    
    /**
     * 
     */
    private void cargarComboServicios(){
        
        List<ServicioEntity> servicios = servicioController.obtenerServicios();
        //quitar de la lista el servicio que ya tiene el contrato (solo mostrar los demás)
        servicios = servicios
                .stream().filter(s -> s.getServicioId().longValue() != suscriptorSeleccionado.getServicioId())
                .collect(Collectors.toList());
        
        if(!servicios.isEmpty())
            servicios.forEach(s -> comboNuevoServicio.addItem(s));
        
    }
    
    /**
     * 
     */
    public void cargarDatosSesion() {
        
        //primero obtener el contrato que se selecciono en la pantalla de busqueda
        suscriptorSeleccionado = sesion.getContratoSeleccionado();
        
        //cabecero
        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        
        //setear el check del servicio
        checkServicio.setSelected(true);
        campoMonto.setEnabled(true);
        comboMeses.setEnabled(true);
        comboAnios.setEnabled(true);
        botonCalculaMonto.setEnabled(true);
        comboTipoDescuento.setEnabled(true);
        campoMotivoDescuento.setEnabled(true);
        comboPromociones.setEnabled(true);
        botonAplicarPromocion.setEnabled(true);
        botonEliminarPromocion.setEnabled(true);
        
        //datos del suscriptor y contrato
        
        StringBuilder nombre = new StringBuilder();
        nombre.append(suscriptorSeleccionado.getNombre());
        if (suscriptorSeleccionado.getApellidoPaterno() != null) {
            nombre.append(" ").append(suscriptorSeleccionado.getApellidoPaterno());
        }
        if (suscriptorSeleccionado.getApellidoMaterno() != null) {
            nombre.append(" ").append(suscriptorSeleccionado.getApellidoMaterno());
        }
        campoSuscriptor.setEditable(false);
        campoSuscriptor.setText(nombre.toString());
        
        campoTelefono.setEditable(false);
        campoTelefono.setText(sesion.getContratoSeleccionado().getTelefono());
        
        campoFolioContrato.setEditable(false);
        campoFolioContrato.setText(String.valueOf(sesion.getContratoSeleccionado().getFolioContrato()));
        
        campoContrato.setEditable(false);
        campoContrato.setText(String.valueOf(sesion.getContratoSeleccionado().getContratoId()));
        
        campoEstatus.setEditable(false);
        campoEstatus.setText(sesion.getContratoSeleccionado().getEstatusContrato());
        
        campoFechaPago.setEditable(false);
        campoFechaPago.setText(util.convertirDateTime2String(sesion.getContratoSeleccionado().getFechaProximoPago(), Constantes.FORMATO_FECHA_TICKET));
        
        campoServicioContratado.setEditable(false);
        campoServicioContratado.setText(sesion.getContratoSeleccionado().getServicio());
        
        campoCostoServicio.setEditable(false);
        campoCostoServicio.setText(String.valueOf(sesion.getContratoSeleccionado().getCostoServicio()));
        
        StringBuilder domicilio = new StringBuilder();
        domicilio.append(sesion.getContratoSeleccionado().getCalle()).append(" ");
        domicilio.append(sesion.getContratoSeleccionado().getNumeroCalle()).append(" ");
        if(sesion.getContratoSeleccionado().getCiudad() != null)
            domicilio.append(sesion.getContratoSeleccionado().getCiudad());
        campoDomicilio.setEditable(false);
        campoDomicilio.setText(domicilio.toString());
       
        comboMeses.removeAllItems();
        cargarComboMeses();
        comboAnios.removeAllItems();
        cargarComboAnios(suscriptorSeleccionado.getFechaProximoPago());
        
        Mes mesEnCurso = util.obtenerMesEnCurso();
        List<Mes> meses = util.obtenerMeses();
        meses.forEach(m -> comboMeses.addItem(m));
        if(mesEnCurso.getNumero() == 12){
            comboMeses.setSelectedIndex(0);
        }else{
            comboMeses.setSelectedItem(util.obtenerMesEnCurso());
        }
        
        setCamposPagoDefault();
        Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
        int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
        
        cargarComboTiposOrden(sesion.getContratoSeleccionado().getEstatusContratoId());
        cargarComboServicios();
        
        //de inicio deshabilitar campos de tv y combo de servicios
        campoTvsExtra.setEnabled(false);
        comboNuevoServicio.setEnabled(false);
        campoCalle.setEnabled(false);
        campoNumeroCalle.setEnabled(false);
        campoColonia.setEnabled(false);
        campoCiudad.setEnabled(false);
        campoCalle1.setEnabled(false);
        campoCalle2.setEnabled(false);
        areaReferencia.setEnabled(false);
        areaReferencia.setLineWrap(true);
        areaReferencia.setRows(4);

        //ImageIcon imagen = new ImageIcon("src/main/resources/logo_grande.jpg");
        //Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(/*etiquetaLogo.getWidth(), etiquetaLogo.getHeight()*/320, 130, Image.SCALE_DEFAULT));
        campoMontoSugerido.setEditable(false);
        etiquetaPromocionAplicada.setVisible(false);
        etiquetaRecargoPagoTardio.setVisible(false);
        
        tablaOrdenes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaOrdenes.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaOrdenes.getColumnModel().getColumn(2).setPreferredWidth(580);
        tablaOrdenes.getColumnModel().getColumn(3).setPreferredWidth(90);
        
        actualizarInformacionPago(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, true);

    }
    
    /**
     * 
     */
    private void limpiarPantalla() {
        
        limpiarDatosSuscriptor();

        campoMotivoDescuento.setText("");
        campoMonto.setText("0.00");
        etiquetaImporte.setText("0.00");
        comboMeses.setSelectedIndex(0);
        comboMeses.setEnabled(true);
        campoObservaciones.setText("");
        campoMotivoDescuento.setText("");
        campoMontoSugerido.setText("");
  
        etiquetaDescPago1.setText("");
        etiquetaDescPago2.setText("");
        etiquetaDescPago3.setText("");
        etiquetaRecargoPagoTardio.setVisible(false);
        
        campoCalle.setText("");
        campoNumeroCalle.setText("");
        campoColonia.setText("");
        campoCiudad.setText("");
        campoCalle1.setText("");
        campoCalle2.setText("");
        areaReferencia.setText("");
        
        campoTvsExtra.setText("");
        comboNuevoServicio.removeAllItems();
        comboTiposOrden.removeAllItems();
        comboTiposOrdenServicio.removeAllItems();
        campoObservacionesOrden.setText("");
        
    }
    
    /**
     * 
     */
    private void limpiarDatosSuscriptor() {
        
        listOrdenesAgregadas.clear();
        
        DefaultTableModel model = (DefaultTableModel) tablaOrdenes.getModel();
        model.getDataVector().clear();
        model.fireTableDataChanged();
                
        suscriptorSeleccionado = null;
        cobroCapturado = null;
        promocionSeleccionada = null;
        campoSuscriptor.setText("");
        campoContrato.setText("");
        campoFolioContrato.setText("");
        campoEstatus.setText("");
        campoFechaPago.setText("");
        campoServicioContratado.setText("");
        campoDomicilio.setText("");
        campoTelefono.setText("");
        campoCostoServicio.setText("");
        etiquetaImporte.setText("0.00");
        etiquetaPromocionAplicada.setVisible(false);
        etiquetaDescPago1.setText("");
        etiquetaDescPago2.setText("");
        etiquetaDescPago3.setText("");
        etiquetaRecargoPagoTardio.setVisible(false);
        comboTipoDescuento.setEnabled(true);
        campoMotivoDescuento.setEnabled(true);
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
        panelInfoContrato = new javax.swing.JPanel();
        etiquetaInfoContrato = new javax.swing.JLabel();
        etiquetaNombreSuscriptor = new javax.swing.JLabel();
        campoSuscriptor = new javax.swing.JTextField();
        etiquetaCOntrato = new javax.swing.JLabel();
        campoContrato = new javax.swing.JTextField();
        etiquetaEstatus = new javax.swing.JLabel();
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
        jLabel15 = new javax.swing.JLabel();
        campoCostoServicio = new javax.swing.JTextField();
        panelPromociones = new javax.swing.JPanel();
        etiquetaInfoPromociones = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        comboPromociones = new javax.swing.JComboBox<>();
        botonAplicarPromocion = new javax.swing.JButton();
        botonEliminarPromocion = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        campoMontoSugerido = new javax.swing.JTextField();
        botonReestablecerMonto = new javax.swing.JButton();
        panelOrdenes = new javax.swing.JPanel();
        etiquetaInfoOrdenes = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        comboTiposOrden = new javax.swing.JComboBox<>();
        botonAgregarOrden = new javax.swing.JButton();
        botonLimpiarOrdenes = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaOrdenes = new javax.swing.JTable();
        panelInfoPago = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        campoMonto = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        comboMeses = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        comboAnios = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        campoObservaciones = new javax.swing.JTextArea();
        etiquetaDescPago1 = new javax.swing.JLabel();
        etiquetaDescPago2 = new javax.swing.JLabel();
        etiquetaDescPago3 = new javax.swing.JLabel();
        etiquetaRecargoPagoTardio = new javax.swing.JLabel();
        botonCalculaMonto = new javax.swing.JButton();
        panelDescuentos = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        comboTipoDescuento = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        campoMotivoDescuento = new javax.swing.JTextField();
        panelImportes = new javax.swing.JPanel();
        etiquetaPesos = new javax.swing.JLabel();
        etiquetaImporte = new javax.swing.JLabel();
        etiquetaPromocionAplicada = new javax.swing.JLabel();
        botonRegresar = new javax.swing.JButton();
        botonCobrar = new javax.swing.JButton();
        checkServicio = new javax.swing.JCheckBox();
        comboTiposOrdenServicio = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        campoTvsExtra = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        comboNuevoServicio = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        campoCalle = new javax.swing.JTextField();
        campoNumeroCalle = new javax.swing.JTextField();
        campoColonia = new javax.swing.JTextField();
        campoCiudad = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        campoCalle1 = new javax.swing.JTextField();
        campoCalle2 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        areaReferencia = new javax.swing.JTextArea();
        jLabel41 = new javax.swing.JLabel();
        campoObservacionesOrden = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        campoCostoOrden = new javax.swing.JTextField();

        setBackground(new java.awt.Color(204, 204, 204));
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

        panelInfoContrato.setBackground(new java.awt.Color(255, 255, 255));

        etiquetaInfoContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaInfoContrato.setForeground(new java.awt.Color(255, 0, 0));
        etiquetaInfoContrato.setText("Información del Contrato");

        etiquetaNombreSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaNombreSuscriptor.setText("Nombre del Suscriptor:");

        campoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        etiquetaCOntrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaCOntrato.setText("ID Sistema:");

        campoContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoContratoActionPerformed(evt);
            }
        });

        etiquetaEstatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaEstatus.setText("Número de Contrato:");

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
        jLabel13.setText("Fecha de Corte:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Teléfono:");

        campoFechaPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoFechaPago.setForeground(java.awt.Color.red);

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Costo:");

        campoCostoServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoCostoServicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoCostoServicioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelInfoContratoLayout = new javax.swing.GroupLayout(panelInfoContrato);
        panelInfoContrato.setLayout(panelInfoContratoLayout);
        panelInfoContratoLayout.setHorizontalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(etiquetaNombreSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(613, 613, 613))
                            .addComponent(campoSuscriptor)))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(etiquetaInfoContrato))
                            .addComponent(etiquetaEstatus))
                        .addGap(1, 1, 1)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(etiquetaCOntrato, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(campoCostoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                        .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(57, 57, 57)
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(39, 39, 39)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(147, 147, 147))
        );
        panelInfoContratoLayout.setVerticalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiquetaInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(etiquetaNombreSuscriptor)
                        .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaEstatus)
                    .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaCOntrato)
                    .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(campoCostoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        panelPromociones.setBackground(new java.awt.Color(255, 255, 255));

        etiquetaInfoPromociones.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaInfoPromociones.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaInfoPromociones.setText("Promociones en el paquete contratado");
        etiquetaInfoPromociones.setToolTipText("");

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

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel25.setForeground(java.awt.Color.magenta);
        jLabel25.setText("Monto de Cobro Sugerido:");

        campoMontoSugerido.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        campoMontoSugerido.setText("0.0");
        campoMontoSugerido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoMontoSugeridoActionPerformed(evt);
            }
        });

        botonReestablecerMonto.setBackground(new java.awt.Color(227, 126, 75));
        botonReestablecerMonto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonReestablecerMonto.setForeground(new java.awt.Color(255, 255, 255));
        botonReestablecerMonto.setText("Restablecer");

        javax.swing.GroupLayout panelPromocionesLayout = new javax.swing.GroupLayout(panelPromociones);
        panelPromociones.setLayout(panelPromocionesLayout);
        panelPromocionesLayout.setHorizontalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addComponent(etiquetaInfoPromociones)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(botonAplicarPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(botonEliminarPromocion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(campoMontoSugerido, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(botonReestablecerMonto)
                        .addGap(105, 105, 105))))
        );
        panelPromocionesLayout.setVerticalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addComponent(etiquetaInfoPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonAplicarPromocion)
                    .addComponent(botonEliminarPromocion)
                    .addComponent(jLabel17)
                    .addComponent(jLabel25)
                    .addComponent(campoMontoSugerido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonReestablecerMonto))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelOrdenes.setBackground(new java.awt.Color(255, 255, 255));

        etiquetaInfoOrdenes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaInfoOrdenes.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaInfoOrdenes.setText("Captura de Ordenes a incluir en el cobro");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel30.setText("Seleccionar Tipo de Orden:");

        comboTiposOrden.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        botonAgregarOrden.setBackground(new java.awt.Color(227, 126, 75));
        botonAgregarOrden.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonAgregarOrden.setForeground(new java.awt.Color(255, 255, 255));
        botonAgregarOrden.setText("Agregar Orden");

        botonLimpiarOrdenes.setBackground(java.awt.Color.red);
        botonLimpiarOrdenes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonLimpiarOrdenes.setForeground(new java.awt.Color(255, 255, 255));
        botonLimpiarOrdenes.setText("Limpiar Ordenes");
        botonLimpiarOrdenes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarOrdenesActionPerformed(evt);
            }
        });

        tablaOrdenes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Número", "Tipo de Orden", "Observaciones", "Costo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaOrdenes);
        if (tablaOrdenes.getColumnModel().getColumnCount() > 0) {
            tablaOrdenes.getColumnModel().getColumn(0).setResizable(false);
            tablaOrdenes.getColumnModel().getColumn(1).setResizable(false);
            tablaOrdenes.getColumnModel().getColumn(2).setResizable(false);
            tablaOrdenes.getColumnModel().getColumn(3).setResizable(false);
        }

        panelInfoPago.setBackground(new java.awt.Color(255, 255, 255));
        panelInfoPago.setForeground(java.awt.Color.red);
        panelInfoPago.setMaximumSize(new java.awt.Dimension(1500, 270));
        panelInfoPago.setMinimumSize(new java.awt.Dimension(1500, 270));
        panelInfoPago.setPreferredSize(new java.awt.Dimension(1500, 270));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 51, 51));
        jLabel28.setText("Captura de Información del monto a cobrar");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel27.setForeground(java.awt.Color.red);
        jLabel27.setText("Monto:");

        campoMonto.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        campoMonto.setText("0.0");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setForeground(java.awt.Color.red);
        jLabel23.setText("Mes Próximo Pago:");

        comboMeses.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel26.setForeground(java.awt.Color.red);
        jLabel26.setText("Año Próximo Pago:");

        comboAnios.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        comboAnios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAniosActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setForeground(java.awt.Color.red);
        jLabel29.setText("Observaciones:");

        campoObservaciones.setColumns(20);
        campoObservaciones.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoObservaciones.setLineWrap(true);
        campoObservaciones.setRows(5);
        jScrollPane3.setViewportView(campoObservaciones);

        etiquetaDescPago1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescPago1.setForeground(java.awt.Color.red);

        etiquetaDescPago2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescPago2.setForeground(java.awt.Color.red);

        etiquetaDescPago3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescPago3.setForeground(java.awt.Color.red);

        etiquetaRecargoPagoTardio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaRecargoPagoTardio.setForeground(java.awt.Color.red);
        etiquetaRecargoPagoTardio.setText(">> Recargo por Pago Tardío");

        botonCalculaMonto.setBackground(new java.awt.Color(227, 126, 75));
        botonCalculaMonto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonCalculaMonto.setForeground(new java.awt.Color(255, 255, 255));
        botonCalculaMonto.setText("Calcular Monto Pago:");

        panelDescuentos.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setText("Seleccione descuento:");

        comboTipoDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setText("Motivo Descuento:");

        campoMotivoDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelDescuentosLayout = new javax.swing.GroupLayout(panelDescuentos);
        panelDescuentos.setLayout(panelDescuentosLayout);
        panelDescuentosLayout.setHorizontalGroup(
            panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDescuentosLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel20)
                .addGap(18, 18, 18)
                .addComponent(comboTipoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(campoMotivoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(313, 313, 313))
        );
        panelDescuentosLayout.setVerticalGroup(
            panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDescuentosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addGroup(panelDescuentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(campoMotivoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(comboTipoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );

        panelImportes.setBackground(new java.awt.Color(255, 255, 255));

        etiquetaPesos.setFont(new java.awt.Font("Segoe UI", 0, 70)); // NOI18N
        etiquetaPesos.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaPesos.setText("$");

        etiquetaImporte.setFont(new java.awt.Font("Segoe UI", 0, 70)); // NOI18N
        etiquetaImporte.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaImporte.setText("0.00");

        etiquetaPromocionAplicada.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaPromocionAplicada.setForeground(new java.awt.Color(255, 51, 51));
        etiquetaPromocionAplicada.setText("Promocion Aplicada!!");

        botonRegresar.setBackground(new java.awt.Color(255, 51, 0));
        botonRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonRegresar.setForeground(new java.awt.Color(255, 255, 255));
        botonRegresar.setText("Regresar");

        botonCobrar.setBackground(new java.awt.Color(0, 153, 51));
        botonCobrar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        botonCobrar.setForeground(new java.awt.Color(255, 255, 255));
        botonCobrar.setText("Cobrar");

        javax.swing.GroupLayout panelImportesLayout = new javax.swing.GroupLayout(panelImportes);
        panelImportes.setLayout(panelImportesLayout);
        panelImportesLayout.setHorizontalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImportesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelImportesLayout.createSequentialGroup()
                        .addComponent(etiquetaPromocionAplicada)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(etiquetaPesos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(etiquetaImporte, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
                    .addGroup(panelImportesLayout.createSequentialGroup()
                        .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        panelImportesLayout.setVerticalGroup(
            panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImportesLayout.createSequentialGroup()
                .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelImportesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(etiquetaImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(etiquetaPesos)))
                    .addGroup(panelImportesLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(etiquetaPromocionAplicada)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonRegresar)
                    .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkServicio.setText("Cobrar servicio");

        javax.swing.GroupLayout panelInfoPagoLayout = new javax.swing.GroupLayout(panelInfoPago);
        panelInfoPago.setLayout(panelInfoPagoLayout);
        panelInfoPagoLayout.setHorizontalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(campoMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel23))
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(comboMeses, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(checkServicio, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboAnios, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(botonCalculaMonto))
                        .addGap(21, 21, 21)
                        .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(etiquetaDescPago1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(etiquetaDescPago3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(etiquetaDescPago2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(etiquetaRecargoPagoTardio, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelImportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelInfoPagoLayout.setVerticalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkServicio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(comboAnios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26)
                            .addComponent(comboMeses)
                            .addComponent(campoMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(etiquetaDescPago1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(botonCalculaMonto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                                .addComponent(etiquetaDescPago2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addComponent(etiquetaDescPago3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31))
                            .addComponent(jLabel29)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(etiquetaRecargoPagoTardio))
                    .addComponent(panelImportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        comboTiposOrdenServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel31.setText("Orden de Servicio:");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel32.setText("Número de televisiones extra:");

        campoTvsExtra.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel33.setText("Seleccione el nuevo paquete a instalar:");

        comboNuevoServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel34.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel34.setText("Calle");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel35.setText("Número de Calle");

        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel36.setText("Colonia");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel37.setText("Ciudad");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel38.setText("Entre Calle");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel39.setText("y Calle");

        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel40.setText("Referencia");

        areaReferencia.setColumns(20);
        areaReferencia.setRows(5);
        jScrollPane2.setViewportView(areaReferencia);

        jLabel41.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel41.setText("Observaciones:");

        campoObservacionesOrden.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel42.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel42.setText("Costo:");

        campoCostoOrden.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelOrdenesLayout = new javax.swing.GroupLayout(panelOrdenes);
        panelOrdenes.setLayout(panelOrdenesLayout);
        panelOrdenesLayout.setHorizontalGroup(
            panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenesLayout.createSequentialGroup()
                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOrdenesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelOrdenesLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelOrdenesLayout.createSequentialGroup()
                                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel34)
                                    .addComponent(jLabel35)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel37))
                                .addGap(18, 18, 18)
                                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(campoCalle, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                    .addComponent(campoNumeroCalle)
                                    .addComponent(campoColonia)
                                    .addComponent(campoCiudad))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel38)
                                    .addComponent(jLabel39)
                                    .addComponent(jLabel40))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelOrdenesLayout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addGap(18, 18, 18)
                                        .addComponent(comboNuevoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(panelOrdenesLayout.createSequentialGroup()
                                        .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(campoCalle2)
                                            .addComponent(campoCalle1)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 910, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(22, 22, 22))))
                            .addGroup(panelOrdenesLayout.createSequentialGroup()
                                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelOrdenesLayout.createSequentialGroup()
                                        .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel30)
                                            .addComponent(jLabel32))
                                        .addGap(18, 18, 18)
                                        .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelOrdenesLayout.createSequentialGroup()
                                                .addComponent(comboTiposOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel31)
                                                .addGap(18, 18, 18)
                                                .addComponent(comboTiposOrdenServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel41)
                                                .addGap(18, 18, 18)
                                                .addComponent(campoObservacionesOrden))
                                            .addGroup(panelOrdenesLayout.createSequentialGroup()
                                                .addComponent(campoTvsExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel42)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(campoCostoOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(botonAgregarOrden)
                                                .addGap(18, 18, 18)
                                                .addComponent(botonLimpiarOrdenes))))
                                    .addComponent(etiquetaInfoOrdenes, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42)))))
                .addContainerGap())
        );
        panelOrdenesLayout.setVerticalGroup(
            panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenesLayout.createSequentialGroup()
                .addComponent(etiquetaInfoOrdenes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(comboTiposOrdenServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboTiposOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel41)
                    .addComponent(campoObservacionesOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboNuevoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel32)
                        .addComponent(campoTvsExtra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel33)
                        .addComponent(botonAgregarOrden)
                        .addComponent(botonLimpiarOrdenes)
                        .addComponent(jLabel42)
                        .addComponent(campoCostoOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelOrdenesLayout.createSequentialGroup()
                        .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(campoCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38)
                            .addComponent(campoCalle1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(campoNumeroCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39)
                            .addComponent(campoCalle2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelOrdenesLayout.createSequentialGroup()
                                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel36)
                                    .addComponent(campoColonia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel40))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel37)
                                    .addComponent(campoCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCabecero, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1563, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelOrdenes, javax.swing.GroupLayout.PREFERRED_SIZE, 1497, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrdenes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void botonEliminarPromocionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarPromocionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonEliminarPromocionActionPerformed

    private void comboAniosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAniosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboAniosActionPerformed

    private void campoMontoSugeridoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoMontoSugeridoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoMontoSugeridoActionPerformed

    private void campoContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoContratoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoContratoActionPerformed

    private void botonLimpiarOrdenesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarOrdenesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonLimpiarOrdenesActionPerformed

    private void campoCostoServicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoCostoServicioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoCostoServicioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaReferencia;
    private javax.swing.JButton botonAgregarOrden;
    private javax.swing.JButton botonAplicarPromocion;
    private javax.swing.JButton botonCalculaMonto;
    private javax.swing.JButton botonCobrar;
    private javax.swing.JButton botonEliminarPromocion;
    private javax.swing.JButton botonLimpiarOrdenes;
    private javax.swing.JButton botonReestablecerMonto;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoCalle;
    private javax.swing.JTextField campoCalle1;
    private javax.swing.JTextField campoCalle2;
    private javax.swing.JTextField campoCiudad;
    private javax.swing.JTextField campoColonia;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoCostoOrden;
    private javax.swing.JTextField campoCostoServicio;
    private javax.swing.JTextField campoDomicilio;
    private javax.swing.JTextField campoEstatus;
    private javax.swing.JTextField campoFechaPago;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoMonto;
    private javax.swing.JTextField campoMontoSugerido;
    private javax.swing.JTextField campoMotivoDescuento;
    private javax.swing.JTextField campoNumeroCalle;
    private javax.swing.JTextArea campoObservaciones;
    private javax.swing.JTextField campoObservacionesOrden;
    private javax.swing.JTextField campoServicioContratado;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JTextField campoTvsExtra;
    private javax.swing.JCheckBox checkServicio;
    private javax.swing.JComboBox<Integer> comboAnios;
    private javax.swing.JComboBox<Mes> comboMeses;
    private javax.swing.JComboBox<ServicioEntity> comboNuevoServicio;
    private javax.swing.JComboBox<PromocionEntity> comboPromociones;
    private javax.swing.JComboBox<TipoDescuentoEntity> comboTipoDescuento;
    private javax.swing.JComboBox<TipoOrden> comboTiposOrden;
    private javax.swing.JComboBox<TipoOrden> comboTiposOrdenServicio;
    private javax.swing.JLabel etiquetaCOntrato;
    private javax.swing.JLabel etiquetaDescPago1;
    private javax.swing.JLabel etiquetaDescPago2;
    private javax.swing.JLabel etiquetaDescPago3;
    private javax.swing.JLabel etiquetaEstatus;
    private javax.swing.JLabel etiquetaImporte;
    private javax.swing.JLabel etiquetaInfoContrato;
    private javax.swing.JLabel etiquetaInfoOrdenes;
    private javax.swing.JLabel etiquetaInfoPromociones;
    private javax.swing.JLabel etiquetaNombreSuscriptor;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaPesos;
    private javax.swing.JLabel etiquetaPromocionAplicada;
    private javax.swing.JLabel etiquetaRecargoPagoTardio;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelDescuentos;
    private javax.swing.JPanel panelImportes;
    private javax.swing.JPanel panelInfoContrato;
    private javax.swing.JPanel panelInfoPago;
    private javax.swing.JPanel panelOrdenes;
    private javax.swing.JPanel panelPromociones;
    private javax.swing.JTable tablaOrdenes;
    // End of variables declaration//GEN-END:variables
}
