package hu.juzraai.harvey

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import hu.juzraai.harvey.cli.ArgumentsParser
import hu.juzraai.harvey.cli.Configuration
import hu.juzraai.harvey.cli.ConfigurationValidator
import hu.juzraai.harvey.cli.PropertiesLoader
import hu.juzraai.toolbox.log.LoggerSetup
import mu.KLogging
import org.apache.log4j.Level
import java.io.File

fun main(args: Array<String>) {
	printLogo()
	println("To override the main class, redefine `main.class` property in your POM.\n")
	HarveyApplication(args).run()
}

fun printLogo() {
	println(ClassLoader.getSystemClassLoader()
			.getResourceAsStream("welcome.txt")
			.bufferedReader().use { it.readText() })
}

open class HarveyApplication(val args: Array<String>) : Runnable {

	companion object : KLogging()

	var configuration: Configuration = Configuration()
	var propertiesFile = File("application.yml")

	override fun run() {
		try {
			// load file based config first
			loadPropertiesFile(propertiesFile)

			// override it with cl arguments
			parseArguments(args)

			// validate configuration
			validateConfiguration(configuration)

			with(configuration) {

				setupLogging(verbosity)

				if (null != wuiPort) startWUI(wuiPort!!)

				// TODO do the magic (db, batch c/t, read input w reader, ...)
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

	protected open fun loadPropertiesFile(propertiesFile: File) {
		PropertiesLoader().loadPropertiesFile(propertiesFile, configuration)
	}

	protected open fun parseArguments(args: Array<String>) {
		ArgumentsParser().parseArguments(args, configuration)
	}

	protected open fun startWUI(wuiPort: Int) {
		logger.error("Sorry, WUI is not implemented yet.")
		// TODO start wui (sparkjava!)
	}

	protected open fun setupLogging(v: Int) {
		val level = arrayOf(Level.OFF, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE)[v]
		LoggerSetup.level(level)
		if (Level.OFF != level) LoggerSetup.outputOnlyToConsole()
	}

	protected open fun validateConfiguration(configuration: Configuration) {
		ConfigurationValidator().validateConfiguration(configuration)
	}

}

