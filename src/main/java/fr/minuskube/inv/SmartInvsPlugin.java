package fr.minuskube.inv;

import com.massivecraft.factions.P;
import landon.warhammercore.WarhammerCore;

public class SmartInvsPlugin {

    private static SmartInvsPlugin instance;
    private static InventoryManager invManager;

    public void onEnable() {
        instance = this;

        invManager = new InventoryManager(P.p);
        invManager.init();
    }

    public static InventoryManager manager() { return invManager; }
    public static SmartInvsPlugin instance() { return instance; }

}
