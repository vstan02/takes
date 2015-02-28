/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xembly source to create an Atom LINK element.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "rel", "href", "type" })
public final class XeLink implements XeSource {

    /**
     * Rel.
     */
    private final transient String rel;

    /**
     * Href.
     */
    private final transient String href;

    /**
     * Type.
     */
    private final transient String type;

    /**
     * Ctor.
     * @param related Related
     * @param link HREF
     */
    public XeLink(final String related, final String link) {
        this(related, link, "text/xml");
    }

    /**
     * Ctor.
     * @param related Related
     * @param link HREF
     * @param ctype Content type
     */
    public XeLink(final String related, final String link, final String ctype) {
        this.rel = related;
        this.href = link;
        this.type = ctype;
    }

    @Override
    public Iterable<Directive> toXembly() {
        return new Directives().add("link")
            .attr("rel", this.rel)
            .attr("href", this.href)
            .attr("type", this.type);
    }
}
