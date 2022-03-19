package com.example.covidtracker;

public class Statemodel {
  private String Sname;
  private  String dname;
    private long active;
    private long Recovered;
    private long Decreased;
    private long Confirmed;

    public Statemodel(String sname, String dname, long active, long recovered, long decreased, long confirmed) {
       this.Sname = sname;
        this.dname = dname;
        this.active = active;
        this.Recovered = recovered;
        this.Decreased = decreased;
        this.Confirmed = confirmed;
    }

    public String getSname() {
        return Sname;
    }

    public void setSname(String sname) {
        this.Sname = sname;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public long getActive() {
        return active;
    }

    public void setActive(long active) {
        this.active = active;
    }

    public long getRecovered() {
        return Recovered;
    }

    public void setRecovered(long recovered) {
        this.Recovered = recovered;
    }

    public long getDecreased() {
        return Decreased;
    }

    public void setDecreased(long decreased) {
        this.Decreased = decreased;
    }

    public long getConfirmed() {
        return Confirmed;
    }

    public void setConfirmed(long confirmed) {
        this.Confirmed = confirmed;
    }
}
