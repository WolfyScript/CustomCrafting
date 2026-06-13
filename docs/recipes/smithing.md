---
outline: [ 2, 3 ]
---

# Smithing Recipes

Smithing recipes are used to upgrade the `base` item with the `addition` item using a `template`.

::: info Properties
----

##### `template` [<Badge type="info" text="Ingredient" />](ingredients)

The template required to upgrade the `base`.

##### `base` [<Badge type="info" text="Ingredient" />](ingredients)

The base item to upgrade with the `addition`.

##### `addition` [<Badge type="info" text="Ingredient" />](ingredients)

The addition with which to upgrade the `base`.

##### `result` [<Badge type="info" text="Result" />](results)

The result of the smithing recipe, including the modifiers to apply and actions to be performed.

##### `copyOptions` [<Badge type="info" text="CopyOptions" />](#copy-options) <Badge type="tip" text="optional" />

Optional options specifying how ItemStack components are copied from the base into the result.

:::

```hocon
type = smithing

template {
  // Ingredient settings
}

base {
  // Ingredient settings
}

addition {
  // Ingredient settings
}

result {
  // Result settings
}

copyOptions {
  // See Copy Options
}
```

## Copy Options <Badge type="info" text="CopyOptions" />

Defines which components are preserved or excluded when copying from the base to the result.

::: info Properties
----

##### `preserveComponents` <Badge type="info" text="list<Key>" />

The components to preserve from the base item.

##### `excludeComponents` <Badge type="info" text="list<Key>" />

The components to exclude from the base item.

:::

```hocon
copyOptions {
  preserveComponents = ["minecraft:custom_data"]
  excludeComponents = ["minecraft:display_name"]
}
```
