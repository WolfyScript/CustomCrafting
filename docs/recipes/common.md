# Common Properties

These properties are present in almost every recipe and are used to define basic behavior.

## Type
The `type` property identifies the category of the recipe. This tells the system which evaluation logic to use.

## Priority
`priority` is an integer. If the system finds multiple recipes that match your input, it will choose the one with the highest priority.

## Conditions
`conditions` is a list of requirements that must be satisfied for a recipe to be considered. 

**Note: Conditions are currently under development (WIP) and no condition types are implemented yet.**

## Group
`group` is a string identifier. If you have multiple recipes that produce different results from the same input, you can put them in the same group. The system will treat them as variants.

## Recipe Structure Example
Every recipe file starts with a `type` field at the root.

```hocon
type = cooking

group = "food_processing"
priority = 10
```
