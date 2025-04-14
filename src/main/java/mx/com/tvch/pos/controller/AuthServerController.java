/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import mx.com.tvch.pos.client.TvchApiClient;
import mx.com.tvch.pos.model.client.AuthRequest;
import mx.com.tvch.pos.model.client.AuthResponse;
import mx.com.tvch.pos.model.client.Request;
import mx.com.tvch.pos.model.client.Response;
import mx.com.tvch.pos.util.Constantes;

/**
 *
 * @author fvega
 */
public class AuthServerController {
    
    private static AuthServerController controller;
    
    private final TvchApiClient client;
    
    public static AuthServerController getAuthServerController(){
        if(controller == null)
            controller = new AuthServerController();
        return controller;
    }
    
    public AuthServerController(){
        client = TvchApiClient.getTvchApiClient();
    }
    
    /**
     * 
     * @param usuario
     * @param password
     * @return 
     */
    public Response<AuthResponse> autenticarUsuario(String usuario, String password){
        
        try{
            
            AuthRequest authRequest = new AuthRequest();
            authRequest.setUsername(usuario);
            authRequest.setPassword(password);
            Request<AuthRequest> request= new Request<>();
            request.setData(authRequest);
            
            return client.autenticarUsuario(request);
            
        }catch(Exception e){
            
            Response<AuthResponse> response = new Response<>();
            response.setCode(Constantes.CODIGO_HTTP_SERVER_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    
}
