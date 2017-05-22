package hu.juzraai.harvey

import com.beust.jcommander.ParameterException
import hu.juzraai.harvey.cli.Arguments
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Zsolt Jurányi
 */
class CliTest {

	object H : HarveyApplication(arrayOf("")) {
		// needs to be public for testing, so...
		public override fun parseArguments(args: Array<String>): Arguments {
			return super.parseArguments(args)
		}
	}

	private fun h(args: String): HarveyApplication {
		H.parseArguments(args.split(' ').toTypedArray())
		return H
	}

	@Test
	fun accepts0AsVerbosity() {
		assertEquals(0, h("-v 0").arguments.verbosity)
	}

	@Test
	fun accepts5AsVerbosity() {
		assertEquals(5, h("-v 5").arguments.verbosity)
	}

	@Test
	fun accepts0AsWuiPort() {
		assertEquals(0, h("-w 0").arguments.wuiPort)
	}

	@Test
	fun accepts65535AsWuiPort() {
		assertEquals(65535, h("-w 65535").arguments.wuiPort)
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfVerbosityIsAbove5() {
		h("-v 6")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfVerbosityIsNegative() {
		h("-v -1")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfWuiPortIsAbove65535() {
		h("-w 65536")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfWuiPortIsNegative() {
		h("-w -1")
	}

}