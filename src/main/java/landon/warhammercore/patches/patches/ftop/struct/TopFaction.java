package landon.warhammercore.patches.patches.ftop.struct;

public class TopFaction {
    int rank;

    StoredFaction storedFaction;

    public int getRank() {
        return this.rank;
    }

    public StoredFaction getStoredFaction() {
        return this.storedFaction;
    }

    public TopFaction(StoredFaction storedFaction, int rank) {
        this.rank = rank;
        this.storedFaction = storedFaction;
    }
}
