package hu.juzraai.harvey

import hu.juzraai.harvey.data.Task

/**
 * @author Zsolt Jurányi
 */
interface IHarveyApplication {

	fun crawlerId(): String
	fun crawlerVersion(): Int
	fun process(task: Task)
	fun tablesToBeCreated(): Array<Class<*>>
}