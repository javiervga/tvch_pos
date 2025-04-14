/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.com.tvch.pos.util;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;

/**
 *
 * @author fvega
 */
public class Calendario {
    
    private static Calendario calendario;
    
    public static Calendario getCalendario(){
        if(calendario == null)
            calendario = new Calendario();
        return calendario;
    }
    
    public JCalendar obtenerCalendario(int largo, int ancho){
        JCalendar calendar = new JCalendar();
        calendar.setSize(largo, ancho);
        calendar.setBackground(Color.WHITE);
        calendar.setMaxDayCharacters(1);
        calendar.setWeekOfYearVisible(false);
        calendar.setTodayButtonVisible(false);
        return calendar;
    }
    
    public JDateChooser obtenerChooser(){
        JDateChooser chooser = new JDateChooser("dd/MM/yyyy", "##/##/####", '_');
        chooser.getJCalendar().setBackground(Color.WHITE);
        chooser.getJCalendar().setMaxDayCharacters(1);
        chooser.getJCalendar().setWeekOfYearVisible(false);
        chooser.getJCalendar().setTodayButtonVisible(false);
        return chooser;
    }
    
}
