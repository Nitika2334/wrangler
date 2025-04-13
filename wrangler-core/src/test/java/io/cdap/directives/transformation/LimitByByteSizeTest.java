package io.cdap.directives.transformation;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.UsageDefinition;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LimitByByteSizeTest {

    private LimitByByteSize directive;

    @Before
    public void setup() {
        directive = new LimitByByteSize();
    }

    private Arguments createArguments(String column, String byteSizeLiteral) throws DirectiveParseException {
        return new Arguments() {
            @Override
            public <T> T value(String name) throws DirectiveParseException {
                switch (name) {
                    case "column":
                        return (T) new ColumnName(column);
                    case "max-bytes":
                        return (T) ByteSize.from(byteSizeLiteral);
                    default:
                        throw new DirectiveParseException("Unknown argument: " + name);
                }
            }

            @Override
            public boolean contains(String name) {
                return name.equals("column") || name.equals("max-bytes");
            }
        };
    }

    @Test
    public void testLimitAllowsAllRowsWithinLimit() throws Exception {
        directive.initialize(createArguments("message", "1KB"));

        List<Row> input = Arrays.asList(
                new Row("message", "short message 1"),
                new Row("message", "short message 2"));

        List<Row> output = directive.execute(input, null);

        assertEquals(2, output.size());
    }

    @Test
    public void testLimitSkipsRowsExceedingByteLimit() throws Exception {
        directive.initialize(createArguments("message", "20B")); // 20 bytes max

        List<Row> input = Arrays.asList(
                new Row("message", "short"), // 5 bytes
                new Row("message", "short again"), // 11 bytes
                new Row("message", "this one is too long") // 21+ bytes
        );

        List<Row> output = directive.execute(input, null);

        // Only first two should be included (5 + 11 = 16 < 20)
        assertEquals(2, output.size());
        assertEquals("short", output.get(0).getValue("message"));
        assertEquals("short again", output.get(1).getValue("message"));
    }

    @Test(expected = DirectiveExecutionException.class)
    public void testNonStringValueThrowsException() throws Exception {
        directive.initialize(createArguments("message", "1KB"));

        List<Row> input = Collections.singletonList(
                new Row("message", 12345) // Not a String
        );

        directive.execute(input, null);
    }

    @Test
    public void testNullValuesAreSkipped() throws Exception {
        directive.initialize(createArguments("message", "100B"));

        List<Row> input = Arrays.asList(
                new Row("message", null),
                new Row("message", "valid"));

        List<Row> output = directive.execute(input, null);

        assertEquals(1, output.size());
        assertEquals("valid", output.get(0).getValue("message"));
    }
}