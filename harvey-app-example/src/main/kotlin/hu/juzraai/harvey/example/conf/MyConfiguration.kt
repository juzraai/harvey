package hu.juzraai.harvey.example.conf

import com.beust.jcommander.Parameter
import com.beust.jcommander.ParametersDelegate
import hu.juzraai.harvey.conf.HarveyConfiguration
import hu.juzraai.harvey.conf.HarveyConfigurationProvider

/**
 * @author Zsolt Jurányi
 */
data class MyConfiguration(
		@ParametersDelegate
		var harveyConfiguration: HarveyConfiguration = HarveyConfiguration(),

		@Parameter(names = arrayOf("-s", "--sleep"))
		var sleep: Int = 0
) : HarveyConfigurationProvider {

	override fun harveyConfiguration(): HarveyConfiguration {
		return harveyConfiguration
	}
}