/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author fvega
 */
public class Utilerias {

    private static Utilerias utilerias;

    public static Utilerias getUtilerias() {
        if (utilerias == null) {
            utilerias = new Utilerias();
        }
        return utilerias;
    }

    public static void main(String args[]) {
        Utilerias utilerias = getUtilerias();
        System.out.println("hora: " + utilerias.obtenerFechaFormatoMysql());
    }
    
    /**
     * 
     * @param diaCorte
     * @param mesesGratis
     * @param fechaPago
     * @param formato
     * @return
     * @throws ParseException 
     */
    public String obtenerNuevaFechaProximoPago(int diaCorte, Integer mesesGratis, Date fechaPagoActual, String formato) throws ParseException {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_MYSQL);
        Calendar cfechaPagoActual = Calendar.getInstance();
        
        if(fechaPagoActual != null)
            cfechaPagoActual.setTime(fechaPagoActual);
        else
            cfechaPagoActual.setTime(new Date());
        
        cfechaPagoActual.set(Calendar.DAY_OF_MONTH, diaCorte);
        cfechaPagoActual.add(Calendar.MONTH, 1);
        
        if(mesesGratis != null){
 
            cfechaPagoActual.add(Calendar.MONTH, mesesGratis);

        }

        return convertirDateTime2String(cfechaPagoActual.getTime(), formato);
    }
    
    private Calendar sumarMes(Calendar fecha, int diaCorte){
        
        fecha.set(Calendar.DAY_OF_MONTH, diaCorte);
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        if(mes == Calendar.DECEMBER){
            fecha.set(Calendar.YEAR, anio+1);
            fecha.set(Calendar.MONTH, Calendar.JANUARY);
        }else{
            fecha.add(Calendar.MONTH, 2);
        }
        return fecha;
    }

    /**
     * 
     * @param diaCorte
     * @param mesesGratis
     * @param fechaPago
     * @return
     * @throws ParseException 
     */
    public String obtenerNuevaFechaProximoPagoOrdenInstalacion(int diaCorte, Integer mesesGratis, String fechaPago) throws ParseException {
        
        Calendar nuevaFechaPago = Calendar.getInstance();
        nuevaFechaPago = Calendar.getInstance();        
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_WEB_SERVICE);
        Date fechaPagoActual =  dateFormat.parse(fechaPago);
        nuevaFechaPago.setTime(fechaPagoActual);
        
        if(mesesGratis != null){
            //agregar los meses
            nuevaFechaPago.add(Calendar.MONTH, mesesGratis);
        }

        return convertirDateTime2String(nuevaFechaPago.getTime(), Constantes.FORMATO_FECHA_WEB_SERVICE);
    }

    public String obtenerFechaFormatoMysql() {
        LocalDateTime ld = LocalDateTime.now(ZoneId.of(Constantes.ZONA_HORARIA));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA_MYSQL);
        return ld.format(formatter);
    }

    public String convertirDateTime2String(java.util.Date date, String formato) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime ld = LocalDateTime.ofInstant(instant, ZoneId.of(Constantes.ZONA_HORARIA));
        //LocalDateTime ld = date.toInstant().atZone(ZoneId.of(Constantes.ZONA_HORARIA)).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
        return ld.format(formatter);
    }
    
    public Date convertirString2Date(String date, String formato) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(formato);
        return format.parse(date);
    }

    public Long generarAperturaCajaId() {

        LocalDateTime ld = LocalDateTime.now(ZoneId.of(Constantes.ZONA_HORARIA));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String cadenaId = ld.format(formatter);
        return Long.valueOf(cadenaId);

    }

    public boolean esMontoValido(String montoCadena, boolean sePermiteCero) {

        boolean esValido = false;
        try {
            if (sePermiteCero) {
                Double monto = Double.valueOf(montoCadena);
                if (monto >= 0) {
                    esValido = true;
                }
            } else {
                Double monto = Double.valueOf(montoCadena);
                if (monto > 0) {
                    esValido = true;
                }
            }
        } catch (NumberFormatException e) {
        }
        return esValido;

    }

}
