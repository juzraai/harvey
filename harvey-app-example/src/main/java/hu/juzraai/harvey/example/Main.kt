package hu.juzraai.harvey.example

/**
 * @author Zsolt Jurányi
 */
fun main(args: Array<String>) {
	val a = "-u root -p root -n test -b example-batch"
	ExampleHarveyApp(a.split(' ').toTypedArray()).run()
}
