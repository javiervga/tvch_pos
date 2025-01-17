/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import mx.com.tvch.pos.config.Sesion;
import mx.com.tvch.pos.dao.AperturaCajaDao;
import mx.com.tvch.pos.dao.CorteCajaDao;
import mx.com.tvch.pos.dao.DetalleCobroTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDescuentoTransaccionDao;
import mx.com.tvch.pos.dao.DetalleDiferenciaCorteCajaDao;
import mx.com.tvch.pos.dao.DetallePromocionTransaccionDao;
import mx.com.tvch.pos.dao.IngresoCajaDao;
import mx.com.tvch.pos.dao.SalidaCajaDao;
import mx.com.tvch.pos.dao.TransaccionDao;
import mx.com.tvch.pos.entity.AperturaCajaEntity;
import mx.com.tvch.pos.entity.CorteCajaEntity;
import mx.com.tvch.pos.entity.DetalleCobroTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDescuentoTransaccionEntity;
import mx.com.tvch.pos.entity.DetalleDiferenciaCorteEntity;
import mx.com.tvch.pos.entity.DetallePromocionTransaccionEntity;
import mx.com.tvch.pos.entity.IngresoCajaEntity;
import mx.com.tvch.pos.entity.SalidaCajaEntity;
import mx.com.tvch.pos.entity.TransaccionEntity;
import mx.com.tvch.pos.model.CorteCaja;
import mx.com.tvch.pos.model.DetalleCorte;
import mx.com.tvch.pos.util.Constantes;
import mx.com.tvch.pos.util.Utilerias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fvega
 */
public class CorteCajaController {

    private static CorteCajaController controller;

    private final AperturaCajaDao aperturaCajaDao;
    private final CorteCajaDao corteCajaDao;
    private final DetalleCobroTransaccionDao detalleCobroTransaccionDao;
    private final DetalleDescuentoTransaccionDao detalleDescuentoTransaccionDao;
    private final DetallePromocionTransaccionDao detallePromocionTransaccionDao;
    private final DetalleDiferenciaCorteCajaDao detalleDiferenciaCorteCajaDao;
    private final TransaccionDao transaccionDao;
    private final SalidaCajaDao salidaCajaDao;
    private final IngresoCajaDao ingresoCajaDao;
    private final Sesion sesion;
    private final Utilerias util;

    Logger logger = LoggerFactory.getLogger(CorteCajaController.class);

    public static CorteCajaController getCorteCajaController() {
        if (controller == null) {
            controller = new CorteCajaController();
        }
        return controller;
    }

    public CorteCajaController() {
        aperturaCajaDao = AperturaCajaDao.getAperturaCajaDao();
        corteCajaDao = CorteCajaDao.getCorteCajaDao();
        detalleCobroTransaccionDao = DetalleCobroTransaccionDao.getDetalleCobroTransaccionDao();
        detalleDescuentoTransaccionDao = DetalleDescuentoTransaccionDao.getDetalleDescuentoTransaccionDao();
        detallePromocionTransaccionDao = DetallePromocionTransaccionDao.getDetallePromocionTransaccionDao();
        detalleDiferenciaCorteCajaDao = DetalleDiferenciaCorteCajaDao.getDetalleDiferenciaCorteCajaDao();
        transaccionDao = TransaccionDao.getTransaccionDao();
        salidaCajaDao = SalidaCajaDao.getSalidaCajaDao();
        ingresoCajaDao = IngresoCajaDao.getIngresoCajaDao();
        sesion = Sesion.getSesion();
        util = Utilerias.getUtilerias();
    }

    public CorteCaja realizarCorte(List<DetalleCorte> detallesCorte, Double montoIngresado) throws Exception {

        try {

            CorteCaja corteCaja = new CorteCaja();
            List<DetalleCorte> list = new ArrayList<>();

            detallesCorte.forEach(d -> list.add(d));

            //obtener cada detalle por separado
            //fondo fijo siempre existe
            DetalleCorte dFondoFijo = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_FONDO_FIJO).findFirst().get();
            //transacciones -> validar que existan
            DetalleCorte dTransacciones = null;
            if (list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_TRANSACCIONES).findAny().isPresent()) {
                dTransacciones = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_TRANSACCIONES).findFirst().get();
            }
            //detalles transaccion -> validar que existan
            DetalleCorte dDetalleTransacciones = null;
            if (list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_DETALLES_TRANSACCION).findAny().isPresent()) {
                dDetalleTransacciones = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_DETALLES_TRANSACCION).findFirst().get();
            }
            //detalles descuentos -> validar que existan
            DetalleCorte dDetalleDescuentos = null;
            if (list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_DESCUENTOS_REALIZADOS).findAny().isPresent()) {
                dDetalleDescuentos = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_DESCUENTOS_REALIZADOS).findFirst().get();
            }
            //detalles promociones -> validar que existan
            DetalleCorte dDetallePromociones = null;
            if (list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_PROMOCIONES_APLICADAS).findAny().isPresent()) {
                dDetallePromociones = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_PROMOCIONES_APLICADAS).findFirst().get();
            }
            //detalles salidas -> validar que existan
            DetalleCorte dSalidas = null;
            if (list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_SALIDAS).findAny().isPresent()) {
                dSalidas = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_SALIDAS).findFirst().get();
            }
            //detalles ingresos -> validar que existan
            DetalleCorte dIngresos = null;
            if (list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_INGRESOS).findAny().isPresent()) {
                dIngresos = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_NUMERO_INGRESOS).findFirst().get();
            }
            
            //monto solicitado siempre existe
            DetalleCorte dMontoSolicitado = list.stream().filter(d -> d.getTipoDetalle() == Constantes.TIPO_DETALLE_CORTE_MONTO_SOLICITADO).findFirst().get();

            //genrar detalle de monto recibido con la cantidad que capturo el cajero
            DetalleCorte dMontoIngresado = new DetalleCorte();
            dMontoIngresado.setCantidad(1);
            dMontoIngresado.setConcepto("Monto capturado por Cajero");
            dMontoIngresado.setMonto(montoIngresado);
            dMontoIngresado.setMontoCadena(String.valueOf(montoIngresado));
            dMontoIngresado.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_MONTO_ENTREGADO);
            list.add(dMontoIngresado);

            //validar cantidades
            DetalleCorte dMontoFaltante = null;
            DetalleCorte dMontoSobrante = null;
            if (dMontoIngresado.getMonto() < dMontoSolicitado.getMonto()) {
                //si falta dinero generar detalle de faltante
                double montoFaltante = dMontoSolicitado.getMonto() - dMontoIngresado.getMonto();
                dMontoFaltante = new DetalleCorte();
                dMontoFaltante.setCantidad(1);
                dMontoFaltante.setConcepto("Monto faltante de entregar");
                dMontoFaltante.setMonto(montoFaltante);
                dMontoFaltante.setMontoCadena(String.valueOf(montoFaltante));
                dMontoFaltante.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_FALTANTES);
                list.add(dMontoFaltante);
            } else if (dMontoIngresado.getMonto() > dMontoSolicitado.getMonto()) {
                //si falta dinero generar detalle de sobrantes
                double montoSobrante = dMontoIngresado.getMonto() - dMontoSolicitado.getMonto();
                dMontoSobrante = new DetalleCorte();
                dMontoSobrante.setCantidad(1);
                dMontoSobrante.setConcepto("Monto sobrante entregado");
                dMontoSobrante.setMonto(montoSobrante);
                dMontoSobrante.setMontoCadena(String.valueOf(montoSobrante));
                dMontoSobrante.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_SOBRANTES);
                list.add(dMontoSobrante);
            }
            
            // generar los entitys que se guardaran en la bd
            //primero la transaccion
            CorteCajaEntity entity = new CorteCajaEntity();
            entity.setCorteCajaId(util.generarIdLocal());
            entity.setAperturaCajaId(sesion.getAperturaCajaId());
            entity.setUsuarioId(sesion.getUsuarioId());
            entity.setSucursalId(sesion.getSucursalId());
            entity.setFondoFijo(dFondoFijo.getMonto());
            entity.setCantidadCobros(dTransacciones.getCantidad());
            entity.setTotalCobros(dTransacciones.getMonto());
            if(dDetalleDescuentos != null){
                entity.setCantidadDescuentos(dDetalleDescuentos.getCantidad());
                entity.setTotalDescuentos(dDetalleDescuentos.getMonto());
            }else{
                entity.setCantidadDescuentos(0);
                entity.setTotalDescuentos(0.0);
            }
            if(dSalidas != null){
                entity.setCantidadSalidas(dSalidas.getCantidad());
                entity.setTotalSalidas(dSalidas.getMonto());
            }else{
                entity.setCantidadSalidas(0);
                entity.setTotalSalidas(0.0);
            }
            if(dIngresos != null){
                entity.setCantidadIngresos(dIngresos.getCantidad());
                entity.setTotalIngresos(dIngresos.getMonto());
            }else{
                entity.setCantidadSalidas(0);
                entity.setTotalSalidas(0.0);
            }
            if(dDetallePromociones != null){
                entity.setPromocionesAplicadas(dDetallePromociones.getCantidad());
            }else{
                entity.setPromocionesAplicadas(0);
            }
            entity.setTotalSolicitado(dMontoSolicitado.getMonto());
            entity.setTotalEntregado(montoIngresado);
            entity.setFechaCorte(util.obtenerFechaFormatoMysql());
            //Long idCorteCaja = 11L;
            Long idCorteCaja = corteCajaDao.registrarCorteCaja(entity);
            aperturaCajaDao.actualizarEstatusApertura(sesion.getAperturaCajaId(), Constantes.ESTATUS_INACTIVO);
            DetalleCorte detalleId = new DetalleCorte();
            detalleId.setConcepto(String.valueOf(idCorteCaja));
            detalleId.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_ID);
            list.add(detalleId);
            
            if(dMontoFaltante != null) {
                //registrar diferencia en bd
                DetalleDiferenciaCorteEntity diferenciaFaltanteEntity = new DetalleDiferenciaCorteEntity();
                diferenciaFaltanteEntity.setDiferenciaId(util.generarIdLocal());
                diferenciaFaltanteEntity.setCorteCajaId(idCorteCaja);
                diferenciaFaltanteEntity.setTipoDiferenciaId(Constantes.TIPO_DIFERENCIA_CORTE_FALTANTE);
                diferenciaFaltanteEntity.setMonto(dMontoFaltante.getMonto());
                detalleDiferenciaCorteCajaDao.registrarDetalleDiferenciaCorte(diferenciaFaltanteEntity);
                corteCaja.setFaltanteEntity(diferenciaFaltanteEntity);
                
            }else if(dMontoSobrante != null){
                //registrar diferencia en bd
                DetalleDiferenciaCorteEntity diferenciaSObranteEntity = new DetalleDiferenciaCorteEntity();
                diferenciaSObranteEntity.setDiferenciaId(util.generarIdLocal());
                diferenciaSObranteEntity.setCorteCajaId(idCorteCaja);
                diferenciaSObranteEntity.setTipoDiferenciaId(Constantes.TIPO_DIFERENCIA_CORTE_FALTANTE);
                diferenciaSObranteEntity.setMonto(dMontoFaltante.getMonto());
                detalleDiferenciaCorteCajaDao.registrarDetalleDiferenciaCorte(diferenciaSObranteEntity);
                corteCaja.setSobranteEntity(diferenciaSObranteEntity);
            }  

            corteCaja.setDetallesCorte(list);
            corteCaja.setEntity(entity);
            corteCaja.setEsExitoso(true);
            corteCaja.setMensaje("corte exitoso");
            return corteCaja;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al realizar corte de caja: \n" + sw.toString());
            throw new Exception("Error al realizar corte. Por favor contacte a soporte.");
        }
    }

    /**
     *
     * @return @throws Exception
     * @throws java.lang.Exception
     */
    public List<DetalleCorte> consultarInformacionCorte() throws Exception {

        List<DetalleCorte> list = new ArrayList<>();

        try {

            AperturaCajaEntity aperturaCaja = aperturaCajaDao.obtenerAperturaCaja(sesion.getAperturaCajaId());
            DetalleCorte detalleFondoFijo = new DetalleCorte();
            detalleFondoFijo.setCantidad(1);
            detalleFondoFijo.setConcepto("Fondo Fijo");
            detalleFondoFijo.setMonto(aperturaCaja.getFondoFijo());
            detalleFondoFijo.setMontoCadena(String.valueOf(aperturaCaja.getFondoFijo()));
            detalleFondoFijo.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_FONDO_FIJO);
            list.add(detalleFondoFijo);

            boolean existenTransacciones = false;
            boolean existenDescuentos = false;
            boolean existenPromociones = false;
            boolean existenSalidas = false;
            boolean existenIngresos = false;
            DetalleCorte detalleNumeroTransacciones = null;
            DetalleCorte detalleDescuentos = null;
            DetalleCorte detallePromociones = null;
            DetalleCorte detalleSalidas = null;
            DetalleCorte detalleIngresos = null;
            DetalleCorte detalleCobros = null;

            List<TransaccionEntity> transacciones = transaccionDao.obtenerTransaccionesxAperturaCaja(aperturaCaja.getAperturaCajaId());
            if (!transacciones.isEmpty()) {
                existenTransacciones = true;

                Double montoOperaciones = transacciones.stream().mapToDouble(TransaccionEntity::getMonto).sum();
                detalleNumeroTransacciones = new DetalleCorte();
                detalleNumeroTransacciones.setCantidad(transacciones.size());
                detalleNumeroTransacciones.setConcepto("Número de operaciones");
                detalleNumeroTransacciones.setMonto(montoOperaciones);
                //detalleNumeroTransacciones.setMontoCadena(String.valueOf(montoOperaciones));
                detalleNumeroTransacciones.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_NUMERO_TRANSACCIONES);
                list.add(detalleNumeroTransacciones);

                Double montoCobros = 0.0;
                Double montoDescuentos = 0.0;
                Integer cantidadDescuentos = 0;
                Integer cantidadPromociones = 0;
                Integer cantidadCobros = 0;
                Double montoDetallesCobro = 0.0;
                for (TransaccionEntity t : transacciones) {

                    List<DetalleCobroTransaccionEntity> cobros = detalleCobroTransaccionDao.obtenerDetallesCobroPorTransaccion(t.getTransaccionId());
                    if (!cobros.isEmpty()) {
                        cantidadCobros = cantidadCobros + cobros.size();
                        montoDetallesCobro = montoDetallesCobro + cobros.stream().mapToDouble(DetalleCobroTransaccionEntity::getMonto).sum();
                    }

                    List<DetalleDescuentoTransaccionEntity> descuentos = detalleDescuentoTransaccionDao.obtenerDetallesDescuentoPorTransaccion(t.getTransaccionId());
                    if (!descuentos.isEmpty()) {
                        existenDescuentos = true;
                        cantidadDescuentos = cantidadDescuentos + descuentos.size();
                        montoDescuentos = montoDescuentos + descuentos.stream().mapToDouble(DetalleDescuentoTransaccionEntity::getMonto).sum();
                    }

                    List<DetallePromocionTransaccionEntity> promociones = detallePromocionTransaccionDao.obtenerDetallesPromocionPorTransaccion(t.getTransaccionId());
                    if (!promociones.isEmpty()) {
                        existenPromociones = true;
                        cantidadPromociones = cantidadPromociones = promociones.size();
                    }

                }

                detalleCobros = new DetalleCorte();
                detalleCobros.setCantidad(cantidadCobros);
                detalleCobros.setConcepto("Detalles de operación(Incluye recargos)");
                detalleCobros.setMonto(montoDetallesCobro);
                detalleCobros.setMontoCadena("+ " + String.valueOf(montoDetallesCobro));
                detalleCobros.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_NUMERO_DETALLES_TRANSACCION);
                list.add(detalleCobros);

                if (existenDescuentos) {
                    detalleDescuentos = new DetalleCorte();
                    detalleDescuentos.setCantidad(cantidadDescuentos);
                    detalleDescuentos.setConcepto("Descuentos realizados");
                    detalleDescuentos.setMonto(montoDescuentos);
                    detalleDescuentos.setMontoCadena("- " + String.valueOf(montoDescuentos));
                    detalleDescuentos.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_DESCUENTOS_REALIZADOS);
                    list.add(detalleDescuentos);
                }

                if (existenPromociones) {
                    detallePromociones = new DetalleCorte();
                    detallePromociones.setCantidad(cantidadPromociones);
                    detallePromociones.setConcepto("Promociones aplicadas");
                    detallePromociones.setMonto(null);
                    detallePromociones.setMontoCadena(null);
                    detallePromociones.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_PROMOCIONES_APLICADAS);
                    list.add(detallePromociones);
                }

            }else{
                detalleNumeroTransacciones = new DetalleCorte();
                detalleNumeroTransacciones.setCantidad(0);
                detalleNumeroTransacciones.setConcepto("Número de operaciones");
                detalleNumeroTransacciones.setMonto(0.0);
                //detalleNumeroTransacciones.setMontoCadena("0.00");
                detalleNumeroTransacciones.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_NUMERO_TRANSACCIONES);
                list.add(detalleNumeroTransacciones);
            }

            List<SalidaCajaEntity> salidas = salidaCajaDao.obtenerSalidasPorAperturaCaja(sesion.getAperturaCajaId());
            if (!salidas.isEmpty()) {
                existenSalidas = true;
                detalleSalidas = new DetalleCorte();
                double montoSalidas = salidas.stream().mapToDouble(SalidaCajaEntity::getMonto).sum();
                detalleSalidas.setCantidad(salidas.size());
                detalleSalidas.setConcepto("Salidas de caja");
                detalleSalidas.setMonto(montoSalidas);
                detalleSalidas.setMontoCadena("- " + String.valueOf(montoSalidas));
                detalleSalidas.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_NUMERO_SALIDAS);
                list.add(detalleSalidas);
            }
            
            List<IngresoCajaEntity> ingresos = ingresoCajaDao.obtenerIngresoPorAperturaCaja(sesion.getAperturaCajaId());
            if (!ingresos.isEmpty()) {
                existenIngresos = true;
                detalleIngresos = new DetalleCorte();
                double montoIngresos = ingresos.stream().mapToDouble(IngresoCajaEntity::getMonto).sum();
                detalleIngresos.setCantidad(salidas.size());
                detalleIngresos.setConcepto("Ingresos a caja");
                detalleIngresos.setMonto(montoIngresos);
                detalleIngresos.setMontoCadena("+ " + String.valueOf(montoIngresos));
                detalleIngresos.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_NUMERO_INGRESOS);
                list.add(detalleIngresos);
            }

            DetalleCorte detalleMontoSolicitado = new DetalleCorte();
            detalleMontoSolicitado.setCantidad(1);
            detalleMontoSolicitado.setConcepto("Efectivo esperado");
            detalleMontoSolicitado.setTipoDetalle(Constantes.TIPO_DETALLE_CORTE_MONTO_SOLICITADO);
            double montoEsperado = detalleFondoFijo.getMonto();
            if (existenTransacciones) {
                //list.add(detalleNumeroTransacciones);
                montoEsperado = montoEsperado + detalleCobros.getMonto();
            }
            if (existenDescuentos) {
                montoEsperado = montoEsperado - detalleDescuentos.getMonto();
            }
            if (existenSalidas) {
                montoEsperado = montoEsperado - detalleSalidas.getMonto();
            }
            if (existenIngresos){
                montoEsperado = montoEsperado + detalleIngresos.getMonto();
            }

            detalleMontoSolicitado.setMonto(montoEsperado);
            detalleMontoSolicitado.setMontoCadena("= " + String.valueOf(montoEsperado));
            list.add(detalleMontoSolicitado);

            return list;

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Fallo al consultar tipos de salida: \n" + sw.toString());
            throw new Exception("Error al consultar informacion de corte. Por favor contacte a soporte.");
        }

    }

}
