---
outline: [ 2, 3 ]
---

# Resource Source

Defines where resources are loaded from and saved to.
Each source has the following common properties:

::: info Properties
----

##### `type` <Badge type="info" text="string" />

The type of the source. There are currently two types `directory` and `sql`.

##### `overwriteExisting` <Badge type="info" text="boolean" />

Whether to overwrite existing resources with resources from this destination.

##### `propagateSavedResources` <Badge type="info" text="boolean" />

Whether resources saved to this destination should propagate to destinations of lower priority.

##### `filter` <Badge type="info" text="Filter" /> <Badge type="tip" text="optional" />

Filter to specify which resources to save to this destination.

:::

## Directory

A source that loads the resources from the defined directory.

::: info Properties
----

##### `path` <Badge type="info" text="string" />

The path to the directory that contains the resources.
Note that this directory will contain the resource type specific directories, that then contain the resources
themselves.

:::

### Example

```hocon
type = "directory"
path = "path/to/resources"
overwriteExisting = true
```

## SQL Source

Defines a source that connects to an SQL database.

::: info Properties
----

##### `connection` <Badge type="info" text="Connection" />

The connection type for the SQL database. Multiple different types are supported.

:::

```hocon
type = "sql"
connection {
  type = <database type>
  // type-specific settings
}
```

## SQL Connections <Badge type="info" text="Connection" />

All database connection types use similar properties.
CustomCrafting abstracts the specific connection string by passing the properties into the type-specific url templates.

### sqlite

Connection to an [SQLite](https://sqlite.org/index.html) database using local database files.

::: info Properties
----

##### `path` <Badge type="info" text="string" />

The path to the sqlite file

:::

```hocon
type = sqlite
path = "/path/to/database.sqlite"
```

----

### h2

Connection to an [H2](https://github.com/h2database/h2database) database using local database files.

::: info Properties
----

##### `path` <Badge type="info" text="string" />

The path to the h2 database

:::

```hocon
type = "h2"
path = "/path/to/database_file"
```

----

### Other Databases

The following database connections are supported.  
`mariadb`, `mysql`, `oracle`, `postgresql`, `mssqlserver`

The mentioned databases all use the same properties:

::: info Properties
----

##### `host` <Badge type="info" text="string" />

The host of the database with port

##### `database` <Badge type="info" text="string" />

The name of (or the path to) the database

##### `user` <Badge type="info" text="string" />

The username for the connection

##### `password` <Badge type="info" text="string" />

The password for that user

:::

#### Example: 
```hocon
type = mariadb 
host = "localhost:3306"
database = "test"
user = "user"
password = "user_pwd"
```
