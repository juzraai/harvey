package hu.juzraai.harvey.conf

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.Parameter

/**
 * @author Zsolt Jurányi
 */
data class HarveyConfiguration(

		@Parameter(names = arrayOf("-b", "--batch-id"),
				description = "ID of the batch (task group). If you additionally specify a tasks file (-t), those tasks will be stored with this batch ID. Otherwise, tasks will be queried from the database using this batch ID.")
		var batchId: String? = null,

		@Parameter(names = arrayOf("-c", "--config-file"), // TODO desc
				hidden = true) // TODO hidden till I figure out whether we need this actually
		var configFile: String? = null,

		@Parameter(names = arrayOf("-h", "--dbhost")) // TODO desc
		var databaseHost: String = "localhost",

		@Parameter(names = arrayOf("-n", "--dbname")) // TODO desc
		var databaseName: String? = null,

		@Parameter(names = arrayOf("-p", "--dbpass")) // TODO desc
		var databasePassword: String? = null,

		@Parameter(names = arrayOf("-P", "--dbport")) // TODO desc
		var databasePort: Int = 3306,

		@Parameter(names = arrayOf("-u", "--dbuser")) // TODO desc
		var databaseUser: String? = null,

		@DynamicParameter(names = arrayOf("-D"),
				description = "Additional (e.g. crawler specific) parameters.")
		var parameters: Map<String, String> = mutableMapOf(),

		@Parameter(names = arrayOf("-t", "--tasks-file")) // TODO desc
		var tasksFile: String? = null,

		@Parameter(names = arrayOf("-v", "--verbosity"),
				description = "Integer in range 0 and 5 (inclusive). 0 = off, 1 = errors, 2 = warnings, 3 = progress info, 4 = debug, 5 = trace. All level includes lower level messages. Default is 3.")
		var verbosity: Int = 3,

		@Parameter(names = arrayOf("-w", "--wui-port"),
				description = "If specified a web user interface will be launched on that port, and the application will remain running. The WUI provides options to browse batches, tasks and informs you about the progress.",
				hidden = true) // TODO hidden till WUI's implemented
		var wuiPort: Int? = null

		// TODO force reprocessing? -f? -F? -a?
		// TODO input format? -f?
		// TODO thread count? -T?
		// TODO option for reading b/c/t from a dir
) : HarveyConfigurationProvider {

	override fun harveyConfiguration(): HarveyConfiguration {
		return this
	}
}