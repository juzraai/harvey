package hu.juzraai.harvey.conf

import com.beust.jcommander.JCommander

/**
 * @author Zsolt Jurányi
 */
open class ArgumentsParser {

	open fun parseArguments(args: Array<String>, config: Any) {
		val jc = JCommander.newBuilder().addObject(config).build()
		jc.parse(*args)
	}
}