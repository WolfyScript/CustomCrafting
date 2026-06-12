# Results

Results define what a recipe produces when completed by a player or when processed by workstations.
It picks the final item from the specified list of `choices`, can modify it using the `modifier`, and run actions upon completion.

::: info Properties
----

##### `choices` <Badge type="info" text="list<RecipeChoice>" />

The items or tags that can be produced.

##### `modifier` [<Badge type="info" text="Modifier" />](#modifiers) <Badge type="tip" text="optional" />

Allows you to apply transformations to the resulting item.

##### `actions` [<Badge type="info" text="list<Action>" />](#actions) <Badge type="tip" text="optional" />

Actions that are executed when

* the result is collected (player-completed recipes, e.g. crafting table)
* the result is processed (automatically processed recipes, e.g. furnace, crafter, etc.)

##### `alwaysKeepPrevious` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

If true, the system will keep the same result until it is collected. This prevents the result from "rerolling" every
time the player looks at the result slot.

:::

```hocon
choices {
  stacks = [
    {
      identifier {
        type = vanilla 
        stack = "{id:'minecraft:diamond_sword'}"
      }
    }
  ]
}
modifier {
  // Modifier settings
}
actions = [
  // Actions
]
alwaysKeepPrevious = true
```

## Modifiers <Badge type="info" text="Modifier" />

Applies transformations that can use the state of input items and modify the resulting item.

## Actions <Badge type="info" text="Action" />

Actions are things that happen when the result is collected. The system supports various types of actions to allow for
custom behaviour during the crafting process.

### Command <Badge type="info" text="type = command" />

The `command` action allows you to execute one or more Minecraft commands when a recipe result is collected.

::: info Properties
----
##### `commands` <Badge type="info" text="list<string>" />

A list of strings representing the commands to execute.  

The commands are executed with the context of the executor that varies based on recipe type and workstation.
For example, the player for crafting table, or the tile entities for furnaces, etc.
They support all vanilla [target selectors](https://minecraft.wiki/w/Target_selectors).

:::

```hocon
type = command
commands = [
  "say Recipe completed!",
  "give @p minecraft:firework_rocket 1"
]
```
