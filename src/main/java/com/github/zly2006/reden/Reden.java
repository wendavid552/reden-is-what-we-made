package com.github.zly2006.reden;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.github.zly2006.reden.access.PlayerData;
import com.github.zly2006.reden.carpet.RedenCarpetSettings;
import com.github.zly2006.reden.fakePlayer.FakeConnection;
import com.github.zly2006.reden.fakePlayer.RedenFakePlayer;
import com.github.zly2006.reden.network.ChannelsKt;
import com.github.zly2006.reden.rvc.RvcCommandKt;
import com.github.zly2006.reden.transformers.ThisIsReden;
import com.github.zly2006.reden.utils.ResourceLoader;
import com.github.zly2006.reden.utils.UtilsKt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class Reden implements ModInitializer, CarpetExtension {
    public static final String MOD_ID = "reden";
    public static final String MOD_NAME = "Reden";
    public static final String CONFIG_FILE = "reden.json";
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static final Version MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion();
    public static final Logger LOGGER = LoggerFactory.getLogger("reden");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final int REDEN_HIGHEST_MIXIN_PRIORITY = 10;

    @Override
    public String version() {
        return "reden";
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(RedenCarpetSettings.class);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return ResourceLoader.loadLang(lang);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(UtilsKt::setServer);
        ChannelsKt.register();
        CarpetServer.manageExtension(this);
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            boolean isDev = true;
            // Debug command
            if (FabricLoader.getInstance().isDevelopmentEnvironment() || isDev) {
                dispatcher.register(CommandManager.literal("fake-player")
                        .then(CommandManager.literal("spawn")
                                .then(CommandManager.argument("name", StringArgumentType.word())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            UUID uuid = Uuids.getOfflinePlayerUuid(name);
                                            RedenFakePlayer fakePlayer = RedenFakePlayer.create(
                                                    context.getSource().getServer(),
                                                    new GameProfile(uuid, name),
                                                    true
                                            );
                                            FakeConnection fakeConnection = new FakeConnection();
                                            fakeConnection.register(context.getSource().getServer().getNetworkIo());
                                            context.getSource().getServer().getPlayerManager().onPlayerConnect(
                                                    fakeConnection,
                                                    fakePlayer,
                                                    new ConnectedClientData(
                                                            new GameProfile(uuid, name),
                                                            0,
                                                            SyncedClientOptions.createDefault()
                                                    )
                                            );
                                            return 1;
                                        })
                                )));
                dispatcher.register(CommandManager.literal("reden-debug")
                                .then(CommandManager.literal("top-undo").executes(context -> {
                                    PlayerData.Companion.data(context.getSource().getPlayer()).topUndo();
                                    return 1;
                                }))
                                .then(CommandManager.literal("top-redo").executes(context -> {
                                    PlayerData.Companion.data(context.getSource().getPlayer()).topRedo();
                                    return 1;
                                }))
                        .then(CommandManager.literal("shadow-item")
                                .then(CommandManager.argument("item", ItemStackArgumentType.itemStack(access))
                                        .executes(context -> {
                                            ItemStackArgument itemStackArgument = ItemStackArgumentType.getItemStackArgument(context, "item");
                                            ItemStack stack = itemStackArgument.createStack(1, true);
                                            PlayerInventory inventory = context.getSource().getPlayer().getInventory();
                                            for (int i = 0; i < 2; i++) {
                                                int emptySlot = inventory.getEmptySlot();
                                                inventory.setStack(emptySlot, stack);
                                            }
                                            context.getSource().getPlayer().currentScreenHandler.syncState();
                                            return 1;
                                        })))
                        .then(CommandManager.literal("delay-test")
                                .executes(context -> {
                                    try {
                                        Thread.sleep(35 * 1000);
                                    } catch (InterruptedException ignored) {
                                    }
                                    context.getSource().sendMessage(Text.of("35 seconds passed"));
                                    return 1;
                                })));
            }
            RvcCommandKt.register(dispatcher);
            if (!(dispatcher instanceof ThisIsReden)) {
                throw new RuntimeException("This is not Reden!");
            } else {
                LOGGER.info("This is Reden!");
            }
        });
    }

    @Contract("_ -> new")
    public static @NotNull Identifier identifier(@NotNull String id) {
        return new Identifier(MOD_ID, id);
    }
}
