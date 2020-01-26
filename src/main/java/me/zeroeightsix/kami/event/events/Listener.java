package me.zeroeightsix.kami.event.events;

import javax.xml.stream.EventFilter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

    /**
     * An array of class filters that will be used to
     * determine if an event listener should be dispatched
     * or not.
     *
     * @return array of filters
     */
    Class<? extends EventFilter>[] filters() default { };

    /**
     * The priority of the event listener in the container.
     *
     * @return listener priority
     */
    ListenerPriority priority() default ListenerPriority.NORMAL;
}
