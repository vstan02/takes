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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.EndsWith;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.Response;

/**
 * Test case for {@link RsFake}.
 * @since 0.24
 */
@SuppressWarnings({"PMD.AvoidUsingHardCodedIP", "PMD.AvoidDuplicateLiterals"})
public final class RsFakeTest {

    /**
     * Can conform to the Object.equals() contract.
     */
    @Test
    void conformsToEquality() {
        new Assertion<>(
            "Must evaluate true equality",
            new RsFake(
                "GET",
                "https://localhost:8080"
            ),
            new IsEqual<>(
                new RsFake(
                    "GET",
                    "https://localhost:8080"
                )
            )
        ).affirm();
    }

    /**
     * RsFake can print correctly.
     * @throws Exception If some problem inside
     */
    @Test
    void printsCorrectly() {
        final RsFake res = new RsFake(
            "GET",
            "/just-a-test HTTP/1.1",
            "test-6=alpha"
        );
        MatcherAssert.assertThat(
            new RsPrint(res),
            Matchers.allOf(
                new HasString("GET /just-a-test HTTP/1.1\r\n"),
                new EndsWith("=alpha")
            )
        );
    }

    /**
     * RsFake can print body only once.
     * @throws Exception If some problem inside
     */
    @Test
    void printsBodyOnlyOnce() throws Exception {
        final String body = "the body text";
        final Response res = new RsFake("", "", body);
        MatcherAssert.assertThat(
            new RsPrint(res).asString(),
            Matchers.containsString(body)
        );
        MatcherAssert.assertThat(
            new RsPrint(res).asString(),
            Matchers.not(Matchers.containsString(body))
        );
    }

}
