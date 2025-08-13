## Fabric Mixins
Mixins must be written in Java due to the way the Mixins work on the Bytecode level.   
Mixins written in Kotlin may not work (e.g. static functions) and break with the annotations created by Kotlin at compile time.

### Injected Extension Interfaces
To prevent issues, Interfaces added to Mixin classes are written in Java too.

### Other Interfaces & Utils
These may be written in Kotlin as they do not interact with the Mixin system directly.