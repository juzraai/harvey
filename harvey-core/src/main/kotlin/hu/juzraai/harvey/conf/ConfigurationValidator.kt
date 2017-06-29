package hu.juzraai.harvey.conf

import com.beust.jcommander.*
import java.io.*

/**
 * @author Zsolt Jurányi
 */
open class ConfigurationValidator {

	// TODO later: no -b but -w for global WUI

	open fun validateConfiguration(configuration: HarveyConfigurationProvider) {
		with(configuration.harveyConfiguration()) {
			validateBatchId(batchId)
			validateConfigFile(configFile)
			validateDatabaseName(databaseName)
			validateDatabasePort(databasePort)
			validateDatabaseUser(databaseUser)
			validateTasksFile(tasksFile)
			validateVerbosity(verbosity)
			validateWuiPort(wuiPort)

			if (null != wuiPort && wuiPort == databasePort)
				throw ParameterException("WUI port number must differ from database port !")
		}
	}

	protected open fun validateBatchId(batchId: String?) {
		if (batchId.isNullOrBlank())
			throw ParameterException("Batch ID is required.")
	}

	protected open fun validateConfigFile(configFile: String?) {
		if (!configFile.isNullOrBlank() && !File(configFile).exists())
			throw ParameterException("Specified config file does not exists !")
	}

	protected open fun validateDatabaseName(databaseName: String?) {
		if (databaseName.isNullOrBlank())
			throw ParameterException("Database name must be specified !")
	}

	protected open fun validateDatabasePort(databasePort: Int) {
		if (databasePort !in 0..65535)
			throw ParameterException("Database port number must be in range 0..65535 !")
	}

	protected open fun validateDatabaseUser(databaseUser: String?) {
		if (databaseUser.isNullOrBlank())
			throw ParameterException("Database user must be specified !")
	}

	protected open fun validateTasksFile(tasksFile: String?) {
		if (!tasksFile.isNullOrBlank() && !File(tasksFile).exists())
			throw ParameterException("Specified tasks file does not exists !")
	}

	protected open fun validateVerbosity(verbosity: Int) {
		if (verbosity !in 0..5)
			throw ParameterException("Verbosity level must be in range 0..5 !")
	}

	protected open fun validateWuiPort(wuiPort: Int?) {
		if (null != wuiPort && wuiPort !in 0..65535)
			throw ParameterException("WUI port number must be in range 0..65535 !")
	}
}