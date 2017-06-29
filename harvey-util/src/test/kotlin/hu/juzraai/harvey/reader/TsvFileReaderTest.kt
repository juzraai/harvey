package hu.juzraai.harvey.reader

import org.junit.*
import org.junit.Assert.*
import java.nio.charset.*

/**
 * @author Zsolt Jurányi
 */
class TsvFileReaderTest {

	private fun read(tsv: String, header: Boolean): List<Map<String, String>> {
		val inputStream = tsv.byteInputStream(StandardCharsets.UTF_8)
		val reader = TsvFileReader(inputStream, header)
		val list = mutableListOf<Map<String, String>>()
		while (reader.hasNext()) list.add(reader.next())
		return list
	}

	@Test
	fun readsTsvWithHeader() {
		val list = read("id\tfruit\n1\tapple\n2\tbanana\n", true)
		assertEquals(2, list.size)
		assertEquals("1", list[0]["id"])
		assertEquals("apple", list[0]["fruit"])
		assertEquals("2", list[1]["id"])
		assertEquals("banana", list[1]["fruit"])
	}

	@Test
	fun readsTsvWithoutHeader() {
		val list = read("1\tapple\n2\tbanana\n", false)
		assertEquals(2, list.size)
		assertEquals("1", list[0]["0"])
		assertEquals("apple", list[0]["1"])
		assertEquals("2", list[1]["0"])
		assertEquals("banana", list[1]["1"])
	}
}