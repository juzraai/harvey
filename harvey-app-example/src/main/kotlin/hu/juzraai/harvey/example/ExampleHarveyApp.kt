package hu.juzraai.harvey.example

import com.beust.jcommander.*
import com.google.gson.*
import hu.juzraai.harvey.*
import hu.juzraai.harvey.conf.*
import hu.juzraai.harvey.data.*
import hu.juzraai.harvey.example.conf.*
import hu.juzraai.harvey.example.core.*
import hu.juzraai.harvey.example.data.*
import hu.juzraai.harvey.reader.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
class ExampleHarveyApp(args: Array<String>) : HarveyApplication(args) {

	companion object : KLogging()

	var myConfiguration = MyConfiguration()

	private fun artistGenre(artist: Artist, genre: String) = ArtistGenre(hash("${artist.id}+$genre"), artist, genre)

	override fun canImportTasks(): Boolean = true // because we read from resource, don't need `-t`

	override fun crawlerId(): String = "harvey-example"

	override fun crawlerVersion(): Int = 0

	private val wikiPageFetcher = WikiPageFetcher()

	override fun defaultConfiguration(): HarveyConfigurationProvider = MyConfiguration()

	override fun process(task: Task) {
		saveTaskState(task, TaskState("mapping"), false)
		val artist: Artist = Gson().fromJson(task.data, Artist::class.java)

		saveTaskState(task, TaskState("storing artist"), false)
		database!!.store(artist)

		saveTaskState(task, TaskState("crawling wiki page"), false)

		with(myConfiguration) {
			if (sleep > 0) {
				logger.debug("Sleeping {} sec", sleep)
				Thread.sleep(sleep * 1000L)
			}
		}

		val doc = wikiPageFetcher.fetchPageFor(artist.name)
		val artistGenres = mutableListOf<ArtistGenre>()
		val genres = WikiPageParser(doc).parseListFromInfoBox("Genres")
		genres.map { artistGenre(artist, it) }.toCollection(artistGenres)

		saveTaskState(task, TaskState("storing genres"), false)
		artistGenres.forEach(database!!::store)

		saveTaskState(task, TaskState("done"), true)
	}

	override fun processTasks() {
		println("CONFIG: $configuration")
		println("H-CONF: $harveyConfiguration")
		super.processTasks()
		postProcess()
	}

	private fun postProcess() {
		val dao = database!!.dao(Artist::class.java)
		dao.forEach { artist ->
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

	override fun validateConfiguration() {
		super.validateConfiguration()
		if (myConfiguration.sleep < 0) throw ParameterException("Sleep must be non-negative !")
	}
}