package hu.juzraai.harvey

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import hu.juzraai.harvey.cli.Arguments
import hu.juzraai.toolbox.log.LoggerSetup
import mu.KLogging
import org.apache.log4j.Level
import java.io.File

fun main(args: Array<String>) {
	printLogo()
	println("To override the main class, redefine `main.class` property in your POM.")
	HarveyApplication(args).run()
}

fun printLogo() {
	println(ClassLoader.getSystemClassLoader()
			.getResourceAsStream("welcome.txt")
			.bufferedReader().use { it.readText() })
}

open class HarveyApplication(val args: Array<String>) : Runnable {

	companion object : KLogging()

	var arguments: Arguments = Arguments()

	protected open fun handleParameterException(e: ParameterException) {
		println("[ERROR] ${e.message}\n")
		e.usage()
	}

	protected fun parameterException(message: String, jc: JCommander): ParameterException {
		val e = ParameterException(message)
		e.jCommander = jc
		return e
	}

	protected open fun parseArguments(args: Array<String>): Arguments {
		val a = Arguments()
		val jc = JCommander.newBuilder().addObject(a).build()
		jc.parse(*args)
		with(a) {
			if (null != configFile && !File(configFile).exists())
				throw parameterException("Specified config file does not exists !", jc)

			if (null != tasksFile && !File(tasksFile).exists())
				throw parameterException("Specified tasks file does not exists !", jc)

			if (verbosity !in 0..5)
				throw parameterException("Verbosity level should be in range 0..5 !", jc)

			if (null != wuiPort && wuiPort!! !in 0..65535)
				throw parameterException("WUI port number should be in range 0..65535 !", jc)
		}
		this.arguments = a
		return a
	}

	override fun run() {
		try {
			parseArguments(args)
			with(arguments) {
				setupLogging(verbosity)
				if (null != wuiPort) startWUI(wuiPort!!)
				// TODO do the magic
			}
		} catch (e: ParameterException) {
			handleParameterException(e)
		}
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

}

