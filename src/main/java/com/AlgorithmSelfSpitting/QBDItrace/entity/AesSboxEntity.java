package com.AlgorithmSelfSpitting.QBDItrace.entity;

import java.util.HashMap;
import java.util.Map;

public class AesSboxEntity {
    private  int aesSboxindex =0;
    private  String aesSboxaddr ="";
    private  String addrindex ="";
    private Map<String,String> map =new HashMap<String,String>();

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getAddrindex() {
        return addrindex;
    }

    public void setAddrindex(String addrindex) {
        this.addrindex = addrindex;
    }

    public String getAesSboxaddr() {
        return aesSboxaddr;
    }

    public void setAesSboxaddr(String aesSboxaddr) {
        this.aesSboxaddr = aesSboxaddr;
    }

    public int getAesSboxindex() {
        return aesSboxindex;
    }

    public void setAesSboxindex(int aesSboxindex) {
        this.aesSboxindex = aesSboxindex;
    }
}
