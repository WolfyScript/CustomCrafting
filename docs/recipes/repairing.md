---
outline: [ 2, 3 ]
---

# Repairing Recipes

Repairing recipes are used to repair items in the Anvil.

::: info Properties
----

##### `base` [<Badge type="info" text="Ingredient" />](ingredients)

The base ingredient, the first slot in the Anvil menu (the item to repair/enchant).

##### `addition` [<Badge type="info" text="Ingredient" />](ingredients)

The addition ingredient, the second slot in the Anvil menu (the item to sacrifice for repair/enchanting).

##### `process` <Badge type="info" text="Process" />

The process to produce the result in the Anvil menu.

:::

## Process <Badge type="info" text="Process" />

The `process` property defines how the repairing process is handled.

### Fixed Result <Badge type="info" text="type = fixed_result" />

Always uses the specified `result` and computes the resulting stack based on the data and context.

::: info Properties
----

##### `rename` <Badge type="info" text="ProcedureRename" /> <Badge type="tip" text="optional" />

How the item name is handled.

##### `result` [<Badge type="info" text="Result" />](results)

The resulting item.

##### `cost` <Badge type="info" text="int" />

The experience cost for the repair.

:::

```hocon
process {
  type = "fixed_result"
  rename = {
    // See ProcedureRename documentation
    type = "..."
  }
  result {
    choices {
      stacks = [
        { identifier { stack = "{id:'minecraft:diamond_sword'}" } }
      ]
    }
    alwaysKeepPrevious = true
    modifier {}
    actions = []
    bulkActions = []
  }
  cost = 1
}
```

### Custom Process <Badge type="info" text="type = custom" />

Tries to mirror the vanilla logic of the anvil as much as possible, while providing lots of customization options.

::: info Properties
----

##### `rename` <Badge type="info" text="ProcedureRename" /> <Badge type="tip" text="optional" />

How the item name is handled.

##### `damageCombine` <Badge type="info" text="ProcedureDamageCombine" /> <Badge type="tip" text="optional" />

How damage is combined.

##### `itemRepair` <Badge type="info" text="ProcedureItemRepair" /> <Badge type="tip" text="optional" />

How the item is repaired.

##### `enchanting` <Badge type="info" text="ProcedureEnchanting" /> <Badge type="tip" text="optional" />

How enchanting is handled.

:::

```hocon
process {
  type = "custom"
  rename = {
    // See ProcedureRename documentation
    type = "..."
  }
  damageCombine = {
    // See ProcedureDamageCombine documentation
    type = "..."
  }
  itemRepair = {
    // See ProcedureItemRepair documentation
    type = "..."
  }
  enchanting {
    // See ProcedureEnchanting documentation
    type = "..."
  }
}
```

## Example

```hocon
base { identifier { stack = "{id:'minecraft:diamond_sword'}" } }
addition { identifier { stack = "{id:'minecraft:diamond'}" } }
process {
  type = "fixed_result"
  rename = {}
  result {
    choices {
      stacks = [
        { identifier { stack = "{id:'minecraft:diamond_sword'}" } }
      ]
    }
    alwaysKeepPrevious = true
    modifier {}
    actions = []
    bulkActions = []
  }
  cost = 1
}
```
