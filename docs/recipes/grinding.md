# Grinding

Grinding recipes are used for operations like using a grindstone.

## Base and Addition

- **Base**: The main item being ground.
- **Addition**: An optional second item that can be included in the grinding process.

## Process

The `process` property defines how the grinding is calculated. There are two main types:

### Fixed Result
Always produces the same result and awards a fixed amount of XP.

### Default
Uses a more complex calculation that includes:
- **Extra XP**: Additional experience gained.
- **Remove Enchants**: Defines how enchantments are removed from the ingredients.
- **Merge Enchants**: Defines how remaining enchantments are combined.
- **Damage Combine**: Defines how the durability of the ingredients is combined.
- **Repair Cost**: Optional cost applied to the result.

## Example Structure (Default Process)

```hocon
type = grinding

base {
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:iron_ingot'}" } }
    ]
  }
  matching {
    type = "item"
  }
}

addition {
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:iron_ingot'}" } }
    ]
  }
  matching {
    type = "item"
  }
}

process {
  type = "default"
  extraXp = 2
  removeEnchants {
    baseEnchants {
      removeCurses = true
      enchants = ["minecraft:fire_aspect"]
      type = "REMOVE"
    }
  }
}
```

