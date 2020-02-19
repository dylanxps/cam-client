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
	private Setting<Integer> r = register(Settings.integerBuilder("BG Red").withMinimum(1).withMaximum(255).withValue(63));
	private Setting<Integer> g = register(Settings.integerBuilder("BG Green").withMinimum(1).withMaximum(255).withValue(0));
	private Setting<Integer> b = register(Settings.integerBuilder("BG Blue").withMinimum(1).withMaximum(255).withValue(31));
    private Setting<Integer> ro = register(Settings.integerBuilder("Outline Red").withMinimum(1).withMaximum(255).withValue(255));
    private Setting<Integer> go = register(Settings.integerBuilder("Outline Green").withMinimum(1).withMaximum(255).withValue(0));
    private Setting<Integer> bo = register(Settings.integerBuilder("Outline Blue").withMinimum(1).withMaximum(255).withValue(255));
    private Setting<Boolean> back = register(Settings.b("Default", false));
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
    public static int Ored() {
        return INSTANCE.ro.getValue();
    }
    public static int Ogreen() {
        return INSTANCE.go.getValue();
    }
    public static int Oblue() {
        return INSTANCE.bo.getValue();
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

    @Override
    public void onUpdate() {
        if(back.getValue()) {
            r.setValue(63);
            g.setValue(0);
            b.setValue(31);
            ro.setValue(255);
            go.setValue(0);
            bo.setValue(255);
            back.setValue(false);
        }
    }

    @Override
    protected void onDisable() {
        enable();
    }
}
