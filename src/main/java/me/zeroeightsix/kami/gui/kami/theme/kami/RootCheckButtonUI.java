package me.zeroeightsix.kami.gui.kami.theme.kami;

import me.zeroeightsix.kami.gui.kami.KamiGUI;
import me.zeroeightsix.kami.gui.rgui.component.container.Container;
import me.zeroeightsix.kami.gui.rgui.component.use.CheckButton;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 4/08/2017.
 */
public class RootCheckButtonUI<T extends CheckButton> extends AbstractComponentUI<CheckButton> {

    protected Color backgroundColour = new Color(27, 5, 200);
    protected Color backgroundColourHover = new Color(255, 1, 0);

    protected Color idleColourNormal = new Color(200, 18, 160);
    protected Color downColourNormal = new Color(0, 190, 124);

    protected Color idleColourToggle = new Color(250, 120, 120);
    protected Color downColourToggle = idleColourToggle.brighter();

    @Override
    public void renderComponent(CheckButton component, FontRenderer ff) {

        glColor4f(backgroundColour.getRed()/255f, backgroundColour.getGreen()/255f, backgroundColour.getBlue()/255f, component.getOpacity());
        if (component.isToggled()){
            glColor3f(.9f, backgroundColour.getGreen()/255f, backgroundColour.getBlue()/255f);
        }
        if (component.isHovered() || component.isPressed()){
            glColor4f(backgroundColourHover.getRed()/255f, backgroundColourHover.getGreen()/255f, backgroundColourHover.getBlue()/255f, component.getOpacity());
        }

        String text = component.getName();
        int c = component.isPressed() ? 0xaaaaaa : component.isToggled() ? 0xff00ff : 0xdddddd;
        if (component.isHovered())
            c = (c & 0x7f7f7f) << 1;

        glColor3f(1,1,1);
        glEnable(GL_TEXTURE_2D);
        KamiGUI.fontRenderer.drawString(1, KamiGUI.fontRenderer.getFontHeight()/2-2, c, text);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    @Override
    public void handleAddComponent(CheckButton component, Container container) {
        component.setWidth(KamiGUI.fontRenderer.getStringWidth("TotemPopCounter") + 3);
        component.setHeight(KamiGUI.fontRenderer.getFontHeight() + 2);
    }
}
