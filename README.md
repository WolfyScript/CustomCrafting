<div align="center">
  <img src="https://i.imgur.com/wLHwJ1c.png" alt="CustomCrafting" />
</div>

> [!note]  
> v5 is in early alpha and may not work properly yet.
> Breaking API changes may be introduced at anytime without prior warning!
> 
> ## Breaking Changes & Planned New Features
> * Removed Elite Crafting Table
> * Removed CustomItem; Focus is instead on integrating items from other plugins/mods
> * Removed Advanced Crafting Table
> * New Recipe structure: more composable recipes
> * New Resource Loader for multiple SQL and custom local destinations
> * Editor API: create and edit recipes via CLI or GUI
> * Cross-Platform: Spigot, Paper, Sponge, Fabric, and more in the future
> * Modded: Makes use of Minecraft internals to better support custom recipes
> * ...
> 

# CustomCrafting

![bStats Servers](https://img.shields.io/bstats/servers/3211)
![Spiget Downloads](https://img.shields.io/spiget/downloads/55883)
![Spiget Stars](https://img.shields.io/spiget/stars/55883)

CustomCrafting allows you to create custom recipes for a vast variety of
workstations including:  
Crafting Table, Furnace, Blast Furnace, Smoker, Smithing, and more.

Additionally, you can toggle vanilla recipes as you like, and disable and override them.

It integrates with other plugins like Oraxen, ItemsAdder, MMOItems, and MythicMobs to support
your custom items and update recipes automatically when they are changed.

Before creating an issue, please go to the wiki. It should clear up frequently asked questions.

- [**Wiki**](https://github.com/WolfyScript/CustomCrafting/wiki)
- [**JavaDocs**](https://wolfyscript.github.io/CustomCrafting-Wiki/)

For any questions join the [Discord](https://discord.gg/qGhDTSr).

![recipe_types](https://github.com/WolfyScript/CustomCrafting/assets/41468455/f36d3d23-c094-4a47-9b45-370f2f314d21)
![customization](https://github.com/WolfyScript/CustomCrafting/assets/41468455/8415f2e3-18cc-49e5-99ae-c910f4c97c56)
![advanced_settings](https://github.com/WolfyScript/CustomCrafting/assets/41468455/8d74313c-e5e8-4171-a120-a0b0bd9e2d74)
![dependencies_updates](https://github.com/WolfyScript/CustomCrafting/assets/41468455/0a80e1ff-0c07-419d-bd88-7172aabe5096)

[![](https://bstats.org/signatures/bukkit/CustomCrafting.svg)](https://bstats.org/plugin/bukkit/CustomCrafting/3211)

## Config & Resource Directory Structure

* `<root>` - actual location depends on the platform. (on spigot `plugins/customcrafting`)
  * `.data` - data that is cached and used internally by CC
  * `config` - CC configuration
  * `resources` - default location for resources
    * `defaults` - the default recipes shipped with CC
    * `*/**` - directories created by users from which to load recipes

## Check out my partner!
<a href="https://billing.kinetichosting.net/aff.php?aff=345">
  <img width="700px" src="https://user-images.githubusercontent.com/41468455/237019976-6b66b7f4-3d26-4b2f-b858-463ffe675531.png" alt="Kinetic Hosting 15% off your first month with code WOLFYSCRIPT"/>
</a>



