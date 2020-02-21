//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import me.zeroeightsix.kami.module.Module.Info;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Info(
        name = "FovSlider",
        category = Category.RENDER
)
public class FovSlider extends Module {
    private Setting<Float> fov = this.register(Settings.floatBuilder("Value").withMinimum(0.0F).withValue(130.0F).withMaximum(170.0F).build());

    public FovSlider() {
    }

    public void onUpdate() {
        mc.gameSettings.fovSetting = (float)Math.round((Float)this.fov.getValue());
    }
}
