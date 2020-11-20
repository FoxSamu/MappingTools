package net.shadew.asm.mappings.io;

import java.io.*;

import net.shadew.asm.mappings.model.*;

public final class BinaryMappingsIO {
    private BinaryMappingsIO() {
    }

    public static void write(Mappings mappings, DataOutputStream stream) {
        try (Out out = new Out(stream)) {
            out.writeMappings(mappings);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(Mappings mappings, DataOutput stream) {
        try (Out out = new Out(stream)) {
            out.writeMappings(mappings);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(Mappings mappings, OutputStream stream) {
        try (Out out = new Out(new DataOutputStream(stream))) {
            out.writeMappings(mappings);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Mappings read(DataInputStream stream) {
        try (In in = new In(stream)) {
            return in.readMappings();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Mappings read(DataInput stream) {
        try (In in = new In(stream)) {
            return in.readMappings();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Mappings read(InputStream stream) {
        try (In in = new In(new DataInputStream(stream))) {
            return in.readMappings();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class Out implements AutoCloseable {
        private final DataOutput out;
        private final Closeable close;

        Out(DataOutputStream out) {
            this.out = out;
            this.close = out;
        }

        Out(DataOutput out) {
            this.out = out;
            this.close = () -> {};
        }

        void writeValue(String val) throws IOException {
            if (val == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                out.writeUTF(val);
            }
        }

        void writeLvt(LvtMapping lvt) throws IOException {
            out.writeInt(lvt.index());
            out.writeUTF(lvt.name());
            out.writeUTF(lvt.desc());
            writeValue(lvt.get());
        }

        void writeField(FieldMapping field) throws IOException {
            out.writeUTF(field.name());
            writeValue(field.get());
        }

        void writeMethod(MethodMapping method) throws IOException {
            out.writeUTF(method.name());
            out.writeUTF(method.desc());
            writeValue(method.get());
            out.writeShort((int) method.lvts().count());
            try {
                method.lvts().forEach(lvt -> {
                    try {
                        writeLvt(lvt);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }

        void writeType(TypeMapping type) throws IOException {
            out.writeUTF(type.name());
            writeValue(type.get());
            try {
                out.writeInt((int) type.fields().count());
                type.fields().forEach(field -> {
                    try {
                        writeField(field);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
                out.writeInt((int) type.methods().count());
                type.methods().forEach(method -> {
                    try {
                        writeMethod(method);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }

        void writeMappings(Mappings mappinsg) throws IOException {
            try {
                out.writeInt((int) mappinsg.types().count());
                mappinsg.types().forEach(type -> {
                    try {
                        writeType(type);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }

        @Override
        public void close() throws IOException {
            close.close();
        }
    }

    private static class In implements AutoCloseable {
        private final DataInput in;
        private final Closeable close;

        In(DataInputStream in) {
            this.in = in;
            this.close = in;
        }

        In(DataInput in) {
            this.in = in;
            this.close = () -> {};
        }

        String readValue() throws IOException {
            return in.readBoolean() ? in.readUTF() : null;
        }

        LvtMapping readLvt(MethodMapping parent) throws IOException {
            return parent.newLvt(
                in.readInt(),
                in.readUTF(),
                in.readUTF(),
                readValue()
            );
        }

        FieldMapping readField(TypeMapping parent) throws IOException {
            return parent.newField(
                in.readUTF(),
                readValue()
            );
        }

        MethodMapping readMethod(TypeMapping parent) throws IOException {
            MethodMapping out = parent.newMethod(
                in.readUTF(),
                in.readUTF(),
                readValue()
            );
            short lvts = in.readShort();
            while (lvts-- > 0) readLvt(out);
            return out;
        }

        TypeMapping readType(Mappings parent) throws IOException {
            TypeMapping out = parent.newType(
                in.readUTF(),
                readValue()
            );
            int fds = in.readInt();
            while (fds-- > 0) readField(out);
            int mds = in.readInt();
            while (mds-- > 0) readMethod(out);
            return out;
        }

        Mappings readMappings() throws IOException {
            Mappings out = Mappings.create();
            int types = in.readInt();
            while (types-- > 0) readType(out);
            return out;
        }

        @Override
        public void close() throws IOException {
            close.close();
        }
    }
}
