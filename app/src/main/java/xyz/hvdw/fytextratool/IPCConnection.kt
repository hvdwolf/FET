package xyz.hvdw.fytextratool;

import android.os.RemoteException
import com.aoe.fytcanbusmonitor.ConnectionObserver
import com.aoe.fytcanbusmonitor.IRemoteToolkit
import com.aoe.fytcanbusmonitor.RemoteModuleProxy

class IPCConnection(private val moduleId: Int) : ConnectionObserver {
    private val callbacks = arrayListOf<Pair<ModuleCallback, Int>>()
    private val remoteProxy = RemoteModuleProxy()

    override fun onConnected(toolkit: IRemoteToolkit?) {
        try {
            remoteProxy.remoteModule = toolkit!!.getRemoteModule(moduleId)

        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        for (callback in callbacks) {
            remoteProxy.register(callback.first, callback.second, 1)
        }
    }

     fun addCallback(callback: ModuleCallback, id: Int) {
         callbacks.add(Pair(callback, id))
     }
    // com.syu.module.ConnectionObserver
    override fun onDisconnected() {
        for (callback in callbacks) {
            remoteProxy.unregister(callback.first, callback.second)
        }
        remoteProxy.remoteModule = null
    }
}