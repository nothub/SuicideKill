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

    private boolean cooldown;

    @Override
    public void onEnable() {
        config();
        new CooldownRunnable(this).runTaskTimer(this, 0, getConfig().getInt("cooldown-ticks"));
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
        new UnvanishRunnable(this, requester).runTaskLater(this, getConfig().getInt("unvanish-ticks"));

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
        player.sendMessage(getConfig().getString("cooldown-message"));
    }

    private void config() {

        getConfig().addDefault("cooldown-message", "Sorry, Death is busy at the moment. Please try again later...");
        getConfig().addDefault("cooldown-ticks", 40);
        getConfig().addDefault("unvanish-ticks", 20);
        getConfig().options().copyDefaults(true);

        if (getConfig().getInt("unvanish-ticks") <= 20) {
            getConfig().set("unvanish-ticks", 20);
        }

        if (getConfig().getInt("cooldown-ticks") <= getConfig().getInt("unvanish-ticks")) {
            getConfig().set("cooldown-ticks", getConfig().getInt("unvanish-ticks") + 1);
        }

        saveConfig();

    }

}
