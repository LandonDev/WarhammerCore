package landon.warhammercore.patchapi.patches.fpoints.utils;

import org.bukkit.entity.Entity;

public class CustomTNTUtils {
    public static boolean isCustomTNT(Entity entity) {
        return (entity.hasMetadata("custom_tnt") || entity.hasMetadata("fastExplosion"));
    }
}
