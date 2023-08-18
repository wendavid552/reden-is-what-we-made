package com.github.zly2006.reden.network

import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier


data class BreakPointInterrupt(
    val bpId: Int,
    val interrupted: Boolean = true
): FabricPacket {
    companion object {
        val id = Identifier("reden", "breakpoint_interrupt")
        val pType = PacketType.create(id) {
            val id = it.readVarInt()
            BreakPointInterrupt(id)
        }
    }

    override fun write(buf: PacketByteBuf) {
        buf.writeVarInt(bpId)
    }

    override fun getType(): PacketType<BreakPointInterrupt> = pType
}