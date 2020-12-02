package ru.xcam.evotor.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.xcam.evotor.example.interactor.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenReceipt.setOnClickListener {
            OpenReceiptInteractor().execute(this)
        }

        btnInternetReceipt.setOnClickListener {
            InternetReceiptInteractor().execute(this)
        }

        btnCorrectionReceipt.setOnClickListener {
            CorrectionReceiptInteractor().execute(this)
        }

        btnPrintDocument.setOnClickListener {
            PrintDocumentInteractor().execute(this)
        }

        btnZReport.setOnClickListener {
            ZReportInteractor().execute(this)
        }
    }

    fun getFirmwareOsVersion(): String {

        packageManager.getPackageInfo("ru.evotor.devices", 0).let {
            it.versionCode
            it.versionName
        }
        return ""
        /*
        var ret = ""
        try {
            val systemProperties = Class.forName("android.os.SystemProperties")

            val paramTypes = arrayOfNulls<Class<*>>(1)
            paramTypes[0] = String::class.java

            val get = systemProperties.getMethod("get", *paramTypes)

            val params = arrayOfNulls<Any>(1)
            params[0] = OS_VERSION_PROP_NAME

            ret = get.invoke(systemProperties, *params) as String
        } catch (e: ClassNotFoundException) {
            // ignore
        } catch (e: NoSuchMethodException) {
            // ignore
        } catch (e: IllegalAccessException) {
            // ignore
        } catch (e: InvocationTargetException) {
            // ignore
        }

        return if (ret.isEmpty()) OS_VERSION_DEFAULT else ret

         */
    }
}

private const val OS_VERSION_PROP_NAME = "ro.atol.version"
private const val OS_VERSION_DEFAULT = "0.0"
