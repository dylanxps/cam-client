

package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import java.util.Iterator;
import java.util.List;
import me.zeroeightsix.kami.gui.rgui.component.container.Container;
import me.zeroeightsix.kami.gui.rgui.util.ContainerHelper;
import me.zeroeightsix.kami.gui.rgui.component.container.use.Frame;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.gui.kami.KamiGUI;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "InventoryViewer", category = Module.Category.RENDER)
public class Inventorypreview extends Module
{
    public boolean visible;
    KamiGUI kamiGUI;

    public Inventorypreview() {
        this.visible = false;
        this.kamiGUI = KamiMod.getInstance().getGuiManager();
    }

    private int invPos(final int i) {
        this.kamiGUI = KamiMod.getInstance().getGuiManager();
        if (this.kamiGUI != null) {
            final List<Frame> frames = ContainerHelper.getAllChildren((Class<? extends Frame>)Frame.class, (Container)this.kamiGUI);
            for (final Frame frame : frames) {
                if (!frame.getTitle().equalsIgnoreCase("inventory viewer")) {
                    continue;
                }
                switch (i) {
                    case 0: {
                        return frame.getX();
                    }
                    case 1: {
                        return frame.getY();
                    }
                    default: {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private boolean isPinned() {
        this.kamiGUI = KamiMod.getInstance().getGuiManager();
        if (this.kamiGUI != null) {
            final List<Frame> frames = ContainerHelper.getAllChildren((Class<? extends Frame>)Frame.class, (Container)this.kamiGUI);
            for (final Frame frame : frames) {
                if (!frame.getTitle().equalsIgnoreCase("inventory viewer")) {
                    continue;
                }
                return frame.isPinned();
            }
        }
        return false;
    }

    public void onDisable() {
        this.enable();
    }

    private static void preItemRender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
    }

    private static void postItemRender() {
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    @Override
    public void onRender() {
        final NonNullList<ItemStack> items = (NonNullList<ItemStack>)Inventorypreview.mc.player.inventory.mainInventory;
        if (this.isPinned()) {
            this.boxRender(this.invPos(0), this.invPos(1));
            this.itemRender(items, this.invPos(0), this.invPos(1));
        }
    }

    private void boxRender(final int x, final int y) {
        KamiTessellator.drawRect((float)(x + 165), (float)(y + 56), (float)x, (float)y, 1963986960);
    }

    private void itemRender(final NonNullList<ItemStack> items, final int x, final int y) {
        for (int size = items.size(), item = 9; item < size; ++item) {
            final int slotX = x + 1 + item % 9 * 18;
            final int slotY = y + 1 + (item / 9 - 1) * 18;
            preItemRender();
            Inventorypreview.mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack)items.get(item), slotX, slotY);
            Inventorypreview.mc.getRenderItem().renderItemOverlays(Inventorypreview.mc.fontRenderer, (ItemStack)items.get(item), slotX, slotY);
            postItemRender();
        }
    }
}
