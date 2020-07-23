package de.codingair.clansystem.utils.clan;

import de.codingair.clansystem.utils.statistics.Statistic;

import java.util.*;
import java.util.function.BiFunction;

public class Clan {
    private final int id; //unique id
    private String name;
    private final HashMap<UUID, Rank> members;
    private final HashMap<Rank, Integer> ranks; //integer value for permission inheritance

    private final HashMap<Statistic, Long> statistics;
    private int level; //>= 0
    private float exp; //relative value between 0 and 1

    public Clan(int id, String name, HashMap<UUID, Rank> members, HashMap<Rank, Integer> ranks, HashMap<Statistic, Long> statistics, int level, float exp) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.ranks = ranks;
        this.statistics = statistics;
        this.level = level;
        this.exp = exp;
    }

    public Set<UUID> getMembersByRank(Rank rank) {
        Set<UUID> members = new HashSet<>();
        if(!ranks.containsKey(rank)) return members;

        this.members.forEach((id, r) -> {
            if(r == rank) members.add(id);
        });

        return members;
    }

    public UUID getMemberByRank(Rank rank) {
        Set<UUID> members = getMembersByRank(rank);
        Iterator<UUID> i = members.iterator();

        if(i.hasNext()) {
            UUID id = i.next();
            members.clear();
            return id;
        } else return null;
    }

    public boolean hasPermission(UUID uuid, Permission permission) {
        Rank r = getRank(uuid);
        return r != null && r.hasPermission(permission);
    }

    public Rank getRank(UUID uuid) {
        return members.get(uuid);
    }

    public int getId() {
        return id;
    }

    public HashMap<UUID, Rank> getMembers() {
        return members;
    }

    public HashMap<Rank, Integer> getRanks() {
        return ranks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Statistic, Long> getStatistics() {
        return statistics;
    }

    public long getStatistic(Statistic statistic) {
        return statistics.getOrDefault(statistic, 0L);
    }

    public void setStatistic(Statistic statistic, long value) {
        this.statistics.put(statistic, value);
    }

    /**
     * Example usage:
     * computeStatistic(statistic, (stat, oldValue) -> {
     * if(oldValue == null) return value;
     * else return oldValue + value;
     * });
     *
     * @param statistic Key
     * @param function  Function to increase/decrease or whatever you like.
     */
    public void computeStatistic(Statistic statistic, BiFunction<Statistic, Long, Long> function) {
        this.statistics.compute(statistic, function);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if(level < 0) throw new IllegalArgumentException("Clan level must be >= 0");
        this.level = level;
    }

    public float getExp() {
        return exp;
    }

    public void setExp(float exp) {
        if(exp < 0 || exp > 1) throw new IllegalArgumentException("Clan exp value must be between 0 and 1.");
        this.exp = exp;
    }
}
