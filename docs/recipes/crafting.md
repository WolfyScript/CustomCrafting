# Crafting

Crafting recipes are used for the player inventory crafting grid, crafting tables, and auto-crafters.

## Formulas

Crafting recipes use a `formula` to define how the input ingredients are arranged. There are two types of formulas:

### Shapeless
Shapeless recipes allow ingredients to be placed in any order in the crafting grid.
- **Ingredients**: A simple list of ingredients.

### Shaped
Shaped recipes require ingredients to be placed in a specific pattern.
- **Mapped Ingredients**: A map of characters to ingredients (e.g., `A` maps to wood, `B` maps to stone).
- **Shape**: Defines the pattern using rows of characters (e.g., `["ABA", "AAA", "AAA"]`).
- **Symmetry**: Defines how the shape can be mirrored or rotated.
    - **Horizontal**: If true, the pattern is mirrored horizontally.
    - **Vertical**: If true, the pattern is mirrored vertically.
    - **Rotate**: If true, the pattern can be rotated.

## Example Structure

### Shapeless Example
```hocon
type = crafting

formula {
    type = "shapeless"
    ingredients = [
        {
          choices {
            stacks = [
              { identifier { type = vanilla, stack = "{id:'minecraft:stick'}" } }
            ]
          }
        },
        {
          choices {
            stacks = [
              { identifier { type = vanilla, stack = "{id:'minecraft:stone'}" } }
            ]
          }
        }
    ]
}
```

### Shaped Example
```hocon
type = crafting

formula {
    type = "shaped"
    mappedIngredients = {
        "A" = {
          choices {
            stacks = [
              { identifier { type = vanilla, stack = "{id:'minecraft:stone'}" } }
            ]
          }
        },
        "B" = {
          choices {
            stacks = [
              { identifier { type = vanilla, stack = "{id:'minecraft:stick'}" } }
            ]
          }
        }
    }
    shape {
        rows = ["ABA", "AAA", "AAA"]
        trim = true
        symmetry {
            horizontal = false
            vertical = false
            rotate = false
        }
    }
}
```


### Shaped Example
```hocon
formula {
    type = "shaped"
    mappedIngredients = {
        "A" = { choices = ["minecraft:stone"] },
        "B" = { choices = ["minecraft:stick"] }
    }
    shape {
        rows = ["ABA", "AAA", "AAA"]
        trim = true
        symmetry {
            horizontal = false
            vertical = false
            rotate = false
        }
    }
}
```
