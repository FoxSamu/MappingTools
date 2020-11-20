package net.test;

public class DEFClass extends ABCClass {
    public static int aField;
    public static int bField;

    public void something() {
        this.bMethod(this, "hello");
    }
}
