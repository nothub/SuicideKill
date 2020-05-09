package not.hub.suicidekill;

import not.hub.suicidekill.tasks.CooldownRunnable;
import not.hub.suicidekill.tasks.UnvanishRunnable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class SuicideKill extends JavaPlugin implements Listener {

    private String cooldownMessage;
    private int cooldownValue;
    private int unvanishDelay;

    private boolean cooldown;

    @Override
    public void onEnable() {
        loadConfig();
        cooldownMessage = getConfig().getString("global-cooldown-message");
        cooldownValue = getConfig().getInt("global-cooldown-ticks");
        unvanishDelay = getConfig().getInt("unvanish-delay-ticks");
        if (unvanishDelay == 0) {
            unvanishDelay = 1;
        }
        new CooldownRunnable(this).runTaskTimer(this, 0, cooldownValue);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        String cmd = event.getMessage().toLowerCase();

        // not our command
        if ((!cmd.startsWith("/kill ")) && (!cmd.equals("/kill"))) {
            return;
        }

        Player requester = event.getPlayer();

        // issuer has to be a player
        if (requester == null) {
            return;
        }

        // if perm is correct, return so /kill $1 command can be used
        if (requester.hasPermission("minecraft.command.kill")) {
            return;
        }

        // cancel original event
        event.setCancelled(true);

        // no kills while cooldown active
        if (cooldown) {
            cooldownMessage(requester);
            return;
        } else {
            cooldown = true;
        }

        getLogger().info("Killing: " + requester.getName());

        // dismount
        Optional<Entity> vehicle = Optional.ofNullable(requester.getVehicle());
        if (vehicle.isPresent()) {
            getLogger().info("Dismounting " + requester.getDisplayName() + " from " + vehicle.get().getName() + " before killing");
            vehicle.get().eject();
        }

        
        // tp exploit protection vanish
        vanish(requester);

        // kill requester
        requester.setHealth(0);
        // TODO - EntityDamageEvent.DamageCause.SUICIDE

        // unvanish requester after n ticks
        new UnvanishRunnable(this, requester).runTaskLater(this, unvanishDelay);

    }

    public void disableCooldown() {
        cooldown = false;
    }

    public void unvanish(Player player) {
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.showPlayer(this, player);
            }
        }
    }

    private void vanish(Player player) {
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.hidePlayer(this, player);
            }
        }
    }

    private void cooldownMessage(Player player) {
        player.sendMessage(cooldownMessage);
    }

    private void loadConfig() {
        getConfig().addDefault("global-cooldown-message", "Sorry, Death is busy at the moment. Please try again later...");
        getConfig().addDefault("global-cooldown-ticks", 60);
        getConfig().addDefault("unvanish-delay-ticks", 20);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
