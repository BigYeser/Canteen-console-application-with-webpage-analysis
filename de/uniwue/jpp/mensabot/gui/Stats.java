package de.uniwue.jpp.mensabot.gui;

public class Stats {
    private String nameStats;
    private String singleMenuStats;
    private String allMenuStats;

    public Stats(String nameStats, String singleMenuStats, String allMenuStats) {
        this.nameStats = nameStats;
        this.singleMenuStats = singleMenuStats;
        this.allMenuStats = allMenuStats;
    }

    public String getNameStats() {
        return nameStats;
    }

    public String getSingleMenuStats() {
        return singleMenuStats;
    }

    public String getAllMenuStats() {
        return allMenuStats;
    }

    public void setNameStats(String nameStats) {
        this.nameStats = nameStats;
    }

    public void setSingleMenuStats(String singleMenuStats) {
        this.singleMenuStats = singleMenuStats;
    }

    public void setAllMenuStats(String allMenuStats) {
        this.allMenuStats = allMenuStats;
    }
}
