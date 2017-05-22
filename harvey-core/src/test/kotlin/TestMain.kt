import hu.juzraai.harvey.HarveyApplication
import hu.juzraai.harvey.printLogo

/**
 * @author Zsolt Jurányi
 */

fun main(args: Array<String>) {
	val a = "-v 5"
	printLogo()
	HarveyApplication(a.split(' ').toTypedArray()).run()
}