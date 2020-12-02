package ru.xcam.evotor.example.device

import android.app.Service
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.IBinder
import androidx.annotation.Nullable
import ru.evotor.devices.drivers.Constants
import java.util.concurrent.atomic.AtomicInteger


class MyDeviceService : Service() {
    private val newDeviceIndex: AtomicInteger = AtomicInteger(0)

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        val action = intent.action
        return when (action) {
            Constants.INTENT_FILTER_VIRTUAL_DRIVER_MANAGER -> MyDriverManagerStub(this@MyDeviceService)
            Constants.INTENT_FILTER_PAY_SYSTEM -> MyPaySystemStub()
            else -> null
        }
    }

    fun createNewDevice(): Int {
        val currentIndex: Int = newDeviceIndex.getAndIncrement()
        return currentIndex
    }
}