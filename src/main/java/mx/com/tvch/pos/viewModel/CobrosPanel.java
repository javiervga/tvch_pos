/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.controller.CobroServicioController;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.PromocionEntity;
import mx.com.tvch.pos.entity.TipoDescuentoEntity;
import mx.com.tvch.pos.model.CobroServicio;
import mx.com.tvch.pos.model.DescuentoCobro;
import mx.com.tvch.pos.model.Mes;
import mx.com.tvch.pos.model.PromocionCobro;
import mx.com.tvch.pos.model.client.Response;
import mx.com.tvch.pos.model.client.UpdateContratoResponse;
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
    private final CobroServicioController controller;
    private final Utilerias util;
    private final Impresora impresora;

    List<ContratoxSuscriptorDetalleEntity> suscriptoresConsultaList;
    private ContratoxSuscriptorDetalleEntity suscriptorSeleccionado;
    //private List<DetallePagoServicio> listaDetallesPago;
    private CobroServicio cobroCapturado;
    private PromocionEntity promocionSeleccionada;
    private Mes mesGuardado;
    private int anioGuardado;

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
        controller = CobroServicioController.getContratoxSuscriptorController();
        util = Utilerias.getUtilerias();
        impresora = Impresora.getImpresora();
        suscriptoresConsultaList = new ArrayList<>();
        cargarComboTiposDescuento();
        crearEventos();
        cargarComboMeses();
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
                    cargarDatosSuscriptor(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, true);
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
                        cargarDatosSuscriptor(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
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

                if (suscriptorSeleccionado != null && cobroCapturado != null) {
                    
                    Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                    int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem(); 
                    if(util.esFechaPagoValida(
                            suscriptorSeleccionado, mesSeleccionado, anioSeleccionado)){
                    
                        try {

                            //Primero realizar las validaciones necesarias

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
                            //cobroCapturado.setMontoTotal(montoPorCobrar);
                            //if(promocionSeleccionada != null)
                            //    cobroCapturado.setMontoTotal(promocionSeleccionada.getCostoPromocion());
                            //else
                            cobroCapturado.setMontoTotal(montoPorCobrar);
                            cobroCapturado.setCadenaMonto("$ ".concat(String.valueOf(montoPorCobrar)));
                            cobroCapturado.setObservaciones(campoObservaciones.getText());

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

                                    try {
                                        impresora.imprimirTicketServicio(transaccionId, cobroCapturado, suscriptorSeleccionado, sesion.getSucursal()/*, numeroMeses*/);
                                    } catch (Exception ex) {
                                        StringWriter sw = new StringWriter();
                                        PrintWriter pw = new PrintWriter(sw);
                                        ex.printStackTrace(pw);
                                        logger.error("Fallo al imprimir ticket de transaccion: \n" + sw.toString());
                                        JOptionPane.showMessageDialog(cobroPanel, "El cobro se realizó correctamente pero ocurrió un error al imprimir su ticket. Si desea una reimpresión vaya a sección de reimpresiones", "", JOptionPane.WARNING_MESSAGE);
                                    }

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
                                                    }   
                                                    break;
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
                    setCamposPagoDefault();
                    Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                    int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
                    if(comboPromociones.getModel().getSize() > 0)
                        comboPromociones.setSelectedIndex(0);
                    promocionSeleccionada = null;
                    cargarDatosSuscriptor(null, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, true);
                    campoMonto.setEnabled(true);
                    comboMeses.setEnabled(true);
                    comboAnios.setEnabled(true);
                    comboTipoDescuento.setEnabled(true);
                    campoMotivoDescuento.setEnabled(true);
                    botonCalculaMonto.setEnabled(true);
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
                    
                    if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_ACTIVO){               
                        PromocionEntity promocion = (PromocionEntity) comboPromociones.getModel().getSelectedItem();
                        
                        if(promocion.getMesesPagados() > 0){                  
                            setCamposPagoPromocion(promocion);
                            Mes mesSeleccionado = (Mes) comboMeses.getModel().getSelectedItem();
                            int anioSeleccionado = (int) comboAnios.getModel().getSelectedItem();
                            promocionSeleccionada = promocion;
                            cargarDatosSuscriptor(promocion, mesSeleccionado, anioSeleccionado, suscriptorSeleccionado, false);
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
                        JOptionPane.showMessageDialog(cobroPanel, "Sólo es posible aplicar promociones a contratos en estatus Activo.", "", JOptionPane.WARNING_MESSAGE);
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

    }

    /**
     * 
     * @param mesSeleccionado
     * @param anioSeleccionado
     * @param contratosuscriptor
     * @param seRefrescanPromociones 
     */
    private void cargarDatosSuscriptor( PromocionEntity promocion, Mes mesSeleccionado, int anioSeleccionado,
            ContratoxSuscriptorDetalleEntity contratosuscriptor, boolean seRefrescanPromociones) {

        System.out.println("Seleccionado: " + contratosuscriptor.getContratoId());
        
        // primero borrar los datos de suscriptores que se hayan seleccionado antes
        etiquetaPromocionAplicada.setVisible(false);
        limpiarDatosSuscriptor();
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
      
        Integer numeroMeses = util.calcularMesesPagados(mesSeleccionado, anioSeleccionado, suscriptorSeleccionado.getFechaProximoPago());
        
        Double montoTotalMeses = 0.0;
        if(contratosuscriptor.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
            comboMeses.setEnabled(false);
            //montoTotalMeses = controller.obtenerMontoPorCobrar(suscriptorSeleccionado);
            montoTotalMeses = suscriptorSeleccionado.getCostoServicio() * numeroMeses;
        }else if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTESIA){
            montoTotalMeses = 0.0;
        }else{
            //montoTotalMeses = controller.obtenerMontoPorCobrar(suscriptorSeleccionado)/* + (suscriptorSeleccionado.getCostoServicio()*numeroMeses)*/;
            if(promocion != null){
                //validar que se cumplan con los meses que se deben pagar para aplicar la promocion
                //if(numeroMeses >= promocion.getMesesPagados()){
                    montoTotalMeses = promocion.getCostoPromocion();
                    //numeroMeses = numeroMeses + promocion.getMesesGratis();
                    etiquetaPromocionAplicada.setVisible(true);
                /*}else{
                    seDebeMostrarAdvertenciaPromocionNoAplicada = true;
                    montoTotalMeses = suscriptorSeleccionado.getCostoServicio() * numeroMeses;
                } */    
            }else{
                montoTotalMeses = suscriptorSeleccionado.getCostoServicio() * numeroMeses;
            } 
        }
        
        campoMontoSugerido.setText(String.valueOf(montoTotalMeses));
        campoMonto.setText(String.valueOf(montoTotalMeses));
        etiquetaImporte.setText(String.valueOf(montoTotalMeses));
        suscriptorSeleccionado.setMesesPorPagar(numeroMeses);
        
        etiquetaDescPago1.setText("Usted está recibiendo el siguiente pago:");
        if(numeroMeses == 1){
            etiquetaDescPago2.setText(util.obtenerDescripcionPagoUnMes(mesSeleccionado, anioSeleccionado));
        }else{
            etiquetaDescPago2.setText(util.obtenerDescripcionVariosMeses(mesPagado, anioPagado, suscriptorSeleccionado.getFechaProximoPago()));
        }

        campoMonto.setEnabled(true);
        comboMeses.setEnabled(true);
        comboAnios.setEnabled(true);

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
        campoCostoServicio.setText(String.valueOf(suscriptorSeleccionado.getCostoServicio()));
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
        
        

        if(seRefrescanPromociones){
            comboPromociones.removeAllItems();
            cargarComboPromociones(suscriptorSeleccionado.getServicioId());
        }
        
        cobroCapturado = new CobroServicio();
        cobroCapturado.setCadenaMonto("$ ".concat(String.valueOf(montoTotalMeses)));
        cobroCapturado.setConcepto(etiquetaDescPago2.getText().replace(">>", "Pago ")); 
        cobroCapturado.setDescuento(null);
        cobroCapturado.setFechaProximoPagoTicket(util.obtenerCadenaFechaPago(mesSeleccionado, anioSeleccionado));
        cobroCapturado.setFechaProximoPago(util.obtenerFechaPago(mesSeleccionado, anioSeleccionado));
        cobroCapturado.setMesesPagados(numeroMeses);
        if(suscriptorSeleccionado.getEstatusContratoId() == Constantes.ESTATUS_CONTRATO_CORTE){
            montoTotalMeses = montoTotalMeses + 50;
             campoMontoSugerido.setText(String.valueOf(montoTotalMeses));
            campoMonto.setText(String.valueOf(montoTotalMeses));
            etiquetaImporte.setText(String.valueOf(montoTotalMeses));
            cobroCapturado.setMontoRecargo(50.0);
            cobroCapturado.setSeCobraRecargo(true);
            etiquetaDescPago4.setVisible(true);
        }else{
            etiquetaDescPago4.setVisible(false);
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
                cadenaMesesPromo.append("Meses Gratis: ").append(promocionSeleccionada.getMesesGratis());
                etiquetaDescPago3.setText(cadenaMesesPromo.toString());
            }else{
                etiquetaDescPago3.setText("Meses a cobrar: ".concat(String.valueOf(numeroMeses)));
            }
  
        }else{
            cobroCapturado.setPromocion(null);
            etiquetaDescPago3.setText("Meses a cobrar: ".concat(String.valueOf(numeroMeses)));
        }
        
        /*if(seDebeMostrarAdvertenciaPromocionNoAplicada){
            JOptionPane.showMessageDialog(cobroPanel, "No se cumplen las condiciones para hacer efectiva la promocion: \n"
                    + "Meses a pagar -> "+promocion.getMesesPagados(), "", JOptionPane.WARNING_MESSAGE);

        }*/
    }

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
        campoMonto.setEnabled(false);
        comboMeses.setEnabled(false);
        comboAnios.setEnabled(false);
    }
    
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
    
    private void habilitarCamposPago(){   
        //posicionarse en el mes en curso
        Calendar fechaEnCurso = Calendar.getInstance();
        int mesEnCurso = fechaEnCurso.get(Calendar.MONTH);
        comboMeses.setSelectedIndex(mesEnCurso+1);
        comboAnios.setSelectedIndex(0);        
        etiquetaImporte.setText("0.00");
        campoMonto.setText("0.00");
        campoMonto.setEnabled(true);
        comboMeses.setEnabled(true);
        comboAnios.setEnabled(true);
    }
    
    private void cargarComboAnios(Date fechaCorte){
        
        List<Integer> anios = util.obtenerAniosPorMostrar(fechaCorte);
        anios.forEach(a -> comboAnios.addItem(a));
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
            comboPromociones.setEnabled(true);

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
    
    public void cargarDatosSesion() {
        
        //primero validar si se selecciono un contrato y se guardo en sesion
        if(sesion.getContratoSeleccionado() == null){
            
        }

        etiquetaNumeroCaja.setText(sesion.getNumeroCaja().toString());
        etiquetaUsuario.setText(sesion.getUsuario());
        etiquetaSucursal.setText(sesion.getSucursal());
        
        ImageIcon imagen = new ImageIcon("src/main/resources/logo_grande.jpg");
        Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(/*etiquetaLogo.getWidth(), etiquetaLogo.getHeight()*/320, 130, Image.SCALE_DEFAULT));
        campoMontoSugerido.setEditable(false);
        etiquetaPromocionAplicada.setVisible(false);
        etiquetaDescPago4.setVisible(false);

    }
    
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
        etiquetaDescPago4.setVisible(false);
        
    }
    
    private void limpiarDatosSuscriptor() {
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
        etiquetaDescPago4.setVisible(false);
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
        jLabel15 = new javax.swing.JLabel();
        campoCostoServicio = new javax.swing.JTextField();
        panelPromociones = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        comboPromociones = new javax.swing.JComboBox<>();
        botonAplicarPromocion = new javax.swing.JButton();
        botonEliminarPromocion = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        campoMontoSugerido = new javax.swing.JTextField();
        botonReestablecerMonto = new javax.swing.JButton();
        panelInfoPago = new javax.swing.JPanel();
        panelImportes = new javax.swing.JPanel();
        etiquetaPesos = new javax.swing.JLabel();
        etiquetaImporte = new javax.swing.JLabel();
        etiquetaPromocionAplicada = new javax.swing.JLabel();
        botonRegresar = new javax.swing.JButton();
        botonCobrar = new javax.swing.JButton();
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
        etiquetaDescPago4 = new javax.swing.JLabel();
        botonCalculaMonto = new javax.swing.JButton();
        panelDescuentos = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        comboTipoDescuento = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        campoMotivoDescuento = new javax.swing.JTextField();

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

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("Información del Contrato");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Nombre del Suscriptor:");

        campoSuscriptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("ID Sistema:");

        campoContrato.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoContratoActionPerformed(evt);
            }
        });

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
        jLabel13.setText("Fecha de Corte:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Teléfono:");

        campoFechaPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoFechaPago.setForeground(java.awt.Color.red);

        campoTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Costo:");

        campoCostoServicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelInfoContratoLayout = new javax.swing.GroupLayout(panelInfoContrato);
        panelInfoContrato.setLayout(panelInfoContratoLayout);
        panelInfoContratoLayout.setHorizontalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelInfoContratoLayout.createSequentialGroup()
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(campoCostoServicio, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                            .addComponent(campoContrato))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                                .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(52, 52, 52))))
        );
        panelInfoContratoLayout.setVerticalGroup(
            panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoContratoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoSuscriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel22)
                    .addComponent(campoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(campoFolioContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(campoEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(campoFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoContratoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(campoServicioContratado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(campoDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(campoCostoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(campoTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 51, 51));
        jLabel25.setText("Monto de Cobro Sugerido:");

        campoMontoSugerido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
                .addGap(12, 12, 12)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addGroup(panelPromocionesLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(botonAplicarPromocion, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonEliminarPromocion)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(campoMontoSugerido, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(botonReestablecerMonto)))
                .addContainerGap(185, Short.MAX_VALUE))
        );
        panelPromocionesLayout.setVerticalGroup(
            panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPromocionesLayout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPromocionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(botonAplicarPromocion)
                        .addComponent(botonEliminarPromocion)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(campoMontoSugerido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(botonReestablecerMonto))
                    .addComponent(jLabel17))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        panelInfoPago.setForeground(java.awt.Color.red);

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
                            .addComponent(etiquetaPesos, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelImportesLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(etiquetaPromocionAplicada)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelImportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonRegresar)
                    .addComponent(botonCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 51, 51));
        jLabel28.setText("Ingrese Información del Pago");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel27.setForeground(java.awt.Color.red);
        jLabel27.setText("Monto:");

        campoMonto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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

        etiquetaDescPago4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaDescPago4.setForeground(java.awt.Color.red);
        etiquetaDescPago4.setText(">> Recargo por Pago Tardío");

        botonCalculaMonto.setBackground(new java.awt.Color(227, 126, 75));
        botonCalculaMonto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        botonCalculaMonto.setForeground(new java.awt.Color(255, 255, 255));
        botonCalculaMonto.setText("Calcular Monto Pago:");

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
                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        javax.swing.GroupLayout panelInfoPagoLayout = new javax.swing.GroupLayout(panelInfoPago);
        panelInfoPago.setLayout(panelInfoPagoLayout);
        panelInfoPagoLayout.setHorizontalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel28)
                        .addGroup(panelInfoPagoLayout.createSequentialGroup()
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(campoMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel23)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(comboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel26)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(comboAnios, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonCalculaMonto))
                .addGap(21, 21, 21)
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(etiquetaDescPago1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(etiquetaDescPago3, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(etiquetaDescPago4, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(etiquetaDescPago2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelImportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInfoPagoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelInfoPagoLayout.setVerticalGroup(
            panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoPagoLayout.createSequentialGroup()
                .addGroup(panelInfoPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoPagoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(etiquetaDescPago3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(etiquetaDescPago4))
                            .addComponent(jLabel29)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelImportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCabecero, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1596, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(318, 318, 318)
                .addComponent(panelPromociones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(187, Short.MAX_VALUE))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAplicarPromocion;
    private javax.swing.JButton botonCalculaMonto;
    private javax.swing.JButton botonCobrar;
    private javax.swing.JButton botonEliminarPromocion;
    private javax.swing.JButton botonReestablecerMonto;
    private javax.swing.JButton botonRegresar;
    private javax.swing.JTextField campoContrato;
    private javax.swing.JTextField campoCostoServicio;
    private javax.swing.JTextField campoDomicilio;
    private javax.swing.JTextField campoEstatus;
    private javax.swing.JTextField campoFechaPago;
    private javax.swing.JTextField campoFolioContrato;
    private javax.swing.JTextField campoMonto;
    private javax.swing.JTextField campoMontoSugerido;
    private javax.swing.JTextField campoMotivoDescuento;
    private javax.swing.JTextArea campoObservaciones;
    private javax.swing.JTextField campoServicioContratado;
    private javax.swing.JTextField campoSuscriptor;
    private javax.swing.JTextField campoTelefono;
    private javax.swing.JComboBox<Integer> comboAnios;
    private javax.swing.JComboBox<Mes> comboMeses;
    private javax.swing.JComboBox<PromocionEntity> comboPromociones;
    private javax.swing.JComboBox<TipoDescuentoEntity> comboTipoDescuento;
    private javax.swing.JLabel etiquetaDescPago1;
    private javax.swing.JLabel etiquetaDescPago2;
    private javax.swing.JLabel etiquetaDescPago3;
    private javax.swing.JLabel etiquetaDescPago4;
    private javax.swing.JLabel etiquetaImporte;
    private javax.swing.JLabel etiquetaNumeroCaja;
    private javax.swing.JLabel etiquetaPesos;
    private javax.swing.JLabel etiquetaPromocionAplicada;
    private javax.swing.JLabel etiquetaSucursal;
    private javax.swing.JLabel etiquetaUsuario;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel panelCabecero;
    private javax.swing.JPanel panelDescuentos;
    private javax.swing.JPanel panelImportes;
    private javax.swing.JPanel panelInfoContrato;
    private javax.swing.JPanel panelInfoPago;
    private javax.swing.JPanel panelPromociones;
    // End of variables declaration//GEN-END:variables
}
