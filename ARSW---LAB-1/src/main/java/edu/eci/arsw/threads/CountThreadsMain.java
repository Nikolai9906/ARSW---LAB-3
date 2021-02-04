/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread hilo1=new CountThread(0,99);
        CountThread hilo2=new CountThread(99,199);
        CountThread hilo3=new CountThread(199,299);

        hilo1.start();
       // hilo1.run();
        hilo2.start();
       // hilo2.run();
        hilo3.start();
      //  hilo3.run();

    }
    
}
