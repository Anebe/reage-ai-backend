package com.gabriel.danael_fernandes_react_manager

import com.gabriel.danael_fernandes_react_manager.pay.CredentialsEfiBank
import com.gabriel.danael_fernandes_react_manager.pay.EfiBank
import com.gabriel.danael_fernandes_react_manager.pay.EfiClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.util.ResourceUtils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

@SpringBootTest
class DanaelFernandesReactManagerApplicationTests {

	@Autowired
	private lateinit var efi: EfiClient
	@Test
	fun aaaaaaa(){
		runBlocking {
			print(EfiBank().charge(BigDecimal("0.2")))

		}
	}

	@Test
	fun aaaaa(){

		println(efi.createCharge(BigDecimal("3.0"), "485f906a-201c-4241-9aa3-6ad81bbdb579"))
	}

	@Test
	fun bbb(){
		val credentials = CredentialsEfiBank()

		val client_id = credentials.client_id
		val client_secret = credentials.client_secret
		val basicAuth: String = Base64.getEncoder().encodeToString(("$client_id:$client_secret".toByteArray()))


		//Diretório em que seu certificado em formato .p12 deve ser inserido
		System.setProperty("javax.net.ssl.keyStore", credentials.certificate)
		val sslsocketfactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

		val url: URL = URL("https://pix-h.api.efipay.com.br/oauth/token") //Para ambiente de Desenvolvimento
//		val conn = url.openConnection()
		val conn = (url.openConnection() as HttpsURLConnection).apply {

		}
		conn.doOutput = true
		conn.requestMethod = "POST"
		conn.setRequestProperty("Content-Type", "application/json")
		conn.setRequestProperty("Authorization", "Basic $basicAuth")
		conn.sslSocketFactory = sslsocketfactory
		val input = "{\"grant_type\": \"client_credentials\"}"

		val os = conn.outputStream
		os.write(input.toByteArray())
		os.flush()

		val reader: InputStreamReader = InputStreamReader(conn.inputStream)
		val br = BufferedReader(reader)

		var response: String?
		while ((br.readLine().also { response = it }) != null) {
			println(response)
		}
		conn.disconnect()
	}
	@Test
	fun aa(){
		val resource: ClassPathResource = ClassPathResource("efi bank develop.p12")

		var certificadoFile: File = resource.file
		var absolutePath = certificadoFile.absolutePath
		var path = certificadoFile.path
		var canonicalPath = certificadoFile.canonicalPath
		var invariantSeparatorsPath = certificadoFile.invariantSeparatorsPath

		println("absolutePath: $absolutePath")
		println("path: $path")
		println("canonicalPath: $canonicalPath")
		println("invariantSeparatorsPath: $invariantSeparatorsPath")

		certificadoFile = ResourceUtils.getFile("efi bank develop.p12")

		absolutePath = certificadoFile.absolutePath
		path = certificadoFile.path
		canonicalPath = certificadoFile.canonicalPath
		invariantSeparatorsPath = certificadoFile.invariantSeparatorsPath

		println("absolutePath: $absolutePath")
		println("path: $path")
		println("canonicalPath: $canonicalPath")
		println("invariantSeparatorsPath: $invariantSeparatorsPath")
	}
}
