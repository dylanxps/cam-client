package me.zeroeightsix.kami.module.modules.movement;


import me.zeroeightsix.kami.event.events.EventPlayerUpdate;
import me.zeroeightsix.kami.event.events.EventStageable;
import me.zeroeightsix.kami.event.events.Listener;

import me.zeroeightsix.kami.event.events.Value;
import me.zeroeightsix.kami.gui.kami.component.UnboundSlider;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.client.Minecraft;


@Module.Info(name = "Timer", category = Module.Category.HIDDEN, description = "timer. my nigga")
public class Timer extends Module {}


    /*    public final class TimerModule extends Module {

                public final Value<Float> speed = new Value<Float>("Speed", new String[]{"Spd"}, "Tick-rate multiplier. [(20tps/second) * (this value)]", 4.0f, 0.0f, 10.0f, 0.1f);



                @Override
                public void onDisable() {
                        super.onDisable();
                        Minecraft.getMinecraft().timer.tickLength = 50;
                }


                public String getMetaData() {
                        return "" + this.speed.getValue();
                }

                @Listener
                public void onUpdate(EventPlayerUpdate event) {
                        if(event.getStage() == EventStageable.EventStage.PRE) {
                                Minecraft.getMinecraft().timer.tickLength = 50.0f / speed.getValue();
                        }
                }

     *///   }}