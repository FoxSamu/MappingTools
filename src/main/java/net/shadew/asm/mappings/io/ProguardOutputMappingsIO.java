package net.shadew.asm.mappings.io;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.shadew.asm.descriptor.*;
import net.shadew.asm.mappings.model.*;

public final class ProguardOutputMappingsIO {
    private ProguardOutputMappingsIO() {
    }

    public static Mappings read(Reader reader) throws IOException {
        return new In(new LineScanner(reader)).read();
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

        boolean hasNext() {
            return next != null;
        }

        LnReader next() throws IOException {
            LnReader out = next;
            next = scanner.hasNext() ? new LnReader(scanner.nextLine(), out.lineNum() + 1) : null;
            while (next != null && next.isEmpty()) {
                next = scanner.hasNext() ? new LnReader(scanner.nextLine(), next.lineNum() + 1) : null;
            }
            if (out != null) out.reset();
            return out;
        }

        boolean nextIsType() throws IOException {
            return next != null && next.isType();
        }

        void next(TypeMapping type, boolean isField, LnReader reader) throws IOException {
            if (isField) reader.readField(type);
            else reader.readMethod(type);
        }

        Mappings read() throws IOException {
            Mappings out = Mappings.create();
            while (hasNext()) {
                if (!nextIsType())
                    throw new ParserException(next.createError("Expected class rule"));
                LnReader next = next();
                TypeMapping mapping = next.readType(out);

                while (this.next != null && !nextIsType()) {
                    LnReader nextLn = next();
                    boolean isField = nextLn.isField();
                    next(mapping, isField, nextLn);
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

        private int skipWs() {
            int c = cursor;
            while (!end() && isWs(peek())) skip(1);
            return cursor - c;
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

        private static boolean isNum(char c) {
            return c >= '0' && c <= '9';
        }

        private static boolean isWord(char c) {
            return c >= '0' && c <= '9'
                       || c >= 'a' && c <= 'z'
                       || c >= 'A' && c <= 'Z'
                       || c == '_' || c == '$'
                       || c == '.' || c == '-'
                       || c == '+' || c == '<'
                       || c == '>';
        }

        String readIdentifier() throws IOException {
            skipWs();
            int start = cursor;
            while (!end() && isWord(peek())) skip(1);
            int end = cursor;
            if (end == start)
                throw new ParserException(createError("Expected identifier"));
            return line.substring(start, end);
        }

        int readInt() throws IOException {
            skipWs();
            int num = 0;
            boolean overflow = false;
            while (!end() && isWord(peek())) {
                char c = read();
                if (!isNum(c)) throw new ParserException(createError("Expected digit"));
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
            if (end() || peek() != c) throw new ParserException(createError("Expected '" + c + "'"));
            skip(1);
        }

        char expect(char c1, char c2) throws IOException {
            skipWs();
            if (end() || peek() != c1 && peek() != c2)
                throw new ParserException(createError("Expected '" + c1 + "' or '" + c2 + "'"));
            return read();
        }

        void expect(String s) throws IOException {
            skipWs();
            if (!line.startsWith(s, cursor)) throw new ParserException(createError("Expected '" + s + "'"));
            skip(s.length());
        }

        void expectEnd() throws IOException {
            skipWs();
            if (!end() && peek() != '#')
                throw new ParserException(createError("Extra line input"));
        }

        boolean isEmpty() {
            skipWs();
            return end() || peek() == '#';
        }

        TypeDescriptor readBaseTypeDesc() throws IOException {
            String id = readIdentifier();
            switch (id) {
                case "void": return PrimitiveDescriptor.VOID;
                case "byte": return PrimitiveDescriptor.BYTE;
                case "short": return PrimitiveDescriptor.SHORT;
                case "int": return PrimitiveDescriptor.INT;
                case "long": return PrimitiveDescriptor.LONG;
                case "float": return PrimitiveDescriptor.FLOAT;
                case "double": return PrimitiveDescriptor.DOUBLE;
                case "char": return PrimitiveDescriptor.CHAR;
                case "boolean": return PrimitiveDescriptor.BOOLEAN;
                default: return ReferenceDescriptor.of(id.replace('.', '/'));
            }
        }

        TypeDescriptor readTypeDesc() throws IOException {
            TypeDescriptor base = readBaseTypeDesc();
            skipWs();
            while (!end() && peek() == '[') {
                skip(1);
                expect(']');
                base = ArrayDescriptor.of(base);
            }
            return base;
        }

        MethodDescriptor readMethodDesc(TypeDescriptor returnType) throws IOException {
            List<TypeDescriptor> descs = new ArrayList<>();

            expect('(');
            skipWs();
            if (peek() != ')') {
                while (!end()) {
                    descs.add(readTypeDesc());
                    char c = expect(',', ')');
                    if (c == ')') break;
                }
            } else {
                skip(1);
            }

            return MethodDescriptor.of(returnType, descs.toArray(new TypeDescriptor[0]));
        }

        FieldMapping readField(TypeMapping parent) throws IOException {
            readTypeDesc();                  // Type
            String name = readIdentifier();  // Name
            expect("->");
            String remap = readIdentifier(); // Mapping
            expectEnd();
            return parent.newField(name, remap);
        }

        MethodMapping readMethod(TypeMapping parent) throws IOException {
            try { // Optional line number indication
                readInt();
                expect(':');
                readInt();
                expect(':');
            } catch (ParserException ignored) {
            }

            TypeDescriptor ret = readTypeDesc();         // Return type
            String name = readIdentifier();              // Name
            MethodDescriptor desc = readMethodDesc(ret); // Descriptor
            expect("->");
            String remap = readIdentifier();             // Mapping
            expectEnd();
            return parent.newMethod(name, desc.toString(), remap);
        }

        TypeMapping readType(Mappings parent) throws IOException {
            String name = readIdentifier().replace('.', '/');
            expect("->");
            String remap = readIdentifier().replace('.', '/');
            expect(':');
            expectEnd();
            return parent.newType(name, remap);
        }

        boolean isType() throws IOException {
            try {
                readIdentifier();
                expect("->");
                readIdentifier();
                expect(':');
                expectEnd();
                reset();
                return true;
            } catch (ParserException e) {
                reset();
                return false;
            }
        }

        boolean isField() throws IOException {
            try {
                readTypeDesc();
                readIdentifier();
                expect("->");
                readIdentifier();
                expectEnd();
                reset();
                return true;
            } catch (ParserException e) {
                reset();
                return false;
            }
        }
    }

    private static class ParserException extends IOException {
        ParserException(String message) {
            super(message);
        }
    }
}
