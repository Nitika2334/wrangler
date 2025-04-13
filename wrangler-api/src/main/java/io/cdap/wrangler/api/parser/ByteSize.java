/*
 * Copyright © 2016-2023 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token implementation for byte size values like 10MB, 1GB, etc.
 */
public class ByteSize implements Token {

    private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)([KMGTP]?B)",
            Pattern.CASE_INSENSITIVE);
    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;
    private static final long PB = TB * 1024;

    /** Byte size string, e.g., "10MB". */
    private final String value;

    /**
     * Creates a {@link ByteSize} instance.
     *
     * @param value the size value string (e.g., "10MB")
     */
    public ByteSize(final String value) {
        this.value = value.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Returns the size in bytes.
     *
     * @return byte size as long
     */
    public final long getSizeInBytes() {
        Matcher matcher = SIZE_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid byte size format: " + value);
        }

        double number = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);

        switch (unit) {
            case "B":
                return (long) number;
            case "KB":
                return (long) (number * KB);
            case "MB":
                return (long) (number * MB);
            case "GB":
                return (long) (number * GB);
            case "TB":
                return (long) (number * TB);
            case "PB":
                return (long) (number * PB);
            default:
                throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    @Override
    public final Object value() {
        return value;
    }

    @Override
    public final TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public final JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public final String toString() {
        return "ByteSize{" + "value='" + value + '\'' + '}';
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ByteSize)) {
            return false;
        }
        ByteSize byteSize = (ByteSize) o;
        return Objects.equals(value, byteSize.value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(value);
    }
}
