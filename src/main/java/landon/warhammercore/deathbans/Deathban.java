package landon.warhammercore.deathbans;

import landon.warhammercore.util.c;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Deathban {
    private UUID uuid;
    private long startedOn;
    private long endsOn;
    private String deathMessage;

    public Deathban(UUID uuid, long duration, String deathMessage) {
        this.uuid = uuid;
        this.startedOn = System.currentTimeMillis();
        this.endsOn = this.startedOn + duration;
        this.deathMessage = deathMessage;
        if(Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).kickPlayer(this.generateKickMessage());
        }
    }

    public Deathban(UUID uuid, long startedOn, long endsOn, String deathMessage) {
        this.uuid = uuid;
        this.startedOn = startedOn;
        this.endsOn = endsOn;
        this.deathMessage = deathMessage;
        if(Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).kickPlayer(this.generateKickMessage());
        }
    }

    public boolean checkExpired() {
        if(System.currentTimeMillis() >= this.endsOn) {
            DeathbanManager.get().deleteDeathban(this);
            return true;
        }
        return false;
    }

    public String generateKickMessage() {
        long secondsLeftOnBan = (this.endsOn - System.currentTimeMillis()) / 1000;
        int days = (int) TimeUnit.SECONDS.toDays(secondsLeftOnBan);
        long hours = TimeUnit.SECONDS.toHours(secondsLeftOnBan) - (days *24);
        long minutes = TimeUnit.SECONDS.toMinutes(secondsLeftOnBan) - (TimeUnit.SECONDS.toHours(secondsLeftOnBan)* 60);
        long seconds = TimeUnit.SECONDS.toSeconds(secondsLeftOnBan) - (TimeUnit.SECONDS.toMinutes(secondsLeftOnBan) *60);
        String cooldownPlaceholder = null;
        if(days != 0) {
            cooldownPlaceholder = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
        } else if(hours != 0) {
            cooldownPlaceholder = hours + "h " + minutes + "m " + seconds + "s";
        } else if(minutes != 0) {
            cooldownPlaceholder = minutes + "m " + seconds + "s";
        } else {
            cooldownPlaceholder = seconds + "s";
        }
        return c.c("&c&lYou are &ndeathbanned!&r\n&7You have run out of lives.\n\n&7Your death:\n" + this.deathMessage + "\n\n&eYour deathban expires in &n" + cooldownPlaceholder + "&e!&r");
    }
}
