/**
 * Copyright (c) 2012, s3auth.com
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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.collect.ImmutableList;
import com.rexsl.test.XhtmlMatchers;
import org.apache.commons.io.Charsets;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link DirectoryListing}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class DirectoryListingTest {
    /**
     * Fetches directory listing for bucket, if bucket does not exist.
     * @throws Exception If something goes wrong
     */
    @Test
    public void fetchesDirectoryListingInXml()
        throws Exception {
        final AmazonS3 client = Mockito.mock(AmazonS3.class);
        final ObjectListing listing = Mockito.mock(ObjectListing.class);
        Mockito.doReturn(listing).when(client)
            .listObjects(Mockito.any(ListObjectsRequest.class));
        final String[] prefixes = {"baz/", "biz/", "boz/"};
        Mockito.doReturn(ImmutableList.copyOf(prefixes)).when(listing)
            .getCommonPrefixes();
        final String[] names = {"baa.txt", "bee.jpg", "boo.png"};
        final ImmutableList.Builder<S3ObjectSummary> builder =
            ImmutableList.builder();
        for (final String key : names) {
            @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
            final S3ObjectSummary summary = new S3ObjectSummary();
            summary.setKey(key);
            builder.add(summary);
        }
        Mockito.doReturn(builder.build()).when(listing).getObjectSummaries();
        final String prefix = "foo/bar/";
        MatcherAssert.assertThat(
            new String(
                ResourceMocker.toByteArray(
                    new DirectoryListing(client, "bucket", prefix)
                ),
                Charsets.UTF_8
            ),
            Matchers.allOf(
                XhtmlMatchers.hasXPaths(
                    String.format("/directory[@prefix=\"%s\"]", prefix)
                ),
                hasCommonPrefixXPath(prefixes[0]),
                hasCommonPrefixXPath(prefixes[1]),
                hasObjectXPath(names[0]),
                hasObjectXPath(names[1]),
                hasObjectXPath(names[2])
            )
        );
    }

    /**
     * Get Matcher for XML object element XPath checking.
     * @param key The key
     * @return Matcher for object element
     */
    private static Matcher<String> hasObjectXPath(final String key) {
        return XhtmlMatchers.hasXPath(
            String.format(
                String.format("/directory[object=\"%s\"]", key)
            )
        );
    }

    /**
     * Get Matcher for XML commonPrefix element XPath checking.
     * @param prefix The key
     * @return Matcher for common prefix element
     */
    private static Matcher<String> hasCommonPrefixXPath(final String prefix) {
        return XhtmlMatchers.hasXPath(
            String.format(
                String.format("/directory[commonPrefix=\"%s\"]", prefix)
            )
        );
    }
}
