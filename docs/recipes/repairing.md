# Repairing

Repairing recipes are used for repairing or enchanting items in an anvil.

## Inputs

- **Base**: The item to be repaired or enchanted (the first slot in the anvil).
- **Addition**: The item sacrificed for the repair or enchantment (the second slot in the anvil).

## Process

The `process` property defines how the result is produced. There are two types:

### Fixed Result
Always produces a specific result.
- **Rename**: Optional renaming of the result.
- **Result**: The resulting item.
- **Cost**: The repair cost.

### Custom Process
Mirrors vanilla anvil logic with customization:
- **Rename**: Optional renaming of the result.
- **Damage Combine**: How the durability of the base is repaired when both items are damageable.
- **Item Repair**: How the base is repaired when the addition is a non-damageable item.
- **Enchanting**: How the enchantments from both items are combined.

## Example Structure (Custom Process)

```hocon
type = repairing

base {
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:iron_sword'}" } }
    ]
  }
}

addition {
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:iron_ingot'}" } }
    ]
  }
}

process {
  type = "custom"
  damageCombine { type = "combine_durability" }
  enchanting { type = "merge_all" }
}
```
