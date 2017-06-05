package hu.juzraai.harvey

import hu.juzraai.harvey.data.Task
import mu.KLogging

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

	companion object : KLogging()

	override fun crawlerId(): String = "default-crawler"

	override fun crawlerVersion(): Int = 0

	override fun process(task: Task) {
		logger.info("Task {} state: {}", task.id, loadTaskState(task))
		saveTaskState(task, null, false)
	}

	override fun tablesToBeCreated(): Array<Class<*>> = arrayOf()

	override fun rawTaskIterator(): Iterator<Map<String, String>>
			= super.rawTaskIterator()
}

