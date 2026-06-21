# Recipes
Recipe configuration files are stored in the directory:   
`<root>/resources/<souce_dir>/recipes/[<sub_dir>]` 

Each recipe consists of the common properties:

::: info Properties
----

##### `type` <Badge type="info" text="string" />

Defines the type-specific properties to be used.

There are several different types of recipes:
* [Crafting](crafting)
* [Cooking](cooking)
* [Grinding](grinding)
* [Mixing](mixing)
* [Repairing](repairing)
* [Smithing](smithing)
* [Stonecutting](stonecutting)

##### `group` <Badge type="info" text="String" /> <Badge type="tip" text="optional" />

The group of the recipe. Recipes with the same group are considered to be variants of each other.

##### `conditions` <Badge type="info" text="Conditions" /> <Badge type="tip" text="optional" />

The conditions that must be met for the recipe to work.

:::

```hocon
type = <recipe_type>

priority = 1

conditions {
  // Condition settings
}

// ... Type specific settings ...
```