package com.github.zly2006.reden.network

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d
import java.util.*


class TntSyncPacket(
    val projectileUUID: UUID,
    val projectilePos: Vec3d,
    val projectileMotion: Vec3d,
    val tntPower: Float,
    val tntPos: Vec3d
): FabricPacket {
    override fun write(buf: PacketByteBuf) {
        buf.writeUuid(projectileUUID)
        buf.writeDouble(projectilePos.x)
        buf.writeDouble(projectilePos.y)
        buf.writeDouble(projectilePos.z)
        buf.writeDouble(projectileMotion.x)
        buf.writeDouble(projectileMotion.y)
        buf.writeDouble(projectileMotion.z)
        buf.writeFloat(tntPower)
        buf.writeDouble(tntPos.x)
        buf.writeDouble(tntPos.y)
        buf.writeDouble(tntPos.z)
    }

    override fun getType(): PacketType<*> = pType

    companion object {
        val pType = PacketType.create(TNT_SYNC_PACKET) {
            val projectileUUID = it.readUuid()
            val projectilePos = Vec3d(it.readDouble(), it.readDouble(), it.readDouble())
            val projectileMotion = Vec3d(it.readDouble(), it.readDouble(), it.readDouble())
            val tntPower = it.readFloat()
            val tntPos = Vec3d(it.readDouble(), it.readDouble(), it.readDouble())
            TntSyncPacket(projectileUUID, projectilePos, projectileMotion, tntPower, tntPos)
        }
        val syncedTntPos = mutableSetOf<Vec3d>()
        fun register() {
            ServerTickEvents.END_SERVER_TICK.register {
                syncedTntPos.clear()
            }
        }
    }
}