/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private boolean band=true;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int n) throws InterruptedException {
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        AtomicInteger ocurrencesCount= new AtomicInteger(0);
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        AtomicInteger checkedListsCount= new AtomicInteger(0);
        ArrayList<Mult> hilo = new ArrayList<Mult>();
        int analysisNumber = skds.getRegisteredServersCount()/n;
        int currentServer = 0;

        for(int i = 0; i < n; i++ ) {
            Mult temporal;
            if (i == n-1) {
                temporal = 	new Mult(skds, ocurrencesCount, BLACK_LIST_ALARM_COUNT, checkedListsCount, ipaddress, blackListOcurrences, currentServer, analysisNumber+(skds.getRegisteredServersCount() % n));
                //System.out.println(analysisNumber+(skds.getRegisteredServersCount() % n) + "numero analiis mas skds");
            }else {
                temporal = new Mult(skds, ocurrencesCount, BLACK_LIST_ALARM_COUNT, checkedListsCount, ipaddress, blackListOcurrences, currentServer, analysisNumber);
            }
            hilo.add(temporal);
            currentServer+= analysisNumber;
            //System.out.println(currentServer+ "servidor actual");
        }
        for (int i=0;i<n;i++) {
            hilo.get(i).start();

        }
        for (int i=0;i<n;i++) {
           hilo.get(i).join();
        }
        while(band){
            if (ocurrencesCount.get()>=BLACK_LIST_ALARM_COUNT||checkedListsCount.get()<=skds.getRegisteredServersCount()){
                skds.reportAsNotTrustworthy(ipaddress);
                for(Mult e:hilo){
                    e.pausar();
                    band=false;}
            }

        }
        if(checkedListsCount.get()== skds.getRegisteredServersCount()){
            skds.reportAsTrustworthy(ipaddress);
        }
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
