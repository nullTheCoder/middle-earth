package net.jesteur.me.world.trees;

import net.jesteur.me.MiddleEarth;
import net.jesteur.me.mixin.TrunkPlacerTypeInvoker;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class ModTrunkPlacerTypes {

    public static final TrunkPlacerType<LargeSeparatingTrunkPlacer> LARGE_SEPARATING_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("me:large_separating_trunk_placer", LargeSeparatingTrunkPlacer.CODEC);

    public static void registerModTrunkPlacerTypes() {
        MiddleEarth.LOGGER.debug("Registering TrunkPlacerTypes for " + MiddleEarth.MOD_ID);
    }

}
