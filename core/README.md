## Core
The core module is the only module that is required for the mod to function.   
It contains the core functionality of the mod, including:
- Custom Recipes 
  - Crafting
  - Furnace
  - Campfire
  - Blast Furnace
  - Smoker
  - Grindstone
  - Stonecutter
  - Anvil
  - Smithingtable
- Composable Recipe Components
  - Ingredient Matcher
  - Ingredient Consumer
  - Ingredient Remainder
  - Recipe Item Modifiers (Transformations, Transmuters)
  - Procedures (Damage Combine, Enchanting, Item Repair, Rename, Repair Cost)
  - Processes (Grinding, Repairing)
  - Recipe Conditions
  - Result Actions (Commands)
- Registration of custom Recipe Components
- Database integration (SQL)
  - H2
  - MariaDB
  - MySQL
  - Oracle
  - PostgreSQL
  - SQL Server
  - SQLite
- Recipe Evaluation & Integration into the Minecraft Recipe System  
- \+ more

## Recipe Configuration
Recipes are fully configurable through files in the `resources` directory. 
The files can be modified with any text-editor that support the HOCON format.

* `<root>` - The root dir. Actual location depends on the platform.  
  (spigot: `plugins/customcrafting`, fabric: `config/customcrafting`)  
    * `.data` - data that is cached and used internally by CC
    * `config` - Customisable configuration files
    * `resources` - default location for resources
        * `defaults` - Includes the default recipes shipped with CC
        * `*/**` - Any directories created the user from which to load custom recipes

> [!note]  
> The optional Editor module provides a way to edit recipes using a UI in-game,
> but is still work in progress and currently not published.
