package hu.juzraai.harvey.data

import com.j256.ormlite.field.*
import com.j256.ormlite.table.*
import hu.juzraai.harvey.*
import hu.juzraai.toolbox.data.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
@DatabaseTable(tableName = HarveyApplication.STATE_TABLE_NAME)
data class State(
		@DatabaseField(id = true)
		var id: String? = null, // "taskId/crawlerId/crawlerVersion" -> SHA1 -> BASE64

		@DatabaseField(canBeNull = false, columnName = "taskId", foreign = true) @Indexed
		var task: Task? = null,

		@DatabaseField(canBeNull = false) @Indexed
		var crawlerId: String = "",

		@DatabaseField(canBeNull = false)
		var crawlerVersion: Int = -1,

		@DatabaseField
		var timestamp: Date? = null,

		@DatabaseField
		var finished: Boolean = false,

		@DatabaseField @Longtext
		var state: String? = null // JSON
)