package net.shadew.asm.mappings.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class LineScannerTest {
    @ParameterizedTest
    @MethodSource("randomSituations")
    void test(Situation situation) throws IOException {
        String[] lns = situation.getLines();
        String test = situation.createInput();

        LineScanner scanner = new LineScanner(new StringReader(test));
        int i = 0;
        System.out.println(situation);
        while (i < lns.length) {
            System.out.printf("%02d: %s%n", i + 1, lns[i]);
            Assertions.assertTrue(scanner.hasNext());
            Assertions.assertEquals(scanner.nextLine(), lns[i]);
            i++;
        }
    }

    static Stream<Situation> randomSituations() {
        List<Situation> situations = new ArrayList<>();
        for (int i = 0; i < 200; i++)
            situations.add(Situation.random());
        return situations.stream();
    }

    static class Situation {
        static final String RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-+=_[]{}|\\:;\"'/?.,<>~` \t";
        static final String LF = "\n";
        static final String CR = "\r";
        static final String CRLF = "\r\n";
        static final String[] LINE_ENDS = {LF, CR, CRLF};

        final String[] lines;
        final String separator;

        Situation(String[] lines, String separator) {
            this.lines = lines;
            this.separator = separator;
        }

        String[] getLines() {
            return lines;
        }

        String createInput() {
            return String.join(separator, lines);
        }

        static String randomString(Random rand) {
            if (rand.nextInt(5) <= 1) {
                return "";
            } else {
                StringBuilder test = new StringBuilder();
                if (rand.nextInt(4) == 0) {
                    int indent = rand.nextInt(5) * 4;
                    for (int i = 0; i < indent; ) {
                        if (indent - i > 4 && rand.nextBoolean()) {
                            test.append("\t");
                            i += 4;
                        } else {
                            test.append(" ");
                            i += 1;
                        }
                    }
                }
                int len = rand.nextInt(40) + 10;
                for (int i = 0; i < len; i++) {
                    test.append(RANDOM_CHARS.charAt(rand.nextInt(RANDOM_CHARS.length())));
                }
                return test.toString();
            }
        }

        static Situation random() {
            Random rand = new Random();
            List<String> list = new ArrayList<>();

            int len = rand.nextInt(20) + 3;
            for (; len > 0; len--) {
                list.add(randomString(rand));
            }
            if (rand.nextBoolean())
                list.add("");

            return new Situation(list.toArray(new String[0]), LINE_ENDS[rand.nextInt(3)]);
        }

        @Override
        public String toString() {
            return "Situation [lines: " + lines.length
                       + ", separator: " + separator.replace("\n", "LF").replace("\r", "CR")
                       + "]";
        }
    }
}
