---
outline: [ 2, 4 ]
---

# Cooking Recipes

Cooking recipes are used for cooking operations, such as furnace recipes that transform inputs into outputs over time.

## Core Properties

##### `processing` <Badge type="info" text="object" />

The processing configuration for this recipe. Defines how the recipe is processed, including time, cost, and other
operational aspects of the cooking process.

##### `result` <Badge type="info" text="object" />

The result of this cooking recipe. Specifies the output item(s) produced when the recipe is successfully processed.
See [results](results).

##### `xp` <Badge type="info" text="int" />

The experience points (XP) awarded when this recipe is completed.

## Processing

The `processing` property defines the workstation and its specific settings.

### Blasting

Configures processing in blast furnaces.

##### `processingTime` <Badge type="info" text="int" />
The time in ticks that the processing takes.

##### `source` <Badge type="info" text="object" />
The source item [ingredient](ingredients) required for the recipe.

```hocon
processing {
  type = "blasting"
  source {identifier {stack = "{id:'minecraft:coal'}"}}
  processingTime = 200
}
```

### Smoking

Configures processing for smokers.

##### `processingTime` <Badge type="info" text="int" />
The time in ticks that the processing takes.

##### `source` <Badge type="info" text="object" />
The source item [ingredient](ingredients) required for the recipe.

```hocon
processing {
  type = "smoking"
  source {identifier {stack = "{id:'minecraft:raw_meat'}"}}
  processingTime = 300
}
```

### Smelting

Configures processing for furnaces.

##### `processingTime` <Badge type="info" text="int" />
The time in ticks that the processing takes.

##### `source` <Badge type="info" text="object" />
The source item [ingredient](ingredients) required for the recipe.

```hocon
processing {
  type = "smelting"
  source {identifier {stack = "{id:'minecraft:iron_ore'}"}}
  processingTime = 200
}
```

### Campfire

Configures processing for both normal and soul campfires.

##### `processingTime` <Badge type="info" text="int" />
The time in ticks that the processing takes.

##### `source` <Badge type="info" text="object" />
The source item [ingredient](ingredients) required for the recipe.

##### `soulCampfire` <Badge type="info" text="boolean" />
Specifies whether the processing works for soul campfires.

##### `normalCampfire` <Badge type="info" text="boolean" />
Specifies whether the processing works for normal campfires.

```hocon
processing {
  type = "campfire"
  source {identifier {stack = "{id:'minecraft:stick'}"}}
  processingTime = 600
  soulCampfire = true
  normalCampfire = false
}
```

## Example

```hocon
processing {
  type = "smelting"
  source {identifier {stack = "{id:'minecraft:iron_ore'}"}}
  processingTime = 200
}
result {
  choices {
    stacks = [
      {identifier {stack = "{id:'minecraft:iron_ingot'}"}}
    ]
  }
  alwaysKeepPrevious = true
  modifier {}
  actions = []
  bulkActions = []
}
xp = 10.0
```
