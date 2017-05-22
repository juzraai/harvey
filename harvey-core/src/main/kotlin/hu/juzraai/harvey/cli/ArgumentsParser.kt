package hu.juzraai.harvey.cli

import com.beust.jcommander.JCommander

/**
 * @author Zsolt Jurányi
 */
open class ArgumentsParser {

	open fun parseArguments(args: Array<String>, config: Configuration) {
		val jc = JCommander.newBuilder().addObject(config).build()
		jc.parse(*args)
	}
}