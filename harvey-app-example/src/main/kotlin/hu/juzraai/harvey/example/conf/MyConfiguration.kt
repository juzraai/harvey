package hu.juzraai.harvey.example.conf

import com.beust.jcommander.*
import hu.juzraai.harvey.conf.*

/**
 * @author Zsolt Jurányi
 */
data class MyConfiguration(
		@ParametersDelegate
		var harveyConfiguration: HarveyConfiguration = HarveyConfiguration(),

		@Parameter(names = arrayOf("-s", "--sleep"))
		var sleep: Int = 0
) : HarveyConfigurationProvider {

	override fun harveyConfiguration(): HarveyConfiguration = harveyConfiguration
}