package hu.juzraai.harvey.conf

import java.io.File

/**
 * @author Zsolt Jurányi
 */
open class PropertiesLoader {

	// TODO would b nice if we could handle profiles too

	open fun loadPropertiesFile(file: File, config: Configuration) {
		// TODO read application.yml into config
	}
}