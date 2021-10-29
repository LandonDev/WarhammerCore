package com.massivecraft.factions.cmd.killtracker;

import com.massivecraft.factions.P;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * @author Saser
 */
public class KillHandler {

    public static void takeKills(Player p, int amount){
        if (getKills(p) >= amount){
            P.p.getFileManager().getMessages().getConfig().set(p.getUniqueId()+".kills", Math.subtractExact(getKills(p), amount));
            P.p.getFileManager().getMessages().saveFile();
        }else{
            return;
        }
    }
    public static void takeDeaths(Player p, int amount){
        if (getDeaths(p) >= amount){
            P.p.getFileManager().getMessages().getConfig().set(p.getUniqueId()+".deaths", Math.subtractExact(getDeaths(p), amount));
            P.p.getFileManager().getMessages().saveFile();
        }else{
            return;
        }
    }
    public static void giveKills(Player p, int amount){
        P.p.getFileManager().getMessages().getConfig().set(p.getUniqueId()+".kills", Math.addExact(getKills(p), amount));
        P.p.getFileManager().getMessages().saveFile();
    }
    public static void giveDeaths(Player p, int amount) {
        P.p.getFileManager().getMessages().getConfig().set(p.getUniqueId() + ".deaths", Math.addExact(getKills(p), amount));
        P.p.getFileManager().getMessages().saveFile();
    }

    public static int getKills(Player p){
        int kills = P.p.getFileManager().getMessages().fetchInt(p.getUniqueId()+".kills");
        return kills;
    }
    public static int getDeaths(Player p){
        int deaths = P.p.getFileManager().getMessages().fetchInt(p.getUniqueId()+".deaths");
        return deaths;
    }
}
