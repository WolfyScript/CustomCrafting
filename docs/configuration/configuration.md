# Configuration

Configuration in CustomCrafting is done using HOCON files with the `.conf` file suffix.

::: warning

CustomCrafting is still in alpha, so the properties and structure of configuration files
will most likely change without backwards compatibility.

:::

## What is HOCON?

HOCON (Human-Optimized Config Object Notation) is a human-friendly configuration format.  
It is a superset of JSON, meaning any valid JSON is also valid HOCON, but HOCON provides additional features such as:

- **Comments**: Use `//` or `#` to add comments.
- **Omitting Quotes**: Keys and string values often don't require quotes.
- **Omitting root braces**: No need for {} around the root object
- **Omitting commas**: in many places newlines can be used instead
- **Allow trailing commas** after the last elements in objects and arrays
- and more

These make editing configs a lot easier and less prone to errors, especially for those unfamiliar with the strict JSON syntax.

For more detailed information and syntax guides, visit the [official HOCON website](https://github.com/lightbend/config/tree/main#using-hocon-the-json-superset).

::: warning HOCON `include` statement
CustomCrafting does **not** support the HOCON `include` statement!
While that is more of a technical limitation at this time, it also is a valid security concern, so
implementing it in the future is **not a priority** and would require thorough research if ever considered.
:::

## Configuration Structure

* `<root>` - The root dir. Actual location depends on the platform.  
   **Spigot/Paper**: `plugins/customcrafting`   
   **Fabric**: `config/customcrafting`

    * `.data` - Data that is cached and used internally by CC

    * `config` - Customisable configuration files
  
      * `resources` - Configuration files for resources
    
    * `resources` - Default location for resources
  
        * `defaults` - Includes the default recipes shipped with CC
      
        * `*/**` - Any directories created the user from which to load custom recipes
      
          * `recipes` - A resource type directory containing recipe config files
          * `ingredients` - A resource type directory containing ingredient config files
