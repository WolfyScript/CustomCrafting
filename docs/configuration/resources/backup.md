---
outline: [ 2, 4 ]
---

# Backup

Backup settings determine how resources are backed up.

## Properties

::: info Properties
----

##### `destinations` <Badge type="info" text="list<Destination>" />

A list of `BackupDestinationSettings` defining where backups are stored.

:::


```hocon
destinations = [
  {
    // Destination settings
  },
  // ...
]

```


## Destinations <Badge type="info" text="Destination" />

### Directory

Creates a directory or zip file for each new backup.

::: info Properties
----

##### `path` <Badge type="info" text="string" />

The path to the zip file or directory

##### `keep` <Badge type="info" text="int" />

The number of backups to keep

##### `compress` <Badge type="info" text="boolean" />

Whether the backup should be compressed into a zip file or saved as a directory.

:::

```hocon
type = directory
path = "backups/recipes"
keep = 5
compress = true
```

###  

