# Stonecutting

Stonecutting recipes are used for the stonecutter block.

## Components

- **Source**: The input ingredient required for the stonecutting recipe.
- **Result**: The result produced by the recipe.
- **Flatten Result**: If true, the system creates a separate button for each result item (vanilla behavior). If false, it groups them.

## Example Structure

```hocon
type = stonecutting

source {
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:stone'}" } }
    ]
  }
}

result {
  modifier {}
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:cobblestone'}" } }
    ]
  }
}

flattenResult = true
```
