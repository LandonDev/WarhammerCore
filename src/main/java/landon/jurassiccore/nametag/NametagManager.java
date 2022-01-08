package landon.jurassiccore.nametag;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import landon.jurassiccore.utils.NMSUtil;
import landon.jurassiccore.JurassicCore;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

public class NametagManager {
    private static Class<?> PacketPlayOutScoreboardTeamClass;
    private final JurassicCore instance;

    static {
        NametagManager.PacketPlayOutScoreboardTeamClass = NMSUtil.getNMSClass("PacketPlayOutScoreboardTeam");
    }

    public NametagManager(final JurassicCore instance) {
        this.instance = instance;
    }

    public Object getTeam(final String teamName, final String teamPrefix, final String teamSuffix) {
        Object packet = null;
        try {
            packet = NametagManager.PacketPlayOutScoreboardTeamClass.newInstance();
            NMSUtil.setField(packet, "a", teamName, true);
            NMSUtil.setField(packet, "b", teamName, true);
            NMSUtil.setField(packet, "c", ChatColor.translateAlternateColorCodes('&', teamPrefix), true);
            NMSUtil.setField(packet, "d", ChatColor.translateAlternateColorCodes('&', teamSuffix), true);
            NMSUtil.setField(packet, "e", "always", true);
        } catch (InstantiationException | IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
        return packet;
    }

    public void createTeam(final Player player, final String teamName, final String teamPrefix, final String teamSuffix) {
        NMSUtil.sendPacket(player, this.getTeam(teamName, teamPrefix, teamSuffix));
    }

    public void addPlayerToTeam(final Player toSendTo, final Player player, final String teamName, final String teamPrefix, final String teamSuffix) {
        final Object packet = this.getTeam(teamName, teamPrefix, teamSuffix);
        NMSUtil.setField(packet, "g", Arrays.asList(player.getName()), true);
        NMSUtil.setField(packet, "h", 3, true);
        NMSUtil.sendPacket(toSendTo, packet);
    }

    public void removePlayerFromTeam(final Player toSendTo, final Player player, final String teamName, final String teamPrefix, final String teamSuffix) {
        final Object packet = this.getTeam(teamName, teamPrefix, teamSuffix);
        NMSUtil.setField(packet, "g", Arrays.asList(player.getName()), true);
        NMSUtil.setField(packet, "h", 4, true);
        NMSUtil.sendPacket(toSendTo, packet);
    }

    public void sendNametags(final Player player) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, (Runnable) new Runnable() {
            @Override
            public void run() {
                for (final Player all : Bukkit.getOnlinePlayers()) {
                    NametagManager.this.sendNametag(all, player);
                    if (all.getName().equals(player.getName())) {
                        continue;
                    }
                    NametagManager.this.sendNametag(player, all);
                }
            }
        });
    }

    public void sendNametag(final Player toSendTo, final Player player) {
        final NametagManager nametagManager = this.instance.getNametagManager();
        final Chat chat = this.instance.getVaultManager().getChat();
        final String groupPrefix = chat.getGroupPrefix("world", chat.getPrimaryGroup(player));
        final String teamPrefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(groupPrefix.substring(0, Math.min(groupPrefix.length(), 13))) + "&r ");
        final Faction faction = FPlayers.i.get(player).getFaction();
        String teamSuffix = "";
        if (faction != null || !faction.isNone()) {
            if (this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Nametag.Faction.Relation")) {
                teamSuffix = " " + this.instance.getFactionManager().getRelationColor(faction, FPlayers.i.get(toSendTo).getFaction()) + faction.getTag().substring(0, Math.min(faction.getTag().length(), 13));
                teamSuffix = teamSuffix.replace(new StringBuilder().append(ChatColor.WHITE).toString(), new StringBuilder().append(ChatColor.GRAY).toString());
            } else {
                teamSuffix = " &7" + faction.getTag().substring(0, Math.min(faction.getTag().length(), 13));
            }
        }
        final UUID randomUUID = UUID.randomUUID();
        final String randomTeamName = randomUUID.toString().substring(0, Math.min(randomUUID.toString().length(), 16));
        nametagManager.createTeam(toSendTo, randomTeamName, teamPrefix, teamSuffix);
        nametagManager.addPlayerToTeam(toSendTo, player, randomTeamName, teamPrefix, teamSuffix);
    }
}

