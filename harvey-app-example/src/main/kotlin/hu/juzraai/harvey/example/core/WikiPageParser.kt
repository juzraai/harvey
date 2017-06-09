package hu.juzraai.harvey.example.core

import org.jsoup.nodes.Document

/**
 * @author Zsolt Jurányi
 */
class WikiPageParser(val doc: Document) {

	fun parseListFromInfoBox(field: String): List<String> {
		val result = mutableListOf<String>()
		doc.select("table.infobox tr").forEach { tr ->
			if (field.equals(tr.select("th").text())) {
				tr.select("li a[title]").forEach { a ->
					result.add(a.attr("title"))
				}
			}
		}
		return result
	}
}