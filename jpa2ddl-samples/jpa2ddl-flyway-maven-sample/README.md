# Flyway with Maven sample

This project shows sample setup of the Flyway migrations based on Maven.

Existing DB schema can be found in the `src/main/resources/migrations/v1_jpa2ddl.sql` file.

It's already applied on a h2 database located in the `src/main/resources/db` directory.

There is already modified `oss.devskiller.model.User` entity which contains two additional fields (`email` and `age`).

Now we want to generate the migration script and apply it to a database.

## Schema migration with JPA2DDL and Flyway

The first step is to build the project. It can be done with the `./mvnw clean package` command. As you can see new migration file has been created: `v2_jpa2ddl.sql`. It contains alter statements adding two new fields.

We can check Flyway migrations status with command `./mvnw flyway:info`

```
+-----------+---------+-------------+------+---------------------+---------+
| Category  | Version | Description | Type | Installed On        | State   |
+-----------+---------+-------------+------+---------------------+---------+
| Versioned | 1       | jpa2ddl     | SQL  | 2018-03-14 16:43:23 | Success |
| Versioned | 2       | jpa2ddl     | SQL  |                     | Pending |
+-----------+---------+-------------+------+---------------------+---------+
```

As you can see Flyway found the new migration and it's ready to be applied. To do so just invoke `./mvnw flyway:migrate`. Flyway now migrates the database schema:

```
[INFO] Successfully validated 2 migrations (execution time 00:00.011s)
[INFO] Current version of schema "PUBLIC": 1
[INFO] Migrating schema "PUBLIC" to version 2 - jpa2ddl
[INFO] Successfully applied 1 migration to schema "PUBLIC" (execution time 00:00.027s)
```

Now we can check if everything has finished correctly by checking the info one more time:

```
+-----------+---------+-------------+------+---------------------+---------+
| Category  | Version | Description | Type | Installed On        | State   |
+-----------+---------+-------------+------+---------------------+---------+
| Versioned | 1       | jpa2ddl     | SQL  | 2018-03-14 16:43:23 | Success |
| Versioned | 2       | jpa2ddl     | SQL  | 2018-03-14 16:52:35 | Success |
+-----------+---------+-------------+------+---------------------+---------+
```