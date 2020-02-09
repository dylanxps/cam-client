// 
// Decompiled by Procyon v0.5.36
// 

package me.zeroeightsix.kami.module.modules.combat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemExpBottle;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "FastUse", category = Module.Category.PLAYER, description = "Makes you use stuff fast")
public class FastUse extends Module
{
    private Setting<Boolean> fastXP;
    private Setting<Boolean> fastBow;
    private Setting<Boolean> fastPlace;

    
    public FastUse() {
        this.fastXP = this.register(Settings.b("FastXP", true));
        this.fastBow = this.register(Settings.b("FastBow", false));
        this.fastPlace = this.register(Settings.b("FastPlace", false));

    }
    
    @Override
    public void onUpdate() {
        final Item main = FastUse.mc.player.getHeldItemMainhand().getItem();
        final Item off = FastUse.mc.player.getHeldItemOffhand().getItem();
        if (this.fastXP.getValue() && (main instanceof ItemExpBottle | off instanceof ItemExpBottle)) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
        if (this.fastBow.getValue() && FastUse.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && FastUse.mc.player.isHandActive() && FastUse.mc.player.getItemInUseMaxCount() >= 3) {
            FastUse.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, FastUse.mc.player.getHorizontalFacing()));
            FastUse.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(FastUse.mc.player.getActiveHand()));
            FastUse.mc.player.stopActiveHand();
        }
        if (this.fastPlace.getValue() && (main instanceof ItemBlock | off instanceof ItemBlock)) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
        if (main instanceof ItemFood | off instanceof ItemFood) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
    }
}
