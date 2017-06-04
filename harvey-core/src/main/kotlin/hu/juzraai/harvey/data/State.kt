package hu.juzraai.harvey.data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import hu.juzraai.harvey.HarveyApplication
import hu.juzraai.toolbox.data.Indexed
import hu.juzraai.toolbox.data.Longtext
import java.util.*

/**
 * @author Zsolt Jurányi
 */
@DatabaseTable(tableName = HarveyApplication.STATE_TABLE_NAME)
class State(
		@DatabaseField(generatedId = true)
		var id: Long? = null,

		@DatabaseField(canBeNull = false, columnName = "taskId", foreign = true) @Indexed
		var task: Task? = null,

		@DatabaseField(canBeNull = false)
		var crawlerId: String = "",

		@DatabaseField(canBeNull = false)
		var crawlerVersion: Int = -1,

		@DatabaseField
		var processedAt: Date? = null,

		@DatabaseField @Longtext
		var state: String = "" // JSON
)