---
outline: [ 2, 3 ]
---

# Ingredients

Ingredients define what items are required to satisfy a recipe.

::: info Properties
----

##### `choices` <Badge type="info" text="list<RecipeChoice>" />

The only required property. They define what items or tags can be used to satisfy the ingredient.

##### `matching` [<Badge type="info" text="Matcher" />](#matchers) <Badge type="tip" text="optional" />

Defines how the system checks if an item in the inventory matches the ingredient.  
See [Matchers](#matchers)

##### `consumption` [<Badge type="info" text="Consumer" />](#consumers) <Badge type="tip" text="optional" />

Defines how the ingredient is removed from the inventory.  
See [Consumers](#consumers)

:::

```hocon
choices = [
  {
    identifier {
      stack = "{id:'minecraft:iron_nugget'}"
    }
  },
  //...
]
matching {
  // Matcher settings
}
consumption {
  // Consumer settings
}
```

## Matchers <Badge type="info" text="Matcher" />

The matchers handle how the system checks if an item in the inventory matches the ingredient.

### Item <Badge type="info" text="type = item" />

The item must match the type and optionally contain or not contain certain components.

::: info Properties
-----

##### `mustContain` <Badge type="info" text="list<Key>" /> <Badge type="tip" text="optional" />

The data components the stack must contain to pass the check.

##### `mustNotContain` <Badge type="info" text="list<Key>" /> <Badge type="tip" text="optional" />

The data components the stack must **not** contain to pass the check.

:::

```hocon
// Example: Item must match the type and must have the item name component
type = item
mustContain = [
  "minecraft:item_name"
]
```

### Exact <Badge type="info" text="type = exact" />

The item must match the choices exactly.

::: info Properties
-----
This type has no properties
:::

```hocon
type = exact
```

## Consumers <Badge type="info" text="Consumer" />

Consumers handle how the ingredient is removed from the inventory.  
There are multiple types with different behaviours that are set via the `type` property.

### Consume <Badge type="info" text="type = consume" />

Removes the item from the inventory.
If the remains cannot be stored in the ingredient slot, then it either stores remains in the inventory or drops them on
the ground.

::: info Properties
-----

##### `remainder` [<Badge type="info" text="Remainder" />](#remainders) <Badge type="tip" text="optional" />

Defines how the remains of the items are determined.

:::

```hocon
type = consume
remainder {
  // Remainder type-specific settings
}
```

### Keep <Badge type="info" text="type = keep" />

The item in the ingredient slot is not removed and kept as is.

::: info Properties
-----

##### `modifier` <Badge type="tip" text="optional" />

Modifies the source item while it remains in the inventory. There are currently no modifier implementations. For now, this uses an empty list of transformations.

:::

```hocon
type = keep
modifier = {
  transformations = []
}
```

### Replace <Badge type="info" text="type = replace" />

Replaces the source item with a specific item regardless of the amount.

::: info Properties
-----

##### `replacement` <Badge type="info" text="ItemStackRef" />

The item stack that will replace the source stack in the slot.

:::

```hocon
type = replace
replacement {
  identifier {
    type = vanilla
    stack = "{id:'minecraft:diamond'}"
  }
} 
```

## Remainders <Badge type="info" text="Remainder" />

### Default <Badge type="info" text="type = default" />

Uses the vanilla remainders or modded/plugin remainders, if available and not ignored.

::: info Properties
-----

##### `ignore` <Badge type="info" text="IgnoreOptions" />

Specifies which remainders should be ignored.

:::

```hocon
type = default
ignore = {
  vanilla = true
  others = true
}
```

### Custom <Badge type="info" text="type = custom" />

Uses a custom remainder and replaces the existing remainders, unless they are ignored.

::: info Properties
-----

##### `ignore` <Badge type="info" text="IgnoreOptions" />

Specifies which remainders should be ignored and should not be replaced. The custom remainder will replace those that are **not** ignored.

##### `remainder` <Badge type="info" text="ItemStackRef" />

The item stack to use as a remainder.

:::

```hocon
type = custom
remainder {
  identifier {
    type = vanilla
    stack = "{id:'minecraft:iron_nugget'}"
  }
}
ignore {
  vanilla = true
  others = true
}
```
