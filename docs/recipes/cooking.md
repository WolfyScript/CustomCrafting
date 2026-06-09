# Cooking

Cooking recipes are used for transformations that happen over time, such as in a furnace or smoker.

## Processing

The `processing` property defines how the recipe is handled by a workstation. It includes the source ingredient (e.g., coal, wood) and the time it takes to complete.

## Workstation Types

The `type` within `processing` determines which workstation can use this recipe:

- **Blasting**: For blast furnaces.
- **Smoking**: For smokers.
- **Smelting**: For standard furnaces.
- **Campfire**: For campfires.
    - **SoulCampfire**: Whether it works on a soul campfire.
    - **NormalCampfire**: Whether it works on a normal campfire.

## Experience Points (XP)

The `xp` property determines how much experience is awarded to the player upon successful completion of the cooking process.

## Example Structure

```hocon
type = cooking

result {
  modifier {}
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'leather'}" } }
    ]
  }
  actions = [
    {
      type = "command"
      commands = ["effect give @p[distance=..4] minecraft:nausea 20 2"]
    }
  ]
}

processing {
  type = "smelting"
  processingTime = 100
  source {
    choices {
      stacks = [
        { identifier { type = vanilla, stack = "{id:'rotten_flesh'}" } }
      ]
    }
  }
}
xp = 1
```

