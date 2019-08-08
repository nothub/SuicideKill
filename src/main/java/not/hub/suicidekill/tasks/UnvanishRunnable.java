package not.hub.suicidekill.tasks;

import not.hub.suicidekill.SuicideKill;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UnvanishRunnable extends BukkitRunnable {

    private final SuicideKill plugin;
    private final Player player;

    public UnvanishRunnable(SuicideKill plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        plugin.unvanish(player);
    }

}
