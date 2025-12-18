/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import br.com.adilson.util.PrinterMatrix;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.entity.AperturaCajaEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorDetalleEntity;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import mx.com.tvch.pos.entity.DetallePromocionTransaccionEntity;
import mx.com.tvch.pos.entity.SalidaExtraordinariaEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.model.TransaccionTicket;
import mx.com.tvch.pos.model.CobroServicio;
import mx.com.tvch.pos.model.CorteCaja;
import mx.com.tvch.pos.model.DetalleCobroTransaccion;
import mx.com.tvch.pos.model.DetalleCorte;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.model.Mes;
import mx.com.tvch.pos.model.Orden;
import mx.com.tvch.pos.model.Transaccion;
import mx.com.tvch.pos.model.client.Suscriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class Impresora {

    private static Impresora impresora;

    //private final LectorProperties properties;
    private final Sesion sesion;
    private final Utilerias utilerias;

    Logger logger = LoggerFactory.getLogger(Impresora.class);

    public static Impresora getImpresora() {
        if (impresora == null) {
            impresora = new Impresora();
        }
        return impresora;
    }

    public Impresora() {
        //properties = LectorProperties.getLectorProperties();
        sesion = Sesion.getSesion();
        utilerias = Utilerias.getUtilerias();
    }

    /*public static void main(String args[]){
        
        try {
            imprimirTicketOrdenInstalacion();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Impresora.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
 
    /**
     * 
     * @param transaccion
     * @throws Exception 
     */
    public void reimprimirTicketCancelacion(
            Transaccion transaccion) throws Exception {

        StringBuilder nombre = new StringBuilder();
        nombre.append(transaccion.getNombre()).append(" ")
                .append(transaccion.getApellidoPaterno()).append(" ")
                .append(transaccion.getApellidoMaterno());

        StringBuilder domicilio = new StringBuilder();
        if (transaccion.getCalle() != null) {
            domicilio.append(transaccion.getCalle()).append(" ");
        }
        if (transaccion.getNumeroCalle() != null) {
            domicilio.append(transaccion.getNumeroCalle()).append(" ");
        }
        if (transaccion.getColonia() != null) {
            domicilio.append(transaccion.getColonia());
        }

        String contrato = transaccion.getNombreSucursal().concat("-")
                .concat(String.valueOf(transaccion.getFolioContrato()));

        /*DetallePagoServicio detalleCobro = detallesPago
                .stream()
                .filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_CANCELACION)
                .findAny()
                .get();*/
        Double importeTotal = transaccion.getMonto();

        PrinterMatrix pm = new PrinterMatrix();

        int cantidadLineas = 51;

        pm.setOutSize(cantidadLineas, 47);
        //pm.printCharAtCol(1, 1, 47, "=");

        int linea = 2;
        pm.printTextLinCol(linea, 1, "\n");
        linea++;
        pm.printTextWrap(linea, 1, 13, 47, "Comprobante de Pago");
        linea = linea + 2;
        pm.printTextWrap(linea, 1, 13, 47, "TV Cable Hidalguense");
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCiudadRfc().intValue(),
                47, sesion.getTicketLineaCiudadRfc());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCalle().intValue(),
                47, sesion.getTicketLineaCalle());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaColonia(),
                47, sesion.getTicketLineaColonia());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaSucursal(),
                47, "Sucursal ".concat(transaccion.getNombreSucursal()));
        linea = linea + 3;

        pm.printTextLinCol(linea, 1, "Fecha:");
        pm.printTextLinCol(linea, 14, transaccion.getFechaTransaccion());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Caja:");
        pm.printTextLinCol(linea, 14, String.valueOf(sesion.getNumeroCaja()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Folio:");
        pm.printTextLinCol(linea, 14, String.valueOf(transaccion.getTransaccionId()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Tipo Pago:");
        pm.printTextLinCol(linea, 14, "Cancelación de Contrato");

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Contrato:");
        pm.printTextLinCol(linea, 14, contrato);
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Suscriptor:");
        pm.printTextLinCol(linea, 14, nombre.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Domicilio:");
        pm.printTextLinCol(linea, 14, domicilio.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Servicio:");
        pm.printTextLinCol(linea, 14, transaccion.getServicio());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Telefono:");
        pm.printTextLinCol(linea, 14, transaccion.getTelefono());

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Total a pagar:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(importeTotal)));
        linea++;
        pm.printTextWrap(linea, 1, 1, 47, "RECONEXION DE 24 a 48 HORAS DESPUES DE SU PAGO");
        linea++;
        pm.printTextWrap(linea, 1, 3, 47, "CANCELACION DEL 25 AL 30 DEL MES PAGADO");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "HORARIO DE OFICINA LUNES A VIERNES 9AM A 6PM,");
        linea++;
        pm.printTextWrap(linea, 1, 14, 47, "SABADO DE 9AM A 2PM");
        linea = linea + 2;

        if (sesion.getTelefonoSucursal() != null && !sesion.getTelefonoSucursal().isEmpty()) {
            pm.printTextLinCol(linea, 10, "Telefono Oficina:");
            pm.printTextLinCol(linea, 29, sesion.getTelefonoSucursal());
            linea++;
        }
        if (sesion.getTelefonoSoporte() != null && !sesion.getTelefonoSoporte().isEmpty()
                && !sesion.getTelefonoSoporte().contains("null")) {
            pm.printTextLinCol(linea, 5, "Soporte Tecnico WhatsApp:");
            pm.printTextLinCol(linea, 31, sesion.getTelefonoSoporte());
        }

        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);

    }

    /**
     *
     * @param transaccionId
     * @param detallesPago
     * @param nombreSucursal
     * @param suscriptor
     * @throws Exception
     */
    public void imprimirTicketCancelacion(
            Long transaccionId,
            List<DetallePagoServicio> detallesPago,
            ContratoxSuscriptorDetalleEntity suscriptor,
            String nombreSucursal) throws Exception {

        StringBuilder nombre = new StringBuilder();
        nombre.append(suscriptor.getNombre()).append(" ").append(suscriptor.getApellidoPaterno()).append(" ").append(suscriptor.getApellidoMaterno());

        StringBuilder domicilio = new StringBuilder();
        if (suscriptor.getCalle() != null) {
            domicilio.append(suscriptor.getCalle()).append(" ");
        }
        if (suscriptor.getNumeroCalle() != null) {
            domicilio.append(suscriptor.getNumeroCalle()).append(" ");
        }
        if (suscriptor.getColonia() != null) {
            domicilio.append(suscriptor.getColonia());
        }

        String contrato = nombreSucursal.concat("-").concat(String.valueOf(suscriptor.getFolioContrato()));

        DetallePagoServicio detalleCobro = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_CANCELACION).findAny().get();
        Double importeTotal = detalleCobro.getMonto();

        PrinterMatrix pm = new PrinterMatrix();

        int cantidadLineas = 51;

        pm.setOutSize(cantidadLineas, 47);
        //pm.printCharAtCol(1, 1, 47, "=");

        int linea = 2;
        pm.printTextLinCol(linea, 1, "\n");
        linea++;
        pm.printTextWrap(linea, 1, 13, 47, "Comprobante de Pago");
        linea = linea + 2;
        pm.printTextWrap(linea, 1, 13, 47, "TV Cable Hidalguense");
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCiudadRfc().intValue(),
                47, sesion.getTicketLineaCiudadRfc());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCalle().intValue(),
                47, sesion.getTicketLineaCalle());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaColonia(),
                47, sesion.getTicketLineaColonia());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaSucursal(),
                47, "Sucursal ".concat(nombreSucursal));
        linea = linea + 3;

        pm.printTextLinCol(linea, 1, "Fecha:");
        pm.printTextLinCol(linea, 14, utilerias.convertirDateTime2String(new Date(), "dd/MM/yyyy HH:mm:ss"));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Caja:");
        pm.printTextLinCol(linea, 14, String.valueOf(sesion.getNumeroCaja()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Folio:");
        pm.printTextLinCol(linea, 14, String.valueOf(transaccionId));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Tipo Pago:");
        pm.printTextLinCol(linea, 14, "Cancelación de Contrato");

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Contrato:");
        pm.printTextLinCol(linea, 14, contrato);
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Suscriptor:");
        pm.printTextLinCol(linea, 14, nombre.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Domicilio:");
        pm.printTextLinCol(linea, 14, domicilio.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Servicio:");
        pm.printTextLinCol(linea, 14, suscriptor.getServicio());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Telefono:");
        pm.printTextLinCol(linea, 14, suscriptor.getTelefono());

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Total a pagar:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(importeTotal)));

        linea++;
        pm.printTextWrap(linea, 1, 1, 47, "RECONEXION DE 24 a 48 HORAS DESPUES DE SU PAGO");
        linea++;
        pm.printTextWrap(linea, 1, 3, 47, "CANCELACION DEL 25 AL 30 DEL MES PAGADO");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "HORARIO DE OFICINA LUNES A VIERNES 9AM A 6PM,");
        linea++;
        pm.printTextWrap(linea, 1, 14, 47, "SABADO DE 9AM A 2PM");
        linea = linea + 2;

        if (sesion.getTelefonoSucursal() != null && !sesion.getTelefonoSucursal().isEmpty()) {
            pm.printTextLinCol(linea, 10, "Telefono Oficina:");
            pm.printTextLinCol(linea, 29, sesion.getTelefonoSucursal());
            linea++;
        }
        if (sesion.getTelefonoSoporte() != null && !sesion.getTelefonoSoporte().isEmpty()
                && !sesion.getTelefonoSoporte().contains("null")) {
            pm.printTextLinCol(linea, 5, "Soporte Tecnico WhatsApp:");
            pm.printTextLinCol(linea, 31, sesion.getTelefonoSoporte());
        }

        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);

    }

    public void imprimirTicketSalidaExtraordinaria(SalidaExtraordinariaEntity entity) throws Exception {

        PrinterMatrix pm = new PrinterMatrix();

        pm.setOutSize(25, 47);
        pm.printCharAtCol(1, 1, 47, "=");
        pm.printTextWrap(1, 1, 13, 47, "Egreso Extraordinario");

        pm.printTextLinCol(4, 1, "Sucursal:");
        pm.printTextLinCol(4, 27, sesion.getSucursal());
        pm.printTextLinCol(5, 1, "Folio Salida:");
        pm.printTextLinCol(5, 27, String.valueOf(entity.getSalidaExtraordinariaId()));
        pm.printTextLinCol(6, 1, "Folio Server:");
        if (entity.getSalidaExtraordinariaServerId() != null) {
            pm.printTextLinCol(6, 27, entity.getSalidaExtraordinariaServerId().toString());
        } else {
            pm.printTextLinCol(6, 27, "");
        }
        pm.printTextLinCol(7, 1, "Num. Caja:");
        pm.printTextLinCol(7, 27, String.valueOf(entity.getCajaId()));
        pm.printTextLinCol(8, 1, "Cajero:");
        pm.printTextLinCol(8, 27, entity.getUsuario());
        pm.printTextLinCol(9, 1, "Monto:");
        pm.printTextLinCol(9, 27, String.valueOf(entity.getMonto()));

        pm.printTextLinCol(11, 1, "Fecha:");
        pm.printTextLinCol(11, 27, utilerias.convertirDateTime2String(entity.getFechaSalida(), Constantes.FORMATO_FECHA_TICKET));
        pm.printTextLinCol(12, 1, "Hora:");
        pm.printTextLinCol(12, 27, utilerias.convertirDateTime2String(entity.getFechaSalida(), Constantes.FORMATO_HORA_TICKET));

        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);
    }

    public void imprimirTicketCorteCaja(List<DetalleCorte> list, CorteCaja corteCaja) throws Exception {

        DetalleCorte detalleId = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_ID).findFirst().get();

        PrinterMatrix pm = new PrinterMatrix();

        pm.setOutSize(40, 47);
        pm.printCharAtCol(1, 1, 47, "=");
        pm.printTextWrap(1, 1, 15, 47, "Corte de caja");

        pm.printTextLinCol(4, 1, "Sucursal:");
        pm.printTextLinCol(4, 27, sesion.getSucursal());
        pm.printTextLinCol(5, 1, "Folio Corte:");
        pm.printTextLinCol(5, 27, String.valueOf(corteCaja.getEntity().getCorteCajaId()));
        pm.printTextLinCol(6, 1, "Folio Server:");
        if (corteCaja.getEntity().getCorteCajaIdServer() != null) {
            pm.printTextLinCol(6, 27, corteCaja.getEntity().getCorteCajaIdServer().toString());
        } else {
            pm.printTextLinCol(6, 27, "");
        }
        pm.printTextLinCol(7, 1, "Num. Caja:");
        pm.printTextLinCol(7, 27, String.valueOf(sesion.getNumeroCaja()));
        pm.printTextLinCol(8, 1, "Cajero:");
        pm.printTextLinCol(8, 27, sesion.getUsuario());
        pm.printTextLinCol(9, 1, "Fondo Fijo:");
        pm.printTextLinCol(9, 27, corteCaja.getEntity().getFondoFijo().toString());
        try {
            pm.printTextLinCol(10, 1, "Fecha:");
            pm.printTextLinCol(10, 27, utilerias.convertirDateTime2String(utilerias.convertirString2Date(corteCaja.getEntity().getFechaCorte(), Constantes.FORMATO_FECHA_MYSQL), Constantes.FORMATO_FECHA_TICKET));
            pm.printTextLinCol(11, 1, "Hora:");
            pm.printTextLinCol(11, 27, utilerias.convertirDateTime2String(utilerias.convertirString2Date(corteCaja.getEntity().getFechaCorte(), Constantes.FORMATO_FECHA_MYSQL), Constantes.FORMATO_HORA_TICKET));
        } catch (ParseException ex) {

        }
        pm.printTextLinCol(13, 1, "Operaciones Realizadas:");
        pm.printTextLinCol(13, 27, String.valueOf(corteCaja.getEntity().getCantidadCobros()));
        pm.printTextLinCol(14, 1, "Monto Total Cobrado:");
        pm.printTextLinCol(14, 27, String.valueOf(corteCaja.getEntity().getTotalCobros()));

        pm.printTextLinCol(16, 1, "Descuentos otorgados:");
        pm.printTextLinCol(16, 27, String.valueOf(corteCaja.getEntity().getCantidadDescuentos()));
        pm.printTextLinCol(17, 1, "Monto de los descuentos:");
        pm.printTextLinCol(17, 27, String.valueOf(corteCaja.getEntity().getTotalDescuentos()));

        pm.printTextLinCol(19, 1, "Salidas de caja:");
        pm.printTextLinCol(19, 27, String.valueOf(corteCaja.getEntity().getCantidadSalidas()));
        pm.printTextLinCol(20, 1, "Monto de los salidas:");
        pm.printTextLinCol(20, 27, String.valueOf(corteCaja.getEntity().getTotalSalidas()));

        pm.printTextLinCol(22, 1, "Ingresos de caja:");
        if (corteCaja.getEntity().getCantidadIngresos() != null) {
            pm.printTextLinCol(22, 27, String.valueOf(corteCaja.getEntity().getCantidadIngresos()));
            pm.printTextLinCol(23, 1, "Monto de los ingresos:");
            pm.printTextLinCol(23, 27, String.valueOf(corteCaja.getEntity().getTotalIngresos()));
        } else {
            pm.printTextLinCol(22, 27, "0");
            pm.printTextLinCol(23, 1, "Monto de los ingresos:");
            pm.printTextLinCol(23, 27, "0.0");
        }

        pm.printTextLinCol(25, 1, "Efectivo calculado:");
        pm.printTextLinCol(25, 27, String.valueOf(corteCaja.getEntity().getTotalSolicitado()));
        pm.printTextLinCol(26, 1, "Efectivo entegado:");
        pm.printTextLinCol(26, 27, String.valueOf(corteCaja.getEntity().getTotalEntregado()));

        if (corteCaja.getFaltanteEntity() != null) {
            pm.printTextLinCol(27, 1, "Faltante registrado:");
            pm.printTextLinCol(27, 27, String.valueOf(corteCaja.getFaltanteEntity().getMonto()));
        } else if (corteCaja.getSobranteEntity() != null) {
            pm.printTextLinCol(27, 1, "Sobrante registrado:");
            pm.printTextLinCol(27, 27, String.valueOf(corteCaja.getSobranteEntity().getMonto()));
        }

        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);

    }

    /**
     *
     * @param transaccionId
     * @param cobro
     * @param nombreSucursal
     * @param suscriptor
     * @throws Exception
     */
    public void imprimirTicketServicio(
            Long transaccionId,
            CobroServicio cobro,
            ContratoxSuscriptorDetalleEntity suscriptor,
            String nombreSucursal) throws Exception {

        StringBuilder nombre = new StringBuilder();
        nombre.append(suscriptor.getNombre()).append(" ").append(suscriptor.getApellidoPaterno()).append(" ").append(suscriptor.getApellidoMaterno());

        StringBuilder domicilio = new StringBuilder();
        if (suscriptor.getCalle() != null) {
            domicilio.append(suscriptor.getCalle()).append(" ");
        }
        if (suscriptor.getNumeroCalle() != null) {
            domicilio.append(suscriptor.getNumeroCalle()).append(" ");
        }
        if (suscriptor.getColonia() != null) {
            domicilio.append(suscriptor.getColonia());
        }

        String contrato = nombreSucursal.concat("-").concat(String.valueOf(suscriptor.getFolioContrato()));

        PrinterMatrix pm = new PrinterMatrix();

        int cantidadLineas = 45;

        if (cobro.getDescuento() != null || cobro.getPromocion() != null || cobro.isSeCobraRecargo()) {
            cantidadLineas = cantidadLineas + 4;
        }

        if (cobro.getOrdenesPago() != null && !cobro.getOrdenesPago().isEmpty()) {
            cantidadLineas = cantidadLineas + (cobro.getOrdenesPago().size() * 2);
        }

        pm.setOutSize(cantidadLineas, 47);

        int linea = 2;
        pm.printTextLinCol(linea, 1, "\n");
        linea++;
        pm.printTextWrap(linea, 1, 13, 47, "Comprobante de Pago");
        linea = linea + 2;
        pm.printTextWrap(linea, 1, 13, 47, "TV Cable Hidalguense");
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCiudadRfc().intValue(),
                47, sesion.getTicketLineaCiudadRfc());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCalle().intValue(),
                47, sesion.getTicketLineaCalle());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaColonia(),
                47, sesion.getTicketLineaColonia());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaSucursal(),
                47, "Sucursal ".concat(nombreSucursal));
        linea = linea + 3;

        pm.printTextLinCol(linea, 1, "Fecha:");
        pm.printTextLinCol(linea, 14, utilerias.convertirDateTime2String(new Date(), "dd/MM/yyyy HH:mm:ss"));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Caja:");
        pm.printTextLinCol(linea, 14, String.valueOf(sesion.getNumeroCaja()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Folio:");
        pm.printTextLinCol(linea, 14, String.valueOf(transaccionId));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Tipo Pago:");

        if (cobro.isSeCobraServicio()) {

            if (cobro.getOrdenesPago() == null || cobro.getOrdenesPago().isEmpty()) {

                if (suscriptor.getMesesPorPagar() == 1) {
                    pm.printTextLinCol(linea, 14, "Pago de Mensualidad");
                } else {
                    StringBuilder descripcionPago = new StringBuilder();
                    descripcionPago.append("Pago de ").append(suscriptor.getMesesPorPagar()).append(" mensualidades");
                    pm.printTextLinCol(linea, 14, descripcionPago.toString());
                }

            } else {
                pm.printTextLinCol(linea, 14, "Pago de Mensualidad/Orden");
            }

        } else {
            pm.printTextLinCol(linea, 14, "Pago de Orden(es) de Servicio");
        }

        if (cobro.isSeCobraServicio()) {
            linea = linea + 2;
            pm.printTextLinCol(linea, 1, "Periodo:");
            pm.printTextLinCol(linea, 14, String.valueOf(cobro.getConcepto().replace("Pago", "").toUpperCase()));
        }

        /*for(OrdenAgregadaPago orden : cobro.getOrdenesPago()){
            linea = linea + 2;
            pm.printTextLinCol(linea, 1, "Tipo:");
            pm.printTextLinCol(linea, 14, String.valueOf(cobro.getConcepto().replace("Pago", "").toUpperCase()));
        }*/
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Contrato:");
        pm.printTextLinCol(linea, 14, contrato);
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Suscriptor:");
        pm.printTextLinCol(linea, 14, nombre.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Domicilio:");
        pm.printTextLinCol(linea, 14, domicilio.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Servicio:");
        pm.printTextLinCol(linea, 14, suscriptor.getServicio());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Telefono:");
        pm.printTextLinCol(linea, 14, suscriptor.getTelefono());

        double montoRecargo = 0;
        if (cobro.isSeCobraRecargo()) {
            montoRecargo = cobro.getMontoRecargo();
        }

        if (cobro.isSeCobraServicio()) {

            if (cobro.getOrdenesPago() == null || cobro.getOrdenesPago().isEmpty()) {

                linea = linea + 2;
                pm.printTextLinCol(linea, 1, "Costo Mensualidad(es):");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(cobro.getMontoSugerido())));

            } else {

                linea = linea + 2;
                pm.printTextLinCol(linea, 1, "Costo Mensualidades/Ordenes:");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(cobro.getMontoSugerido())));
            }

            if (cobro.isSeCobraRecargo()) {
                linea++;
                pm.printTextLinCol(linea, 1, "Pago tardio:");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(cobro.getMontoRecargo())));
            }
            if (cobro.getPromocion() != null) {
                linea++;
                pm.printTextLinCol(linea, 1, "Costo Promoción:");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(cobro.getPromocion().getCostoPromocion())));
            } else {
                if (cobro.getDescuento() != null) {
                    linea++;
                    pm.printTextLinCol(linea, 1, "Descuento:");
                    pm.printTextLinCol(linea, 39, "-$ ".concat(String.valueOf(cobro.getMontoSugerido() - cobro.getMontoTotal())));
                }
            }
        } else {
            linea = linea + 2;
            pm.printTextLinCol(linea, 1, "Costo Orden(es):");
            pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(cobro.getMontoSugerido())));
        }

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Total a pagar:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(cobro.getMontoTotal())));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Proximo pago antes de:");
        pm.printTextLinCol(linea, 25, cobro.getFechaProximoPagoTicket());
        linea++;
        pm.printTextWrap(linea, 1, 1, 47, "RECONEXION DE 24 a 48 HORAS DESPUES DE SU PAGO");
        linea++;
        pm.printTextWrap(linea, 1, 3, 47, "CANCELACION DEL 25 AL 30 DEL MES PAGADO");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "HORARIO DE OFICINA LUNES A VIERNES 9AM A 6PM,");
        linea++;
        pm.printTextWrap(linea, 1, 14, 47, "SABADO DE 9AM A 2PM");
        linea = linea + 2;

        if (sesion.getTelefonoSucursal() != null && !sesion.getTelefonoSucursal().isEmpty()) {
            pm.printTextLinCol(linea, 10, "Telefono Oficina:");
            pm.printTextLinCol(linea, 29, sesion.getTelefonoSucursal());
            linea++;
        }
        if (sesion.getTelefonoSoporte() != null && !sesion.getTelefonoSoporte().isEmpty()
                && !sesion.getTelefonoSoporte().contains("null")) {
            pm.printTextLinCol(linea, 5, "Soporte Tecnico WhatsApp:");
            pm.printTextLinCol(linea, 31, sesion.getTelefonoSoporte());
        }

        //pm.printTextLinCol(4, 1, "Folio Caja:");
        //pm.printTextLinCol(4, 15, "");
        //pm.printTextLinCol(5, 1, "Folio Server:");
        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        //int impresiones = 0;
        //do{
        imprimirArchivo(nombreArchivo);
        //impresiones = impresiones+1;
        //}while(impresiones<2);

    }

    /**
     * 
     * @param transaccion
     * @throws Exception 
     */
    public void reimprimirTicketServicio(
            Transaccion transaccion) throws Exception {
        
        boolean seCobraServicio = false;
        boolean existenOrdenes = false;
        boolean existeRecargo = false;
        double montoRecargo = 0;
        Integer mesesPagados = 0;
        if (transaccion.getDetallesCobro()
                .stream()
                .filter(d -> d.getTipoCobroId() == Constantes.TIPO_COBRO_SERVICIO)
                .findAny()
                .isPresent()) { 
            seCobraServicio = true;
            DetalleCobroTransaccion detalle = transaccion.getDetallesCobro()
                .stream()
                .filter(d -> d.getTipoCobroId() == Constantes.TIPO_COBRO_SERVICIO)
                .findFirst()
                .get();
            mesesPagados = detalle.getNumeroMeses();
        }
        if (transaccion.getDetallesCobro()
                .stream()
                .filter(d -> d.getTipoCobroId() == Constantes.TIPO_COBRO_RECARGO_MENSUALIDAD)
                .findAny()
                .isPresent()) {
            existeRecargo = true;
            DetalleCobroTransaccion detalle = transaccion.getDetallesCobro()
                .stream()
                .filter(d -> d.getTipoCobroId() == Constantes.TIPO_COBRO_RECARGO_MENSUALIDAD)
                .findFirst()
                .get();
            montoRecargo = detalle.getMonto();
        }
        if (transaccion.getDetallesCobro()
                .stream()
                .filter(d -> d.getTipoCobroId() != Constantes.TIPO_COBRO_SERVICIO &&
                        d.getTipoCobroId() != Constantes.TIPO_COBRO_RECARGO_MENSUALIDAD)
                .findAny()
                .isPresent()) { 
            existenOrdenes = true;
        }

        StringBuilder nombreCOmpleto = new StringBuilder();
        nombreCOmpleto.append(transaccion.getNombre())
                .append(" ").append(transaccion.getApellidoPaterno())
                .append(" ").append(transaccion.getApellidoMaterno());

        StringBuilder domicilio = new StringBuilder();
        if (transaccion.getCalle() != null) {
            domicilio.append(transaccion.getCalle()).append(" ");
        }
        if (transaccion.getNumeroCalle() != null) {
            domicilio.append(transaccion.getNumeroCalle()).append(" ");
        }
        if (transaccion.getColonia() != null) {
            domicilio.append(transaccion.getColonia());
        }

        String contrato = transaccion.getNombreSucursal().concat("-").concat(transaccion.getFolioContrato());

        PrinterMatrix pm = new PrinterMatrix();

        int cantidadLineas = 45;

        if(transaccion.getDescuento() != null)
            cantidadLineas = cantidadLineas + 4;

        if (transaccion.getDetallesCobro()
                .stream()
                .filter(d -> d.getTipoCobroId() != Constantes.TIPO_COBRO_SERVICIO)
                .findAny()
                .isPresent()) { 
            cantidadLineas = cantidadLineas + 2;
        }
        
        transaccion.getNuevaFechaCorte();
        Date fechaCorte = utilerias.convertirString2Date(transaccion.getNuevaFechaCorte(), "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaCorte);
        Mes mes = utilerias.obtenerMes(cal.get(Calendar.MONTH)+1);
        String fechaCorteTicket = utilerias.obtenerCadenaFechaPago( mes, cal.get(Calendar.YEAR));

        pm.setOutSize(cantidadLineas, 47);

        int linea = 2;
        pm.printTextLinCol(linea, 1, "\n");
        linea++;
        pm.printTextWrap(linea, 1, 13, 47, "Comprobante de Pago");
        linea = linea + 2;
        pm.printTextWrap(linea, 1, 13, 47, "TV Cable Hidalguense");
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCiudadRfc().intValue(),
                47, sesion.getTicketLineaCiudadRfc());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaCalle().intValue(),
                47, sesion.getTicketLineaCalle());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaColonia(),
                47, sesion.getTicketLineaColonia());
        linea++;
        pm.printTextWrap(linea,
                1, sesion.getTicketSangriaSucursal(),
                47, "Sucursal ".concat(transaccion.getNombreSucursal()));
        linea = linea + 3;

        pm.printTextLinCol(linea, 1, "Fecha:");
        //pm.printTextLinCol(linea, 14, utilerias.convertirDateTime2String(new Date(), "dd/MM/yyyy HH:mm:ss"));
        pm.printTextLinCol(linea, 14, transaccion.getFechaTransaccion());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Caja:");
        pm.printTextLinCol(linea, 14, String.valueOf(sesion.getNumeroCaja()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Folio:");
        pm.printTextLinCol(linea, 14, String.valueOf(transaccion.getTransaccionId()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Tipo Pago:");

        if (seCobraServicio) {

            if (!existenOrdenes) {

                if (mesesPagados == 1) {
                    pm.printTextLinCol(linea, 14, "Pago de Mensualidad");
                } else {
                    StringBuilder descripcionPago = new StringBuilder();
                    descripcionPago.append("Pago de ").append(mesesPagados).append(" mensualidades");
                    pm.printTextLinCol(linea, 14, descripcionPago.toString());
                }

            } else {
                pm.printTextLinCol(linea, 14, "Pago de Mensualidad/Orden");
            }

        } else {
            pm.printTextLinCol(linea, 14, "Pago de Orden(es) de Servicio");
        }

        if (seCobraServicio) {
            linea = linea + 2;
            pm.printTextLinCol(linea, 1, "Periodo:");
            pm.printTextLinCol(linea, 14, String.valueOf(transaccion.getPeriodo().toUpperCase()));
        }

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Contrato:");
        pm.printTextLinCol(linea, 14, contrato);
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Suscriptor:");
        pm.printTextLinCol(linea, 14, nombreCOmpleto.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Domicilio:");
        pm.printTextLinCol(linea, 14, domicilio.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Servicio:");
        pm.printTextLinCol(linea, 14, transaccion.getServicio());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Telefono:");
        pm.printTextLinCol(linea, 14, transaccion.getTelefono());

        if (seCobraServicio) {

            if (!existenOrdenes) {

                linea = linea + 2;
                pm.printTextLinCol(linea, 1, "Costo Mensualidad(es):");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(transaccion.getMonto())));

            } else {

                linea = linea + 2;
                pm.printTextLinCol(linea, 1, "Costo Mensualidades/Ordenes:");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(transaccion.getMonto())));
            }

            if (existeRecargo) {
                linea++;
                pm.printTextLinCol(linea, 1, "Pago tardio:");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(montoRecargo)));
            }
            
            if (transaccion.getPromocion() != null) {
                linea++;
                pm.printTextLinCol(linea, 1, "Costo Promoción:");
                pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(transaccion.getPromocion().getCostoPromocion())));
            } else {
                if (transaccion.getDescuento() != null) {
                    linea++;
                    pm.printTextLinCol(linea, 1, "Descuento:");
                    pm.printTextLinCol(linea, 39, "-$ ".concat(String.valueOf(transaccion.getDescuento().getMonto())));
                }
            }
        } else {
            linea = linea + 2;
            pm.printTextLinCol(linea, 1, "Costo Orden(es):");
            pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(transaccion.getMonto())));
        }

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Total a pagar:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(transaccion.getMonto())));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Proximo pago antes de:");
        pm.printTextLinCol(linea, 25, fechaCorteTicket);
        linea++;
        pm.printTextWrap(linea, 1, 1, 47, "RECONEXION DE 24 a 48 HORAS DESPUES DE SU PAGO");
        linea++;
        pm.printTextWrap(linea, 1, 3, 47, "CANCELACION DEL 25 AL 30 DEL MES PAGADO");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "HORARIO DE OFICINA LUNES A VIERNES 9AM A 6PM,");
        linea++;
        pm.printTextWrap(linea, 1, 14, 47, "SABADO DE 9AM A 2PM");
        linea = linea + 2;

        if (sesion.getTelefonoSucursal() != null && !sesion.getTelefonoSucursal().isEmpty()) {
            pm.printTextLinCol(linea, 10, "Telefono Oficina:");
            pm.printTextLinCol(linea, 29, sesion.getTelefonoSucursal());
            linea++;
        }
        if (sesion.getTelefonoSoporte() != null && !sesion.getTelefonoSoporte().isEmpty()
                && !sesion.getTelefonoSoporte().contains("null")) {
            pm.printTextLinCol(linea, 5, "Soporte Tecnico WhatsApp:");
            pm.printTextLinCol(linea, 31, sesion.getTelefonoSoporte());
        }

        //pm.printTextLinCol(4, 1, "Folio Caja:");
        //pm.printTextLinCol(4, 15, "");
        //pm.printTextLinCol(5, 1, "Folio Server:");
        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        //int impresiones = 0;
        //do{
        imprimirArchivo(nombreArchivo);
        //impresiones = impresiones+1;
        //}while(impresiones<2);


    }

    /**
     *
     * @param entity
     * @throws Exception
     */
    public void imprimirTicketAperturaCaja(AperturaCajaEntity entity) throws Exception {

        PrinterMatrix pm = new PrinterMatrix();

        pm.setOutSize(20, 47);
        pm.printCharAtCol(1, 1, 47, "=");
        pm.printTextWrap(1, 1, 15, 47, "Apertura de caja");

        pm.printTextLinCol(4, 1, "Sucursal:");
        pm.printTextLinCol(4, 24, sesion.getSucursal());
        pm.printTextLinCol(5, 1, "Folio Caja:");
        pm.printTextLinCol(5, 24, entity.getAperturaCajaId().toString());
        pm.printTextLinCol(6, 1, "Folio Server:");
        if (entity.getAperturaCajaServer() != null) {
            pm.printTextLinCol(6, 24, entity.getAperturaCajaServer().toString());
        } else {
            pm.printTextLinCol(6, 24, "");
        }
        pm.printTextLinCol(7, 1, "Num. Caja:");
        pm.printTextLinCol(7, 24, entity.getNumeroCaja().toString());
        pm.printTextLinCol(8, 1, "Cajero:");
        pm.printTextLinCol(8, 24, entity.getUsuario());
        pm.printTextLinCol(9, 1, "Fondo Fijo:");
        pm.printTextLinCol(9, 24, entity.getFondoFijo().toString());
        pm.printTextLinCol(10, 1, "Fecha:");
        pm.printTextLinCol(10, 24, utilerias.convertirDateTime2String(entity.getFechaApertura(), Constantes.FORMATO_FECHA_TICKET));
        pm.printTextLinCol(11, 1, "Hora:");
        pm.printTextLinCol(11, 24, utilerias.convertirDateTime2String(entity.getFechaApertura(), Constantes.FORMATO_HORA_TICKET));

        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);

    }

    /*
    private static void imprimirArchivo(String nombreArchivo) throws Exception {

        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(nombreArchivo);
        } catch (FileNotFoundException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            //logger.error("Error al obtener archivo para imprimir: \n" + sw.toString());
            throw new Exception("Error al generar impresion. Por favor contacte a soporte");
            //ex.printStackTrace();
        }

        DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(inputStream, docFormat, null);

        PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        if (defaultPrintService != null) {
            DocPrintJob printJob = defaultPrintService.createPrintJob();
            try {
                printJob.print(document, requestAttributeSet);;
            } catch (PrintException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new Exception("No se detectaron impresoras instaladas. Por favor contacte a soporte");
            //System.out.println("No hay impresoras instaladas");
        }

    }*/

    private static void imprimirArchivo(String nombreArchivo) throws Exception {
        FileInputStream inputStream = null;

        try {
            // 1. LEER EL ARCHIVO ORIGINAL
            String contenidoOriginal;
            try {
                contenidoOriginal = new String(Files.readAllBytes(Paths.get(nombreArchivo)));
            } catch (IOException e) {
                throw new Exception("No se pudo leer el archivo: " + nombreArchivo);
            }

            // 2. AGREGAR COMANDOS PARA CORTE DE PAPEL
            //    Primero avanzamos varias líneas para posicionar el papel
            //    Luego agregamos el comando ESC/POS para corte parcial
            // Secuencia para avanzar 2 líneas (ajustable según necesidades)
            String avanceLineas = "\n\n";

            // Comando ESC/POS para corte parcial:
            // 0x1B = ESC (27 decimal) - Carácter de escape
            // 0x69 = 'i' (105 decimal) - Comando de corte (para muchas Epson)
            // Algunas impresoras usan: 0x1D (GS), 0x56 (V), 0x41 (A) para corte parcial
            String comandoCorte;

            // Opción 1: ESC i (común en Epson)
            comandoCorte = new String(new byte[]{0x1B, 0x69});

            // Opción 2: GS V A (alternativa)
            // comandoCorte = new String(new byte[]{0x1D, 0x56, 0x41});
            // Opción 3: GS V 1 (corte parcial con 1 unidad de feed)
            // comandoCorte = new String(new byte[]{0x1D, 0x56, 0x01});
            // Combinar todo: contenido + avance + comando de corte
            String contenidoConCorte = contenidoOriginal + avanceLineas + comandoCorte;

            // 3. CREAR ARCHIVO TEMPORAL CON EL CORTE
            File tempFile = null;
            try {
                // Crear archivo temporal
                tempFile = File.createTempFile("ticket_con_corte_", ".txt");
                tempFile.deleteOnExit(); // Se eliminará al cerrar la JVM

                // Escribir contenido con corte
                Files.write(tempFile.toPath(), contenidoConCorte.getBytes());

                // Usar el archivo temporal para impresión
                inputStream = new FileInputStream(tempFile);

            } catch (IOException e) {
                throw new Exception("Error al crear archivo temporal: " + e.getMessage());
            }

            // 4. CONFIGURAR E IMPRIMIR 
            DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc document = new SimpleDoc(inputStream, docFormat, null);

            PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();

            // Opcional: forzar impresión en texto plano (para comandos ESC/POS)
            // requestAttributeSet.add(new PrinterResolution(203, 203, PrinterResolution.DPI));
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

            if (defaultPrintService != null) {
                System.out.println("Imprimiendo en: " + defaultPrintService.getName());

                DocPrintJob printJob = defaultPrintService.createPrintJob();
                try {
                    // Agregar listener para saber cuando termina
                    printJob.addPrintJobListener(new PrintJobAdapter() {
                        @Override
                        public void printJobCompleted(PrintJobEvent pje) {
                            System.out.println("Impresión completada");
                        }

                        @Override
                        public void printJobFailed(PrintJobEvent pje) {
                            System.out.println("Error en impresión");
                        }
                    });

                    // Ejecutar impresión
                    printJob.print(document, requestAttributeSet);

                } catch (PrintException ex) {
                    throw new Exception("Error durante la impresión: " + ex.getMessage());
                }
            } else {
                throw new Exception("No se detectaron impresoras instaladas");
            }

            // 5. LIMPIAR ARCHIVO TEMPORAL (opcional)
            // Esperar un momento antes de eliminar
            Thread.sleep(1000);
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }

        } finally {
            // Cerrar streams
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignorar error al cerrar
                }
            }
        }
    }

    /*public static void main(String[] args) {

        PrinterMatrix pm = new PrinterMatrix();

        String numeroFactura = "B001";
        String nombreVnededor = "Javier";
        String nombreComprador = "Jose";

        //Extenso e = new Extenso();
        //e.setNumber(101.85);

        pm.setOutSize(8, 32);
        pm.printCharAtCol(1, 1, 32, "=");
        pm.printTextWrap(1, 1, 8, 32, "Factura Venta");
        pm.printTextWrap(2, 2, 1, 32, "Num. Boleta: " + numeroFactura);
        pm.printTextWrap(3, 3, 1, 32, "Fecha emision: 13/12/2024");
        pm.printTextWrap(4, 4, 1, 32, "Hora emision: 19:47");
        pm.printTextWrap(5, 5, 1, 32, "Vendedor: " + nombreVnededor);
        pm.printTextWrap(6, 6, 1, 32, "Comprador: " + nombreComprador);
        pm.printTextLinCol(7, 1, "Pruebas:");
        pm.printTextLinCol(7, 15, "XOXOXOXXOxoxoxoxoxoxoxoxoxoxoxoxoxoxoxoxoxooxoxoxoxoxoxoxo");
        pm.printTextLinCol(8, 1, "Pruebas 2:");
        pm.printTextLinCol(8, 15, "AMPM");

        pm.toFile("impresion.txt");
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream("impresion.txt");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        if (inputStream == null) {
            return;
        }

        DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(inputStream, docFormat, null);

        PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        if (defaultPrintService != null) {
            DocPrintJob printJob = defaultPrintService.createPrintJob();
            try {
                printJob.print(document, requestAttributeSet);;
            } catch (PrintException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("No hay impresoras instaladas");
        }

    }*/
}
