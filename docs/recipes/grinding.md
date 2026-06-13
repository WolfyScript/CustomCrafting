---
outline: [ 2, 4 ]
---

# Grinding Recipes

Grinding recipes involve using a grinding tool (like a grindstone) to process a `base` item, optionally with an
`addition`.

::: info Properties
----

##### `base` <Badge type="info" text="Ingredient" />

The base ingredient for this grinding recipe.

##### `addition` <Badge type="info" text="Ingredient" />

An optional additional ingredient for this grinding recipe.

##### `process` <Badge type="info" text="Process" />

The grinding process for this recipe.

:::

```hocon
type = cooking
priority = 10
group = "group_name"
conditions {
  // Condition settings
}

base {
  // Ingredient settings
}

addition {
  // Ingredient settings
}

process {
  // Process settings
}
```

## Process <Badge type="info" text="Process" />

The `process` property defines how the grinding process is computed.

### Fixed Result <Badge type="info" text="fixed_result" />

A process that always returns the specified `result` and `xp`.

::: info Properties
----

##### `result` [<Badge type="info" text="Result" />](results)

The result of the grinding process.

##### `xp` <Badge type="info" text="int" />

The experience points (XP) awarded when this recipe is completed.

:::

```hocon
type = fixed_result
result {
  // Result settings
}
xp = 1
```

### Default Process <Badge type="info" text="default" />

A process that follows default grinding logic with various customization options.

::: info Properties
----

##### `extraXp` <Badge type="info" text="int" />

Extra experience points awarded.

##### `removeEnchants` <Badge type="info" text="ProcedureEnchantRemoval" />

Defines how enchants are removed.

##### `mergeEnchants` <Badge type="info" text="ProcedureEnchanting" />

Defines how enchants are merged.

##### `damageCombine` <Badge type="info" text="ProcedureDamageCombine" />

Defines how damage is combined.

##### `repairCost` <Badge type="info" text="ProcedureRepairCost" />

Defines how repair cost is computed.

:::

```hocon
type = default
extraXp = 2
removeEnchants {
  // See ProcedureEnchantRemoval documentation
  type = "..."
}
mergeEnchants {
  // See ProcedureEnchanting documentation
  type = "..."
}
damageCombine {
  // See ProcedureDamageCombine documentation
  type = "..."
}
repairCost {
  // See ProcedureRepairCost documentation
  type = "..."
}
```
