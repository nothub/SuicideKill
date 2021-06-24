package not.hub.suicidekill;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Plugin extends JavaPlugin implements Listener {

    private final Set<UUID> cooldowns = ConcurrentHashMap.newKeySet();
    private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();

    @Override
    public void onEnable() {
        new Metrics(this, 11813);
        config();
        getServer()
                .getPluginManager()
                .registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        vanished
                .stream()
                .map(uuid -> getServer().getPlayer(uuid))
                .forEach(this::unvanish);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        if (!event.getMessage().equalsIgnoreCase("/kill")) return;

        if (event.getPlayer() == null) return;

        final Player player = event.getPlayer();

        // if permission is correct, return, so /kill command can be used normally
        if (player.hasPermission("minecraft.command.kill")) return;

        // cancel original event
        event.setCancelled(true);

        // no kill while cooldown active
        if (cooldowns.contains(player.getUniqueId())) {
            player.sendMessage(getConfig().getString("cooldown-message"));
            return;
        } else {
            cooldowns.add(player.getUniqueId());
        }

        getLogger().info("Suiciding: " + player.getName());

        // dismount
        Optional
                .ofNullable(player.getVehicle())
                .ifPresent(Entity::eject);

        // coordinate exploit protection vanish
        vanish(player);

        // kill requester
        player.setHealth(0);
        // TODO - EntityDamageEvent.DamageCause.SUICIDE

        // unvanish
        new BukkitRunnable() {
            @Override
            public void run() {
                unvanish(player);
            }
        }.runTaskLater(this, getConfig().getInt("unvanish-ticks"));

        // cooldown
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(this, getConfig().getInt("cooldown-ticks"));

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (vanished.contains(event.getPlayer().getUniqueId())) {
            unvanish(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (vanished.contains(event.getPlayer().getUniqueId())) {
            unvanish(event.getPlayer());
        }
    }

    private void vanish(Player player) {
        vanished.add(player.getUniqueId());
        getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                .forEach(p -> p.hidePlayer(this, player));
    }

    public void unvanish(Player player) {
        getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                .forEach(p -> p.showPlayer(this, player));
        vanished.remove(player.getUniqueId());
    }

    private void config() {

        getConfig().addDefault("cooldown-message", ChatColor.RED + "Sorry, Death is too busy at the moment. Please try again later..." + ChatColor.RESET);
        getConfig().addDefault("cooldown-ticks", 20);
        getConfig().addDefault("unvanish-ticks", 10);
        getConfig().options().copyDefaults(true);

        if (getConfig().getInt("unvanish-ticks") < 10) {
            getConfig().set("unvanish-ticks", 10);
        }

        if (getConfig().getInt("cooldown-ticks") <= getConfig().getInt("unvanish-ticks")) {
            getConfig().set("cooldown-ticks", getConfig().getInt("unvanish-ticks") + 1);
        }

        saveConfig();

    }

}
