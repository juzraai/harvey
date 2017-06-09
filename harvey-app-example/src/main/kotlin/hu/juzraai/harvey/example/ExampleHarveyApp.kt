package hu.juzraai.harvey.example

import com.google.gson.Gson
import com.j256.ormlite.dao.Dao
import hu.juzraai.harvey.HarveyApplication
import hu.juzraai.harvey.data.Task
import hu.juzraai.harvey.example.core.WikiPageFetcher
import hu.juzraai.harvey.example.core.WikiPageParser
import hu.juzraai.harvey.example.data.Artist
import hu.juzraai.harvey.example.data.ArtistGenre
import hu.juzraai.harvey.example.data.TaskState
import hu.juzraai.harvey.reader.TsvFileReader
import mu.KLogging

/**
 * @author Zsolt Jurányi
 */
class ExampleHarveyApp(args: Array<String>) : HarveyApplication(args) {

	companion object : KLogging()

	private fun artistGenre(artist: Artist, genre: String) = ArtistGenre(hash("${artist.id}+$genre"), artist, genre)

	override fun canImportTasks(): Boolean = true // because we read from resource, don't need `-t`

	override fun crawlerId(): String = "harvey-example"

	override fun crawlerVersion(): Int = 0

	private val wikiPageFetcher = WikiPageFetcher()

	override fun process(task: Task) {
		saveTaskState(task, TaskState("mapping"), false)
		val artist: Artist = Gson().fromJson(task.data, Artist::class.java)

		saveTaskState(task, TaskState("storing artist"), false)
		database!!.store(artist)

		saveTaskState(task, TaskState("crawling wiki page"), false)
		val doc = wikiPageFetcher.fetchPageFor(artist.name)
		val artistGenres = mutableListOf<ArtistGenre>()
		val genres = WikiPageParser(doc).parseListFromInfoBox("Genres")
		genres.map { artistGenre(artist, it) }.toCollection(artistGenres)

		saveTaskState(task, TaskState("storing genres"), false)
		artistGenres.forEach(database!!::store)

		saveTaskState(task, TaskState("done"), true)
	}

	override fun processTasks() {
		super.processTasks()
		postProcess()
	}

	private fun postProcess() {
		val dao = database!!.dao(Artist::class.java) as Dao<Artist, String>
		dao.forEach { artist ->
			dao.refresh(artist) // query genres too
			println("\nArtist: ${artist.name}")
			println("Genres:")
			artist.genres?.forEach { println("\t - ${it.genre}") }
		}
	}

	override fun rawTaskIterator(): Iterator<Map<String, String>> {
		return TsvFileReader(this.javaClass.classLoader.getResourceAsStream("example-tasks.tsv"), true)
	}

	override fun tablesToBeCreated(): Array<Class<*>> = arrayOf(
			Artist::class.java,
			ArtistGenre::class.java
	)
}