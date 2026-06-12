---
outline: [2, 4]
---

# Configuration

Configuration in CustomCrafting is handled using HOCON files located in the `config` directory of your installation.

## Configuration Structure

- **resources**: Settings for resource loading and saving.
- **mechanics**: Settings for game mechanics (e.g., crafting, cooking).

## Example Configuration
A typical `resources.conf` file:
```hocon
sources = [
  {
    type = "directory"
    path = "mods/custom_recipes/data"
    overwriteExisting = true
    propagateSavedResources = true
  }
]

backup {
  destinations = [
    {
      type = "directory"
      path = "backups/recipes"
      keep = 5
      compress = true
    }
  ]
}
```
