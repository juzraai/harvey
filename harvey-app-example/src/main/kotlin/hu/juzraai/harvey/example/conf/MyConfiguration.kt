package hu.juzraai.harvey.example.conf

import com.beust.jcommander.Parameter
import com.beust.jcommander.ParametersDelegate
import hu.juzraai.harvey.conf.Configuration

/**
 * @author Zsolt Jurányi
 */
data class MyConfiguration(
		@ParametersDelegate
		var harveyConfiguration: Configuration = Configuration(),

		@Parameter(names = arrayOf("-s", "--sleep"))
		var sleep: Int = 0
)