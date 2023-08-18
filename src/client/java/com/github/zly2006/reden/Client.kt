package com.github.zly2006.reden

import com.github.zly2006.reden.debugger.breakpoint.breakpoints
import com.github.zly2006.reden.network.Rollback
import com.github.zly2006.reden.network.TagBlockPos
import com.github.zly2006.reden.network.TntSyncPacket
import com.github.zly2006.reden.pearl.pearlTask
import com.github.zly2006.reden.render.BlockBorder
import com.github.zly2006.reden.utils.debugLogger
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.text.Text

fun init() {
    ClientPlayNetworking.registerGlobalReceiver(Rollback.pType) { packet, player, res ->
        player.sendMessage(
            when (packet.status) {
                0 -> Text.literal("[Reden/Undo] Rollback success")
                1 -> Text.literal("[Reden/Undo] Restore success")
                2 -> Text.literal("[Reden/Undo] No blocks info")
                16 -> Text.literal("[Reden/Undo] No permission")
                32 -> Text.literal("[Reden/Undo] Not recording")
                65536 -> Text.literal("[Reden/Undo] Unknown error")
                else -> Text.literal("[Reden/Undo] Unknown status")
            }
        )
    }

    ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
        breakpoints.clear()
    }

    ClientPlayNetworking.registerGlobalReceiver(TntSyncPacket.pType) { packet, client, _ ->
        pearlTask?.onTntSyncPacket(packet)
        debugLogger("TntSyncPacket: TNT${packet.tntPower} @ ${packet.tntPos}")
    }

    ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> BlockBorder.tags.clear()}
    ClientPlayNetworking.registerGlobalReceiver(TagBlockPos.pType) { packet, _, _ ->
        BlockBorder.tags[packet.pos.asLong()] = packet.status
    }
}