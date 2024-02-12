# Spleef - A Minecraft Server Plugin

Spleef is a plugin designed to enhance the PvP gameplay experience on your Minecraft server. It allows players to engage in exciting Spleef matches, where the objective is to break blocks beneath your opponents' feet and be the last one standing.

## Usage
The plugin constantly monitors player activity in the spleef world. When the player count in the world reaches 10, a 30-second countdown timer begins. During this countdown, players can still leave the world, which pauses the timer. Once the timer reaches 0, the game starts automatically. You can join the spleef world using the in-game command `/spleef`.

## Installation
To install Spleef on your Minecraft server:
1. Download the latest release from the Releases section.
2. Build the plugin using Maven or Gradle.
3. Place the generated .jar file into your Minecraft Server's `plugins/` folder.
4. Ensure that your server includes a world named "spleef." To import it, upload the world folder and name it `spleef`. In-game, run the command `/mv import spleef NORMAL` to import the world.
5. Restart the server to load the plugin.

### Requirements
Spleef is compatible with Paper, Bukkit, and Spigot servers. While the plugin has not been tested on all server versions, it is expected to work with any Minecraft version. I encourage users to test the plugin and report any issues encountered.

## License
See [License.md](License.md) for details.
