package ru.xcam.evotor.example.device

import android.os.RemoteException
import ru.evotor.devices.drivers.IPaySystemDriverService
import ru.evotor.devices.drivers.paysystem.PayInfo
import ru.evotor.devices.drivers.paysystem.PayResult
import java.lang.IllegalStateException


class MyPaySystemStub() :
    IPaySystemDriverService.Stub() {
    @Throws(RemoteException::class)
    override fun payment(instanceId: Int, payInfo: PayInfo?): PayResult {
        throw NullPointerException("<Driver text of NPE>")
//        return PayResult("", emptyArray())
    }

    @Throws(RemoteException::class)
    override fun cancelPayment(instanceId: Int, payInfo: PayInfo?, rrn: String?): PayResult {
        return PayResult("", emptyArray())
    }

    @Throws(RemoteException::class)
    override fun payback(instanceId: Int, payInfo: PayInfo?, rrn: String?): PayResult {
        return PayResult("", emptyArray())
    }

    @Throws(RemoteException::class)
    override fun cancelPayback(instanceId: Int, payInfo: PayInfo?, rrn: String?): PayResult {
        return PayResult("", emptyArray())
    }

    @Throws(RemoteException::class)
    override fun closeSession(instanceId: Int): PayResult {
        return PayResult("", emptyArray())
    }

    @Throws(RemoteException::class)
    override fun openServiceMenu(instanceId: Int) {

    }

    @Throws(RemoteException::class)
    override fun getBankName(instanceId: Int): String {
        return "paySystemService.getPaySystem(instanceId).getBankName()"
    }

    @Throws(RemoteException::class)
    override fun getTerminalID(instanceId: Int): String {
        return "paySystemService.getPaySystem(instanceId).getTerminalID()"
    }

    @Throws(RemoteException::class)
    override fun getMerchNumber(instanceId: Int): String {
        return "paySystemService.getPaySystem(instanceId).getMerchEngName()"
    }

    @Throws(RemoteException::class)
    override fun getMerchCategoryCode(instanceId: Int): String {
        return "paySystemService.getPaySystem(instanceId).getMerchCategoryCode()"
    }

    @Throws(RemoteException::class)
    override fun getMerchEngName(instanceId: Int): String {
        return "paySystemService.getPaySystem(instanceId).getMerchEngName()"
    }

    @Throws(RemoteException::class)
    override fun getCashier(instanceId: Int): String {
        return "paySystemService.getPaySystem(instanceId).getCashier()"
    }

    @Throws(RemoteException::class)
    override fun getServerIP(instanceId: Int): String {
        return "paySystemService.getPaySystem(instanceId).getServerIP()"
    }

    override fun getTerminalNumber(p0: Int): Int {
        return 54321
    }

    @Throws(RemoteException::class)
    override fun isNotNeedRRN(instanceId: Int): Boolean {
        return true
    }
}