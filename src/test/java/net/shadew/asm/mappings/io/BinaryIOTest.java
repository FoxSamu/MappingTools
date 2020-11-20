package net.shadew.asm.mappings.io;

import java.io.*;

import net.shadew.asm.mappings.model.Mappings;

public final class BinaryIOTest {
    private BinaryIOTest() {
    }

    public static void main(String[] args) {
        Mappings mappings = Mappings.create();
        mappings.newType("abc", "net/test/ABCClass")
                .newMethod("a", "()V", "aMethod").parent()
                .newMethod("b", "()V", "bMethod").parent()
                .newMethod("c", "(I)V", "cMethod")
                .newLvt(0, "", "Lnet/test/ABCClass;", "this").parent()
                .newLvt(1, "", "I", "i").parent()
                .parent()
                .newField("x", "xField").parent()
                .newField("y", "yField").parent();
        mappings.newType("def", "net/test/DEFClass")
                .newField("a", "aField").parent()
                .newField("b", "bField").parent();

        BinaryMappingsIO.write(mappings, new TestOut());
    }

    static class TestOut implements DataOutput {
        @Override
        public void write(int b) {
            System.out.printf("--- %02X%n", b);
        }

        @Override
        public void write(byte[] b) {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            for (int i = 0; i < len; i++) {
                write(b[i + off]);
            }
        }

        @Override
        public void writeBoolean(boolean v) {
            System.out.println("BLN " + v);
        }

        @Override
        public void writeByte(int v) {
            System.out.println("BYT " + v);
        }

        @Override
        public void writeShort(int v) {
            System.out.println("SRT " + v);
        }

        @Override
        public void writeChar(int v) {
            System.out.println("CHR '" + v + "'");
        }

        @Override
        public void writeInt(int v) {
            System.out.println("INT " + v);
        }

        @Override
        public void writeLong(long v) {
            System.out.println("LNG " + v);
        }

        @Override
        public void writeFloat(float v) {
            System.out.println("FLT " + v);
        }

        @Override
        public void writeDouble(double v) {
            System.out.println("DBL " + v);
        }

        @Override
        public void writeBytes(String s) {
            System.out.print("BTS " + s.length());
            System.out.println(" \"" + s + "\"");
        }

        @Override
        public void writeChars(String s) {
            System.out.print("CRS " + s.length());
            System.out.println(" \"" + s + "\"");
        }

        @Override
        public void writeUTF(String s) {
            System.out.print("UTF " + s.length());
            System.out.println(" \"" + s + "\"");
        }
    }
}
