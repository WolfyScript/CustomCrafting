# Ingredients
Ingredients define what items are required to satisfy a recipe.

## Choices
The `choices` property defines what items or tags can be used to satisfy the ingredient.

## Matching
The `matching` property defines how the system checks if an item in the inventory matches the ingredient.
- **Exact**: The item must match the choices exactly.
- **Item**: The item must match the type and optionally contain or not contain certain components.

## Consumption
The `consumption` property defines how the ingredient is removed from the inventory.

### Consume
The item is removed from the inventory.

- `type = "consume"`
- `remainder`: Optional. Defines what happens to the remaining amount of the item.

#### Default Remainder
Uses standard vanilla/modded remainders.

- `type = "default"`
- `ignore`: Optional. Specifies which remainders to ignore (`vanilla` and `others` booleans).

```HOCON
// Consumption with Default Remainder and Ignoring Others
{
  consumption {
    type = "consume"
    remainder {
      type = "default"
      ignore = { vanilla = true, others = false }
    }
  }
}
```

#### Custom Remainder
Replaces remainders with a specific item.

- `type = "custom"`
- `remainder`: The item stack to use as a remainder.

```hocon
// Consumption with Custom Remainder
{
  consumption {
    type = "consume"
    remainder {
      type = "custom"
      remainder = "{id:'minecraft:iron_nugget'}"
    }
  }
}
```

### Keep

The item is not removed.

- `type = "keep"`
- `modifier`: Optional. Modifies the source item while it remains in the inventory. For now, this uses an empty list of transformations.

```hocon
// Keep with Modifier
{
  consumption {
    type = "keep"
    modifier = { transformations = [] }
  }
}
```

### Replace
Replaces the source item with a specific item regardless of the amount.

- `type = "replace"`
- `replacement`: The item stack to replace the source with.

```hocon
// Replace
{
  consumption {
    type = "replace"
    replacement = "{id:'minecraft:diamond'}"
  }
}
```
