# Results and Modifiers

The `result` property in every recipe defines what the player gets after the recipe is successful.

## Recipe Choices

The `choices` property defines what items or tags can be produced.
- **Stacks**: A list of specific items.
- **Tags**: A list of tags. The system will pick an item from one of the matching tags.

## Modifiers

The `modifier` property allows you to apply transformations to the resulting item.
- **Transformations**: A list of transformations applied to the item.
- Examples include renaming, adding attributes, or changing other components.

## Actions

Actions are things that happen when the result is collected. The system supports various types of actions to allow for custom behavior during the crafting process.

- **Actions**: Performed when a single result is collected.
- **Bulk Actions**: Performed when multiple results are collected (e.g., shift-clicking).

| Action Type | Description | Status |
|-------------|-------------|--------|
| `command`   | Executes arbitrary commands when a result is collected. | Implemented |
| Others      | Future action types. | WIP |

### Command Action

The `command` action allows you to execute one or more Minecraft commands when a recipe result is collected.

- **type**: Must be set to `"command"`.
- **commands**: A list of strings representing the commands to execute. These are executed in the context of the player or the workstation where the recipe was completed.

### Example
```hocon
{
  type = "command"
  commands = [
    "say Recipe completed!",
    "give @p minecraft:firework_rocket 1"
  ]
}
```

## Always Keep Previous

- **alwaysKeepPrevious**: If true, the system will keep the same result until it is collected. This prevents the result from "rerolling" every time the player looks at the result slot.

## Example Structure

```hocon
result {
  modifier {}
  choices {
    stacks = [
      { identifier { type = vanilla, stack = "{id:'minecraft:diamond_sword'}" } }
    ]
  }
  actions = [
    {
      type = "command"
      commands = ["say Recipe completed!"]
    }
  ]
  alwaysKeepPrevious = true
}
```
