# SuicideKill

[![DL](https://img.shields.io/github/downloads/nothub/SuicideKill/total?label=DL&style=popout-square)](https://github.com/nothub/SuicideKill/releases/latest)
[![LoC](https://img.shields.io/tokei/lines/github/nothub/SuicideKill?label=LoC&style=popout-square)](https://github.com/nothub/SuicideKill)

A simple Minecraft suicide plugin

Inspired by: [OldschoolKill](https://www.spigotmc.org/resources/oldschoolkill.4047)

---

##### Command Usage

* Suicides Players with insufficient permissions when they run: `/kill`
* Players with correct permissions can use command as intended: `kill NAME`

---

##### Config defaults

```
cooldown-message: §cSorry, Death is too busy at the moment. Please try again later...§r
cooldown-ticks: 20
unvanish-ticks: 10
```

---

Uses [vanish](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer(org.bukkit.plugin.Plugin,org.bukkit.entity.Player)) to mitigate [Coordinate Exploitation](https://2b2t.miraheze.org/wiki/Coordinate_Exploits#Debug_Exploit/).

---

This Project uses [bStats](https://bstats.org/plugin/bukkit/SuicideKill/11813).
