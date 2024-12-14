/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class LectorProperties {
    
    private static LectorProperties lectorProperties;
    
    Logger logger = LoggerFactory.getLogger(LectorProperties.class);
    
    public static LectorProperties getLectorProperties(){
        if(lectorProperties == null)
            lectorProperties = new LectorProperties();
        return lectorProperties;
    }
    
    public String obtenerPropiedad(String propiedadRequerida){
        
        try {
            Properties propiedades = new Properties();
            
            propiedades.load(new FileInputStream("C:\\pos\\pos.properties"));
            
            String propiedad = propiedades.getProperty(propiedadRequerida);
            
            return propiedad;
        } catch (FileNotFoundException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Error, El archivo de propiedades no existe: \n"+sw.toString());
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Error, No se puede leer el archivo de propiedades: \n"+sw.toString());
        }
    
        return null;
    }
}
