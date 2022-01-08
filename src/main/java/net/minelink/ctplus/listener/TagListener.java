package net.minelink.ctplus.listener;

import com.google.common.collect.ImmutableSet;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.Tag;
import net.minelink.ctplus.TagManager;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import net.minelink.ctplus.task.SafeLogoutTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public final class TagListener implements Listener {
    private static final Set<PotionEffectType> harmfulEffects = ImmutableSet.of(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, new PotionEffectType[]{PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER});

    private final CombatTagPlus plugin;

    public TagListener(CombatTagPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void tagPlayer(EntityDamageByEntityEvent event) {
        Entity victimEntity = event.getEntity();
        Entity attackerEntity = event.getDamager();
        Player victim = determineVictim(victimEntity);
        if (victim == null)
            return;
        if (victim.getGameMode() == GameMode.CREATIVE && this.plugin.getSettings().disableCreativeTags())
            victim = null;
        LivingEntity attacker = determineAttacker(attackerEntity, victim);
        if (attacker == null)
            return;
        Player attackingPlayer = (attacker instanceof Player) ? (Player) attacker : null;
        if (Objects.equals(victim, attacker) && this.plugin.getSettings().disableSelfTagging())
            return;
        if (attackingPlayer != null && attackingPlayer.getGameMode() == GameMode.CREATIVE && this.plugin.getSettings().disableCreativeTags())
            return;
        this.plugin.getTagManager().tag(victim, attackingPlayer);
    }

    @Nullable
    private Player determineVictim(Entity victimEntity) {
        if (victimEntity instanceof Tameable) {
            AnimalTamer owner = ((Tameable) victimEntity).getOwner();
            if (!(owner instanceof Player))
                return null;
            return (Player) owner;
        }
        if (victimEntity instanceof Player)
            return (Player) victimEntity;
        return null;
    }

    @Nullable
    private LivingEntity determineAttacker(Entity attackerEntity, Player victim) {
        if (attackerEntity instanceof org.bukkit.entity.Creature && this.plugin.getSettings().mobTagging())
            return (LivingEntity) attackerEntity;
        if (attackerEntity instanceof Projectile) {
            Entity source;
            Projectile p = (Projectile) attackerEntity;
            if (p.getShooter() instanceof Entity) {
                source = (Entity) p.getShooter();
            } else {
                return null;
            }
            if (p.getType() == EntityType.ENDER_PEARL && Objects.equals(victim, source))
                return null;
            return determineAttacker(source, victim);
        }
        if (attackerEntity instanceof Tameable) {
            AnimalTamer owner = ((Tameable) attackerEntity).getOwner();
            if (owner instanceof Player)
                return (LivingEntity) owner;
        } else if (attackerEntity instanceof Player) {
            return (LivingEntity) attackerEntity;
        }
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void tagPlayer(PotionSplashEvent event) {
        ProjectileSource source = event.getEntity().getShooter();
        if (!(source instanceof Player))
            return;
        Player attacker = (Player) source;
        boolean isHarmful = false;
        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (harmfulEffects.contains(effect.getType())) {
                isHarmful = true;
                break;
            }
        }
        if (!isHarmful)
            return;
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player))
                continue;
            Player victim = (Player) entity;
            if (victim == attacker)
                continue;
            if (!this.plugin.getNpcPlayerHelper().isNpc(victim))
                this.plugin.getTagManager().tag(victim, attacker);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void tagPlayer(ProjectileLaunchEvent event) {
        TagManager.Flag flag;
        if (this.plugin.getSettings().resetTagOnPearl())
            return;
        Projectile entity = event.getEntity();
        if (entity.getType() != EntityType.ENDER_PEARL)
            return;
        if (!(entity.getShooter() instanceof Player))
            return;
        Player player = (Player) entity.getShooter();
        if (player.hasPermission("ctplus.bypass.tag"))
            return;
        Tag tag = this.plugin.getTagManager().getTag(player.getUniqueId());
        if (tag == null)
            return;
        if (player.getUniqueId().equals(tag.getAttackerId())) {
            flag = TagManager.Flag.TAG_ATTACKER;
        } else if (player.getUniqueId().equals(tag.getVictimId())) {
            flag = TagManager.Flag.TAG_VICTIM;
        } else {
            return;
        }
        Player victim = null;
        if (tag.getVictimId() != null)
            victim = Bukkit.getPlayer(tag.getVictimId());
        Player attacker = null;
        if (tag.getAttackerId() != null)
            attacker = Bukkit.getPlayer(tag.getAttackerId());
        this.plugin.getTagManager().tag(victim, attacker, EnumSet.of(flag));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void untagPlayer(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!this.plugin.getNpcPlayerHelper().isNpc(player))
            this.plugin.getTagManager().untag(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void untagPlayer(PlayerKickEvent event) {
        if (!this.plugin.getSettings().untagOnKick())
            return;
        if (this.plugin.getSettings().getUntagOnKickBlacklist().contains(event.getReason()))
            return;
        Player player = event.getPlayer();
        this.plugin.getTagManager().untag(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void sendTagMessage(PlayerCombatTagEvent event) {
        Player attacker = event.getAttacker();
        Player victim = event.getVictim();
        if (victim != null && !this.plugin.getTagManager().isTagged(victim.getUniqueId()) &&
                !this.plugin.getSettings().onlyTagAttacker())
            if (attacker != null) {
                String message = this.plugin.getSettings().getTagMessage();
                if (!message.isEmpty())
                    victim.sendMessage(message.replace("{opponent}", attacker.getName()));
            } else {
                String message = this.plugin.getSettings().getTagUnknownMessage();
                if (!message.isEmpty())
                    victim.sendMessage(message);
            }
        if (attacker != null && !this.plugin.getTagManager().isTagged(attacker.getUniqueId()))
            if (victim != null) {
                String message = this.plugin.getSettings().getTagMessage();
                if (!message.isEmpty())
                    attacker.sendMessage(message.replace("{opponent}", victim.getName()));
            } else {
                String message = this.plugin.getSettings().getTagUnknownMessage();
                if (!message.isEmpty())
                    attacker.sendMessage(message);
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void cancelLogout(PlayerCombatTagEvent event) {
        Player player = event.getPlayer();
        if (!SafeLogoutTask.cancel(player))
            return;
        if (!this.plugin.getSettings().getLogoutCancelledMessage().isEmpty())
            player.sendMessage(this.plugin.getSettings().getLogoutCancelledMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void disableInWorld(PlayerCombatTagEvent event) {
        String world = event.getPlayer().getWorld().getName();
        if (this.plugin.getSettings().getDisabledWorlds().contains(world))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void disableInSafeRegion(PlayerCombatTagEvent event) {
        if (!this.plugin.getHookManager().isPvpEnabledAt(event.getPlayer().getLocation()))
            event.setCancelled(true);
    }
}
