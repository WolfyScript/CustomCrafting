---
outline: [ 2, 3 ]
---

# Mixing Recipes

Mixing recipes are used to mix items in the Cauldron.

::: info Properties
----

##### `processingTime` <Badge type="info" text="int" />

The time required to complete the mixing process.

##### `xp` <Badge type="info" text="float" />

The experience points (XP) awarded when this recipe is completed.

##### `results` [<Badge type="info" text="list<Result>" />](results)

A list of possible results produced by the mixing process.

##### `ingredients` [<Badge type="info" text="list<Ingredient>" />](#ingredients)

A list of ingredients required for the mixing process.

##### `fluidRequirement` <Badge type="info" text="FluidRequirement" /> <Badge type="tip" text="optional" />

Optional fluid requirements for the mixing process.

##### `campfireRequirement` <Badge type="info" text="CampfireRequirement" /> <Badge type="tip" text="optional" />

Optional campfire requirements for the mixing process.

:::

```hocon
xp = 2

ingredients = [
  {
    // Ingredient settings
  },
  // ... more ingredients ...
]

results = [
  {
    // Result settings
  },
  // ... more results ...
]

fluidRequirement { }

campfireRequirement { }
```

## Fluid Requirement <Badge type="info" text="FluidRequirement" />

Defines the fluid requirements for the mixing process.

::: info Properties
----

##### `lava` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

Whether lava is required.

##### `water` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

Whether water is required.

##### `level` <Badge type="info" text="int" /> <Badge type="tip" text="optional" />

The required fluid level.

:::

```hocon
lava = false
water = true
level = 10
```

## Campfire Requirement <Badge type="info" text="CampfireRequirement" />

Defines the campfire requirements for the mixing process.

::: info Properties
----

##### `soulCampfire` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

Whether a soul campfire is required.

##### `normalCampfire` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

Whether a normal campfire is required.

##### `signalFire` <Badge type="info" text="boolean" /> <Badge type="tip" text="optional" />

Whether a signal fire is required.

:::

```hocon
soulCampfire = false
normalCampfire = false
signalFire = true
```

## Example

```hocon
processingTime = 600
xp = 5.0
results = [
  {
    choices {
      stacks = [
        {identifier {stack = "{id:'minecraft:potion'}"}}
      ]
    }
    alwaysKeepPrevious = true
    modifier {}
    actions = []
    bulkActions = []
  }
]
ingredients = [
  {identifier {stack = "{id:'minecraft:water_bottle'}"}}
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
