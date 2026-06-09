# Smithing

Smithing recipes are used for upgrading items using a smithing table.

## Components

- **Template**: The template required to upgrade the base (e.g., a netherite upgrade template).
- **Base**: The item to be upgraded.
- **Addition**: The item used to upgrade the base.
- **Result**: The result of the smithing process.
- **Copy Options**: Optional settings for which components of the base item should be copied to the result.
    - **Preserve Components**: A list of components to keep.
    - **Exclude Components**: A list of components to remove.

## Example Structure

```hocon
type = smithing

template = null

base {
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:diamond_sword'}" } }
    ]
  }
}

addition {
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:netherite_ingot'}" } }
    ]
  }
}

copyOptions {
  preserveComponents = ["minecraft:damage"]
  excludeComponents = ["minecraft:enchantments"]
}

result {
  modifier {}
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:netherite_sword'}" } }
    ]
  }
}
```
