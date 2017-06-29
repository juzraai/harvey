package hu.juzraai.harvey.data

import com.j256.ormlite.dao.*
import com.j256.ormlite.field.*
import com.j256.ormlite.table.*
import hu.juzraai.harvey.*
import hu.juzraai.toolbox.data.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
@DatabaseTable(tableName = HarveyApplication.TASK_TABLE_NAME)
data class Task(
		@DatabaseField(id = true)
		var id: String = "", // data -> SHA1 -> BASE64

		@DatabaseField(canBeNull = false)
		var importedAt: Date = Date(),

		@DatabaseField @Longtext
		var data: String = "", // TSV -> MAP -> JSON

		@ForeignCollectionField(eager = false)
		var states: ForeignCollection<State>? = null
) {
	constructor(id: String, data: String) : this(id, Date(), data, null)
}