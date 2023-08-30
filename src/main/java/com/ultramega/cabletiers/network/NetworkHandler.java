package com.ultramega.cabletiers.network;

import com.ultramega.cabletiers.CableTiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private final String protocolVersion = Integer.toString(1);
    private final ResourceLocation channel = new ResourceLocation(CableTiers.MOD_ID, "main_channel");
    private final SimpleChannel handler = NetworkRegistry.ChannelBuilder
            .named(channel)
            .clientAcceptedVersions(protocolVersion::equals)
            .serverAcceptedVersions(protocolVersion::equals)
            .networkProtocolVersion(() -> protocolVersion)
            .simpleChannel();

    public void register() {
        int id = 0;
        this.handler.registerMessage(id++, TieredSetFilterSlotMessage.class, TieredSetFilterSlotMessage::encode, TieredSetFilterSlotMessage::decode, TieredSetFilterSlotMessage::handle);
    }

    public void sendToServer(Object message) {
        this.handler.sendToServer(message);
    }
}
