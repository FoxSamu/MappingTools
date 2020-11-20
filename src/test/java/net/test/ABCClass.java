package net.test;

public class ABCClass {
    public DEFClass xField;
    public String yField;

    public void aMethod() {
        System.out.println(yField);
    }

    public ABCClass bMethod(DEFClass def, String str) {
        ABCClass cls = new ABCClass();
        cls.xField = def;
        cls.yField = str;
        return cls;
    }

    public void cMethod(int i) {
        while (i-- > 0) System.out.println(yField);
    }
}
