package ru.xcam.evotor.example.device

import android.os.RemoteException
import ru.evotor.devices.drivers.IVirtualDriverManagerService

class MyDriverManagerStub(private val myDeviceService: MyDeviceService) :
    IVirtualDriverManagerService.Stub() {
    @Throws(RemoteException::class)
    override fun addNewVirtualDevice(): Int {
        return myDeviceService.createNewDevice()
    }

    @Throws(RemoteException::class)
    override fun recreateNewVirtualDevice(instanceId: Int) {
    }

    @Throws(RemoteException::class)
    override fun destroy(i: Int) {
    }

}