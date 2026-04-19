/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.viewModel;

/**
 *
 * @author fvega
 */
public class EstatusContrato {

    private Long id;

    private String Descripcion;

    public EstatusContrato(Long id, String Descripcion) {
        this.id = id;
        this.Descripcion = Descripcion;
    }

    public EstatusContrato() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    @Override
    public String toString() {
        return Descripcion;
    }

}
