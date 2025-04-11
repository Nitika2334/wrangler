package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

/**
 * Token implementation for byte size values like 10MB, 1GB, etc.
 */
public class ByteSize implements Token {
    private final String value;

    public ByteSize(String value) {
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public String toString() {
        return "ByteSize{" + "value='" + value + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ByteSize))
            return false;
        ByteSize byteSize = (ByteSize) o;
        return Objects.equals(value, byteSize.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
