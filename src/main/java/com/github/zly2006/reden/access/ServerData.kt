package com.github.zly2006.reden.access

import net.minecraft.server.MinecraftServer
import java.util.*

class ServerData {
    var status: Long = 0
    var uuid: UUID? = null
    var address: String = ""
    interface ServerDataAccess {
        fun getRedenServerData(): ServerData
    }

    interface ClientSideServerDataAccess {
        fun getRedenServerData(): ServerData
    }

    companion object {
        fun MinecraftServer.data(): ServerData {
            return (this as ServerDataAccess).getRedenServerData()
        }
    }
}