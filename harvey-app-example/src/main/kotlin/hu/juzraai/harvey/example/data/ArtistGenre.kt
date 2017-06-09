package hu.juzraai.harvey.example.data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import hu.juzraai.toolbox.data.Indexed

/**
 * @author Zsolt Jurányi
 */
@DatabaseTable(tableName = "ex_artist_genre")
data class ArtistGenre(

		@DatabaseField(id = true)
		var id: String = "",

		@DatabaseField(canBeNull = false, columnName = "artist_id", foreign = true) @Indexed
		var artist: Artist? = null,

		@DatabaseField(canBeNull = false) @Indexed
		var genre: String = ""
)