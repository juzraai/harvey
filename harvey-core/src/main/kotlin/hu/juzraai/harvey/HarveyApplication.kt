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
	var propertiesFile = File("application.yml")

	override fun run() {
		try {
			loadPropertiesFile()
			parseArguments()
			validateConfiguration()
			setupLogging()
			startWUI()
			initDatabaseConnection().use { db ->
				db.createTables(*tablesToBeCreated()) // TODO move to fun
				// TODO do the magic (db, batch c/t, read input w reader, ...)

				// TODO:
				// if we got a tasksfile, import tasks
				// -
				// query all tasks for batchId and process them

				// TODO for input reading, we can try SuperCSV
			}
		} catch (e: ParameterException) {
			handleParameterException(e)
		}
	}

	protected open fun handleParameterException(e: ParameterException) {
		println("[ERROR] ${e.message}\n")
		JCommander.newBuilder()
				.programName("harvey-app")
				.addObject(Configuration())
				.build()
				.usage()
	}

	protected open fun initDatabaseConnection(): OrmLiteDatabase {
		with(configuration) {
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
		if (null != configuration.wuiPort) {
			logger.error("Sorry, WUI is not implemented yet.")
			// TODO start wui (sparkjava!)
		}
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

