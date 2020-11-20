package net.shadew.asm.mappings.io;

import java.io.*;

import net.shadew.asm.mappings.model.*;

public final class RMapMappingsIO {
    private RMapMappingsIO() {
    }

    public static Mappings read(Reader reader) throws IOException {
        return new In(new LineScanner(reader)).read();
    }

    public static void write(Appendable writer, Mappings mappings) throws IOException {
        new Out(writer).print(mappings);
    }

    private static class In {
        private final LineScanner scanner;
        private LnReader next;

        private In(LineScanner scanner) throws IOException {
            this.scanner = scanner;

            next = scanner.hasNext() ? new LnReader(scanner.nextLine(), 1) : null;
            while (next != null && next.isEmpty()) {
                next = scanner.hasNext() ? new LnReader(scanner.nextLine(), next.lineNum() + 1) : null;
            }
        }

        boolean hasNext() throws IOException {
            return next != null;
        }

        LnReader next() throws IOException {
            LnReader out = next;
            next = scanner.hasNext() ? new LnReader(scanner.nextLine(), out.lineNum() + 1) : null;
            while (next != null && next.isEmpty()) {
                next = scanner.hasNext() ? new LnReader(scanner.nextLine(), next.lineNum() + 1) : null;
            }
            out.reset();
            return out;
        }

        String peekType() throws IOException {
            if (next == null) return null;
            next.reset();
            return next.readType();
        }

        void next(TypeMapping type, String lineType, LnReader reader) throws IOException {
            if ("F".equals(lineType))
                reader.readField(type);
            else if ("M".equals(lineType)) {
                MethodMapping method = reader.readMethod(type);
                while ("L".equals(peekType())) {
                    LnReader next = next();
                    String t = next.readType();
                    if (!"L".equals(t))
                        throw new IOException(next.createError("Expected L-type"));
                    next.readLvt(method);
                }
            } else {
                throw new IOException(next.createError("Expected F-type or M-type"));
            }
        }

        private static boolean isFOrM(String t) {
            return "F".equals(t) || "M".equals(t);
        }

        Mappings read() throws IOException {
            Mappings out = Mappings.create();
            while ("C".equals(peekType())) {
                LnReader next = next();
                String t = next.readType();
                if (!t.equals("C"))
                    throw new IOException(next.createError("Expected C-type"));
                TypeMapping mapping = next.readType(out);

                while (isFOrM(peekType())) {
                    LnReader nextLn = next();
                    String type = nextLn.readType();
                    next(mapping, type, nextLn);
                }
            }
            return out;
        }
    }

    private static class LnReader {
        private int cursor;
        private final String line;
        private final int lineNum;

        private LnReader(String line, int lineNum) {
            this.line = line;
            this.lineNum = lineNum;
        }

        private void skip(int amount) {
            cursor += amount;
        }

        private char peek() {
            return line.charAt(cursor);
        }

        private char read() {
            return line.charAt(cursor++);
        }

        private boolean end() {
            return cursor >= line.length();
        }

        private static boolean isWs(char c) {
            return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
        }

        private void skipWs() {
            while (!end() && isWs(peek())) skip(1);
        }

        String createError(String problem) {
            StringBuilder arrow = new StringBuilder();
            for (int i = 0; i < cursor; i++)
                arrow.append(' ');
            arrow.append('^');
            return String.format("Line %d: %s%n%s%n%s", lineNum, problem, line, arrow);
        }

        int lineNum() {
            return lineNum;
        }

        void reset() {
            cursor = 0;
        }

        String readType() throws IOException {
            skipWs();
            if (end() || peek() != '[') throw new IOException(createError("Expected '['"));
            skip(1);
            int start = cursor;
            while (!end() && peek() != ']') skip(1);
            int end = cursor;
            if (end() || peek() != ']') throw new IOException(createError("Expected ']'"));
            skip(1);
            return line.substring(start, end);
        }

        String readString() throws IOException {
            skipWs();
            if (end() || peek() != '"') throw new IOException(createError("Expected '\"'"));
            skip(1);
            int start = cursor;
            while (!end() && peek() != '"') skip(1);
            int end = cursor;
            if (end() || peek() != '"') throw new IOException(createError("Expected '\"'"));
            skip(1);
            return line.substring(start, end);
        }

        private static boolean isNum(char c) {
            return c >= '0' && c <= '9';
        }

        private static boolean isWord(char c) {
            return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'
                       || c == '_' || c == '$' || c == '.' || c == '-' || c == '+';
        }

        int readInt() throws IOException {
            skipWs();
            int num = 0;
            boolean overflow = false;
            while (!end() && isWord(peek())) {
                char c = read();
                if (!isNum(c)) throw new IOException(createError("Expected digit"));
                if (overflow) continue;
                if (num * 10 < 0) {
                    overflow = true;
                    num = Integer.MAX_VALUE;
                    continue;
                }
                num *= 10;
                num += c - '0';
            }
            return num;
        }

        void expect(char c) throws IOException {
            skipWs();
            if (end() || peek() != c) throw new IOException(createError("Expected '" + c + "'"));
            skip(1);
        }

        void expectEnd() throws IOException {
            skipWs();
            if (!end() && peek() != '#')
                throw new IOException(createError("Extra line input"));
        }

        boolean isEmpty() {
            skipWs();
            return end() || peek() == '#';
        }

        FieldMapping readField(TypeMapping parent) throws IOException {
            String name = readString();
            expect('=');
            String remap = readString();
            expectEnd();
            return parent.newField(name, remap);
        }

        LvtMapping readLvt(MethodMapping parent) throws IOException {
            int index = readInt();
            expect(',');
            String desc = readString();
            expect(',');
            String name = readString();
            expect('=');
            String remap = readString();
            expectEnd();
            return parent.newLvt(index, name, desc, remap);
        }

        MethodMapping readMethod(TypeMapping parent) throws IOException {
            String name = readString();
            expect(',');
            String desc = readString();
            expect('=');
            String remap = readString();
            expectEnd();
            return parent.newMethod(name, desc, remap);
        }

        TypeMapping readType(Mappings parent) throws IOException {
            String name = readString();
            expect('=');
            String remap = readString();
            expectEnd();
            return parent.newType(name, remap);
        }
    }

    private static class Out {
        private final Appendable out;

        private Out(Appendable out) {
            this.out = out;
        }

        void printField(FieldMapping mapping) {
            try {
                out.append(String.format("    [F] \"%s\" = \"%s\"%n", mapping.name(), mapping.remap()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        void printLvt(LvtMapping mapping) {
            try {
                out.append(String.format("        [L] %d, \"%s\", \"%s\" = \"%s\"%n", mapping.index(), mapping.desc(), mapping.name(), mapping.remap()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        void printMethod(MethodMapping mapping) {
            try {
                out.append(String.format("    [M] \"%s\", \"%s\" = \"%s\"%n", mapping.name(), mapping.desc(), mapping.remap()));
                mapping.lvts().forEach(this::printLvt);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        void printType(TypeMapping mapping) {
            try {
                out.append(String.format("[C] \"%s\" = \"%s\"%n", mapping.name(), mapping.remap()));
                mapping.fields().forEach(this::printField);
                mapping.methods().forEach(this::printMethod);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        void print(Mappings mappings) throws IOException {
            try {
                mappings.types().forEach(this::printType);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }
    }
}
