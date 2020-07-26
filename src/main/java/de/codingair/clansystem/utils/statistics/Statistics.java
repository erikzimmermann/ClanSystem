package de.codingair.clansystem.utils.statistics;

public enum Statistics {
    UNKNOWN(null)
    ;

    public static Statistics[] VALUES = values();
    private final Statistic statistic;

    Statistics(Statistic statistic) {
        this.statistic = statistic;
    }

    public Statistic getStatistic() {
        return statistic;
    }
}
