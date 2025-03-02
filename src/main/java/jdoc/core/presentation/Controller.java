package jdoc.core.presentation;

import jdoc.core.di.Injected;
import jdoc.core.di.Module;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Controller<T> {
    protected T argument;
    private Module module;
    public void setArgument(Object argument) {
        this.argument = (T) argument;
    }

    public void setModule(Module module) {
        this.module = module;
        for (var field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Injected.class)) {
                field.setAccessible(true);
                try {
                    field.set(this, inject(field.getType()));
                } catch (IllegalAccessException e) {
                    log.error(e.toString(), e);
                }
            }
        }
    }

    public <R> R inject(Class<R> clazz) {
        return module.get(clazz);
    }

    public abstract void init();
}
