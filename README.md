# Harvey

*...the harvester :)*

**under development**



## 1. About


### 1.1. Goal / Use case

In work I often have the task of searching for the same input on similar websites. Usually I create an application which **reads an input file (e.g. TSV)** and crawls the relevant parts of the site. The input files have almost the same columns and the applications are very similar too.

*Harvey* aims to reduce the work when I need to implement a new crawler similar to the previous ones. It provides an **abstract application structure** which has the necessary default, common behaviour but at the same time it's flexible enough.

It can **save each task's state:** when it was processed, with which version of your crawler implementation. Also *Harvey* can skip automatically those tasks which were already processed by the current version.

So basically it's sort of a **batch framework** but specialized to **my** needs.


### 1.2. How it works

The magic happens in `HarveyApplication` abstract class, look at `run()` method first. When you implement your application by extending this class, you must call `run()` in your main method to start the predefined mechanism. Let's see what it calls:

```text
run()

    // Phase I.
    // No logging, no database, just a `configuration` property to be set up

    loadPropertiesFile()                       // Loads configuration from file (not implemented)
    parseArguments()                           // Loads configuration from args
    validateConfiguration()                    // Validates configuration
    handleParameterException()                 // Prints out error and usage if needed
    setupLogging()                             // Sets up logger framework

    // Phase II.
    // Logging's on, `database` and `dao` field is set up after first call

    database = initDatabaseConnection()        // Sets up MySQL connection (OrmLiteDatabase)
    dao = initDao()                            // Instantiates `dao`
    if (canStartWUI()) startWUI()              // Starts WUI if possible (not implemented)
    createDatabaseTables()                     // Creates system tables and child application's
        tablesToBeCreated()                    //     tables returned by this abstract method
    if (canImportTasks()) importTasks()        // Imports tasks if got filename
        rawTaskIterator()                      // Reads Maps from a TSV file
        generateTaskRecord(Map)                // Generates a Task object
        dao.storeTask(Task)                    // Saves Task record
        generateBatchRecord(String, Task)      // Generates a Batch object
        dao.storeBatch(Batch)                  // Saves Batch record
    processTasks()                             // Processes tasks of the batch
        dao.tasksOfBatch(String)               // Queries all tasks of given batch
        alreadyProcessed(Task)                 // Already processed tasks will be skipped
        process(Task)                          // The abstract method which does what you want
            saveTaskState(Task, Any?, Boolean) //     You can call this to save a task's state
            loadTaskState(Task): State?        //     And this will retrieve a previously saved state
```

All of the above methods are declared as `protected open` functions so you can freely override them in your implementation. Methods of `HarveyApplication` have access to `database` and `dao` properties which are guaranteed to be non-null after the 2 `initD*` method calls.

`database` is currently an `OrmLiteDatabase` object from *[Toolbox](https://github.com/juzraai/toolbox)*, while `dao` is a `HarveyDao` implemented by `harvey-core`.

Command line arguments are parsed by *[JCommander](http://jcommander.org/)*.

About *Harvey*'s data:

* `Task` ID is a hash of the raw task record's JSON string.
* `Batch` record stores (taskID, batchID) pairs actually. Batch record's own ID is a hash of the batch ID + task ID.
* `State` ID is the hash of taskID + crawlerID + crawler version. The `Any?` parameter passed to `saveTaskState` will be transformed into a JSON string.

So:

* A task can be in any number of batches. Batches can contain any number of tasks.
* A state describes a task's processing state with one crawler's one version.


### 1.3. What will come (TODO)

* default properties file loading mechanism (inspired by *Spring Boot*)
* default WUI which aims to provide progress information and batch/task browsing
* `database` and `dao` should be implemented in a more flexible way, with an interface (a single one?)
* parallel processing
* also planning HTTP client (on top of Jsoup) and other goodies


### 1.4. Project structure

* `harvey` is the root module which only contains a POM definition. It has 2 purposes:
	* Other modules are referenced here, so calling `mvn install` on this root POM will build and install them.
	* Refers to `maven-parents/kotlin-project` as parent. And because modules reference `harvey` as parent, they inherit settings defined in `kotlin-project` (e.g. dependency versions, build plugin config).
* `harvey-util` contains reusable utility classes which would have place in `toolbox` too if it were a Kotlin project :D
* `harvey-core` defines the soul of *Harvey*. It depends on `harvey-util`.
* `harvey-app-starter` can be used as a parent in your application, it adds `harvey-core` and `harvey-util` as dependencies.
* `harvey-app-example` is a sample program which shows you how to create a *Harvey* application.

```text
           [ kotlin-project ]
                   ^
maven-parents/     |
...................|....................
harvey/            |
                   | parent
                   |
    +-------- [ harvey ] <--------+
    |                             |
    |                             |
    +-----> [ harvey-util ] ------+  p
    |              ^              |  a
 b  |              | dep          |  r
 u  |              |              |  e
 i  +-----> [ harvey-core ] ------+  n
 l  |              ^              |  t
 d  |              | dep          |
    |              |              |
    +--> [ harvey-app-starter ] --+
    |              ^
    |              | parent
    |              |
    +--> [ harvey-app-example ]
```



## 2. Cookbook


### 2.1. Creating a *Harvey* application

1. Clone/download the source code
2. Call `mvn clean install` from the root directory of the source code
3. Set `harvey-app-starter` as parent in your POM:

```xml
<parent>
	<groupId>hu.juzraai.harvey</groupId>
	<artifactId>harvey-app-starter</artifactId>
	<version>VERSION</version>
</parent>
```

4. Create file with a main method and specify it in properties:

```xml
<properties>
	<main.class>your.fully.qualified.ClassName</main.class>
	...
</properties>
```

If you need to have another parent project, or you only need some parts of *Harvey*, you can just add the modules as dependencies:

```xml
<dependencies>
	<dependency>
		<groupId>hu.juzraai.harvey</groupId>
		<artifactId>harvey-core</artifactId>
		<version>VERSION</version>
	</dependency>
	<dependency>
		<groupId>hu.juzraai.harvey</groupId>
		<artifactId>harvey-util</artifactId>
		<version>VERSION</version>
	</dependency>
	...
</dependencies>
```

5. Create a class which extends `HarveyApplication`
6. Instantiate it and call its `run()` method to launch it

```kotlin
fun main(args: Array<String>) {
	YourHarveyApplication(args).run()
}
```

**You can find a working example in `harvey-app-example` directory. ;)**



### 2.2. What you have to implement

* `crawlerId(): String`: This method should return a short string which can identify your application.
* `crawlerVersion(): Int`: This method should return your application's revision number. This is used alongside with `crawlerId` to determine whether a task is already processed by the current application version. So, if you modify something and want to re-process your tasks, increase this number.
* `tablesToBeCreated(): Array<Class<*>>`: This method should return `Class` objects that can be passed to *OrmLite* to create the database table based on their annotations. You don't have to include *Harvey's* table classes, and if you don't need any other tables, you can just return an empty array.
* `process(Task)`: You can implement the most important thing here, what you want to do with the tasks.

In `process` method, you can call `saveTaskState(Task, Any?, Boolean)` which saves a state for the given task. You can use this later e.g. to resume processing of a task (`loadTaskState(Task)` can help). The 2nd argument is the state information which is up to you (it can be anything, even `null`). If the 3rd argument is `true`, then the task will be marked as *processed* (so will be skipped next time).


### 2.3. What you can override

Basically any method listed far above. :D


### 2.4. What you may want to override


#### 2.4.1. Inserting before/after methods

*Harvey* doesn't declare overrideable pre/post methods, because there would be a lot of them. Instead you can just override an existing method and do your stuff before/after calling `super`. For example, check `harvey-app-example`'s `postProcess` function:

```kotlin
override fun processTasks() {
	super.processTasks()
	postProcess()
}

private fun postProcess() {
	// do something e.g. with `database`
}
```


#### 2.4.2. `rawTaskIterator()`

Its default implementation is:

```kotlin
protected open fun rawTaskIterator(): Iterator<Map<String, String>>
	= TsvFileReader(harveyConfiguration.tasksFile!!, true)
```

You may need to read from the standard input instead:

```kotlin
override fun rawTaskIterator(): Iterator<Map<String, String>>
	= TsvFileReader(System.`in`, true)

override fun canImportTasks(): Boolean = true
```

As you can see, `canImportTasks()` also needs to be overriden in this case, because its default behaviour is to check whether the configuration contains a tasks filename.

`harvey-app-example` shows a similar sample but it reads from a resource file, check it out!


#### 2.4.3. Custom arguments

You may need to add your own configuration parameters as command line arguments. You need to do these steps:

1. Create a new class for the model which include a *Harvey* configuration field and implements `HarveyConfigurationProvider`:

```kotlin
data class YourConfiguration(
	@ParameterDelegate
	var harveyConfiguration: HarveyConfiguration = HarveyConfiguration(),

	@Parameter(...)
	var yourParameter: Any
) : HarveyConfigurationProvider {

	override fun harveyConfiguration(): HarveyConfiguration = harveyConfiguration
}
```

2. Override `defaultConfiguration` to return your model:

```kotlin
override fun defaultConfiguration(): HarveyConfigurationProvider = YourConfiguration()
```

*Harvey* will call *JCommander* to parse the whole model, then *Harvey* will use only `configuration.harveyConfigration()` in internal methods.

3. Override `validateConfiguration` to be able to verify parsed values:

```kotlin
override fun validateConfiguration() {
	super.validateConfiguration() // validates configuration.harveyConfigration()
	with (configuration) {
		// TODO validate your fields
	}
}
```
