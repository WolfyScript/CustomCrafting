---
outline: [2, 4]
---

# Repairing Recipes

Repairing recipes are used to repair items in the Anvil.

## Core Properties

- `base`: The base ingredient, the first slot in the Anvil menu (the item to repair/enchant).
- `addition`: The addition ingredient, the second slot in the Anvil menu (the item to sacrifice for repair/enchanting).
- `process`: The process to produce the result in the Anvil menu.

## Process

The `process` property defines how the repairing process is handled.

### Fixed Result
Always uses the specified `result` and computes the resulting stack based on the data and context.

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

### Custom Process
Tries to mirror the vanilla logic of the anvil as much as possible, while providing lots of customization options.

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
  enchanting = {
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
