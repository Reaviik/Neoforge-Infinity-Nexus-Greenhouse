package com.Infinity.Nexus.Greenhouse.compat;

import com.Infinity.Nexus.Greenhouse.block.custom.Greenhouse;
import com.Infinity.Nexus.Greenhouse.compat.jade.GreenhouseOwner;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeModPlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(GreenhouseOwner.INSTANCE, Greenhouse.class);

        //registration.registerItemStorageClient(TombstoneProvider.INSTANCE);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        //registration.registerItemStorage(MinerOwner.INSTANCE, MinerBlockEntity.class);
    }
}