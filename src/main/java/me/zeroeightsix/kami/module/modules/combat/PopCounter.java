// 
// Decompiled by Procyon v0.5.36
// 

package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.TotemPopEvent;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;

@Module.Info(name = "TotemPopCounter", description = "Counts the times your enemy pops", category = Module.Category.COMBAT)
public class PopCounter extends Module
{
    private HashMap<String, Integer> popList;
    @EventHandler
    public Listener<TotemPopEvent> totemPopEvent;
    @EventHandler
    public Listener<PacketEvent.Receive> totemPopListener;

    public PopCounter() {
        this.popList = new HashMap<String, Integer>();
        final int[] popCounter = new int[1];
        final int[] newPopCounter = new int[1];
        this.totemPopEvent = new Listener<TotemPopEvent>(event -> {
            if (this.popList == null) {
                this.popList = new HashMap<String, Integer>();
            }
            if (this.popList.get(event.getEntity().getName()) == null) {
                this.popList.put(event.getEntity().getName(), 1);
                Command.sendChatMessage("\u00A79" + event.getEntity().getName() + " popped " + 1 + " totem!");
            }
            else if (this.popList.get(event.getEntity().getName()) != null) {
                popCounter[0] = this.popList.get(event.getEntity().getName());
                newPopCounter[0] = ++popCounter[0];
                this.popList.put(event.getEntity().getName(), newPopCounter[0]);
                Command.sendChatMessage("\u00A79" + event.getEntity().getName() + " popped " + newPopCounter[0] + " totems!");
            }
        });
        final SPacketEntityStatus[] packet = new SPacketEntityStatus[1];
        final Entity[] entity = new Entity[1];
        this.totemPopListener = new Listener<PacketEvent.Receive>(event -> {
            if (PopCounter.mc.world != null && PopCounter.mc.player != null) {
                if (event.getPacket() instanceof SPacketEntityStatus) {
                    packet[0] = (SPacketEntityStatus)event.getPacket();
                    if (packet[0].getOpCode() == 35) {
                        entity[0] = packet[0].getEntity(PopCounter.mc.world);
                        KamiMod.EVENT_BUS.post(new TotemPopEvent(entity[0]));
                    }
                }
            }
        });
    }

    @Override
    public void onUpdate() {
        for (final EntityPlayer player : PopCounter.mc.world.playerEntities) {
            if (player.getHealth() <= 0.0f && this.popList.containsKey(player.getName())) {
                Command.sendChatMessage("\u00A7a" + player.getName() + " died after popping \u00A7a" + this.popList.get(player.getName()) + " \u00A7ftotems!");
                this.popList.remove(player.getName(), this.popList.get(player.getName()));
            }
        }
    }
}
