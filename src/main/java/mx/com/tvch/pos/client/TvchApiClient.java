/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.client;

/**
 *
 * @author fvega
 */
public class TvchApiClient {
    
    private static TvchApiClient apiClient;
    
    public static TvchApiClient getTvchApiClient(){
        if(apiClient == null)
            apiClient = new TvchApiClient();
        return apiClient;
    }
    
    
    
}
