package landon.warhammercore.armor;

import com.massivecraft.factions.P;
import landon.warhammercore.patchapi.patches.combattag.CombatLog;
import landon.warhammercore.patchapi.patches.fpoints.utils.FactionUtils;
import landon.warhammercore.util.armorequip.ArmorEquipEvent;
import landon.warhammercore.util.c;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArmorListener implements Listener {
    @EventHandler
    public void armorEquip(ArmorEquipEvent e) {
        Bukkit.getScheduler().runTaskLater(P.p, () -> {
            Player player = e.getPlayer();
            if(wearingFullSet(player, "LEATHER")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 2));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                player.sendMessage(c.c("&a&l+ &aSPEED III &7[Leather Armor]"));
            } else {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
            if(wearingFullSet(player, "IRON")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10000000, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 0));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                player.sendMessage(c.c("&a&l+ &aHASTE II &7[Iron Armor]"));
                player.sendMessage(c.c("&a&l+ &aNIGHT VISION I &7[Iron Armor]"));
            } else {
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
            if(wearingFullSet(player, "DIAMOND")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 0));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                player.sendMessage(c.c("&a&l+ &aRESISTANCE &7[Diamond Armor]"));
            } else {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
            if(e.getNewArmorPiece() != null && e.getNewArmorPiece().getType().toString().startsWith("GOLD")) {
                if(CombatLog.inCombat(player)) {
                    e.setCancelled(true);
                    player.sendMessage(c.c("&c&l(!) &cYou cannot equip GOLD ARMOR while in combat!"));
                }
            }
        }, 1L);
    }

    @EventHandler
    public void consumeItem(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            Bukkit.getScheduler().runTaskLater(P.p, () -> {
                if(wearingFullSet(player, "GOLD")) {
                    if(FactionUtils.getFaction(player).isWilderness() || FactionUtils.getFaction(player) == null) {
                        player.sendMessage(c.c("&c&l(!) &cYou cannot use Bard effects without a faction!"));
                        return;
                    }
                    ItemStack clicked = e.getItem();
                    if(clicked != null && !clicked.hasItemMeta()) {
                        if(player.hasMetadata("bardCooldown") && (System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) < 15000) {
                            player.sendMessage(c.c("&c&l(!) &cYou cannot use another Bard Item for &n" + (15 - ((System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) / 1000)) + " seconds&c!"));
                            return;
                        }
                        if(clicked.getType() == Material.GHAST_TEAR) {
                            player.setMetadata("bardCooldown", new FixedMetadataValue(P.p, System.currentTimeMillis()));
                            player.removePotionEffect(PotionEffectType.REGENERATION);
                            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 4));
                            player.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nREGENERATION V&7 [5s]"));
                            for(int i = 0; i < 3; i++) {
                                player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 2);
                            }
                            for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                                if(nearbyEntity instanceof Player) {
                                    Player players = (Player) nearbyEntity;
                                    if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                        players.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 4));
                                        players.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nREGENERATION V&7 [5s]"));
                                    }
                                }
                            }
                            clicked.setAmount(clicked.getAmount() - 1);
                            player.setItemInHand(clicked);
                            Bukkit.getScheduler().runTaskLater(P.p, () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerItemHeldEvent(player, 0, 0));
                            }, 100L);
                        }
                        if(clicked.getType() == Material.MAGMA_CREAM) {
                            player.setMetadata("bardCooldown", new FixedMetadataValue(P.p, System.currentTimeMillis()));
                            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 20, 1));
                            player.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nFIRE RESISTANCE II&7 [5s]"));
                            for(int i = 0; i < 3; i++) {
                                player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 2);
                            }
                            for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                                if(nearbyEntity instanceof Player) {
                                    Player players = (Player) nearbyEntity;
                                    if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                        players.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 20, 1));
                                        players.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nFIRE RESISTANCE II&7 [5s]"));
                                    }
                                }
                            }
                            clicked.setAmount(clicked.getAmount() - 1);
                            player.setItemInHand(clicked);
                            Bukkit.getScheduler().runTaskLater(P.p, () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerItemHeldEvent(player, 0, 0));
                            }, 100L);
                        }
                        if(clicked.getType() == Material.SUGAR) {
                            player.setMetadata("bardCooldown", new FixedMetadataValue(P.p, System.currentTimeMillis()));
                            player.removePotionEffect(PotionEffectType.SPEED);
                            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
                            player.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nSPEED III&7 [5s]"));
                            for(int i = 0; i < 3; i++) {
                                player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 2);
                            }
                            for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                                if(nearbyEntity instanceof Player) {
                                    Player players = (Player) nearbyEntity;
                                    if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                        players.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
                                        players.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nSPEED III&7 [5s]"));
                                    }
                                }
                            }
                            clicked.setAmount(clicked.getAmount() - 1);
                            player.setItemInHand(clicked);
                            Bukkit.getScheduler().runTaskLater(P.p, () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerItemHeldEvent(player, 0, 0));
                            }, 100L);
                        }
                        if(clicked.getType() == Material.FEATHER) {
                            player.setMetadata("bardCooldown", new FixedMetadataValue(P.p, System.currentTimeMillis()));
                            player.removePotionEffect(PotionEffectType.JUMP);
                            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5 * 20, 5));
                            player.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nJUMP VI&7 [5s]"));
                            for(int i = 0; i < 3; i++) {
                                player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 2);
                            }
                            for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                                if(nearbyEntity instanceof Player) {
                                    Player players = (Player) nearbyEntity;
                                    if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                        players.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5 * 20, 5));
                                        players.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nJUMP VI&7 [5s]"));
                                    }
                                }
                            }
                            clicked.setAmount(clicked.getAmount() - 1);
                            player.setItemInHand(clicked);
                            Bukkit.getScheduler().runTaskLater(P.p, () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerItemHeldEvent(player, 0, 0));
                            }, 100L);
                        }
                        if(clicked.getType() == Material.BLAZE_POWDER) {
                            player.setMetadata("bardCooldown", new FixedMetadataValue(P.p, System.currentTimeMillis()));
                            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 * 20, 1));
                            player.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nSTRENGTH II&7 [5s]"));
                            for(int i = 0; i < 3; i++) {
                                player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 2);
                            }
                            for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                                if(nearbyEntity instanceof Player) {
                                    Player players = (Player) nearbyEntity;
                                    if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                        players.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 * 20, 1));
                                        players.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nSTRENGTH II&7 [5s]"));
                                    }
                                }
                            }
                            clicked.setAmount(clicked.getAmount() - 1);
                            player.setItemInHand(clicked);
                            Bukkit.getScheduler().runTaskLater(P.p, () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerItemHeldEvent(player, 0, 0));
                            }, 60L);
                        }
                        if(clicked.getType() == Material.IRON_INGOT) {
                            player.setMetadata("bardCooldown", new FixedMetadataValue(P.p, System.currentTimeMillis()));
                            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 4));
                            player.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nRESISTANCE V&7 [5s]"));
                            for(int i = 0; i < 3; i++) {
                                player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 2);
                            }
                            for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                                if(nearbyEntity instanceof Player) {
                                    Player players = (Player) nearbyEntity;
                                    if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                        players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 4));
                                        players.sendMessage(c.c("&e&lBARD: &aUsed consumable to gain &nRESISTANCE V&7 [5s]"));
                                    }
                                }
                            }
                            clicked.setAmount(clicked.getAmount() - 1);
                            player.setItemInHand(clicked);
                            Bukkit.getScheduler().runTaskLater(P.p, () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerItemHeldEvent(player, 0, 0));
                            }, 100L);
                        }
                    }
                }
            }, 1L);
        }
    }

    @EventHandler
    public void heldItem(PlayerItemHeldEvent e) {
        Bukkit.getScheduler().runTaskLater(P.p, () -> {
            Player player = e.getPlayer();
            if(wearingFullSet(player, "GOLD")) {
                if(player.getItemInHand().getType() == Material.GHAST_TEAR) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10000000, 0));
                    for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                        if(nearbyEntity instanceof Player) {
                            Player players = (Player) nearbyEntity;
                            if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                players.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10000000, 0));
                                players.setMetadata("bardPassiveEffect", new FixedMetadataValue(P.p, player.getUniqueId().toString()));
                            }
                        }
                    }
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &aApplied &nRegeneration I")), (byte)2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(player.getItemInHand().getType() != Material.GHAST_TEAR || !wearingFullSet(player, "GOLD")) {
                                if(player.hasMetadata("bardCooldown") && (System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) < 5000) {
                                    return;
                                }
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(onlinePlayer.hasMetadata("bardPassiveEffect") && onlinePlayer.getMetadata("bardPassiveEffect").get(0).asString().equals(player.getUniqueId().toString())) {
                                        onlinePlayer.removePotionEffect(PotionEffectType.REGENERATION);
                                    }
                                }
                                player.removePotionEffect(PotionEffectType.REGENERATION);
                                PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &cRemoved &nRegeneration I")), (byte)2);
                                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                                cancel();
                            }
                        }
                    }.runTaskTimer(P.p, 0L, 1L);
                    return;
                }
                if(player.getItemInHand().getType() == Material.BLAZE_POWDER) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10000000, 0));
                    for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                        if(nearbyEntity instanceof Player) {
                            Player players = (Player) nearbyEntity;
                            if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                players.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10000000, 0));
                                players.setMetadata("bardPassiveEffect", new FixedMetadataValue(P.p, player.getUniqueId().toString()));
                            }
                        }
                    }
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &aApplied &nStrength I")), (byte)2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(player.getItemInHand().getType() != Material.BLAZE_POWDER || !wearingFullSet(player, "GOLD")) {
                                if(player.hasMetadata("bardCooldown") && (System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) < 3000) {
                                    return;
                                }
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(onlinePlayer.hasMetadata("bardPassiveEffect") && onlinePlayer.getMetadata("bardPassiveEffect").get(0).asString().equals(player.getUniqueId().toString())) {
                                        onlinePlayer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                    }
                                }
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &cRemoved &nStrength I")), (byte)2);
                                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                                cancel();
                            }
                        }
                    }.runTaskTimer(P.p, 0L, 1L);
                    return;
                }
                if(player.getItemInHand().getType() == Material.MAGMA_CREAM) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000000, 0));
                    for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                        if(nearbyEntity instanceof Player) {
                            Player players = (Player) nearbyEntity;
                            if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                players.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000000, 0));
                                players.setMetadata("bardPassiveEffect", new FixedMetadataValue(P.p, player.getUniqueId().toString()));
                            }
                        }
                    }
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &aApplied &nFire Resistance I")), (byte)2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(player.getItemInHand().getType() != Material.MAGMA_CREAM || !wearingFullSet(player, "GOLD")) {
                                if(player.hasMetadata("bardCooldown") && (System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) < 5000) {
                                    return;
                                }
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(onlinePlayer.hasMetadata("bardPassiveEffect") && onlinePlayer.getMetadata("bardPassiveEffect").get(0).asString().equals(player.getUniqueId().toString())) {
                                        onlinePlayer.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                                    }
                                }
                                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                                PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &cRemoved &nFire Resistance I")), (byte)2);
                                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                                cancel();
                            }
                        }
                    }.runTaskTimer(P.p, 0L, 1L);
                    return;
                }
                if(player.getItemInHand().getType() == Material.IRON_INGOT) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 1));
                    for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                        if(nearbyEntity instanceof Player) {
                            Player players = (Player) nearbyEntity;
                            if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 0));
                                players.setMetadata("bardPassiveEffect", new FixedMetadataValue(P.p, player.getUniqueId().toString()));
                            }
                        }
                    }
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &aApplied &nResistance II")), (byte)2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(player.getItemInHand().getType() != Material.IRON_INGOT || !wearingFullSet(player, "GOLD")) {
                                if(player.hasMetadata("bardCooldown") && (System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) < 5000) {
                                    return;
                                }
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(onlinePlayer.hasMetadata("bardPassiveEffect") && onlinePlayer.getMetadata("bardPassiveEffect").get(0).asString().equals(player.getUniqueId().toString())) {
                                        onlinePlayer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                    }
                                }
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &cRemoved &nResistance II")), (byte)2);
                                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                                cancel();
                            }
                        }
                    }.runTaskTimer(P.p, 0L, 1L);
                    return;
                }
                if(player.getItemInHand().getType() == Material.SUGAR) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 1));
                    for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                        if(nearbyEntity instanceof Player) {
                            Player players = (Player) nearbyEntity;
                            if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                players.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0));
                                players.setMetadata("bardPassiveEffect", new FixedMetadataValue(P.p, player.getUniqueId().toString()));
                            }
                        }
                    }
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &aApplied &nSpeed II")), (byte)2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(player.getItemInHand().getType() != Material.SUGAR || !wearingFullSet(player, "GOLD")) {
                                if(player.hasMetadata("bardCooldown") && (System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) < 5000) {
                                    return;
                                }
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(onlinePlayer.hasMetadata("bardPassiveEffect") && onlinePlayer.getMetadata("bardPassiveEffect").get(0).asString().equals(player.getUniqueId().toString())) {
                                        onlinePlayer.removePotionEffect(PotionEffectType.SPEED);
                                    }
                                }
                                player.removePotionEffect(PotionEffectType.SPEED);
                                PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &cRemoved &nSpeed II")), (byte)2);
                                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                                cancel();
                            }
                        }
                    }.runTaskTimer(P.p, 0L, 1L);
                    return;
                }
                if(player.getItemInHand().getType() == Material.FEATHER) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000000, 1));
                    for (Entity nearbyEntity : player.getNearbyEntities(15.0, 15.0, 15.0)) {
                        if(nearbyEntity instanceof Player) {
                            Player players = (Player) nearbyEntity;
                            if(FactionUtils.getFaction(player).getTag().equals(FactionUtils.getFaction(players).getTag())) {
                                players.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000000, 0));
                                players.setMetadata("bardPassiveEffect", new FixedMetadataValue(P.p, player.getUniqueId().toString()));
                            }
                        }
                    }
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &aApplied &nJump II")), (byte)2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(player.getItemInHand().getType() != Material.FEATHER || !wearingFullSet(player, "GOLD")) {
                                if(player.hasMetadata("bardCooldown") && (System.currentTimeMillis() - player.getMetadata("bardCooldown").get(0).asLong()) < 5000) {
                                    return;
                                }
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(onlinePlayer.hasMetadata("bardPassiveEffect") && onlinePlayer.getMetadata("bardPassiveEffect").get(0).asString().equals(player.getUniqueId().toString())) {
                                        onlinePlayer.removePotionEffect(PotionEffectType.JUMP);
                                    }
                                }
                                player.removePotionEffect(PotionEffectType.JUMP);
                                PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(c.c("&e&lBARD:&r &cRemoved &nJump II")), (byte)2);
                                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                                cancel();
                            }
                        }
                    }.runTaskTimer(P.p, 0L, 1L);
                    return;
                }
            }
        }, 1L);
    }

    @EventHandler
    public void bowDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            if(e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                if(wearingFullSet(attacker, "LEATHER")) {
                    e.setDamage(e.getDamage() * 1.5);
                }
            }
        }
    }

    public boolean wearingFullSet(Player player, String type) {
        return (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().toString().startsWith(type + "_") && player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType().toString().startsWith(type + "_") && player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType().toString().startsWith(type + "_") && player.getInventory().getBoots() != null && player.getInventory().getBoots().getType().toString().startsWith(type + "_"));
    }
}
