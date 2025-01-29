/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.client;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import mx.com.tvch.pos.config.JwtSesion;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.mapper.PosMapper;
import mx.com.tvch.pos.model.client.AuthRequest;
import mx.com.tvch.pos.model.client.AuthResponse;
import mx.com.tvch.pos.model.client.ListOrdenesCambioDomicilioPosRequest;
import mx.com.tvch.pos.model.client.ListOrdenesCambioDomicilioResponse;
import mx.com.tvch.pos.model.client.ListOrdenesInstalacionPosRequest;
import mx.com.tvch.pos.model.client.ListOrdenesInstalacionResponse;
import mx.com.tvch.pos.model.client.ListOrdenesServicioPosRequest;
import mx.com.tvch.pos.model.client.ListOrdenesServicioResponse;
import mx.com.tvch.pos.model.client.ListPromocionesOrdenInstalacionRequest;
import mx.com.tvch.pos.model.client.ListPromocionesOrdenInstalacionResponse;
import mx.com.tvch.pos.model.client.ListSuscriptoresRequest;
import mx.com.tvch.pos.model.client.ListSuscriptoresResponse;
import mx.com.tvch.pos.model.client.ListTiposDescuentoResponse;
import mx.com.tvch.pos.model.client.Request;
import mx.com.tvch.pos.model.client.Response;
import mx.com.tvch.pos.model.client.UpdateEstatusPagadaOrdenCambioDomicilioRequest;
import mx.com.tvch.pos.model.client.UpdateEstatusPagadaOrdenInstalacionRequest;
import mx.com.tvch.pos.model.client.UpdateEstatusPagadaOrdenServicioRequest;
import mx.com.tvch.pos.model.client.UpdateOrdenCambioDomicilioResponse;
import mx.com.tvch.pos.model.client.UpdateOrdenInstalacionResponse;
import mx.com.tvch.pos.model.client.UpdateOrdenServicioResponse;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.LectorProperties;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class TvchApiClient {
    
    private static TvchApiClient apiClient;
    
    private final PosMapper mapper;
    private final LectorProperties properties;
    private final JwtSesion jwtSesion;
    private final Sesion sesion;
    
    Logger logger = LoggerFactory.getLogger(TvchApiClient.class);
    
    public static TvchApiClient getTvchApiClient(){
        if(apiClient == null)
            apiClient = new TvchApiClient();
        return apiClient;
    }
    
    public TvchApiClient() {
        mapper = PosMapper.getPosMapper();
        properties = LectorProperties.getLectorProperties();
        jwtSesion = JwtSesion.getJwtSesion();
        sesion = Sesion.getSesion();
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public Response<UpdateOrdenServicioResponse> updateEstatusPagoOrdenServicio(Request<UpdateEstatusPagadaOrdenServicioRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<UpdateOrdenServicioResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request update estatus pago orden de servicio: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPut httpPut = new HttpPut(url + properties.obtenerPropiedad(Constantes.TVCH_API_ORDENES_SERVICIO_UPDATE_PAGO));
        
        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Authorization", obtenerToken());
        httpPut.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de update orden servicio");
        CloseableHttpResponse httpResponse = httpClient.execute(httpPut);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de update de orden servicio a estatus pagado exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2UpdateOrdenServicioResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de update de orden servicio estatus pagado: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de update de orden servicio estatus pagado: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public Response<UpdateOrdenInstalacionResponse> updateEstatusPagoOrdenInstalacion(Request<UpdateEstatusPagadaOrdenInstalacionRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<UpdateOrdenInstalacionResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request update estatus pago orden de instalacion: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPut httpPut = new HttpPut(url + properties.obtenerPropiedad(Constantes.TVCH_API_ORDENES_INSTALACION_UPDATE_PAGO));
        
        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Authorization", obtenerToken());
        httpPut.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de update orden instalacion estatus pago");
        CloseableHttpResponse httpResponse = httpClient.execute(httpPut);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de update de orden instalacion a estatus pagado exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2UpdateOrdenInstalacionResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de update de orden instalacion estatus pagado: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de update de orden instalacion estatus pagadoo: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public Response<UpdateOrdenCambioDomicilioResponse> updateEstatusPagoOrdenCambioDomicilio(Request<UpdateEstatusPagadaOrdenCambioDomicilioRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<UpdateOrdenCambioDomicilioResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request update estatus pago orden de cambio de domicilio: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPut httpPut = new HttpPut(url + properties.obtenerPropiedad(Constantes.TVCH_API_ORDENES_CAMBIO_DOMICILIO_UPDATE_PAGO));
        
        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Authorization", obtenerToken());
        httpPut.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de update orden cambio domicilio estatus pago");
        CloseableHttpResponse httpResponse = httpClient.execute(httpPut);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de update de orden cambio domicilio a estatus pagado exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2UpdateOrdenCambioDomicilioResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de update de orden cambio domicilio estatus pagado: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de update de orden cambio domicilio estatus pagadoo: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    
    public Response<ListTiposDescuentoResponse> consultarTiposDescuento() throws UnsupportedEncodingException, IOException, Exception {
        
        Response<ListTiposDescuentoResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        //String jsonString = gson.toJson(request);
        //System.out.println("Request ordenes de instalacion: " + jsonString);

        //StringEntity entity = new StringEntity(jsonString);
        HttpPost httpPost = new HttpPost(url + properties.obtenerPropiedad(Constantes.TVCH_API_LIST_TIPOS_DESCUENTO));
        
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", obtenerToken());
        //httpPost.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de tipos de descuento");
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de consulta de tipos de descuento exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2ListTiposDescuentoResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de consulta de tipos de descuento: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de consulta de tipos de descuento: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public Response<ListPromocionesOrdenInstalacionResponse> consultarPromocionesOrdenesInstalacion(Request<ListPromocionesOrdenInstalacionRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<ListPromocionesOrdenInstalacionResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request promociones de ordenes de instalacion: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPost httpPost = new HttpPost(url + properties.obtenerPropiedad(Constantes.TVCH_API_LIST_ORDENES_INSTALACION_PROMOCIONES));
        
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", obtenerToken());
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de consulta de promociones de ordenes de instalacion: \n"+jsonString);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de consulta de promociones de ordenes de instalacion exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2ListPromocionesOrdenesInstalacionResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de consulta de promociones de ordenes de instalacion: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de consulta de promociones de ordenes de instalacion: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public Response<ListOrdenesInstalacionResponse> consultarOrdenesInstalacion(Request<ListOrdenesInstalacionPosRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<ListOrdenesInstalacionResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request ordenes de instalacion: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPost httpPost = new HttpPost(url + properties.obtenerPropiedad(Constantes.TVCH_API_LIST_ORDENES_INSTALACION));
        
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", obtenerToken());
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de consulta de ordenes de instalacion: \n"+jsonString);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de consulta de ordenes de instalacion exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2ListOrdenesInstalacionResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de consulta de ordenes de instalacion: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de consulta de ordenes de instalacion: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public Response<ListOrdenesServicioResponse> consultarOrdenesServicio(Request<ListOrdenesServicioPosRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<ListOrdenesServicioResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request ordenes de servicio: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPost httpPost = new HttpPost(url + properties.obtenerPropiedad(Constantes.TVCH_API_LIST_ORDENES_SERVICIO));
        
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", obtenerToken());
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de consulta de ordenes de servicio: \n"+jsonString);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de consulta de ordenes de servicio exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2ListOrdenesServicioResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de consulta de ordenes de servicio: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de consulta de ordenes de servicio: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public Response<ListOrdenesCambioDomicilioResponse> consultarOrdenesCambioDomicilio(Request<ListOrdenesCambioDomicilioPosRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<ListOrdenesCambioDomicilioResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request ordenes de servicio: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPost httpPost = new HttpPost(url + properties.obtenerPropiedad(Constantes.TVCH_API_LIST_ORDENES_CAMBIO_DOMICILIO));
        
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", obtenerToken());
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de consulta de ordenes de cambio de domicilio: \n"+jsonString);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de consulta de ordenes de cambio de domicilio exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2ListOrdenesCambioDomicilioResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de consulta de ordenes de cambio de domicilio: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de consulta de ordenes de cambio de domicilio: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    public Response<ListSuscriptoresResponse> consultarSuscriptores(Request<ListSuscriptoresRequest> request) throws UnsupportedEncodingException, IOException, Exception {
        
        Response<ListSuscriptoresResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        System.out.println("Request suscriptores: " + jsonString);

        StringEntity entity = new StringEntity(jsonString);
        HttpPost httpPost = new HttpPost(url + properties.obtenerPropiedad(Constantes.TVCH_API_LIST_SUSCRIPTORES));
        
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", obtenerToken());
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de consulta de suscriptores: \n"+jsonString);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                && httpResponse.getStatusLine().getStatusCode() != Constantes.CODIGO_HTTP_NO_CONTENT) {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Respuesta de consulta de suscriptores exitosa: \n"+responseBody);
            response = gson.fromJson(responseBody, Response.class);
            response.setData(mapper.object2ListSuscriptoresResponse(response.getData()));
            jwtSesion.setToken(httpResponse.getFirstHeader("Authorization").getValue());
        }else{
            if(httpResponse.getEntity() != null){
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.warn("Fallo en respuesta de consulta de suscriptores: \n"+responseBody);
            }else{
                logger.warn("Fallo en respuesta de consulta de suscriptores: \nCódigo"+httpResponse.getStatusLine());
            }
            response.setCode(httpResponse.getStatusLine().getStatusCode());
        }

        return response;
        
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    public Response<AuthResponse> autenticarUsuario(Request<AuthRequest> request) throws UnsupportedEncodingException, IOException {

        Response<AuthResponse> response = new Response<>();
        String url = properties.obtenerPropiedad(Constantes.TVCH_API_URL);

        Gson gson = new Gson();
        String jsonString = gson.toJson(request);

        StringEntity entity = new StringEntity(jsonString);
        HttpPost httpPost = new HttpPost(url + properties.obtenerPropiedad(Constantes.TVCH_API_LOGIN_URL));
        httpPost.setHeader("Content-type", "application/json");

        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        logger.info("Enviando peticion de login");
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        response = gson.fromJson(responseBody, Response.class);
        if (httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK
                || httpResponse.getStatusLine().getStatusCode() == Constantes.CODIGO_HTTP_OK_WARNING) {
            logger.info("Respuesta de login exitosa: \n"+responseBody);
            response.setData(mapper.object2AuthResponse(response.getData()));
        }else{
            logger.warn("Fallo en respuesta de login: \n"+responseBody);
        }

        return response;
    }
    
    private String obtenerToken() throws Exception{
        
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(sesion.getUsuario());
        authRequest.setPassword(sesion.getPassword());
        Request<AuthRequest> request = new Request<>();
        request.setData(authRequest);
        Response<AuthResponse> response = autenticarUsuario(request);
        return response.getData().getToken();
        
    }
    
}
