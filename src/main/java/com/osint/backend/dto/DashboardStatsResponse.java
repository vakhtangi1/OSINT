package com.osint.backend.dto;

public class DashboardStatsResponse {

    private long totalCases;
    private long openCases;
    private long inProgressCases;
    private long closedCases;
    private long totalPersons;
    private long totalSources;
    private long totalUsers;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalCases,
                                  long openCases,
                                  long inProgressCases,
                                  long closedCases,
                                  long totalPersons,
                                  long totalSources,
                                  long totalUsers) {
        this.totalCases = totalCases;
        this.openCases = openCases;
        this.inProgressCases = inProgressCases;
        this.closedCases = closedCases;
        this.totalPersons = totalPersons;
        this.totalSources = totalSources;
        this.totalUsers = totalUsers;
    }

    public long getTotalCases() {
        return totalCases;
    }

    public long getOpenCases() {
        return openCases;
    }

    public long getInProgressCases() {
        return inProgressCases;
    }

    public long getClosedCases() {
        return closedCases;
    }

    public long getTotalPersons() {
        return totalPersons;
    }

    public long getTotalSources() {
        return totalSources;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalCases(long totalCases) {
        this.totalCases = totalCases;
    }

    public void setOpenCases(long openCases) {
        this.openCases = openCases;
    }

    public void setInProgressCases(long inProgressCases) {
        this.inProgressCases = inProgressCases;
    }

    public void setClosedCases(long closedCases) {
        this.closedCases = closedCases;
    }

    public void setTotalPersons(long totalPersons) {
        this.totalPersons = totalPersons;
    }

    public void setTotalSources(long totalSources) {
        this.totalSources = totalSources;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
}