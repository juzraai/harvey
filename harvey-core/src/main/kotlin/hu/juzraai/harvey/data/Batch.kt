package hu.juzraai.harvey.data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import hu.juzraai.harvey.HarveyApplication
import hu.juzraai.toolbox.data.Indexed

/**
 * @author Zsolt Jurányi
 */
@DatabaseTable(tableName = HarveyApplication.BATCH_TABLE_NAME)
data class Batch(
		@DatabaseField(id = true)
		var id: String? = null, // "batchId/taskId" -> SHA1 -> BASE64

		@DatabaseField(canBeNull = false) @Indexed
		var batchId: String = "",

		@DatabaseField(canBeNull = false, columnName = "taskId", foreign = true) @Indexed
		var task: Task? = null
)