---
outline: [2, 4]
---

# Stonecutting Recipes

Stonecutting recipes are used to process a `source` ingredient into a result.

## Core Properties

- `source`: The input ingredient required for this stonecutting recipe.
- `result`: The result produced by this stonecutting recipe, encompassing output choices, modifiers, and actions. See [results.md](#).
- `flattenResult`: Creates a proxy recipe (vanilla recipe) for each result item. Actions and other result settings apply to all of those recipes.

## Example

```hocon
source { identifier { stack = "{id:'minecraft:stone'}" } }
result {
  choices {
    stacks = [
      { identifier { stack = "{id:'minecraft:stone_axe'}" } }
    ]
  }
  alwaysKeepPrevious = true
  modifier {}
  actions = []
  bulkActions = []
}
flattenResult = true
```
