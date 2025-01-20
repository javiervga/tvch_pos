/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import mx.com.tvch.pos.config.Sesion;

/**
 *
 * @author fvega
 */
public class Utilerias {

    private static Utilerias utilerias;
    private final Sesion sesion;

    public static Utilerias getUtilerias() {
        if (utilerias == null) {
            utilerias = new Utilerias();
        }
        return utilerias;
    }

    public Utilerias() {
        sesion = Sesion.getSesion();
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

        if (fechaPagoActual != null) {
            cfechaPagoActual.setTime(fechaPagoActual);
        } else {
            cfechaPagoActual.setTime(new Date());
        }

        cfechaPagoActual.set(Calendar.DAY_OF_MONTH, diaCorte);
        cfechaPagoActual.add(Calendar.MONTH, 1);

        if (mesesGratis != null) {

            cfechaPagoActual.add(Calendar.MONTH, mesesGratis);

        }

        return convertirDateTime2String(cfechaPagoActual.getTime(), formato);
    }

    private Calendar sumarMes(Calendar fecha, int diaCorte) {

        fecha.set(Calendar.DAY_OF_MONTH, diaCorte);
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        if (mes == Calendar.DECEMBER) {
            fecha.set(Calendar.YEAR, anio + 1);
            fecha.set(Calendar.MONTH, Calendar.JANUARY);
        } else {
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
    public String obtenerNuevaFechaProximoPagoOrdenInstalacion(int diaCorte, Integer mesesGratis) throws ParseException {

        if (diaCorte == 0) 
            diaCorte = 10;
        
        /*
        
        nuevaFechaPago = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_WEB_SERVICE);
        Date fechaPagoActual = dateFormat.parse(fechaPago);
        nuevaFechaPago.setTime(fechaPagoActual);

        return convertirDateTime2String(nuevaFechaPago.getTime(), Constantes.FORMATO_FECHA_WEB_SERVICE);*/
        
        Date nuevaFechaPago = obtenerFechaProximoPago(diaCorte);
        Calendar calFecha = Calendar.getInstance();
        calFecha.setTime(nuevaFechaPago);
        if (mesesGratis != null) {
            //agregar los meses
            calFecha.add(Calendar.MONTH, mesesGratis);
        }
        return convertirDateTime2String(calFecha.getTime(), Constantes.FORMATO_FECHA_WEB_SERVICE);
        
    }

    public Date obtenerFechaProximoPago(int diaCorte) {
        Calendar hoy = Calendar.getInstance();
        int diaMes = hoy.get(Calendar.DAY_OF_MONTH);
        Calendar diaPago = Calendar.getInstance();
        diaPago.set(Calendar.DAY_OF_MONTH, diaCorte);
        if (diaMes > 15) {
            // se da gratis el mes en curso y el proximo
            int mes = hoy.get(Calendar.MONTH);
            switch (mes) {
                case Calendar.NOVEMBER:
                    diaPago.set(Calendar.MONTH, Calendar.JANUARY);
                    diaPago.add(Calendar.YEAR, 1);
                    break;
                case Calendar.DECEMBER:
                    diaPago.set(Calendar.MONTH, Calendar.FEBRUARY);
                    diaPago.add(Calendar.YEAR, 1);
                    break;
                default:
                    diaPago.add(Calendar.MONTH, 2);
                    break;
            }
        } else {
            // se da gratis el mes en curso
            int mes = hoy.get(Calendar.MONTH);
            switch (mes) {
                case Calendar.DECEMBER:
                    diaPago.set(Calendar.MONTH, Calendar.JANUARY);
                    diaPago.add(Calendar.YEAR, 1);
                    break;
                default:
                    diaPago.add(Calendar.MONTH, 1);
                    break;
            }
        }

        Date in = diaPago.getTime();
        LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.of(Constantes.ZONA_HORARIA));
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
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

    public Date obtenerFecha() throws ParseException {
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.of(Constantes.ZONA_HORARIA)).toInstant());
        return date;
    }

    public Date convertirString2Date(String date, String formato) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(formato);
        return format.parse(date);
    }

    public Long generarIdLocal() {

        LocalDateTime ld = LocalDateTime.now(ZoneId.of(Constantes.ZONA_HORARIA));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String cadenaId = String.valueOf(sesion.getCajaId()).concat(ld.format(formatter));
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
