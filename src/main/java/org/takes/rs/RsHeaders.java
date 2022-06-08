/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.rs;

import lombok.EqualsAndHashCode;
import org.cactoos.text.*;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.misc.VerboseList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * HTTP headers parsing
 *
 * <p>All implementations of this interface must be immutable and
 * thread-safe.</p>
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface RsHeaders extends Response {

    /**
     * Get single header.
     *
     * @param key Header name
     * @return List of values (can be empty)
     * @throws IOException If fails
     */
    List<String> header(CharSequence key) throws IOException;

    /**
     * Get all header names.
     *
     * @return All names
     * @throws IOException If fails
     */
    Set<String> names() throws IOException;

    /**
     * Response decorator, for HTTP headers parsing.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.13.8
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RsWrap implements RsHeaders {
        /**
         * Ctor.
         *
         * @param res Original response
         */
        public Base(final Response res) {
            super(res);
        }

        @Override
        public List<String> header(final CharSequence key)
            throws IOException {
            final List<String> values = this.map().getOrDefault(
                new UncheckedText(
                    new Lowered(key.toString())
                ).asString(),
                Collections.emptyList()
            );
            final List<String> list;
            if (values.isEmpty()) {
                list = new VerboseList<>(
                    Collections.emptyList(),
                    new FormattedText(
                        // @checkstyle LineLengthCheck (1 line)
                        "there are no headers by name \"%s\" among %d others: %s",
                        key,
                        this.map().size(),
                        this.map().keySet()
                    )
                );
            } else {
                list = new VerboseList<>(
                    values,
                    new FormattedText(
                        // @checkstyle LineLengthCheck (1 line)
                        "there are only %d headers by name \"%s\"",
                        values.size(),
                        key
                    )
                );
            }
            return list;
        }

        @Override
        public Set<String> names() throws IOException {
            return this.map().keySet();
        }

        /**
         * Parse them all in a map.
         *
         * @return Map of them
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        private Map<String, List<String>> map() throws IOException {
            final Iterator<String> head = this.head().iterator();
            if (!head.hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    "a valid response must contain at least one line in the head"
                );
            }
            head.next();
            final Map<String, List<String>> map = new HashMap<>(0);
            while (head.hasNext()) {
                final String line = head.next();
                final String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        String.format("invalid HTTP header: \"%s\"", line)
                    );
                }
                final String key = new UncheckedText(
                    new Lowered(new Trimmed(new TextOf(parts[0])))
                ).asString();
                if (!map.containsKey(key)) {
                    map.put(key, new LinkedList<>());
                }
                map.get(key).add(
                    new UncheckedText(
                        new Trimmed(new TextOf(parts[1]))
                    ).asString()
                );
            }
            return map;
        }
    }

    /**
     * Smart decorator, with extra features.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.16
     */
    @EqualsAndHashCode
    final class Smart implements RsHeaders {
        /**
         * Original.
         */
        private final RsHeaders origin;

        /**
         * Ctor.
         * @param res Original response
         */
        public Smart(final RsHeaders res) {
            this.origin = res;
        }

        /**
         * Ctor.
         * @param res Original response
         */
        public Smart(final Response res) {
            this(new Base(res));
        }

        @Override
        public List<String> header(final CharSequence name)
            throws IOException {
            return this.origin.header(name);
        }

        @Override
        public Set<String> names() throws IOException {
            return this.origin.names();
        }

        @Override
        public Iterable<String> head() throws IOException {
            return this.origin.head();
        }

        @Override
        public InputStream body() throws IOException {
            return this.origin.body();
        }

        /**
         * Get single header or throw HTTP exception.
         * @param name Name of header
         * @return Value of it
         * @throws IOException If fails
         */
        public String single(final CharSequence name) throws IOException {
            final Iterator<String> params = this.header(name).iterator();
            if (!params.hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "header \"%s\" is mandatory, not found among %s",
                        name, this.names()
                    )
                );
            }
            return params.next();
        }

        /**
         * If header is present, returns the first header value.
         * If not, returns a default value.
         * @param name Name of header key
         * @param def Default value
         * @return Header Value or default value
         * @throws IOException If fails
         */
        public String single(final CharSequence name, final CharSequence def)
            throws IOException {
            final String value;
            final Iterator<String> params = this.header(name).iterator();
            if (params.hasNext()) {
                value = params.next();
            } else {
                value = def.toString();
            }
            return value;
        }

    }
}
