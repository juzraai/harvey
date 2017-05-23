package hu.juzraai.harvey

import com.beust.jcommander.ParameterException
import hu.juzraai.harvey.cli.ArgumentsParser
import hu.juzraai.harvey.cli.Configuration
import hu.juzraai.harvey.cli.ConfigurationValidator
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Zsolt Jurányi
 */
open class ConfigurationValidatorTest {

	var parser = ArgumentsParser()
	var validator = ConfigurationValidator()

	private fun parse(args: String): Configuration {
		val configuration = Configuration()
		parser.parseArguments(args.split(' ').toTypedArray(), configuration)
		validator.validateConfiguration(configuration)
		return configuration
	}

	@Test
	fun accepts0AsDatabasePort() {
		assertEquals(0, parse("-b test -P 0").databasePort)
	}

	@Test
	fun accepts65535AsDatabasePort() {
		assertEquals(65535, parse("-b test -P 65535").databasePort)
	}

	@Test
	fun accepts0AsVerbosity() {
		assertEquals(0, parse("-b test -v 0").verbosity)
	}

	@Test
	fun accepts5AsVerbosity() {
		assertEquals(5, parse("-b test -v 5").verbosity)
	}

	@Test
	fun accepts0AsWuiPort() {
		assertEquals(0, parse("-b test -w 0").wuiPort)
	}

	@Test
	fun accepts65535AsWuiPort() {
		assertEquals(65535, parse("-b test -w 65535").wuiPort)
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfDatabasePortEqualsWuiPort() {
		parse("-b test -P 1234 -w 1234")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfDatabasePortIsAbove65535() {
		parse("-b test -P 65536")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfDatabasePortIsNegative() {
		parse("-b test -P -1")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfVerbosityIsAbove5() {
		parse("-b test -v 6")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfVerbosityIsNegative() {
		parse("-b test -v -1")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfWuiPortIsAbove65535() {
		parse("-b test -w 65536")
	}

	@Test(expected = ParameterException::class)
	fun throwsExIfWuiPortIsNegative() {
		parse("-b test -w -1")
	}

}