package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.init.Items;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "NoMiningTrace", category = Module.Category.MISC, description = "Blocks entities from stopping you from mining")
public class NoMiningTrace extends Module {

    private Setting<TraceMode> mode = register(Settings.e("Mode", TraceMode.DYNAMIC));

    private static NoMiningTrace INSTANCE;

    public NoMiningTrace() {
        NoMiningTrace.INSTANCE = this;
    }

    public static boolean shouldBlock() {
        return INSTANCE.isEnabled() && (INSTANCE.mode.getValue() == TraceMode.STATIC || mc.playerController.isHittingBlock) && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;
    }

    private enum TraceMode {
        STATIC, DYNAMIC
    }
}