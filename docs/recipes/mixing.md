# Mixing

Mixing recipes are used for mixing items in a cauldron.

## Requirements

- **Processing Time**: How long it takes to mix.
- **XP**: Experience awarded.
- **Results**: A list of possible results.
- **Ingredients**: A list of ingredients required.
- **Fluid Requirement**: Optional requirement for a fluid (e.g., water or lava).
    - **Lava**: Whether lava is required.
    - **Water**: Whether water is required.
    - **Level**: The required fluid level.
- **Campfire Requirement**: Optional requirement for a campfire.
    - **SoulCampfire**: Whether a soul campfire is required.
    - **NormalCampfire**: Whether a normal campfire is required.
    - **SignalFire**: Whether a signal fire is required.

## Example Structure

```hocon
type = mixing

processingTime = 100
xp = 1
results = [
    {
      choices {
        stacks = [
          { identifier { type = vanilla, stack = "{id:'minecraft:potion'}" } }
        ]
      }
    }
]
ingredients = [
    {
      choices {
        stacks = [
          { identifier { type = vanilla, stack = "{id:'minecraft:nether_wart'}" } }
        ]
      }
    }
]
fluidRequirement {
  water = true
  level = 1
}
```

