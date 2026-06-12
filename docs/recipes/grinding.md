---
outline: [2, 4]
---

# Grinding Recipes

Grinding recipes involve using a grinding tool (like a grindstone) to process a `base` item, optionally with an `addition`.

## Core Properties

- `base`: The base ingredient for this grinding recipe.
- `addition`: An optional additional ingredient for this grinding recipe.
- `process`: The grinding process for this recipe.

## Process

The `process` property defines how the grinding process is computed.

### Fixed Result
A process that always returns the specified `result` and `xp`.

```hocon
process {
  type = "fixed_result"
  result {
    choices {
      stacks = [
        { identifier { stack = "{id:'minecraft:flint'}" } }
      ]
    }
    alwaysKeepPrevious = true
    modifier {}
    actions = []
    bulkActions = []
  }
  xp = 1
}
```

### Default Process
A process that follows default grinding logic with various customization options.

```hocon
process {
  type = "default"
  extraXp = 2
  removeEnchants = {
    // See ProcedureEnchantRemoval documentation
    type = "..."
  }
  mergeEnchants = {
    // See ProcedureEnchanting documentation
    type = "..."
  }
  damageCombine = {
    // See ProcedureDamageCombine documentation
    type = "..."
  }
  repairCost = {
    // See ProcedureRepairCost documentation
    type = "..."
  }
}
```

## Example

```hocon
base { identifier { stack = "{id:'minecraft:flint'}" } }
addition { identifier { stack = "{id:'minecraft:stick'}" } }
process {
  type = "fixed_result"
  result {
    choices {
      stacks = [
        { identifier { stack = "{id:'minecraft:flint'}" } }
      ]
    }
    alwaysKeepPrevious = true
    modifier {}
    actions = []
    bulkActions = []
  }
  xp = 1
}
```
