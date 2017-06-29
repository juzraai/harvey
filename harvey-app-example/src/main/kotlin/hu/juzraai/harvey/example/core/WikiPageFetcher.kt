package hu.juzraai.harvey.example.core

import org.jsoup.*
import org.jsoup.nodes.*

/**
 * @author Zsolt Jurányi
 */
class WikiPageFetcher {

	fun fetchPageFor(wiki: String): Document {
		return Jsoup.connect("https://en.wikipedia.org/wiki/$wiki").get()
	}
}