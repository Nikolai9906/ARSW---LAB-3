package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Mult extends Thread{

    private HostBlacklistsDataSourceFacade skds;
    private AtomicInteger ocurrencesCount;
    private int BLACK_LIST_ALARM_COUNT;
    private AtomicInteger checkedListsCount;
    private String ipaddress;
    private LinkedList<Integer> blackListOcurrences;
    private int currentServer;
    private int analysisNumber;
    private boolean pausa=false;

    public Mult(HostBlacklistsDataSourceFacade skds, AtomicInteger ocurrencesCount, int BLACK_LIST_ALARM_COUNT, AtomicInteger checkedListsCount, String ipaddress, LinkedList<Integer> blackListOcurrences, int currentServer, int analysisNumber) {
        this.skds= skds;
        this.ocurrencesCount=ocurrencesCount;
        this.BLACK_LIST_ALARM_COUNT =BLACK_LIST_ALARM_COUNT;
        this.checkedListsCount = checkedListsCount;
        this.ipaddress =  ipaddress;
        this.blackListOcurrences = blackListOcurrences;
        this.currentServer = currentServer;
        this.analysisNumber = analysisNumber;
    }


    @Override
    public void run() {
        for(int i = currentServer; i < (currentServer + analysisNumber) && ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT ; i++){
            checkedListsCount.getAndIncrement();
            if (skds.isInBlackListServer(i, ipaddress)){
                blackListOcurrences.add(i);
                ocurrencesCount.getAndIncrement();
            }
            synchronized (this){
                if (pausa){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void pausar(){
        pausa=true;

    }

}
