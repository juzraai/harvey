package hu.juzraai.harvey.cli

import com.beust.jcommander.Parameter

/**
 * @author Zsolt Jurányi
 */
data class Arguments(

		@Parameter(names = arrayOf("-b", "--batch-id"),
				required = true,
				description = "ID of the batch (task group). If you additionally specify a tasks file (-t), those tasks will be stored with this batch ID. Otherwise, tasks will be queried from the database using this batch ID.")
		var batchId: String? = null,

		@Parameter(names = arrayOf("-c", "--config-file")) // TODO desc
		var configFile: String? = null,

		@Parameter(names = arrayOf("-t", "--tasks-file")) // TODO desc
		var tasksFile: String? = null,

		@Parameter(names = arrayOf("-v", "--verbosity"),
				description = "Integer in range 0 and 5 (inclusive). 0 = off, 1 = errors, 2 = warnings, 3 = progress info, 4 = debug, 5 = trace. All level includes lower level messages. Default is 3.")
		var verbosity: Int = 3,

		@Parameter(names = arrayOf("-w", "--wui-port"),
				description = "If specified a web user interface will be launched on that port, and the application will remain running. The WUI provides options to browse batches, tasks and informs you about the progress.",
				hidden = true) // TODO hidden till WUI's implemented
		var wuiPort: Int? = null

		// TODO read b/c/t from dir
		// TODO dynamic parameters
)