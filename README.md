# Cuery - A Streaming API for SQLiteDatabase
Exhausted by SQL concatenations,
I developed this simple util to simplify my data-accessing job.
## Features
1. Streaming API.
2. Use `SQLiteStatement` to query, which means a higher performance.
3. Synchronous and asynchronous query.

## Useage
Before executing an `Query`,
you have to open an database by yourself.
```java
        SQLiteDatabase db;
```

SELECT:
The query below would be simply compiled to
"SELECT username,password FROM User WHERE username=?,password=?",
and the value of username and password would be bind as the arguments of `SQLiteStatment`.
```java


        Query query = new Query();
        ResultSet rs = query.select("username", "password")
                .table("User")
                .startWhere()
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .endWhere()
                .execute(db);
```

INSERT:
The query below would be simply complied to
"INSERT INTO User (username,password) VALUES (?,?)",
and the value of username and password would be bind as the arguments of `SQLiteStatment`.
```java
        Query query = new Query();
        ResultSet rs = query.insert()
                .table("User")
                .columns("username", "password")
                .values(username, password)
                .execute(db);
```

UPDATE:
The query below would be simply complied to
"UPDATE User SET password=?",
and the value of newPassword would be bind as the arguments of `SQLiteStatment`.
```java
        Query query = new Query();
        ResultSet rs = query.update()
                .table("User")
                .set("password", newPassword)
                .startWhere()
                .whereEqualTo("username", username)
                .endWhere()
                .execute(db);
```

DELETE:
The query below would be simply complied to
"DELETE FROM User WHERE username=?,password=?",
and the value of username and password would be bind as the arguments of `SQLiteStatment`.
```java
        Query query = new Query();
        ResultSet rs = query.delete()
                .table("User")
                .startWhere()
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .endWhere()
                .execute(db);
```

`ResultSet` holds the result of an Query.

When you use a SELECT Query, `ResultSet.getCursor()` returns the
Cursor for the Query.

When using a INSERT Query, `ResultSet.getRowId()` returns the
the row ID of the newly inserted row.

When using a UPDATE or DELETE Query, `ResultSet.getRowAffected()`
returns the number of rows that affected by the query.

You could use **asynchronous** query simply by `executeAsync(db, callback)`.
```java
        Query query = new Query();
        query.select("username", "password")
                .table("User")
                .startWhere()
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .endWhere()
                .executeAsync(db, new Executor.Callback() {
                     @Override
                     public void onResult(ResultSet rs) {
                        // To use the ResultSet
                     }
                });
```
