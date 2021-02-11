package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    private boolean pausa;
    private int health;
    private boolean stop=false;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    private AtomicInteger aInt = new AtomicInteger();


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.aInt.getAndAdd(health);

    }

    public void run() {

        while (!stop) {

            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);
            this.fight(im);


            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(pausa){
                synchronized (immortalsPopulation){
                    for(Immortal o:immortalsPopulation){
                        try {
                            immortalsPopulation.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public void matar(Immortal muerto){
        immortalsPopulation.remove(muerto);
        //System.out.println("matan");

    }

    public void fight(Immortal i2) {
        synchronized (immortalsPopulation) {
        if (Integer.parseInt(i2.aInt.toString()) >0) {
                int t = i2.getHealth() - 10;
                i2.changeHealth(t);
                this.aInt.addAndGet(10);
                updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
            } else{
                if (i2.getHealth() <= 0) {
                    immortalsPopulation.remove(i2);
                    i2.f();
                    for(Immortal u:immortalsPopulation){u.matar(i2);}
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }

            }
        }

    }
    public void pausar(){
        pausa=true;
    }
    public void resum(){

        synchronized (immortalsPopulation){
            pausa=false;
            immortalsPopulation.notifyAll();

        }
    }
    public void f(){stop=true;}
    public void detener(){
        synchronized (immortalsPopulation){
            for(Immortal o:immortalsPopulation){
                o.f();
            }

        }
    }

    public synchronized void changeHealth(int v) {
        aInt=new AtomicInteger(v);
    }

    public synchronized int getHealth() {
       // System.out.println("perra");
        return Integer.parseInt(aInt.toString());

    }

    @Override
    public String toString() {

        return name + "[ " + Integer.parseInt(aInt.toString()) + " ]";
    }

}
