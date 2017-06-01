package hu.juzraai.harvey

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import hu.juzraai.harvey.cli.ArgumentsParser
import hu.juzraai.harvey.cli.Configuration
import hu.juzraai.harvey.cli.ConfigurationValidator
import hu.juzraai.harvey.cli.PropertiesLoader
import hu.juzraai.toolbox.data.OrmLiteDatabase
import hu.juzraai.toolbox.jdbc.ConnectionString
import hu.juzraai.toolbox.log.LoggerSetup
import mu.KLogging
import org.apache.log4j.Level
import java.io.File

abstract class HarveyApplication(val args: Array<String>) : Runnable, IHarveyApplication {

	companion object : KLogging()

	var configuration: Configuration = Configuration()
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
			if (canStartWUI()) startWUI()
			createDatabaseTables()
			if (canImportTasks()) importTasks()

			// TODO do the magic (db, batch c/t, read input w reader, ...)

			// TODO:
			// if we got a tasksfile, import tasks - if a task is already in db and it's different, mark it as unprocessed
			// -
			// query all tasks for batchId and process them

			// TODO for input reading, we can try SuperCSV

			// TODO should we wait here for WUI exit before closing db or what?
		}
	}

	protected open fun canImportTasks(): Boolean = !configuration.tasksFile.isNullOrBlank()

	protected open fun canStartWUI(): Boolean = null != configuration.wuiPort

	protected open fun createDatabaseTables() {
		logger.debug("Creating database tables")
		database?.createTables(*tablesToBeCreated())
	}

	protected open fun handleParameterException(e: ParameterException) {
		println("[ERROR] ${e.message}\n")
		JCommander.newBuilder()
				.programName("harvey-app")
				.addObject(Configuration())
				.build()
				.usage()
	}

	protected open fun importTasks() {
		with(configuration) {
			logger.info("Importing tasks from {} for batch {}", tasksFile, batchId)
			// TODO request input stream... maybe from a tasksFileIterator() fun?
			// TODO import tasks from tasks file into database
			// TODO if a task is already in db AND it's different, mark it as unprocessed
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

	protected open fun startWUI() {
		logger.info("Sorry, WUI is not implemented yet.")
		// TODO start wui (sparkjava!)
	}

	protected open fun setupLogging() {
		val level = arrayOf(Level.OFF, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE)[configuration.verbosity]
		LoggerSetup.level(level)
		if (Level.OFF != level) LoggerSetup.outputOnlyToConsole()
	}

	protected open fun validateConfiguration() {
		ConfigurationValidator().validateConfiguration(configuration)
	}

}

