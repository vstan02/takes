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
package org.takes.facets.ret;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsReturn}.
 * @since 0.20
 */
final class RsReturnTest {

    /**
     * RsReturn can add cookies.
     * @throws IOException If some problem inside
     */
    @Test
    void addsCookieToResponse() throws IOException {
        final String destination = "/return/to";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsReturn(new RsEmpty(), destination)
            ),
            new HasString(
                new FormattedText(
                    "Set-Cookie: RsReturn=%s;Path=/",
                    URLEncoder.encode(
                        destination,
                        Charset.defaultCharset().name()
                    )
                )
            )
        );
    }

    /**
     * RsReturn can reject invalid location.
     */
    @Test
    void rejectsInvalidLocation() {
        Assertions.assertThrows(
            IOException.class,
            () -> {
                new RsReturn(new RsEmpty(), "http://www.netbout.com/,PsCookie=");
            }
        );
    }
}
