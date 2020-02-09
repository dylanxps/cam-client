// 
// Decompiled by Procyon v0.5.36
// 

package me.zeroeightsix.kami.module.modules.combat;

import java.util.function.ToIntFunction;

import me.zeroeightsix.kami.module.modules.combat.AutoTotemDev;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiContainer;
import me.zeroeightsix.kami.module.modules.combat.AutoTotem;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "OffhandGap", category = Module.Category.COMBAT, description = "Auto Offhand Gapple")
public class OffhandGap extends Module
{
    private int gapples;
    private boolean moving;
    private boolean returnI;
    private Setting<Boolean> life;
    private Setting<Boolean> soft;
    private Setting<Boolean> totemOnDisable;
    private Setting<TotemMode> totemMode;
    
    public OffhandGap() {
        this.moving = false;
        this.returnI = false;
        this.life = this.register(Settings.b("life", false));
        this.soft = this.register(Settings.b("Soft", false));
        this.totemOnDisable = this.register(Settings.b("TotemOnDisable", true));
        this.totemMode = this.register((Setting<TotemMode>)Settings.enumBuilder(TotemMode.class).withName("TotemMode").withValue(TotemMode.KAMI).withVisibility(v -> this.totemOnDisable.getValue()).build());
    }
    
    public void onEnable() {
        if (ModuleManager.getModuleByName("AutoTotem").isEnabled()) {
            ModuleManager.getModuleByName("AutoTotem").disable();
        }
        if (ModuleManager.getModuleByName("AutoTotemDev").isEnabled()) {
            ModuleManager.getModuleByName("AutoTotemDev").disable();
        }
    }
    
    public void onDisable() {
        if (!this.totemOnDisable.getValue()) {
            return;
        }
        if (this.totemMode.getValue().equals(TotemMode.KAMI)) {
            final AutoTotem autoTotem = (AutoTotem)ModuleManager.getModuleByName("AutoTotem");
            autoTotem.disableSoft();
            if (autoTotem.isDisabled()) {
                autoTotem.enable();
            }
        }

    }
    
    @Override
    public void onUpdate() {
        if (OffhandGap.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.returnI) {
            int t = -1;
            for (int i = 0; i < 45; ++i) {
                if (OffhandGap.mc.player.inventory.getStackInSlot(i).isEmpty) {
                    t = i;
                    break;
                }
            }
            if (t == -1) {
                return;
            }
            OffhandGap.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
            this.returnI = false;
        }
        this.gapples = OffhandGap.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (OffhandGap.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            ++this.gapples;
        }
        else {
            if (this.soft.getValue() && !OffhandGap.mc.player.getHeldItemOffhand().isEmpty) {
                return;
            }
            if (this.moving) {
                OffhandGap.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
                this.moving = false;
                if (!OffhandGap.mc.player.inventory.itemStack.isEmpty()) {
                    this.returnI = true;
                }
                return;
            }
            if (OffhandGap.mc.player.inventory.itemStack.isEmpty()) {
                if (this.gapples == 0) {
                    return;
                }
                int t = -1;
                for (int i = 0; i < 45; ++i) {
                    if (OffhandGap.mc.player.inventory.getStackInSlot(i).getItem() == Items.GOLDEN_APPLE) {
                        t = i;
                        break;
                    }
                }
                if (t == -1) {
                    return;
                }
                OffhandGap.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
                this.moving = true;
            }
            else if (!this.soft.getValue()) {
                int t = -1;
                for (int i = 0; i < 45; ++i) {
                    if (OffhandGap.mc.player.inventory.getStackInSlot(i).isEmpty) {
                        t = i;
                        break;
                    }
                }
                if (t == -1) {
                    return;
                }
                OffhandGap.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
            }
        }




    }

    
    @Override
    public String getHudInfo() {
        return String.valueOf(this.gapples);
    }
    
    private enum TotemMode
    {
        KAMI, 
    }
}
