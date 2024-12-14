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
    
    public Impresora(){
        properties = LectorProperties.getLectorProperties();
        sesion = Sesion.getSesion();
        utilerias = Utilerias.getUtilerias();
    }
    
    public void imprimirTicketAperturaCaja(AperturaCajaEntity entity) throws  Exception{
        
        if(properties.obtenerPropiedad("impresora.tipo").equals(Constantes.IMPRESORA_58MM)){
            imprimirTicketAperturaCaja58MM(entity);
        }else if(properties.obtenerPropiedad("impresora.tipo").equals(Constantes.IMPRESORA_80MM)){
            
        }else{
            throw new Exception("No se encontro configuracion de impresora en archivo de propiedades. Por favor contacte a soporte.");
        }
        
    }
    
    public void imprimirTicketAperturaCaja58MM(AperturaCajaEntity entity) throws  Exception{
        
        PrinterMatrix pm = new PrinterMatrix();
        
        pm.setOutSize(14, 32);
        pm.printCharAtCol(1, 1, 32, "=");
        pm.printTextWrap(1, 1, 7, 32, "Apertura de caja");
        pm.printTextLinCol(1, 1, "\n");
        pm.printTextLinCol(3, 1, "Sucursal:");
        pm.printTextLinCol(3, 15, sesion.getSucursal());
        pm.printTextLinCol(4, 1, "Folio Caja:");
        pm.printTextLinCol(4, 15, entity.getAperturaCajaId().toString());
        pm.printTextLinCol(5, 1, "Folio Server:");
        if(entity.getAperturaCajaServer() !=null)
            pm.printTextLinCol(5, 15, entity.getAperturaCajaServer().toString());
        else
            pm.printTextLinCol(5, 15, "");
        pm.printTextLinCol(6, 1, "Num. Caja:");
        pm.printTextLinCol(6, 15, entity.getNumeroCaja().toString());
        pm.printTextLinCol(7, 1, "Usuario:");
        pm.printTextLinCol(7, 15, entity.getUsuario());
        pm.printTextLinCol(8, 1, "Fondo Fijo:");
        pm.printTextLinCol(8, 15, entity.getFondoFijo().toString());
        pm.printTextLinCol(9, 1, "Fecha:");
        pm.printTextLinCol(9, 15, utilerias.convertirDateTime2String(entity.getHoraApertura(), Constantes.FORMATO_FECHA_TICKET));
        pm.printTextLinCol(10, 1, "Hora:");
        pm.printTextLinCol(10, 15, utilerias.convertirDateTime2String(entity.getHoraApertura(), Constantes.FORMATO_HORA_TICKET));
        
        String nombreArchivo = "ApCaja".concat(entity.getAperturaCajaId().toString()).concat(".txt");
        pm.toFile(nombreArchivo);
        
        imprimirArchivo(nombreArchivo);
    }
    
    private void imprimirArchivo(String nombreArchivo) throws  Exception{
        
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(nombreArchivo);
        } catch (FileNotFoundException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.error("Error al obtener archivo para imprimir: \n" + sw.toString());
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

    public static void main(String[] args) {

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

    }

}
