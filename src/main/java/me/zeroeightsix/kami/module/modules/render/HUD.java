package me.zeroeightsix.kami.module.modules.render;

import java.awt.Color;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

/**
 * Created by 086 on 9/04/2018.
 */
@Module.Info(name = "HUD", description = "", category = Module.Category.RENDER)
public class HUD extends Module {
	private Setting<Integer> r = register(Settings.integerBuilder("Red").withMinimum(1).withMaximum(255).withValue(255));
	private Setting<Integer> g = register(Settings.integerBuilder("Green").withMinimum(1).withMaximum(255).withValue(0));
	private Setting<Integer> b = register(Settings.integerBuilder("Blue").withMinimum(1).withMaximum(255).withValue(255));
	private static HUD INSTANCE = new HUD();
	
    public HUD() {
        INSTANCE = this;
    }

	public static int red() {
        return INSTANCE.r.getValue();
    }
	public static int green() {
        return INSTANCE.g.getValue();
    }
	public static int blue() {
        return INSTANCE.b.getValue();
    }
	
	public static float redF() {
        return INSTANCE.r.getValue().floatValue();
    }
	public static float greenF() {
        return INSTANCE.g.getValue().floatValue();
    }
	public static float blueF() {
        return INSTANCE.b.getValue().floatValue();
    }
	
	public enum Pos {
		TopRight, TopLeft, BottomRight, BottomLeft, None;
	}
}
