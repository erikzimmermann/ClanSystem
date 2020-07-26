package de.codingair.clansystem.utils.clan;

import de.codingair.clansystem.transfer.Serializable;
import de.codingair.clansystem.utils.statistics.Statistic;
import de.codingair.clansystem.utils.statistics.Statistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

public class Clan implements Serializable {
    protected long id; //unique id
    protected String name;
    protected final HashMap<Rank, Integer> ranks; //integer value for permission inheritance
    protected final HashMap<UUID, Rank> members;

    protected final HashMap<Statistic, Long> statistics;
    protected int level; //>= 0
    protected float exp; //relative value between 0 and 1

    public Clan() {
        this.ranks = new HashMap<>();
        this.members = new HashMap<>();
        this.statistics = new HashMap<>();
    }

    public Clan(long id, String name, HashMap<Rank, Integer> ranks, HashMap<UUID, Rank> members, HashMap<Statistic, Long> statistics, int level, float exp) {
        this.id = id;
        this.name = name;
        this.ranks = ranks;
        this.members = members;
        this.statistics = statistics;
        this.level = level;
        this.exp = exp;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(id);
        out.writeUTF(name);

        out.writeByte(ranks.size()); //max 255 ranks
        ranks.forEach((r, i) -> {
            try {
                r.write(out);
                out.writeByte(i);
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        out.writeShort(members.size()); //max 65.535 members
        members.forEach((id, r) -> {
            try {
                out.writeLong(id.getMostSignificantBits());
                out.writeLong(id.getLeastSignificantBits());
                out.writeByte(r.getId());
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        out.writeByte(statistics.size()); //max 255 statistics
        statistics.forEach((stat, value) -> {
            try {
                out.writeByte(stat.getId());
                out.writeLong(value);
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        out.writeInt(level);
        out.writeFloat(exp);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        id = in.readLong();
        name = in.readUTF();

        int i = in.readUnsignedByte();

        HashMap<Integer, Rank> index = new HashMap<>();
        for(int j = 0; j < i; j++) {
            Rank r = new Rank();
            r.read(in);
            ranks.put(r, in.readUnsignedByte());
            index.put(r.getId(), r);
        }

        i = in.readUnsignedShort();
        for(int j = 0; j < i; j++) {
            members.put(new UUID(in.readLong(), in.readLong()), index.get(in.readUnsignedByte()));
        }
        index.clear();

        i = in.readUnsignedByte();
        for(int j = 0; j < i; j++) {
            statistics.put(Statistics.VALUES[in.readUnsignedByte()].getStatistic(), in.readLong());
        }

        level = in.readInt();
        exp = in.readFloat();
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

    public long getId() {
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
