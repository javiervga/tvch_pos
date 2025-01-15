/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import br.com.adilson.util.PrinterMatrix;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.entity.AperturaCajaEntity;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.entity.CorteCajaEntity;
import mx.com.tvch.pos.model.CorteCaja;
import mx.com.tvch.pos.model.DetalleCorte;
import mx.com.tvch.pos.model.DetallePagoServicio;
import mx.com.tvch.pos.model.Orden;
import mx.com.tvch.pos.model.client.Suscriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class Impresora {

    private static Impresora impresora;

    private final LectorProperties properties;
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
        properties = LectorProperties.getLectorProperties();
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
    public void imprimirTicketCorteCaja(List<DetalleCorte> list, CorteCaja corteCaja) throws Exception{

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
        
        pm.printTextLinCol(22, 1, "Efectivo calculado:");
        pm.printTextLinCol(22, 27, String.valueOf(corteCaja.getEntity().getTotalSolicitado()));
        pm.printTextLinCol(23, 1, "Efectivo entegado:");
        pm.printTextLinCol(23, 27, String.valueOf(corteCaja.getEntity().getTotalEntregado()));
        
        if(corteCaja.getFaltanteEntity() != null){
            pm.printTextLinCol(24, 1, "Faltante registrado:");
            pm.printTextLinCol(24, 27, String.valueOf(corteCaja.getFaltanteEntity().getMonto()));
        }else if(corteCaja.getSobranteEntity() != null){
            pm.printTextLinCol(24, 1, "Sobrante registrado:");
            pm.printTextLinCol(24, 27, String.valueOf(corteCaja.getSobranteEntity().getMonto()));
        }
        
        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);

    }

    /**
     *
     * @param detallesPago
     * @param orden
     * @param nombreSucursal
     * @param suscriptor
     * @throws Exception
     */
    public void imprimirTicketServicio(List<DetallePagoServicio> detallesPago, ContratoxSuscriptorEntity suscriptor, String nombreSucursal) throws Exception {

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

        String contrato = String.valueOf(suscriptor.getContratoId());
        if (suscriptor.getContratoAnteriorId() != null && suscriptor.getContratoAnteriorId() > 0) {
            contrato = nombreSucursal.concat("-").concat(String.valueOf(suscriptor.getContratoAnteriorId()));
        }

        DetallePagoServicio detalleCobro = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_SERVICIO).findAny().get();
        Double importeTotal = detalleCobro.getMonto();

        PrinterMatrix pm = new PrinterMatrix();

        int cantidadLineas = 51;

        if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().isPresent()
                || detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().isPresent()
                || detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_RECARGO).findAny().isPresent()) {
            cantidadLineas = cantidadLineas + 4;
        }

        pm.setOutSize(cantidadLineas, 47);
        //pm.printCharAtCol(1, 1, 47, "=");

        int linea = 2;
        pm.printTextLinCol(linea, 1, "\n");
        linea++;
        pm.printTextWrap(linea, 1, 13, 47, "Comprobante de Pago");
        linea = linea + 2;
        pm.printTextWrap(linea, 1, 13, 47, "TV Cable Hidalguense");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "Mineral de la Reforma   R.F.C. TCH151120HY6");
        linea++;
        pm.printTextWrap(linea, 1, 11, 47, "Calle San Rafael No. 150,");
        linea++;
        pm.printTextWrap(linea, 1, 7, 47, "Colonia La Providencia C.P. 42186");
        linea++;
        pm.printTextWrap(linea, 1, 9, 47, "Sucursal ".concat(nombreSucursal));
        linea = linea + 3;

        pm.printTextLinCol(linea, 1, "Fecha:");
        pm.printTextLinCol(linea, 14, utilerias.convertirDateTime2String(new Date(), "dd/MM/yyyy HH:mm:ss"));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Caja:");
        pm.printTextLinCol(linea, 14, String.valueOf(sesion.getCajaId()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Tipo Pago:");
        pm.printTextLinCol(linea, 14, "Pago de Mensualidad");
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
        pm.printTextLinCol(linea, 1, "Costo Mensualidad:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(detalleCobro.getMonto())));
        if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_RECARGO).findAny().isPresent()) {
            DetallePagoServicio detalleRecargo = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_RECARGO).findFirst().get();
            importeTotal = importeTotal + detalleRecargo.getMonto();
            linea++;
            pm.printTextLinCol(linea, 1, "Pago tardio:");
            pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(detalleRecargo.getMonto())));
        }
        if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findAny().isPresent()) {
            DetallePagoServicio detallePromocion = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findFirst().get();
            linea++;
            importeTotal = detallePromocion.getMonto();
            pm.printTextLinCol(linea, 1, "Promoción:");
            pm.printTextLinCol(linea, 38, "- $ ".concat(String.valueOf(detalleCobro.getMonto()
                    - detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_PROMOCION).findFirst().get().getMonto())));
        } else {
            if (detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().isPresent()) {
                linea++;
                DetallePagoServicio detalleDescuento = detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findFirst().get();
                importeTotal = importeTotal -detalleDescuento.getMonto();
                pm.printTextLinCol(linea, 1, "Descuento:");
                pm.printTextLinCol(linea, 38, "- $ ".concat(String.valueOf(detallesPago.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_COBRO_DESCUENTO).findAny().get().getMonto())));
            }
        }

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Total:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(importeTotal)));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Proximo pago antes de:");
        pm.printTextLinCol(linea, 25, detalleCobro.getFechaProximoPago());
        linea++;
        pm.printTextWrap(linea, 1, 1, 47, "RECONEXION DE 24 a 48 HORAS DESPUES DE SU PAGO");
        linea++;
        pm.printTextWrap(linea, 1, 3, 47, "CANCELACION DEL 25 AL 30 DEL MES PAGADO");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "HORARIO DE OFICINA LUNES A VIERNES 9AM A 6PM,");
        linea++;
        pm.printTextWrap(linea, 1, 14, 47, "SABADO DE 9AM A 2PM");
        linea = linea + 2;
        pm.printTextLinCol(linea, 10, "Telefono Oficina:");
        pm.printTextLinCol(linea, 29, "7713212773");
        linea++;
        pm.printTextLinCol(linea, 5, "Soporte Tecnico WhatsApp:");
        pm.printTextLinCol(linea, 31, "7717769686");

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
     * @param orden
     * @param suscriptor
     * @throws Exception
     */
    public void imprimirTicketOrdenInstalacion(Orden orden, Suscriptor suscriptor, String nombreSucursal) throws Exception {

        //String nombreSucursal = "11 de Julio";

        /*Suscriptor suscriptor = new Suscriptor();
        suscriptor.setContratoAnterior(3001L);
        suscriptor.setApellidoMaterno("Herrera");
        suscriptor.setApellidoPaterno("Sunderley");
        suscriptor.setNombre("Karina");
        suscriptor.setDomicilio("ESTACIONAMIENTO 33.2 E. 50 DP.403 11 DE JULIO");
        suscriptor.setTelefono("5529462506");
        
        //suscriptor.set
        
        Orden orden = new Orden(1L, 986L, 1, "Orden de Instalacion", 150.0, "06/01/2025", 0.0);
        //orden.setImportePagar(150.0);
        orden.setFechaProximoPago("10/02/2025");
        orden.setServicio("TV + INTERNET 6MB");
        orden.setCostoPromocion(0.0);
        orden.setPromocionId(1L);*/
        StringBuilder nombre = new StringBuilder();
        nombre.append(suscriptor.getNombre()).append(" ").append(suscriptor.getApellidoPaterno()).append(" ").append(suscriptor.getApellidoMaterno());

        String contrato = String.valueOf(orden.getContratoId());
        if (suscriptor.getContratoAnterior() != null && suscriptor.getContratoAnterior() > 0) {
            contrato = nombreSucursal.concat("-").concat(String.valueOf(suscriptor.getContratoAnterior()));
        }

        PrinterMatrix pm = new PrinterMatrix();

        int cantidadLineas = 51;

        if (orden.getPromocionId() != null || orden.getMotivoDescuento() != null) {
            cantidadLineas = cantidadLineas + 2;
        }

        pm.setOutSize(cantidadLineas, 47);
        //pm.printCharAtCol(1, 1, 47, "=");

        int linea = 2;
        pm.printTextLinCol(linea, 1, "\n");
        linea++;
        pm.printTextWrap(linea, 1, 13, 47, "Comprobante de Pago");
        linea = linea + 2;
        pm.printTextWrap(linea, 1, 13, 47, "TV Cable Hidalguense");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "Mineral de la Reforma   R.F.C. TCH151120HY6");
        linea++;
        pm.printTextWrap(linea, 1, 11, 47, "Calle San Rafael No. 150,");
        linea++;
        pm.printTextWrap(linea, 1, 7, 47, "Colonia La Providencia C.P. 42186");
        linea++;
        pm.printTextWrap(linea, 1, 9, 47, "Sucursal ".concat(nombreSucursal));
        linea = linea + 3;

        pm.printTextLinCol(linea, 1, "Fecha:");
        pm.printTextLinCol(linea, 14, utilerias.convertirDateTime2String(new Date(), "dd/MM/yyyy HH:mm:ss"));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Caja:");
        pm.printTextLinCol(linea, 14, String.valueOf(sesion.getCajaId()));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Tipo Pago:");
        pm.printTextLinCol(linea, 14, "Instalacion de Servicio");
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Contrato:");
        pm.printTextLinCol(linea, 14, contrato);
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Suscriptor:");
        pm.printTextLinCol(linea, 14, nombre.toString());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Domicilio:");
        pm.printTextLinCol(linea, 14, suscriptor.getDomicilio());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Servicio:");
        pm.printTextLinCol(linea, 14, orden.getServicio());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Telefono:");
        pm.printTextLinCol(linea, 14, suscriptor.getTelefono());
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Costo Instalacion:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(orden.getCosto())));
        if (orden.getPromocionId() != null) {
            linea++;
            pm.printTextLinCol(linea, 1, "Promoción:");
            pm.printTextLinCol(linea, 38, "- $ ".concat(String.valueOf(orden.getCosto() - orden.getCostoPromocion())));
        } else {
            if (orden.getMotivoDescuento() != null) {
                linea++;
                pm.printTextLinCol(linea, 1, "Descuento:");
                pm.printTextLinCol(linea, 38, "- $ ".concat(String.valueOf(orden.getImporteDescuento())));
            }
        }

        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Total:");
        pm.printTextLinCol(linea, 40, "$ ".concat(String.valueOf(orden.getImportePagar())));
        linea = linea + 2;
        pm.printTextLinCol(linea, 1, "Próximo pago antes de:");
        pm.printTextLinCol(linea, 25, orden.getFechaProximoPago());
        linea++;
        pm.printTextWrap(linea, 1, 1, 47, "RECONEXION DE 24 a 48 HORAS DESPUES DE SU PAGO");
        linea++;
        pm.printTextWrap(linea, 1, 3, 47, "CANCELACION DEL 25 AL 30 DEL MES PAGADO");
        linea++;
        pm.printTextWrap(linea, 1, 2, 47, "HORARIO DE OFICINA LUNES A VIERNES 9AM A 6PM,");
        linea++;
        pm.printTextWrap(linea, 1, 14, 47, "SABADO DE 9AM A 2PM");
        linea = linea + 2;
        pm.printTextLinCol(linea, 10, "Telefono Oficina:");
        pm.printTextLinCol(linea, 29, "7713212773");
        linea++;
        pm.printTextLinCol(linea, 5, "Soporte Tecnico WhatsApp:");
        pm.printTextLinCol(linea, 31, "7717769686");

        //pm.printTextLinCol(4, 1, "Folio Caja:");
        //pm.printTextLinCol(4, 15, "");
        //pm.printTextLinCol(5, 1, "Folio Server:");
        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);

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
        pm.printTextLinCol(10, 24, utilerias.convertirDateTime2String(entity.getHoraApertura(), Constantes.FORMATO_FECHA_TICKET));
        pm.printTextLinCol(11, 1, "Hora:");
        pm.printTextLinCol(11, 24, utilerias.convertirDateTime2String(entity.getHoraApertura(), Constantes.FORMATO_HORA_TICKET));

        String nombreArchivo = ("impresion.txt");
        pm.toFile(nombreArchivo);

        imprimirArchivo(nombreArchivo);

    }

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
