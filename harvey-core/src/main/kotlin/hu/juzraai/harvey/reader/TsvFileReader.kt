package hu.juzraai.harvey.reader

import java.io.FileInputStream
import java.io.InputStream
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class TsvFileReader(inputStream: InputStream, headerLine: Boolean) : Iterator<Map<String, String>> {
	// TODO move to harvey-util

	companion object {
		const val SEPARATOR = '\t'
	}

	val scanner = Scanner(inputStream, "UTF-8")
	val header = mutableListOf<String>()

	constructor(filename: String, headerLine: Boolean) : this(FileInputStream(filename), headerLine)

	init {
		if (headerLine && hasNext()) header.addAll(scanner.nextLine().split(SEPARATOR))
	}

	override fun hasNext(): Boolean {
		val hasNext = scanner.hasNextLine()
		if (!hasNext) scanner.close()
		return hasNext
	}

	override fun next(): Map<String, String> {
		val map = mutableMapOf<String, String>()
		scanner.nextLine().split(SEPARATOR).forEachIndexed { i, cell ->
			map.put(header.getOrElse(i, { "$i" }), cell)
		}
		return map
	}
}