package hu.juzraai.harvey.example.data

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import hu.juzraai.toolbox.data.Indexed

/**
 * @author Zsolt Jurányi
 */
@DatabaseTable(tableName = "ex_artist")
data class Artist(

		@DatabaseField(id = true)
		var id: String = "",

		@DatabaseField(canBeNull = false) @Indexed
		var name: String = "",

		@ForeignCollectionField
		var genres: ForeignCollection<ArtistGenre>? = null
)