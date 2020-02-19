package me.zeroeightsix.kami.gui.kami.theme.kami;

import me.zeroeightsix.kami.gui.kami.RenderHelper;
import me.zeroeightsix.kami.gui.kami.component.SettingsPanel;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import me.zeroeightsix.kami.module.modules.render.HUD;

import static org.lwjgl.opengl.GL11.glColor4f;

/**
 * Created by 086 on 6/08/2017.
 */
public class RootSettingsPanelUI extends AbstractComponentUI<SettingsPanel> {
    float red = 0;
    float green = 0;
    float blue = 0;
    @Override
    public void renderComponent(SettingsPanel component, FontRenderer fontRenderer) {
        red = HUD.redF() / 255f;
        green = HUD.greenF() / 255f;
        blue = HUD.blueF() / 255f;
        glColor4f(1f,0,1f,1f);
        RenderHelper.drawOutlinedRoundedRectangle(0,0,component.getWidth(),component.getHeight(), 6f, red,green,blue,.6f,1f);
    }

}
