import hu.juzraai.harvey.DefaultHarveyApplication

/**
 * @author Zsolt Jurányi
 */

fun main(args: Array<String>) {
	val a = "-v 5 -u root -p root -n test -b test-batch"
	DefaultHarveyApplication(a.split(' ').toTypedArray()).run()
}