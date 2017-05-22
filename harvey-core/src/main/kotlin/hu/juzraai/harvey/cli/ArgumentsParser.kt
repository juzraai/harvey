package hu.juzraai.harvey.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import java.io.File

/**
 * @author Zsolt Jurányi
 */
open class ArgumentsParser {

	protected fun parameterException(message: String, jc: JCommander): ParameterException {
		val exception = ParameterException(message)
		exception.jCommander = jc
		return exception
	}

	open fun parseArguments(args: Array<String>, config: Configuration) {
		val jc = JCommander.newBuilder().addObject(config).build()
		jc.parse(*args)
		with(config) {
			// TODO move out to functions per field
			if (batchId.isNullOrBlank())
				throw parameterException("Batch ID is required.", jc)

			if (null != configFile && !File(configFile).exists())
				throw parameterException("Specified config file does not exists !", jc)

			if (null != tasksFile && !File(tasksFile).exists())
				throw parameterException("Specified tasks file does not exists !", jc)

			if (verbosity !in 0..5)
				throw parameterException("Verbosity level must be in range 0..5 !", jc)

			if (null != wuiPort && wuiPort!! !in 0..65535)
				throw parameterException("WUI port number must be in range 0..65535 !", jc)
		}
	}
}