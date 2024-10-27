package jdoc.presentation;

public abstract class Controller<T> {
    protected T argument;
    public void setArgument(Object argument) {
        this.argument = (T) argument;
    }

    public abstract void init();
}
