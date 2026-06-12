---
outline: [2, 4]
---

# Smithing Recipes

Smithing recipes are used to upgrade the `base` item with the `addition` item using a `template`.

## Core Properties

- `template`: The template required to upgrade the `base`. See [ingredients.md](#).
- `base`: The base item to upgrade with the `addition`.
- `addition`: The addition with which to upgrade the `base`.
- `result`: The result of the smithing recipe, including the modifiers to apply and actions to be performed. See [results.md](#).
- `copyOptions`: Optional options specifying how ItemStack components are copied from the base into the result.

## Copy Options

Defines which components are preserved or excluded when copying from the base to the result.

```hocon
copyOptions {
  preserveComponents = ["minecraft:custom_data"]
  excludeComponents = ["minecraft:display_name"]
}
```

## Example

```hocon
template { identifier { stack = "{id:'minecraft:netherite_upgrade_smithing_template'}" } }
base { identifier { stack = "{id:'minecraft:netherite_scabbard'}" } }
addition { identifier { stack = "{id:'minecraft:netherite_ingot'}" } }
result {
  choices {
    stacks = [
      { identifier { stack = "{id:'minecraft:netherite_sword'}" } }
    ]
  }
  alwaysKeepPrevious = true
  modifier {}
  actions = []
  bulkActions = []
}
copyOptions {
  preserveComponents = ["minecraft:custom_data"]
  excludeComponents = ["minecraft:display_name"]
}
```
