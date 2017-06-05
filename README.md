# Harvey

*...the harvester :)*

**under development**



## Goal / Use case

In work I often have the task of searching for the same input on similar websites. Usually I create an application which **reads an input file (e.g. TSV)** and crawls the relevant parts of the site. The input files have almost the same columns and the applications are very similar too.

*Harvey* aims to reduce the work when I need to implement a new crawler similar to the previous ones. It provides an **abstract application structure** which has the necessary default, common behaviour but at the same time it's flexible enough.

It can **save each task's state:** when it was processed, with which version of your crawler implementation. Also *Harvey* can skip automatically those tasks which were already processed by the current version.

So it's basically a **flexible batch framework** with some extra functions. Also it aims to fit **>>my<<** needs. :-)



## How it works

The magic happens in `HarveyApplication` abstract class, look at `run()` method first. When you implement your application by extending this class, you must call `run()` in your main method to start the predefined mechanism. Let's see what it calls:

```text
run()

    // Phase I.
    // No logging, no database, just a `configuration` property to be set up
    
    loadPropertiesFile()                  // Loads configuration from file (not implemented)
    parseArguments()                      // Loads configuration from args
    validateConfiguration()               // Validates configuration
    handleParameterException()            // Prints out error and usage if needed
    setupLogging()                        // Sets up logger framework
    
    // Phase II.
    // Logging's on, `database` and `dao` field is set up after first call
    
    database = initDatabaseConnection()   // Sets up MySQL connection (OrmLiteDatabase)
    dao = initDao()                       // Instantiates `dao`
    if (canStartWUI()) startWUI()         // Starts WUI if possible (not implemented)
    createDatabaseTables()                // Creates system tables and child application's
        tablesToBeCreated()               //     tables returned by this abstract method
    if (canImportTasks()) importTasks()   // Imports tasks if got filename
        rawTaskIterator()                 // Reads Maps from a TSV file
        generateTaskRecord(Map)           // Generates a Task object
        dao.storeTask(Task)               // Saves Task record
        generateBatchRecord(String, Task) // Generates a Batch object
        dao.storeBatch(Batch)             // Saves Batch record
    processTasks()
        dao.tasksOfBatch(String)          // Queries all tasks of given batch
        alreadyProcessed(Task)            // Tasks that already processed will be skipped
        process(Task)                     // The abstract method which does what you want
            saveState(Task, Any, Boolean) //     You can call this to save a task's state
```

All of the above methods are declared as `protected open` functions so you can freely override them in your implementation. Methods of `HarveyApplication` have access to `database` and `dao` properties which are guaranteed to be non-null after the 2 `initD*` method calls.

`database` is currently an `OrmLiteDatabase` object from *[Toolbox](https://github.com/juzraai/toolbox)*, while `dao` is a `HarveyDao` implemented by `harvey-core`.

Command line arguments are parsed by *[JCommander](http://jcommander.org/)*.



## What will come

* default properties file loading mechanism (inspired by *Spring Boot*)
* default WUI which aims to provide progress information and batch/task browsing
* `database` and `dao` should be implemented in a more flexible way, with an interface

TODO:
- how to create new app
- what to override (rawTaskIterator, e.g. to read System.in)