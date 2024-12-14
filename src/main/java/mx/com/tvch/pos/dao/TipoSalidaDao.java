/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.dao;

/**
 *
 * @author fvega
 */
public class TipoSalidaDao {
    
    private static TipoSalidaDao tipoSalidaDao;
    
    public static TipoSalidaDao getTipoSalidaDao(){
        if(tipoSalidaDao == null)
            tipoSalidaDao = new TipoSalidaDao();
        return tipoSalidaDao;
    }
    
    
    
}
