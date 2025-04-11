/*
 *  Copyright © 2024
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.cdap.directives.transformation;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.lineage.Lineage;
import io.cdap.wrangler.api.lineage.Mutation;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * A Wrangler directive to limit processing of rows based on the total byte size
 * of a column's values.
 */
@Plugin(type = Directive.TYPE)
@Name(LimitByByteSize.NAME)
@Categories(categories = { "transform" })
@Description("Limits processing of rows once the total byte size of a column's values exceeds a given threshold.")
public class LimitByByteSize implements Directive, Lineage {

    public static final String NAME = "limit-by-byte-size";

    private String column;
    private long byteLimit;
    private long accumulatedBytes;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
        builder.define("column", TokenType.COLUMN_NAME);
        builder.define("max-bytes", TokenType.BYTE_SIZE);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.column = ((ColumnName) args.value("column")).value();
        this.byteLimit = ((ByteSize) args.value("max-bytes")).getSizeInBytes();
        this.accumulatedBytes = 0;
    }

    @Override
    public void destroy() {
        // no-op
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        List<Row> results = new ArrayList<>();
        for (Row row : rows) {
            Object val = row.getValue(column);
            if (val == null) {
                continue;
            }

            if (!(val instanceof String)) {
                throw new DirectiveExecutionException(NAME,
                        String.format("Column '%s' must contain String values to measure byte size.", column));
            }

            byte[] valueBytes = ((String) val).getBytes();
            long rowSize = valueBytes.length;

            if (accumulatedBytes + rowSize > byteLimit) {
                break;
            }

            accumulatedBytes += rowSize;
            results.add(new Row(row));
        }
        return results;
    }

    @Override
    public Mutation lineage() {
        return Mutation.builder()
                .readable("Limit processing based on byte size threshold for column '%s'", column)
                .relation(column, column)
                .build();
    }
}
