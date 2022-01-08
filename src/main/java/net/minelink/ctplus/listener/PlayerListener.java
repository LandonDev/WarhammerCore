package net.minelink.ctplus.listener;

import com.massivecraft.factions.P;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.Tag;
import net.minelink.ctplus.event.CombatLogEvent;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import net.minelink.ctplus.task.SafeLogoutTask;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public final class PlayerListener implements Listener {
    private final CombatTagPlus plugin;

    public PlayerListener(CombatTagPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void addPlayer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getNpcPlayerHelper().createPlayerList(player);
        this.plugin.getPlayerCache().addPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.isDead())
            return;
        boolean isTagged = this.plugin.getTagManager().isTagged(player.getUniqueId());
        if (!isTagged && !this.plugin.getSettings().alwaysSpawn())
            return;
        if (this.plugin.getSettings().getDisabledWorlds().contains(player.getWorld().getName()))
            return;
        if (!this.plugin.getHookManager().isPvpEnabledAt(player.getLocation()))
            return;
        if (player.hasPermission("ctplus.bypass.tag"))
            return;
        if (SafeLogoutTask.isFinished(player))
            return;
        P.p.getServer().getPluginManager().callEvent((Event) new CombatLogEvent(player, isTagged ? CombatLogEvent.Reason.TAGGED : CombatLogEvent.Reason.UNSAFE_LOGOUT));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removePlayer(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.plugin.getNpcPlayerHelper().removePlayerList(player);
        this.plugin.getPlayerCache().removePlayer(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void broadcastKill(PlayerDeathEvent event) {
        String message = this.plugin.getSettings().getKillMessage();
        String messageItem = this.plugin.getSettings().getKillMessageItem();
        if (message.isEmpty() && messageItem.isEmpty())
            return;
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        if (this.plugin.getNpcPlayerHelper().isNpc(player))
            playerId = this.plugin.getNpcPlayerHelper().getIdentity(player).getId();
        Tag tag = this.plugin.getTagManager().getTag(playerId, true);
        if (tag == null)
            return;
        String victim = tag.getVictimName();
        String attacker = tag.getAttackerName();
        Player p = this.plugin.getPlayerCache().getPlayer(tag.getAttackerId());
        if (victim == null || attacker == null || p == null)
            return;
        if (!tag.getVictimId().equals(playerId)) {
            victim = tag.getAttackerName();
            attacker = tag.getVictimName();
        }
        ItemStack item = p.getItemInHand();
        if (item.getType() != Material.AIR) {
            String name = WordUtils.capitalizeFully(item.getType().name().replace("_", " "));
            message = messageItem.replace("{item}", name);
        }
        message = message.replace("{victim}", victim).replace("{attacker}", attacker);
        Bukkit.broadcast(message, "ctplus.notify.kill");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("ctplus.bypass.command"))
            return;
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        String message = event.getMessage();
        if (this.plugin.getSettings().isCommandBlacklisted(message)) {
            event.setCancelled(true);
            if (!this.plugin.getSettings().getDisabledCommandMessage().isEmpty())
                player.sendMessage(this.plugin.getSettings().getDisabledCommandMessage().replace("{command}", message));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableBlockEdit(BlockBreakEvent event) {
        if (!this.plugin.getSettings().disableBlockEdit())
            return;
        Player player = event.getPlayer();
        if (player.hasPermission("ctplus.bypass.blockedit"))
            return;
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        event.setCancelled(true);
        if (!this.plugin.getSettings().getDisableBlockEditMessage().isEmpty())
            player.sendMessage(this.plugin.getSettings().getDisableBlockEditMessage());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableBlockEdit(BlockPlaceEvent event) {
        if (!this.plugin.getSettings().disableBlockEdit())
            return;
        Player player = event.getPlayer();
        if (player.hasPermission("ctplus.bypass.blockedit"))
            return;
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        event.setCancelled(true);
        if (!this.plugin.getSettings().getDisableBlockEditMessage().isEmpty())
            player.sendMessage(this.plugin.getSettings().getDisableBlockEditMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void disableStorageAccess(PlayerCombatTagEvent event) {
        if (!this.plugin.getSettings().disableStorageAccess())
            return;
        if (event.getVictim() != null)
            tryDisableStorageAccess(event.getVictim(), null);
        if (event.getAttacker() != null)
            tryDisableStorageAccess(event.getAttacker(), null);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableStorageAccess(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        if (tryDisableStorageAccess(player, event.getView()))
            event.setCancelled(true);
    }

    private boolean tryDisableStorageAccess(Player player, @Nullable InventoryView view) {
        if (!this.plugin.getSettings().disableStorageAccess())
            return false;
        if (player.hasPermission("ctplus.bypass.storageaccess"))
            return false;
        if (view == null)
            view = player.getOpenInventory();
        view.close();
        String message = this.plugin.getSettings().getDisableStorageAccessMessage();
        if (!message.isEmpty())
            player.sendMessage(message);
        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void disableEnderpearls(PlayerInteractEvent event) {
        if (!this.plugin.getSettings().disableEnderpearls())
            return;
        Player player = event.getPlayer();
        if (player.hasPermission("ctplus.bypass.enderpearl"))
            return;
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR))
            return;
        if (!player.getItemInHand().getType().equals(Material.ENDER_PEARL))
            return;
        event.setCancelled(true);
        if (!this.plugin.getSettings().getDisableEnderpearlsMessage().isEmpty())
            player.sendMessage(this.plugin.getSettings().getDisableEnderpearlsMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void disableFlying(PlayerCombatTagEvent event) {
        if (!this.plugin.getSettings().disableFlying())
            return;
        Player p = event.getVictim();
        if (p != null && p.isFlying() && !p.hasPermission("ctplus.bypass.flying")) {
            p.setAllowFlight(false);
            if (!this.plugin.getSettings().getDisableFlyingMessage().isEmpty())
                p.sendMessage(this.plugin.getSettings().getDisableFlyingMessage());
        }
        p = event.getAttacker();
        if (p != null && p.isFlying() && !p.hasPermission("ctplus.bypass.flying")) {
            p.setAllowFlight(false);
            if (!this.plugin.getSettings().getDisableFlyingMessage().isEmpty())
                p.sendMessage(this.plugin.getSettings().getDisableFlyingMessage());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableFlying(PlayerToggleFlightEvent event) {
        if (!this.plugin.getSettings().disableFlying())
            return;
        Player player = event.getPlayer();
        if (player.isFlying())
            return;
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        if (player.hasPermission("ctplus.bypass.flying"))
            return;
        player.setAllowFlight(false);
        event.setCancelled(true);
        if (!this.plugin.getSettings().getDisableFlyingMessage().isEmpty())
            player.sendMessage(this.plugin.getSettings().getDisableFlyingMessage());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableTeleportation(PlayerTeleportEvent event) {
        if (!this.plugin.getSettings().disableTeleportation())
            return;
        Player player = event.getPlayer();
        switch (event.getCause()) {
            case ENDER_PEARL:
                return;
            case COMMAND:
            case PLUGIN:
            case UNKNOWN:
                if (this.plugin.getSettings().untagOnPluginTeleport())
                    this.plugin.getTagManager().untag(player.getUniqueId());
                return;
        }
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        if (player.hasPermission("ctplus.bypass.teleport"))
            return;
        event.setCancelled(true);
        if (!this.plugin.getSettings().getDisableTeleportationMessage().isEmpty())
            player.sendMessage(this.plugin.getSettings().getDisableTeleportationMessage());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableCrafting(CraftItemEvent event) {
        if (!this.plugin.getSettings().disableCrafting())
            return;
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return;
        if (player.hasPermission("ctplus.bypass.craft"))
            return;
        event.setCancelled(true);
        if (!this.plugin.getSettings().getDisableCraftingMessage().isEmpty())
            player.sendMessage(this.plugin.getSettings().getDisableCraftingMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public void denySafeZoneEntry(PlayerTeleportEvent event) {
        if (this.plugin.getSettings().denySafezoneEnderpearl() && this.plugin
                .getTagManager().isTagged(event.getPlayer().getUniqueId()) && event
                .getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
                !this.plugin.getHookManager().isPvpEnabledAt(event.getTo()) && this.plugin
                .getHookManager().isPvpEnabledAt(event.getFrom()))
            event.setCancelled(true);
    }
}
