package com.zyx.decorator;

public class Client {

    public static void main(String[] args) {
        Component component = new ConcreteDecorator2(new ConcreteDecorator1(new ConcreteComonent()));

        component.doSomething();
    }
}
