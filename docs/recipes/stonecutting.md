---
outline: [ 2, 4 ]
---

# Stonecutting Recipes

Stonecutting recipes are used to process a `source` ingredient into a result.

::: info Properties
----

##### `source` [<Badge type="info" text="Ingredient" />](ingredients)

The input ingredient required for this stonecutting recipe.

##### `result` [<Badge type="info" text="Result" />](results)

The result produced by this stonecutting recipe, encompassing output choices, modifiers, and actions.

##### `flattenResult` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

Creates a proxy recipe (vanilla recipe) for each result item. Actions and other result settings apply to all of those recipes.

:::

```hocon
type = stonecutting

source {
  // Ingredient settings
}

result {
  // Result settings
}

flattenResult = true
```
