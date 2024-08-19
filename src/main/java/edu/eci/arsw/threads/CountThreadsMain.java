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
        Thread t = new Thread(new CountThread(0, 99));
        Thread m = new Thread(new CountThread(99, 199));
        Thread n = new Thread(new CountThread(200, 299));

        t.run();
        m.run();
        n.run();
    }
    
}
