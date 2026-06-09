# Introduction

This wiki explains how to create custom recipes using `.conf` files in the CustomCrafting system. Every recipe in CustomCrafting follows a structured format that defines its behavior, input requirements, and output results.

## Core Concepts

A recipe is essentially a rule that says: "Given these inputs and these conditions, produce this output."

The `.conf` files for recipes map directly to Kotlin interfaces. This means that the structure of the configuration file follows the structure of the code.

### Key Components of a Recipe

Every recipe contains these base properties:

- **Type**: Defines what kind of recipe it is (e.g., Crafting, Cooking, Smithing).
- **Priority**: Determines which recipe is selected if multiple recipes match the input. Higher numbers have higher priority.
- **Conditions**: A list of requirements that must be met before the recipe can be evaluated (Currently WIP - no condition types implemented yet).
- **Group**: A way to organize recipes. Recipes in the same group are considered variants of each other.
- **Result**: What the recipe produces, including any modifications or actions.
