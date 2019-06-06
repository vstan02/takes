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
package org.takes.tk;

import org.cactoos.text.JoinedText;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.llorllale.cactoos.matchers.TextHasString;
import org.llorllale.cactoos.matchers.TextIs;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkHtml}.
 * @since 0.10
 */
public final class TkHtmlTest {

    /**
     * TkHTML can create a text.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsTextResponse() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            new RsPrint(new TkHtml(body).act(new RqFake())),
            new TextIs(
                new JoinedText(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/html",
                    "",
                    body
                )
            )
        );
    }

    /**
     * TkHTML can print multiple times.
     * @throws Exception If some problem inside
     */
    @Test
    public void printsResourceMultipleTimes() throws Exception {
        final String body = "<html>hello, dude!</html>";
        final Take take = new TkHtml(body);
        MatcherAssert.assertThat(
            new RsPrint(take.act(new RqFake())),
            new TextHasString(body)
        );
        MatcherAssert.assertThat(
            new RsPrint(take.act(new RqFake())),
            new TextHasString(body)
        );
    }

}
