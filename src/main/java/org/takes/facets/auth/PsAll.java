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
package org.takes.facets.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * A Pass which you can enter only if you can enter every Pass in a list.
 * @since 0.22
 */
public final class PsAll implements Pass {

    /**
     * Passes that have to be entered.
     */
    private final List<? extends Pass> all;

    /**
     * Index of identity to return.
     */
    private final int index;

    /**
     * Ctor.
     * @param passes All Passes to be checked.
     * @param identity Index of a Pass whose Identity to return on successful
     *  {@link PsAll#enter(Request)}
     */
    public PsAll(final List<? extends Pass> passes, final int identity) {
        this.all = new ArrayList<Pass>(passes);
        this.index = this.validated(identity);
    }

    @Override
    public Opt<Identity> enter(final Request request) throws Exception {
        final Opt<Identity> result;
        if (this.allMatch(request)) {
            result = this.all.get(this.index).enter(request);
        } else {
            result = new Opt.Empty<>();
        }
        return result;
    }

    @Override
    public Response exit(final Response response, final Identity identity)
        throws Exception {
        if (this.index >= this.all.size()) {
            throw new IOException(
                "Index of identity is greater than Pass collection size"
            );
        }
        return this.all.get(this.index).exit(response, identity);
    }

    /**
     * Validate index.
     * @param idx Index of a Pass whose Identity to return on successful
     *  {@link PsAll#enter(Request)}
     * @return Validated index
     */
    private int validated(final int idx) {
        if (idx < 0) {
            throw new IllegalArgumentException(
                String.format("Index %d must be >= 0.", idx)
            );
        }
        if (idx >= this.all.size()) {
            throw new IllegalArgumentException(
                String.format(
                    "Trying to return index %d from a list of %d passes",
                    idx,
                    this.all.size()
                )
            );
        }
        return idx;
    }

    /**
     * Checks if you can enter every Pass with a request.
     * @param request Request that is used to enter Passes.
     * @return True if every request can be entered, false otherwise
     * @throws Exception If any of enter attempts fail
     */
    private boolean allMatch(final Request request) throws Exception {
        boolean success = true;
        for (final Pass pass : this.all) {
            if (!pass.enter(request).has()) {
                success = false;
                break;
            }
        }
        return success;
    }
}
