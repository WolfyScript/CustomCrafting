---
outline: [2, 4]
---

# Resource Settings

Configuration that determines how CustomCrafting loads and saves its resources.
It declares the sources from which resources (recipes, ingredients) are loaded from.
Additionally, the backup settings allow configuring custom backup destinations.

## Properties

::: info Properties
----

##### `sources` <Badge type="info" text="list<Source>" />
A sorted list of [sources](sources) from which resources are loaded, or to which resources are saved to.
Resources are loaded from the defined sources from top to bottom, each following source can override resources from previous sources.

##### `backup` <Badge type="info" text="Backup" />
Defines how and where [backups](backup) are made.

:::

```hocon
sources = [
  {                     //  |  
    // Source settings  //  | Source Load/Save order
  },                    //  |  
  //...                 //  V
]

backup {
  // Backup settings
  destinations = []
}
```
