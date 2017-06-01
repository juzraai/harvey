package hu.juzraai.harvey

/**
 * @author Zsolt Jurányi
 */
interface IHarveyApplication {

	fun crawlerId(): String
	fun crawlerVersion(): Int
	fun tablesToBeCreated(): Array<Class<*>>
}