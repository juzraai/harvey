package hu.juzraai.harvey.cli

import com.beust.jcommander.ParameterException

/**
 * @author Zsolt Jurányi
 */
open class ConfigurationValidator {

	open fun validateConfiguration(configuration: Configuration) {
		with(configuration) {
			// TODO move out to functions per field
			if (batchId.isNullOrBlank())
				throw ParameterException("Batch ID is required.")

			if (null != configFile && !java.io.File(configFile).exists())
				throw ParameterException("Specified config file does not exists !")

			if (databasePort !in 0..65535)
				throw ParameterException("Database port number must be in range 0..65535 !")

			if (null != tasksFile && !java.io.File(tasksFile).exists())
				throw ParameterException("Specified tasks file does not exists !")

			if (verbosity !in 0..5)
				throw ParameterException("Verbosity level must be in range 0..5 !")

			if (null != wuiPort) {
				if (wuiPort!! !in 0..65535)
					throw ParameterException("WUI port number must be in range 0..65535 !")
				if (wuiPort!! == databasePort)
					throw ParameterException("WUI port number must differ from database port !")
			}
		}
	}
}