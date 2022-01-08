package landon.core.util;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleManager {
    public static void playEffect(Player p, String effect, Location loc, float xOffset, float yOffset, float zOffset, float speed, int amount) {
        ParticleEffects1_8_8 e = new ParticleEffects1_8_8(
                EnumParticle.valueOf(effect.toUpperCase()), loc, xOffset, yOffset,
                zOffset, speed, amount);
        e.sendToAll(p);
    }
    public static void playEffect(Player p, String effect, Location loc, float speed, int amount) {
        ParticleEffects1_8_8 e = new ParticleEffects1_8_8(
                EnumParticle.valueOf(effect.toUpperCase()), loc, 0, 0,
                0, speed, amount);
        e.sendToAll(p);
    }
}
