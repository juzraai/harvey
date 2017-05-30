import hu.juzraai.harvey.DefaultHarveyApplication
import hu.juzraai.harvey.printLogo

/**
 * @author Zsolt Jurányi
 */

fun main(args: Array<String>) {
	val a = "-v 5"
	printLogo()
	DefaultHarveyApplication(a.split(' ').toTypedArray()).run()
}

// TODO example? app with extended Config class - see if it works