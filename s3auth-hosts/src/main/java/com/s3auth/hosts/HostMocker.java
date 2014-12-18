/**
 * Copyright (c) 2012-2014, s3auth.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the s3auth.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.s3auth.hosts;

import java.io.IOException;
import java.net.URI;
import lombok.experimental.Builder;

/**
 * Mocker of {@link Host}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class HostMocker {

    /**
     * The mock.
     */
    private final transient MkHost.MkHostBuilder host = MkHost.builder();

    /**
     * Public ctor.
     */
    public HostMocker() {
        this.host
            .resource(new ResourceMocker().withContent("hello").mock())
            .authorized(true)
            .hidden(true);
    }

    /**
     * With this content for this URI.
     * @param uri The URI to match
     * @param content The content to return
     * @return This object
     */
    public HostMocker withContent(final URI uri, final String content) {
        this.host.resource(new ResourceMocker().withContent(content).mock());
        return this;
    }

    /**
     * With this syslog.
     * @param syslog The syslog to return
     * @return This object
     */
    public HostMocker withSyslog(final String syslog) {
        this.host.syslog(syslog);
        return this;
    }

    /**
     * Mock it.
     * @return The host
     */
    public Host mock() {
        return this.host.build();
    }

    @Builder
    private static class MkHost implements Host {
        private final transient Resource resource;
        private final transient boolean authorized;
        private final transient boolean hidden;
        private final transient String syslog;
        private final transient Stats stats;

        @Override
        public void close() throws IOException {
        }

        @Override
        public Resource fetch(URI uri, Range range, Version version)
            throws IOException {
            return resource;
        }

        @Override
        public boolean isHidden(URI uri) throws IOException {
            return hidden;
        }

        @Override
        public boolean authorized(String user, String password)
            throws IOException {
            return authorized;
        }

        @Override
        public String syslog() {
            return syslog;
        }

        @Override
        public Stats stats() {
            return stats;
        }
    }
}