/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.entity.ContratoxSuscriptorEntity;
import mx.com.tvch.pos.model.Mes;

/**
 *
 * @author fvega
 */
public class Utilerias {

    private static Utilerias utilerias;
    private final Sesion sesion;
    DecimalFormat df = new DecimalFormat("#.##");
    List<Mes> meses = new ArrayList<>();
    
    public static Utilerias getUtilerias() {
        if (utilerias == null) {
            utilerias = new Utilerias();
        }
        return utilerias;
    }

    public Utilerias() {
        sesion = Sesion.getSesion();
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
    }
    
    /**
     * 
     * @param fechaCorte
     * @param fechaEnCurso
     * @return 
     */
    public Integer obtenerDiferenciaMeses(Calendar fechaCorte, Calendar fechaEnCurso) {
        LocalDate fechaInicio = LocalDate
                .of(fechaCorte.get(Calendar.YEAR), fechaCorte.get(Calendar.MONTH), fechaCorte.get(Calendar.DAY_OF_MONTH));
        LocalDate fechaFin = LocalDate
                .of(fechaEnCurso.get(Calendar.YEAR), fechaEnCurso.get(Calendar.MONTH), fechaEnCurso.get(Calendar.DAY_OF_MONTH));

        Period periodo = Period.between(fechaInicio, fechaFin);
        return periodo.getMonths();

    }

    
    public Date obtenerFechaPago(Mes mesSeleccionado, int anioSeleccionado){
        
        Calendar fechaPago = Calendar.getInstance();
        fechaPago.setTime(new Date());
        fechaPago.set(Calendar.DAY_OF_MONTH, sesion.getDiaCorte());
        fechaPago.set(Calendar.MONTH, mesSeleccionado.getNumero()-1);
        fechaPago.set(Calendar.YEAR, anioSeleccionado);
        fechaPago.set(Calendar.HOUR, 0);
        fechaPago.set(Calendar.MINUTE, 0);
        fechaPago.set(Calendar.SECOND, 0);

        return fechaPago.getTime();
    }
    
    public String obtenerCadenaFechaPago(Mes mesSeleccionado, int anioSeleccionado){
        
        StringBuilder cadena = new StringBuilder();
                
        int diaCorte = sesion.getDiaCorte();
        
        String cadenaDia = String.valueOf(diaCorte);
        
        if(diaCorte < 10)
            cadena.append("0");
        cadena.append(diaCorte).append("/");
        cadena.append(mesSeleccionado.getNombre()).append("/");
        cadena.append(anioSeleccionado);

        return cadena.toString();
    }
    
    public boolean esFechaPagoValida(ContratoxSuscriptorEntity suscriptor, Mes mesSeleccionado, int anioSeleccionado){
        
        boolean esValida = false;
        
        //primero obtener la fecha de corte
        Calendar fechaCorte = Calendar.getInstance();
        fechaCorte.setTime(suscriptor.getFechaProximoPago());
        
        //despues obtener la fecha selccionada en los combos de mes y año
        Calendar fechaSeleccionada = Calendar.getInstance();
        fechaSeleccionada.setTime(new Date());
        fechaSeleccionada.set(Calendar.MONTH, mesSeleccionado.getNumero()-1);
        fechaSeleccionada.set(Calendar.YEAR, anioSeleccionado);
        fechaSeleccionada.set(Calendar.DAY_OF_MONTH, sesion.getDiaCorte());
        
        //tambien obtener la fecha en curso
        Calendar fechaEnCurso = Calendar.getInstance();
        fechaEnCurso.setTime(new Date());
        
        //primero validar que la fecha seleccionada en los combos sea mayor a la fecha de corte
        int diferenciaAnios = anioSeleccionado - fechaCorte.get(Calendar.YEAR);
        int diferenciaMeses = (diferenciaAnios*12) + mesSeleccionado.getNumero() - (fechaCorte.get(Calendar.MONTH)+1);
        if(diferenciaMeses > 0){
            //despues validar que la fecha seleccionada en los combos sea posterior al mes en curso
            //este caso podria dar cuando un contrato lleve dos o mas meses sin pago pero aparezca en activo 
            //y la fecha seleccionada en los combos sea menor a la fecha en curso
            if(fechaSeleccionada.getTime().after(fechaEnCurso.getTime()))
                esValida = true;
        }
        
        return esValida;
    }
    
    public Mes obtenerMesAnterior(Mes mesSeleccionado){
        
        int mes = mesSeleccionado.getNumero();
        switch (mes) {
            case 1:
                return new Mes(12, "DICIEMBRE");
            case 2:
                return new Mes(1, "ENERO");
            case 3:
                return new Mes(2, "FEBRERO");
            case 4:
                return new Mes(3, "MARZO");
            case 5:
                return new Mes(4, "ABRIL");
            case 6:
                return new Mes(5, "MAYO");
            case 7:
                return new Mes(6, "JUNIO");
            case 8:
                return new Mes(7, "JULIO");
            case 9:
                return new Mes(8, "AGOSTO");
            case 10:
                return new Mes(9, "SEPTIEMBRE");
            case 11:
                return new Mes(10, "OCTUBRE");
            case 12:
                return new Mes(11, "NOVIEMBRE");
        }
        return null;
        
    }
    
    public String obtenerDescripcionPagoUnMes(Mes mesSeleccionado, int anioSeleccionado){
        
        //si el mes es enero, se resta un año
        if(mesSeleccionado.getNumero() == 1){
            anioSeleccionado = anioSeleccionado -1;
        }
        
        StringBuilder desc = new StringBuilder();
        Mes mesPagado = obtenerMesAnterior(mesSeleccionado);
        desc.append(">>").append(mesPagado.getNombre()).append(" ").append(anioSeleccionado);
        
        return desc.toString();
    }
    
    public String obtenerDescripcionVariosMeses(Mes mesPagado, int anioPagado, Date fechaCorte){
        
        Calendar fechaUltimoCorte = Calendar.getInstance();
        fechaUltimoCorte.setTime(fechaCorte);
        int mesEnCurso = fechaUltimoCorte.get(Calendar.MONTH)+1;
        Mes mes = meses.stream().filter(m -> m.getNumero() == mesEnCurso).findFirst().get();
        
        Calendar fechaSeleccionada = Calendar.getInstance();
        fechaSeleccionada.setTime(fechaCorte);
        
        StringBuilder desc = new StringBuilder();
        desc.append(">>").append(mes.getNombre()).append(" ").append(fechaUltimoCorte.get(Calendar.YEAR));
        desc.append(" a ").append(mesPagado.getNombre()).append(" ").append(anioPagado);
        
        return desc.toString();
    }
    
    public int calcularMesesPagados(Mes mesSeleccionado, int anioSeleccionado, Date fechaCorte){
        
        Date fecha = new Date();
        
        Calendar fechaUltimoCorte = Calendar.getInstance();
        fechaUltimoCorte.setTime(fechaCorte);
        //int mesEnCurso = fechaUltimoCorte.get(Calendar.MONTH)+1;
        //int anioCorte = fechaUltimoCorte.get(Calendar.YEAR);
        
        Calendar fechaSeleccionada = Calendar.getInstance();
        fechaSeleccionada.setTime(fecha);
        fechaSeleccionada.set(Calendar.YEAR, anioSeleccionado);
        fechaSeleccionada.set(Calendar.MONTH, mesSeleccionado.getNumero()-1);
        
        //si esta vencido es porque la fecha ya se paso
        int diferenciaAnios = anioSeleccionado - fechaUltimoCorte.get(Calendar.YEAR);
        int diferenciaMeses = (diferenciaAnios*12) + mesSeleccionado.getNumero() - (fechaUltimoCorte.get(Calendar.MONTH)+1);
        
        return diferenciaMeses;
    }
    
    public List<Integer> obtenerAniosPorMostrar(Date fechaCorte){
        
        List<Integer> anios = new ArrayList<>();
        
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(fechaCorte);
        int anioCorte = fecha.get(Calendar.YEAR);
        int siguienteAnio = anioCorte + 1;
        int siguiente2Anio = siguienteAnio + 1;
        anios.add(anioCorte);
        anios.add(siguienteAnio);
        anios.add(siguiente2Anio);
        return anios;
    }
    
    public Double redondearMonto(Double monto){
        String montoCadena = df.format(monto);
        return Double.valueOf(montoCadena);
    }

    

    public static void main(String args[]) {
        Utilerias utilerias = getUtilerias();
        System.out.println("hora: " + utilerias.obtenerFechaFormatoMysql());
    }

    public Date ajustarFechaInicio(Date fecha) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();

    }

    public Date ajustarFechaFin(Date fecha) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();

    }

    /**
     *
     * @param diaCorte
     * @param mesesGratis
     * @param fechaPagoActual
     * @param numeroMesesPagados
     * @param formato
     * @return
     * @throws ParseException
     */
    public String obtenerNuevaFechaProximoPago(
            int diaCorte, 
            Integer mesesGratis, 
            Date fechaPagoActual, 
            String formato, 
            Integer numeroMesesPagados,
            Integer estatusContrato) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_MYSQL);
        Calendar cfechaPagoActual = Calendar.getInstance();

        if (fechaPagoActual != null && estatusContrato != Constantes.ESTATUS_CONTRATO_CORTE) {
            cfechaPagoActual.setTime(fechaPagoActual);
        } else {
            cfechaPagoActual.setTime(new Date());
        }

        cfechaPagoActual.set(Calendar.DAY_OF_MONTH, diaCorte);
        if(estatusContrato == Constantes.ESTATUS_CONTRATO_CORTE)
            cfechaPagoActual.add(Calendar.MONTH, 1);
        else
            cfechaPagoActual.add(Calendar.MONTH, numeroMesesPagados);

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
    public String obtenerNuevaFechaProximoPagoOrdenInstalacion(int diaCorte, Integer mesesGratis, String formato) throws ParseException {

        if (diaCorte == 0) {
            diaCorte = 10;
        }

        Date nuevaFechaPago = obtenerFechaProximoPago(diaCorte);
        Calendar calFecha = Calendar.getInstance();
        calFecha.setTime(nuevaFechaPago);
        if (mesesGratis != null) {
            //agregar los meses
            calFecha.add(Calendar.MONTH, mesesGratis);
        }
        return convertirDateTime2String(calFecha.getTime(), formato);

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
