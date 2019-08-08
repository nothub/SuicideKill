package not.hub.suicidekill.tasks;

import not.hub.suicidekill.SuicideKill;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownRunnable extends BukkitRunnable {

    private final SuicideKill plugin;

    public CooldownRunnable(SuicideKill plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.disableCooldown();
    }

}
