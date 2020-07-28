package de.codingair.clansystem.utils.clan;

import de.codingair.clansystem.transfer.Serializable;
import de.codingair.clansystem.utils.clan.exceptions.*;
import de.codingair.clansystem.utils.statistics.Statistic;
import de.codingair.clansystem.utils.statistics.Statistics;
import de.codingair.codingapi.tools.time.TimeSet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

public abstract class Clan implements Serializable {
    protected long id; //unique id
    protected String name;
    protected final HashMap<Integer, Rank> ranks; //integer value for permission inheritance
    protected final HashMap<UUID, Rank> members;
    private Rank president;

    protected final HashMap<Statistic, Long> statistics;
    protected int level; //>= 0
    protected float exp; //relative value between 0 and 1
    protected int money; //unsigned

    protected final TimeSet<Object> invites = new TimeSet<>(); //temporary

    public Clan() {
        this.ranks = new HashMap<>();
        this.members = new HashMap<>();
        this.statistics = new HashMap<>();
    }

    public Clan(long id, String name, HashMap<Integer, Rank> ranks, HashMap<UUID, Rank> members, Rank president, HashMap<Statistic, Long> statistics, int level, float exp, int money) {
        this.id = id;
        this.name = name;
        this.ranks = ranks;
        this.members = members;
        this.president = president;
        this.statistics = statistics;
        this.level = level;
        this.exp = exp;
        this.money = money;

        //apply pre/successor
        applyRankTrace();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(id);
        out.writeUTF(name);

        out.writeByte(president.getId());
        out.writeByte(ranks.size()); //max 255 ranks
        ranks.forEach((i, r) -> {
            try {
                r.write(out);
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
        out.writeInt(money);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        id = in.readLong();
        name = in.readUTF();

        int presidentId = in.readUnsignedByte();
        int i = in.readUnsignedByte();

        for(int j = 0; j < i; j++) {
            Rank r = new Rank();
            r.read(in);
            ranks.put(r.getId(), r);

            if(presidentId == r.getId()) president = r;
        }

        i = in.readUnsignedShort();
        for(int j = 0; j < i; j++) {
            members.put(new UUID(in.readLong(), in.readLong()), ranks.get(in.readUnsignedByte()));
        }

        i = in.readUnsignedByte();
        for(int j = 0; j < i; j++) {
            statistics.put(Statistics.VALUES[in.readUnsignedByte()].getStatistic(), in.readLong());
        }

        level = in.readInt();
        exp = in.readFloat();
        money = in.readInt();

        //apply pre/successor
        applyRankTrace();
    }

    private void applyRankTrace() {
        List<Rank> ranks = new ArrayList<>(this.ranks.values());

        for(int i = 0; i < ranks.size(); i++) {
            Rank r = ranks.get(i);

            for(int j = i; j < ranks.size(); j++) {
                Rank rank = ranks.get(j);

                if(rank.getTrace() == r.getId()) {
                    r.setSuccessor(rank);
                    rank.setPredecessor(r);
                }
            }
        }

        ranks.clear();
    }

    public void invite(UUID executor, UUID player) throws PermissionException, NotAMemberException, AlreadyInvitedException {
        Rank r = getRank(executor);

        if(r == null) throw new NotAMemberException();
        if(!hasPermission(r, Permission.INVITE)) throw new PermissionException();

        if(!invites.add(player)) throw new AlreadyInvitedException();
    }

    public void acceptInvite(UUID executor) throws NotInvitedException {
        if(!invites.remove(executor)) throw new NotInvitedException();
    }

    public void denyInvite(UUID executor) throws NotInvitedException {
        if(!invites.remove(executor)) throw new NotInvitedException();
    }

    public void deposit(UUID executor, int money) throws PermissionException, NotAMemberException {
        Rank r = getRank(executor);

        if(r == null) throw new NotAMemberException();
        if(!hasPermission(r, Permission.DEPOSIT)) throw new PermissionException();

        deposit(money);
    }

    public void withdraw(UUID executor, int money) throws PermissionException, NotAMemberException {
        Rank r = getRank(executor);

        if(r == null) throw new NotAMemberException();
        if(!hasPermission(r, Permission.WITHDRAW)) throw new PermissionException();

        withdraw(money);
    }

    public void kick(UUID executor, UUID player) throws PermissionException, NotAMemberException {
        Rank rankE = getRank(executor);

        if(rankE == null) throw new NotAMemberException();
        if(!hasPermission(rankE, Permission.KICK)) throw new PermissionException();

        Rank rankK = getRank(player);
        if(rankK == null)
            throw new NotAMemberException();

        if(rankE.equals(rankK) || hasInheritance(rankK, rankE))
            //player inherits from executor (player > executor) OR
            // has the same rank -> cancel
            throw new LowerRankException();

        members.remove(player);
    }

    public void leave(UUID executor) throws HighestRankException, NotAMemberException {
        Rank r = getRank(executor);

        if(r == null) throw new NotAMemberException();
        if(president.equals(r)) throw new HighestRankException();

        members.remove(executor);
    }

    public void promote(UUID executor, UUID promote) throws PermissionException, NotAMemberException {
        Rank rankE = getRank(executor);

        if(rankE == null) throw new NotAMemberException();
        if(!hasPermission(rankE, Permission.PROMOTE)) throw new PermissionException();

        Rank rankP = getRank(promote);
        if(rankP == null)
            throw new NotAMemberException();
        if(rankE.equals(rankP) || hasInheritance(rankP, rankE))
            //player inherits from executor (player > executor) OR
            // has the same rank -> cancel
            throw new LowerRankException();
        if(rankE.equals(rankP.getSuccessor()))
            //cannot promote if the new rank equals the executor's rank
            throw new SameRankException();

        //rankP cannot be higher than the rank before PRESIDENT (executor must be higher ranked and the successor of rankP cannot be the same rank as rankE)
        this.members.replace(promote, rankP);
    }

    public void demote(UUID executor, UUID demote) throws PermissionException, NotAMemberException {
        Rank rankE = getRank(executor);

        if(rankE == null) throw new NotAMemberException();
        if(!hasPermission(rankE, Permission.DEMOTE)) throw new PermissionException();

        Rank rankD = getRank(demote);
        if(rankD == null)
            throw new NotAMemberException();
        if(rankE.equals(rankD) || hasInheritance(rankD, rankE))
            //demote inherits from executor (demote > executor) OR
            // has the same rank OR -> cancel
            throw new LowerRankException();
        if(rankD.getPermissions() == null)
            //already lowest rank
            throw new LowestRankException();

        this.members.replace(demote, rankD);
    }

    public void rename(UUID executor, String name) throws PermissionException, NotAMemberException {
        Rank rankE = getRank(executor);

        if(rankE == null) throw new NotAMemberException();
        if(!hasPermission(rankE, Permission.RENAME)) throw new PermissionException();

        //warning
        throw new IllegalStateException("Still waiting for Clan renaming #33.");
    }

    public void transfer(UUID executor, UUID owner) throws NotAMemberException, PermissionException {
        Rank rankE = getRank(executor);

        if(rankE == null) throw new NotAMemberException();
        if(!hasPermission(rankE, Permission.TRANSFER)) throw new PermissionException();

        Rank rankO = getRank(owner);
        if(rankO == null)
            //owner is not a member of this clan
            throw new NotAMemberException();

        this.members.replace(executor, rankE.getPredecessor());
        this.members.replace(owner, president);
    }

    public String list() {
        //todo
        return "<empty>";
    }

    /**
     * rank has a higher ranking if it inherits from legacy.
     * Example: A rank member can kick a legacy member, but a legacy member cannot kick a rank member.
     *
     * @param rank   Rank
     * @param legacy Rank
     * @return true if rank inherits from legacy.
     */
    public boolean hasInheritance(Rank rank, Rank legacy) {
        if(rank == null) return false;
        else if(rank.equals(legacy)) return true;
        else return hasInheritance(getRank(rank.getTrace()), legacy);
    }

    public boolean hasPermission(Rank r, Permission p) {
        if(r == null) return false;
        else if(r.hasPermission(p)) return true;
        else if(r.getTrace() == -1) return false;
        else return hasPermission(getRank(r.getTrace()), p);
    }

    public void registerRank(Rank predecessor, Rank successor, Rank rank) {
        if(rank == null) throw new NullPointerException("Rank cannot be null!");
        if(predecessor == null) throw new NullPointerException("Predecessor of '" + rank.getName() + "' cannot be null!");
        if(successor == null) throw new NullPointerException("Successor of '" + rank.getName() + "' cannot be null!");

        rank.setPredecessor(predecessor);
        rank.setSuccessor(successor);
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

    public Rank getRank(UUID uuid) {
        return members.get(uuid);
    }

    public Rank getRank(int id) {
        return ranks.get(id);
    }

    public long getId() {
        return id;
    }

    public HashMap<UUID, Rank> getMembers() {
        return members;
    }

    public HashMap<Integer, Rank> getRanks() {
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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void withdraw(int money) {
        this.money -= money;
    }

    public void deposit(int money) {
        this.money += money;
    }

    public Rank getPresident() {
        return president;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Clan clan = (Clan) o;
        return id == clan.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
