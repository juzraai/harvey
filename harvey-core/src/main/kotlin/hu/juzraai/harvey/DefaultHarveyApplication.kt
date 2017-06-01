package hu.juzraai.harvey

fun main(args: Array<String>) {
	printLogo()
	println("To override the main class, redefine `main.class` property in your POM.\n")
	DefaultHarveyApplication(args).run()
}

fun printLogo() {
	println(ClassLoader.getSystemClassLoader()
			.getResourceAsStream("welcome.txt")
			.bufferedReader().use { it.readText() })
}

/**
 * @author Zsolt Jurányi
 */
class DefaultHarveyApplication(args: Array<String>) : HarveyApplication(args) {

	override fun crawlerId(): String = "default-crawler"

	override fun crawlerVersion(): Int = 0

	override fun tablesToBeCreated(): Array<Class<*>> = arrayOf()
}

