package hu.juzraai.harvey

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.google.gson.Gson
import com.j256.ormlite.misc.TransactionManager
import hu.juzraai.harvey.conf.ArgumentsParser
import hu.juzraai.harvey.conf.Configuration
import hu.juzraai.harvey.conf.ConfigurationValidator
import hu.juzraai.harvey.conf.PropertiesLoader
import hu.juzraai.harvey.data.Batch
import hu.juzraai.harvey.data.HarveyDao
import hu.juzraai.harvey.data.State
import hu.juzraai.harvey.data.Task
import hu.juzraai.harvey.reader.TsvFileReader
import hu.juzraai.toolbox.data.OrmLiteDatabase
import hu.juzraai.toolbox.jdbc.ConnectionString
import hu.juzraai.toolbox.log.LoggerSetup
import mu.KLogging
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.apache.log4j.Level
import java.io.File

abstract class HarveyApplication(val args: Array<String>) : Runnable, IHarveyApplication {

	companion object : KLogging() {
		const val TABLE_PREFIX = "harv_"
		const val BATCH_TABLE_NAME = TABLE_PREFIX + "batch"
		const val STATE_TABLE_NAME = TABLE_PREFIX + "state"
		const val TASK_TABLE_NAME = TABLE_PREFIX + "task"
	}

	var configuration: Configuration = Configuration()
	var dao: HarveyDao? = null
	var database: OrmLiteDatabase? = null
	var propertiesFile = File("application.yml")

	override fun run() {

		// No logging, no database

		loadPropertiesFile()
		try {
			parseArguments()
		} catch (e: ParameterException) {
			handleParameterException(e)
			return
		}
		validateConfiguration()
		setupLogging()

		// Now with logging and database

		initDatabaseConnection().use { db ->
			database = db
			dao = HarveyDao(database!!)
			if (canStartWUI()) startWUI()
			createDatabaseTables()
			if (canImportTasks()) importTasks()
			// TODO query all tasks for batchId and process them
			// TODO should we wait here for WUI exit before closing db or what?
		}
		logger.info("Harvey's shutting down")
	}

	protected open fun canImportTasks(): Boolean = !configuration.tasksFile.isNullOrBlank()

	protected open fun canStartWUI(): Boolean = null != configuration.wuiPort

	protected open fun createDatabaseTables() {
		logger.debug("Creating database tables")
		database?.createTables(
				Batch::class.java,
				State::class.java,
				Task::class.java
		)
		database?.createTables(*tablesToBeCreated())
	}

	protected open fun generateBatchRecord(batchId: String, task: Task): Batch {
		val id = hash("$batchId/${task.id}")
		return Batch(id, batchId, task)
	}

	protected open fun generateTaskRecord(map: Map<String, String>): Task {
		val json = toJson(map)
		return Task(hash(json), json)
	}

	protected open fun handleParameterException(e: ParameterException) {
		println("[ERROR] ${e.message}\n")
		JCommander.newBuilder()
				.programName("harvey-app")
				.addObject(Configuration())
				.build()
				.usage()
	}

	protected open fun hash(input: String): String = Base64.encodeBase64String(DigestUtils.sha1(input))

	protected open fun importTasks() {
		with(configuration) {
			logger.info("Importing tasks from {} for batch {}", tasksFile, batchId)
			var allTask = 0
			var newTask = 0
			TransactionManager.callInTransaction(database!!.connectionSource) {
				rawTaskIterator().forEach { map ->
					allTask++
					val task = generateTaskRecord(map)
					newTask += dao?.storeTask(task) ?: 0
					dao?.storeBatch(generateBatchRecord(batchId!!, task))
				}
			}
			logger.info("Imported {} tasks ({} new)", allTask, newTask)
		}
	}

	protected open fun initDatabaseConnection(): OrmLiteDatabase {
		with(configuration) {
			logger.info("Initializing database connection {}@{}/{}", databaseUser, databaseHost, databaseName)
			val cs = ConnectionString.MYSQL()
					.host(databaseHost)
					.port(databasePort)
					.schema(databaseName!!)
					.build()
			return OrmLiteDatabase.build(cs, databaseUser!!, databasePassword)
		}
	}

	protected open fun loadPropertiesFile() {
		PropertiesLoader().loadPropertiesFile(propertiesFile, configuration)
	}

	protected open fun parseArguments() {
		ArgumentsParser().parseArguments(args, configuration)
	}

	protected open fun rawTaskIterator(): Iterator<Map<String, String>> {
		return TsvFileReader(configuration.tasksFile!!, true)
	}

	protected open fun startWUI() {
		logger.info("Sorry, WUI is not implemented yet.")
		// TODO start wui (sparkjava!)
	}

	protected open fun setupLogging() {
		val level = arrayOf(Level.OFF, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE)[configuration.verbosity]
		LoggerSetup.level(Level.OFF) // muting other libs
		LoggerSetup.level("hu.juzraai.harvey", level)
		if (Level.OFF != level) LoggerSetup.outputOnlyToConsole()
	}

	protected open fun toJson(map: Map<String, String>): String {
		return Gson().toJson(map)
	}

	protected open fun validateConfiguration() {
		ConfigurationValidator().validateConfiguration(configuration)
	}

}

