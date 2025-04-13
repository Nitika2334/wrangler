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

import java.util.Objects;

/**
 * Token implementation for time duration values like 30s, 5m, 2h, etc.
 */
public class TimeDuration implements Token {

    /**
     * The time duration value, such as "5m" or "30s".
     */
    private final String value;

    /**
     * Constructs a TimeDuration token with the specified value.
     *
     * @param value the time duration value
     */
    public TimeDuration(final String value) {
        this.value = value;
    }

    /**
     * Returns the raw string value of the time duration token.
     *
     * @return the token value
     */
    @Override
    public final Object value() {
        return value;
    }

    /**
     * Returns the type of this token.
     *
     * @return the token type
     */
    @Override
    public final TokenType type() {
        return TokenType.TIME_DURATION;
    }

    /**
     * Converts this token to a JSON element.
     *
     * @return the JSON representation
     */
    @Override
    public final JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    /**
     * Returns the string representation of this token.
     *
     * @return string representation
     */
    @Override
    public final String toString() {
        return "TimeDuration{" + "value='" + value + '\'' + '}';
    }

    /**
     * Checks equality based on the token value.
     *
     * @param o the object to compare
     * @return true if values are equal
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeDuration)) {
            return false;
        }
        TimeDuration that = (TimeDuration) o;
        return Objects.equals(value, that.value);
    }

    /**
     * Returns the hash code for this token.
     *
     * @return hash code
     */
    @Override
    public final int hashCode() {
        return Objects.hash(value);
    }
}
