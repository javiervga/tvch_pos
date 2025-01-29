/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

/**
 *
 * @author fvega
 */
public class Constantes {
    
    public static final String ZONA_HORARIA = "America/Mexico_City";
    public static final String FORMATO_FECHA_MYSQL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMATO_FECHA_TICKET = "dd/MM/yy";
    public static final String FORMATO_FECHA_WEB_SERVICE = "dd/MM/yyyy";
    public static final String FORMATO_HORA_TICKET = "HH:mm:ss";
    
    public static final long ESTATUS_SUSCRIPTOR_ACTIVO = 2;
    
    public static final long ESTATUS_CONTRATO_NUEVO = 1;
    public static final long ESTATUS_CONTRATO_PENDIENTE_INSTALAR = 2;
    public static final long ESTATUS_CONTRATO_ACTIVO = 3;
    public static final long ESTATUS_CONTRATO_CORTESIA = 4;
    public static final long ESTATUS_CONTRATO_CORTE = 5;
    public static final long ESTATUS_CONTRATO_RECONEXION = 6;
    public static final long ESTATUS_CONTRATO_CANCELADO_PENDIENTE_RETIRO = 7;
    public static final long ESTATUS_CONTRATO_CANCELADO_RETIRADO = 8;
    
    public static final int ESTATUS_CAJA_NUEVA = 1;
    public static final int ESTATUS_CAJA_INSTALADA = 2;
    public static final int ESTATUS_CAJA_ACTIVA = 3;
    public static final int ESTATUS_CAJA_INACTIVA = 4;
    
    public static final int ESTATUS_ACTIVO = 1;
    public static final int ESTATUS_INACTIVO = 0;
    
    public static final long TIPO_PROMOCION_SERVICIO = 1;
    public static final long TIPO_PROMOCION_ORDEN_INSTALACION = 2;
    
    public static final long ESTATUS_ORDEN_NUEVA = 1;
    public static final long ESTATUS_ORDEN_PAGADA = 2;
    public static final long ESTATUS_ORDEN_AGENDADA = 3;
    public static final long ESTATUS_ORDEN_REAGENDADA = 4;
    public static final long ESTATUS_ORDEN_EN_CAMPO = 5;
    public static final long ESTATUS_ORDEN_PENDIENTE_OK_CLIENTE = 6;
    public static final long ESTATUS_ORDEN_OK_CLIENTE = 7;
    public static final long ESTATUS_ORDEN_CANCELADA = 8;
    
    //public static final String DATE_PATTERN_POS = "dd/MM/yyyy";
    //public static final String DATETIME_PATTERN_POS = "dd/MM/yyyy HH:mm:ss";
    //public static final String DATETIME_PATTERN_MYSQL = "yyyy-MM-dd HH:mm:ss";
    
    public static final String IMPRESORA_58MM = "58";
    public static final String IMPRESORA_80MM = "80";
    
    public static final int TIPO_DETALLE_COBRO_SERVICIO = 1;
    public static final int TIPO_DETALLE_COBRO_RECARGO = 2;
    public static final int TIPO_DETALLE_COBRO_PROMOCION = 3;
    public static final int TIPO_DETALLE_COBRO_DESCUENTO = 4;
    
    public static final int TIPO_BUSQUEDA_CONTRATO = 1;
    public static final int TIPO_BUSQUEDA_CONTRATO_ANTERIOR = 2;
    public static final int TIPO_BUSQUEDA_NOMBRE = 3;
    public static final int TIPO_BUSQUEDA_APELLIDO_PATERNO = 4;
    public static final int TIPO_BUSQUEDA_APELLIDO_MATERNO = 5;
    public static final int TIPO_BUSQUEDA_DOMICILIO = 6;
    
    public static final int TIPO_ORDEN_INSTALACION = 1;
    public static final int TIPO_ORDEN_SERVICIO = 2;
    public static final int TIPO_ORDEN_CAMBIO_DOMICILIO = 3;
    
    public static final long TIPO_COBRO_ORDEN_INSTALACION = 1;
    public static final long TIPO_COBRO_SERVICIO = 2;
    public static final long TIPO_COBRO_ORDEN_SERVICIO = 3;
    public static final long TIPO_COBRO_ORDEN_CAMBIO_DOMICILIO = 4;
    public static final long TIPO_COBRO_RECARGO_MENSUALIDAD = 5;
    
    // Propiedades
    public static final String TVCH_API_URL = "tvch.api.path.url";
    public static final String TVCH_API_LIST_SUSCRIPTORES = "tvch.api.path.suscriptores";
    public static final String TVCH_API_LIST_ORDENES_INSTALACION = "tvch.api.path.ordenes.instalacion";
    public static final String TVCH_API_LIST_ORDENES_INSTALACION_PROMOCIONES = "tvch.api.path.ordenes.instalacion.promociones";
    public static final String TVCH_API_ORDENES_INSTALACION_UPDATE_PAGO = "tvch.api.path.ordenes.instalacion.pago";
    public static final String TVCH_API_ORDENES_SERVICIO_UPDATE_PAGO = "tvch.api.path.ordenes.servicio.pago";
    public static final String TVCH_API_ORDENES_CAMBIO_DOMICILIO_UPDATE_PAGO = "tvch.api.path.ordenes.cambio.domicilio.pago";
    public static final String TVCH_API_LIST_TIPOS_DESCUENTO = "tvch.api.path.tipos.descuento";
    public static final String TVCH_API_LIST_ORDENES_SERVICIO = "tvch.api.path.ordenes.servicio";
    public static final String TVCH_API_LIST_ORDENES_CAMBIO_DOMICILIO = "tvch.api.path.ordenes.cambio.domicilio";
    
    public static final String TVCH_API_LOGIN_URL = "tvch.api.login.url";
    public static final String TVCH_API_LIST_ZONAS = "tvch.api.zonas.url";
    public static final String TVCH_API_LIST_SUCURSALES = "tvch.api.sucursales.url";
    public static final String TVCH_API_LIST_TIPOS_COBRO = "tvch.api.tipos.cobro.url";
    //public static final String TVCH_API_LIST_ESTATUS_CAJA = "tvch.api.estatus.cajas.url";
    public static final String TVCH_API_LIST_CAJAS = "tvch.api.cajas.url";
    public static final String TVCH_API_READ_CAJA = "tvch.api.cajas.read.url";
    public static final String TVCH_API_UPDATE_CAJA = "tvch.api.cajas.update.url";
    public static final String TVCH_API_LIST_SERVICIOS = "tvch.api.servicios.url";
    public static final String TVCH_API_LIST_PROMOCIONES = "tvch.api.promociones.url";
    public static final String TVCH_API_LIST_TIPOS_SALIDA = "tvch.api.salidas.url";
    public static final String TVCH_API_LIST_ESTATUS_USUARIOS = "tvch.api.estatus.usuario.url";
    public static final String TVCH_API_LIST_USUARIOS_SUCURSAL = "tvch.api.usuarios.sucursal.url";
    public static final String TVCH_API_LIST_CONTRATOS_SUSCRIPTOR = "tvch.api.contratos.suscriptor.url";
    
    // Codigos HTTP
    public static final int CODIGO_HTTP_OK = 200;
    public static final int CODIGO_HTTP_OK_WARNING = 202;
    public static final int CODIGO_HTTP_NO_CONTENT = 204;
    public static final int CODIGO_HTTP_PERMISOS_ERROR = 403;
    public static final int CODIGO_HTTP_TVCH_ERROR = 409;
    public static final int CODIGO_HTTP_SERVER_ERROR = 500;
    
    public static final int TIPO_DETALLE_CORTE_FONDO_FIJO = 1;
    public static final int TIPO_DETALLE_CORTE_NUMERO_TRANSACCIONES = 2;
    public static final int TIPO_DETALLE_CORTE_NUMERO_DETALLES_TRANSACCION = 3;
    public static final int TIPO_DETALLE_CORTE_NUMERO_SALIDAS = 4;
    public static final int TIPO_DETALLE_CORTE_NUMERO_INGRESOS = 5;
    public static final int TIPO_DETALLE_CORTE_PROMOCIONES_APLICADAS = 6;
    public static final int TIPO_DETALLE_CORTE_DESCUENTOS_REALIZADOS = 7;
    public static final int TIPO_DETALLE_CORTE_MONTO_SOLICITADO = 8;
    public static final int TIPO_DETALLE_CORTE_MONTO_ENTREGADO = 9;
    public static final int TIPO_DETALLE_CORTE_FALTANTES = 10;
    public static final int TIPO_DETALLE_CORTE_SOBRANTES = 11;
    public static final int TIPO_DETALLE_CORTE_ID = 12;
    
    public static final long TIPO_DIFERENCIA_CORTE_FALTANTE = 1;
    public static final long TIPO_DIFERENCIA_CORTE_SOBRANTE = 2;
    
}
