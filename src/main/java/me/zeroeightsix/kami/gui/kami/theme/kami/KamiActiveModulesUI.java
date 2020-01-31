package me.zeroeightsix.kami.gui.kami.theme.kami;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.gui.kami.component.ActiveModules;
import me.zeroeightsix.kami.gui.rgui.component.AlignedComponent;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.util.Wrapper;
import me.zeroeightsix.kami.module.modules.render.HUD;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glDisable;

/**
 * Created by 086 on 4/08/2017.
 */
public class KamiActiveModulesUI extends AbstractComponentUI<ActiveModules> {
    @Override
    public void renderComponent(ActiveModules component, FontRenderer f) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        FontRenderer renderer = Wrapper.getFontRenderer();
        List<Module> mods = ModuleManager.getModules().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparing(module -> renderer.getStringWidth(module.getName()+(module.getHudInfo()==null?"":module.getHudInfo()+" "))*(component.sort_up?-1:1)))
                .collect(Collectors.toList());

        final int[] y = {2};


        boolean lAlign = component.getAlignment() == AlignedComponent.Alignment.LEFT;
        Function<Integer, Integer> xFunc;
        switch (component.getAlignment()) {
            case RIGHT:
                xFunc = i -> component.getWidth() - i;
                break;
            case CENTER:
                xFunc = i -> component.getWidth() / 2 - i / 2;
                break;
            case LEFT:
            default:
                xFunc = i -> 0;
                break;
        }

        mods.stream().forEach(module -> {
            String s = module.getHudInfo();
            String text = module.getName() + (s==null?"" : " " + Command.SECTIONSIGN() + "7" + s);
            int textwidth = renderer.getStringWidth(text);
            int textheight = renderer.getFontHeight()+1;
            int red = HUD.red()
            int green = HUD.green();
            int blue = HUD.blue();

            renderer.drawStringWithShadow(xFunc.apply(textwidth), y[0], red,green,blue, text);
            hue[0] +=.02f;
            y[0] += textheight;
        });

        component.setHeight(y[0]);

        GL11.glEnable(GL11.GL_CULL_FACE);
        glDisable(GL_BLEND);
    }

    @Override
    public void handleSizeComponent(ActiveModules component) {
        component.setWidth(100);
        component.setHeight(100);
    }
}
