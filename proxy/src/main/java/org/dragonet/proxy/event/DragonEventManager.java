package org.dragonet.proxy.event;

import org.dragonet.api.Proxy;
import org.dragonet.api.event.*;
import org.pf4j.Plugin;
import org.pf4j.PluginState;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

@SuppressWarnings({"Unused", "WeakerAccess"})
public class DragonEventManager implements EventManager {

    private Logger logger;
    private HashMap<Class<? extends Event>, HandlerList> usedHandlers;

    @Inject
    public DragonEventManager(Proxy proxy, Logger logger) {
        this.logger = logger;
        usedHandlers = new HashMap<>();
        proxy.getPluginManager().addPluginStateListener(event -> {
            if (event.getPluginState() == PluginState.DISABLED || event.getPluginState() == PluginState.STOPPED) {
                unregisterEvents(event.getPlugin().getPlugin());
            }
        });
    }

    @Override
    public void registerEvents(Plugin plugin, Listener listener) {
        Collection<Method> methods = getMethodsRecursively(listener.getClass(), Event.class);
        for (Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);
            if (handler != null) {
                if (method.getParameters().length != 1 || (method.getParameters()[0].getType()).isAssignableFrom(Event.class)) {
                    logger.warn("The EventHandler method '" + method.getName() + "' must have exactly 1 parameter extending Event!");
                    continue;
                }
                try {
                    Field handlerField = null;
                    try {
                        handlerField = method.getParameters()[0].getType().getDeclaredField("handlerList");
                        handlerField.setAccessible(true);
                    } catch (Exception ex) {
                        logger.warn("The Event " + method.getParameters()[0].getType().getName() + " is missing this line: 'private static final HandlerList handlerList = new HandlerList();'");
                        continue;
                    }
                    HandlerList eventHandler = (HandlerList) handlerField.get(null);
                    method.setAccessible(true);
                    eventHandler.registerListener(new WrappedListener(plugin, listener, method));
                    usedHandlers.put((Class<? extends Event>) method.getParameters()[0].getType(), eventHandler);
                } catch (SecurityException | IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void unregisterEvents(Listener listener) {
        for (HandlerList handler : usedHandlers.values()) {
            handler.unregisterListener(listener);
        }
    }

    @Override
    public void unregisterEvents(Plugin plugin) {
        for (HandlerList handler : usedHandlers.values()) {
            handler.unregisterListener(plugin);
        }
    }

    @Override
    public void callEvent(Event event) {
        HandlerList handler = usedHandlers.get(event.getClass());
        if (handler != null) {
            for (WrappedListener wrappedListener : handler.getHandlerList()) {
                try {
                    wrappedListener.callEvent(event);
                } catch (Exception ex) {
                    logger.warn("Error when passing event " + event.getClass().getName() + " to " + wrappedListener.getListener().getClass().getName() + " by " + wrappedListener.getPlugin().getWrapper().getPluginId());
                }
            }
        }
    }

    private Collection<Method> getMethodsRecursively(Class<?> startClass, Class<?> exclusiveParent) {
        Collection<Method> methods = Arrays.asList(startClass.getDeclaredMethods());
        Class<?> parentClass = startClass.getSuperclass();
        if (parentClass != null && (!(parentClass.equals(exclusiveParent)))) {
            methods.addAll(getMethodsRecursively(parentClass, exclusiveParent));
        }
        return methods;
    }

}
