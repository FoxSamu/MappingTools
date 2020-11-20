package net.shadew.asm.mappings.io;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.shadew.asm.mappings.model.Mappings;
import net.shadew.asm.mappings.model.TypeMapping;

public final class ProguardMappingsIO {
    private final UndoableLineScanner scanner;
    private final List<ClassRule> rules = new ArrayList<>();
    private final Mappings mappings = Mappings.create();

    private ProguardMappingsIO(LineScanner scanner) {
        this.scanner = new UndoableLineScanner(scanner);
    }

    private static final Pattern CLASS_RULE = Pattern.compile("(\\S+?)\\s*->\\s*(\\S+?):");

    private Mappings readMappings() throws IOException {
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (line.trim().isEmpty()) continue;
            if (line.trim().startsWith("#")) continue;

            Matcher matcher = CLASS_RULE.matcher(line);
            if (!matcher.matches())
                throw new IOException("Did not find class rule: " + line);

            String a = internalName(matcher.group(1));
            String b = internalName(matcher.group(2));

            ClassRule rule = new ClassRule(a, b);
            while (scanner.hasNext()) {
                String next = scanner.next();
                if (!next.matches("^\\s+.+")) {
                    scanner.undo(next);
                    break;
                }

                rule.rules.add(next.trim());
            }

            rules.add(rule);
        }

        for (ClassRule rule : rules) {
            processMemberRules(rule, mappings.newType(rule.oldName, rule.newName));
        }

        return mappings;
    }

    private static final Pattern FIELD_RULE = Pattern.compile("(?:\\S+?)\\s+([^\\s(]+?)\\s*->\\s*(\\S+)");
    private static final Pattern METHOD_RULE = Pattern.compile("(?:\\d+?:\\d+?:)?(\\S+?)\\s+(\\S+?)\\((.*?)\\)\\s*->\\s*(\\S+)");

    private void processMemberRules(ClassRule cls, TypeMapping mapping) throws IOException {
        for (String rule : cls.rules) {
            Matcher field = FIELD_RULE.matcher(rule);
            if (field.matches()) {
                String a = field.group(1);
                String b = field.group(2);

                mapping.newField(a, b);
                continue;
            }

            Matcher method = METHOD_RULE.matcher(rule);
            if (method.matches()) {
                String ret = method.group(1);
                String a = method.group(2);
                String params = method.group(3);
                String b = method.group(4);

                StringBuilder desc = new StringBuilder("(");

                if (!params.isEmpty()) {
                    String[] pars = params.split("\\s*,\\s*");
                    for (String par : pars) {
                        desc.append(internalType(par));
                    }
                }

                desc.append(")");
                desc.append(internalType(ret));

                mapping.newMethod(a, desc.toString(), b);
                continue;
            }

            throw new IOException("Did not find method or field rule: " + rule);
        }
    }

    private String internalType(String external) {
        if (external.endsWith("[]")) {
            return "[" + internalType(external.substring(0, external.length() - 2));
        }
        switch (external) {
            case "float": return "F";
            case "double": return "D";
            case "byte": return "B";
            case "short": return "S";
            case "int": return "I";
            case "long": return "J";
            case "char": return "C";
            case "void": return "V";
            case "boolean": return "Z";
            default: return "L" + internalName(external) + ";";
        }
    }

    private String internalName(String external) {
        return external.replace('.', '/');
    }

    private static class ClassRule {
        final String oldName;
        final String newName;

        final List<String> rules = new ArrayList<>();

        private ClassRule(String oldName, String newName) {
            this.oldName = oldName;
            this.newName = newName;
        }
    }

    private static class UndoableLineScanner {
        final Stack<String> undoLines = new Stack<>();
        final LineScanner scanner;
        int lineNum;

        private UndoableLineScanner(LineScanner scanner) {
            this.scanner = scanner;
        }

        public boolean hasNext() throws IOException {
            return !undoLines.empty() || scanner.hasNext();
        }

        public String next() throws IOException {
            lineNum++;
            if (!undoLines.empty()) {
                return undoLines.pop();
            }
            return scanner.nextLine();
        }

        public void undo(String next) {
            undoLines.push(next);
            lineNum--;
        }
    }

    public static Mappings readMappings(LineScanner scanner) throws IOException {
        return new ProguardMappingsIO(scanner).readMappings();
    }

    public static Mappings readMappings(Reader reader) throws IOException {
        return readMappings(new LineScanner(reader));
    }

    public static Mappings readMappings(File file) throws IOException {
        return readMappings(new LineScanner(new FileReader(file)));
    }

    public static Mappings readMappings(String str) throws IOException {
        return readMappings(new LineScanner(new StringReader(str)));
    }
}
