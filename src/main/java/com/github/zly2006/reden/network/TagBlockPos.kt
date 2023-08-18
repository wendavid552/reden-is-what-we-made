package com.github.zly2006.reden.network

import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class TagBlockPos(
    val world: Identifier,
    val pos: BlockPos,
    val status: Int
): FabricPacket {
    override fun getType(): PacketType<*> = pType
    override fun write(buf: PacketByteBuf) {
        buf.writeIdentifier(world)
        buf.writeBlockPos(pos)
        buf.writeVarInt(status)
    }

    companion object {
        val pType = run {
            PacketType.create(TAG_BLOCK_POS) {
                TagBlockPos(
                    it.readIdentifier(),
                    it.readBlockPos(),
                    it.readVarInt()
                )
            }
        }

        const val clear = 0
        const val green = 1
        const val red = 2
    }
}

private operator fun Vec3d.minus(pos: Vec3d): Vec3d {
    return Vec3d(x - pos.x, y - pos.y, z - pos.z)
}

private fun BlockPos.vec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
