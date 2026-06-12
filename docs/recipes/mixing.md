---
outline: [2, 4]
---

# Mixing Recipes

Mixing recipes are used to mix items in the Cauldron.

## Core Properties

- `processingTime`: The time required to complete the mixing process.
- `xp`: The experience points (XP) awarded when this recipe is completed.
- `results`: A list of possible results produced by the mixing process. See [results.md](#).
- `ingredients`: A list of ingredients required for the mixing process. See [ingredients.md](#).
- `fluidRequirement`: Optional fluid requirements for the mixing process.
- `campfireRequirement`: Optional campfire requirements for the mixing process.

## Fluid Requirement

Defines the fluid requirements for the mixing process.

```hocon
fluidRequirement {
  lava = false
  water = true
  level = 10
}
```

## Campfire Requirement

Defines the campfire requirements for the mixing process.

```hocon
campfireRequirement {
  soulCampfire = false
  normalCampfire = false
  signalFire = true
}
```

## Example

```hocon
processingTime = 600
xp = 5.0
results = [
  {
    choices {
      stacks = [
        { identifier { stack = "{id:'minecraft:potion'}" } }
      ]
    }
    alwaysKeepPrevious = true
    modifier {}
    actions = []
    bulkActions = []
  }
]
ingredients = [
  { identifier { stack = "{id:'minecraft:water_bottle'}" } }
]
fluidRequirement {
  lava = false
  water = true
  level = 10
}
campfireRequirement {
  soulCampfire = false
  normalCampfire = false
  signalFire = true
}
```
