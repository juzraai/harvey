package hu.juzraai.harvey.string

import com.google.gson.Gson
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils

/**
 * @author Zsolt Jurányi
 */
class StringUtils {

	companion object {

		fun toBase64String(ba: ByteArray): String = Base64.encodeBase64String(ba)

		fun toJson(o: Any): String = Gson().toJson(o)

		fun toSha1Base64String(s: String): String = toBase64String(toSha1Bytes(s))

		fun toSha1Bytes(s: String): ByteArray = DigestUtils.sha1(s)
	}
}