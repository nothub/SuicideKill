# SuicideKill
<a href="https://github.com/blockparole/SuicideKill/releases/latest" alt="Download"><img src="https://img.shields.io/github/downloads/blockparole/SuicideKill/latest/total.svg?label=download%20latest&style=popout-square" /></a>
<a href="https://github.com/blockparole/SuicideKill" alt="Download"><img src="https://img.shields.io/github/languages/code-size/blockparole/SuicideKill.svg?label=repo%20size&style=popout-square" /></a>

A simple Plugin that kills Players with insufficient permissions when they run: `/kill`  
For Players with correct permissions, the command works as intended: `kill <target>`.

Uses [vanish](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer-org.bukkit.plugin.Plugin-org.bukkit.entity.Player-) to mitigate [Coordinate Exploitation](https://2b2t.miraheze.org/wiki/Coordinate_Exploits#Debug_Exploit/).

This Project was tested on [Paper](https://papermc.io/) [1.12.2](https://papermc.io/api/v1/paper/1.12.2/1618).

Inspired by: [OldschoolKill](https://www.spigotmc.org/resources/oldschoolkill.4047/)
