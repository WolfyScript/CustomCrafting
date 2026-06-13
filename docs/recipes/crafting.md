# Crafting Recipes

Crafting recipes are used for the player inventory crafting grid, crafting table, and auto-crafter.

::: info Properties
----

##### `result` [<Badge type="info" text="Result" />](results)

Defines the resulting item that this recipe produces.

##### `formula` [<Badge type="info" text="Formula" />](#formula-1)

Defines the crafting recipe evaluation logic.

:::

```hocon
type = crafting
priority = 10
group = "group_name"
conditions {
  // Condition settings
}
formula {
  // Formula settings
}
result {
  // Result settings
}
```

## Formula <Badge type="info" text="Formula" />

The `formula` defines the crafting logic used to evaluate the items in the grid.

### Shapeless <Badge type="info" text="type = shapeless" />

A crafting formula with a list of ingredients that can be arranged in any order.

::: info Properties

##### `ingredients` <Badge type="info" text="list<Ingredient>" />

A list of ingredients required for the recipe. See [Ingredient](ingredients) documentation.

:::

```hocon
type = shapeless
ingredients = [
  {
    // Ingredient settings
  },
  // ...
]
```

### Shaped <Badge type="info" text="type = shaped" />

A crafting formula that requires ingredients to be arranged in a specified shape.
Ingredients are mapped to characters in the shape.

::: info Properties
----

##### `mappedIngredients` <Badge type="info" text="Map<char, Ingredient>" />

A map of character identifiers to [Ingredients](ingredients)

##### `shape` [<Badge type="info" text="Shape" />](#shape-1)

The shape of the recipe.

:::

```hocon
type = shaped
mappedIngredients = {
  I: {
    // Ingredient settings
  },
  // ...
}
shape {
  // Shape settings
}
```

## Shape <Badge type="info" text="Shape" />

The layout and symmetry settings for a shaped recipe.

::: info Properties
----

##### `rows` <Badge type="info" text="list<String>" />

The rows of the recipe shape.

##### `symmetry` [<Badge type="info" text="Symmetry" />](#shape-symmetry) <Badge type="tip" text="optional" />

Symmetry settings for the shape.

##### `trim` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

Whether the shape should be trimmed.

This removes any trailing empty columns and rows from the shape.
Which allows shapes that only take up a fraction of the grid to be placed in different places.  
For example, a 2x2 recipe may be placed in each of the corners.

When disabled items must be placed exactly where they are in the shape including empty slots.

:::

```hocon
rows = [
  "ISI",
  " S ",
  " S "
]
symmetry {
  // Shape symmetry settings
}
trim = true
```

## Shape Symmetry <Badge type="info" text="Symmetry" />

Symmetry settings for the shape.

::: info Properties
----

##### `horizontal` <Badge type="tip" text="optional" />

Whether horizontal symmetry is applied.

##### `vertical` <Badge type="tip" text="optional" />

Whether vertical symmetry is applied.

##### `rotate` <Badge type="tip" text="optional" />

Whether rotation symmetry is applied.  
This allows the recipe to be rotated within the grid i.e. `horizontal` **and** `vertical` symmetry combined, equals rotated.  
When disable it must either be `horizontal` **or** `vertical` symmetry. 

:::

```hocon
horizontal = false
vertical = false
rotate = false
```
