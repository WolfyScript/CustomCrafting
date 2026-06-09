---
outline: [2, 4]
---

# Ingredients

Ingredients define what items are required to satisfy a recipe.

The `choices` is the only required property. They define what items or tags can be used to satisfy the ingredient.

```hocon
choices = [
  {
    identifier {
      stack = "{id:'minecraft:iron_nugget'}"
    }
  },
  //...
]
```

Ingredients support additional properties to customize the matching and consumption behaviour.

## Matching

The `matching` property defines how the system checks if an item in the inventory matches the ingredient.

- **Exact**: The item must match the choices exactly.
- **Item**: The item must match the type and optionally contain or not contain certain components.

```hocon
matching {
  type = "item"
}
```

## Consumption

The `consumption` property defines how the ingredient is removed from the inventory.  
There are multiple types with different behaviours that are set via the `type` property.

### Consume 
> `type = "consume"`  

Removes the item from the inventory.
If the remains cannot be stored in the ingredient slot, then it either stores remains in the inventory or drops them on the ground.

`remainder`: Optional. Defines what happens to the remaining amount of the item.

```hocon
consumption {
  type = "consume"
  remainder {
    // See types below    
  }
}
```

#### Default Remainder
> `type = "default"`

Uses the vanilla remainders or modded/plugin remainders, if available and not ignored.

`ignore`: Specifies which remainders should be ignored.

```hocon
remainder {
  type = "custom"
  ignore = {
    vanilla = true
    others = true
  }
}
```

#### Custom Remainder
> `type = "custom"`

Uses a custom remainder and replaces the existing remainders, if not ignored.

`ignore`: Specifies which remainders should be ignored. The custom remainder will replace those that are **not** ignored.  
`remainder`: The item stack to use as a remainder.

```hocon
remainder {
  type = "custom"
  remainder = "{id:'minecraft:iron_nugget'}"
  ignore {
    vanilla = true
    others = true
  }
}
```

### Keep
> `type = "keep"`

The item is not removed.

`modifier`: Optional. Modifies the source item while it remains in the inventory. For now, this uses an empty list of
  transformations.

```hocon
consumption {
  type = "keep"
  modifier = {
    transformations = []
  }
}
```

### Replace
> `type = "replace"`

Replaces the source item with a specific item regardless of the amount.

`replacement`: The item stack to replace the source with.

```hocon
consumption {
  type = "replace"
  replacement = "{id:'minecraft:diamond'}"
}
```
