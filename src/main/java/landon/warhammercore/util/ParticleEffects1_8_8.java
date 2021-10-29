package landon.warhammercore.util;


import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleEffects1_8_8 {
    private PacketPlayOutWorldParticles packet;

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public ParticleEffects1_8_8(EnumParticle enumParticle, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count) {
        float x = (float)loc.getX();
        float y = (float)loc.getY();
        float z = (float)loc.getZ();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(enumParticle, false, x, y, z, xOffset,
                yOffset, zOffset, speed, count, null);
        this.packet = packet;
    }

    public void sendToAll(Player p) {
        (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)this.packet);
    }
}
