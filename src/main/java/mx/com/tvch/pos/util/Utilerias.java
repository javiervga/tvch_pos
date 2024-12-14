/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author fvega
 */
public class Utilerias {
    
    private static Utilerias utilerias;
    
    public static Utilerias getUtilerias(){
        if(utilerias == null)
            utilerias = new Utilerias();
        return utilerias;
    }
    
    public static void main(String args[]){
        Utilerias utilerias = getUtilerias();
        System.out.println("hora: "+utilerias.obtenerFechaFormatoMysql());
    }
    
    public String obtenerFechaFormatoMysql(){
        LocalDateTime ld = LocalDateTime.now(ZoneId.of(Constantes.ZONA_HORARIA));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA_MYSQL);
        return ld.format(formatter);
    }
    
    public String convertirDateTime2String(java.util.Date date, String formato){
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime ld = LocalDateTime.ofInstant(instant, ZoneId.of(Constantes.ZONA_HORARIA));
        //LocalDateTime ld = date.toInstant().atZone(ZoneId.of(Constantes.ZONA_HORARIA)).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
        return ld.format(formatter);
    }
            
    public Long generarAperturaCajaId(){
        
        LocalDateTime ld = LocalDateTime.now(ZoneId.of(Constantes.ZONA_HORARIA));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String cadenaId = ld.format(formatter);
        return Long.valueOf(cadenaId);
        
    }
    
    public boolean esMontoValido(String montoCadena){
        
        boolean esValido = false;      
        try{
            Double monto = Double.valueOf(montoCadena);
            if(monto >= 0)
            esValido = true;
        }catch(NumberFormatException e){   
        }
        return esValido;
        
    }
    
    
    
    
    
}
