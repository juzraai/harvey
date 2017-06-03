package hu.juzraai.harvey.model

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import hu.juzraai.harvey.HarveyApplication
import hu.juzraai.toolbox.data.Longtext
import java.util.*

/**
 * @author Zsolt Jurányi
 */
@DatabaseTable(tableName = HarveyApplication.TASK_TABLE_NAME)
class Task(
		@DatabaseField(id = true)
		var id: String = "", // data -> SHA1 -> BASE64

		@DatabaseField(canBeNull = false)
		var importedAt: Date = Date(),

		@DatabaseField @Longtext
		var data: String = "", // TSV -> MAP -> JSON

		@ForeignCollectionField(eager = false)
		var states: ForeignCollection<State>? = null
)