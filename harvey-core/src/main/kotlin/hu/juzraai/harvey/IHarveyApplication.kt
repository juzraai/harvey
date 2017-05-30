package hu.juzraai.harvey

/**
 * @author Zsolt Jurányi
 */
interface IHarveyApplication {

	fun tablesToBeCreated(): Array<Class<*>>
}