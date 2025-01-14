/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.services.s3.crt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class CrtDownloadErrorTest {
    private static final String BUCKET = "my-bucket";
    private static final String KEY = "my-key";
    private static final WireMockServer WM = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    private S3AsyncClient s3;

    @BeforeAll
    public static void setup() {
        WM.start();
        // Execute this statement before constructing the SDK service client.
        Log.initLoggingToStdout(Log.LogLevel.Trace);
    }

    @AfterAll
    public static void teardown() {
        WM.stop();
    }

    @AfterEach
    public void methodTeardown() {
        if (s3 != null) {
            s3.close();
        }
        s3 = null;
    }

    @Test
    public void getObject_headObjectOk_getObjectThrows_operationThrows() {
        s3 = S3AsyncClient.crtBuilder()
                                        .endpointOverride(URI.create("http://localhost:" + WM.port()))
                                        .forcePathStyle(true)
                                        .region(Region.US_EAST_1)
                                        .build();

        String path = String.format("/%s/%s", BUCKET, KEY);

        WM.stubFor(WireMock.head(WireMock.urlPathEqualTo(path))
                           .willReturn(WireMock.aResponse()
                                               .withStatus(200)
                                               .withHeader("ETag", "etag")
                                               .withHeader("Content-Length", "5")));

        String errorContent = ""
                              + "<Error>\n"
                              + "  <Code>AccessDenied</Code>\n"
                              + "  <Message>User does not have permission</Message>\n"
                              + "  <RequestId>request-id</RequestId>\n"
                              + "  <HostId>host-id</HostId>\n"
                              + "</Error>";
        WM.stubFor(WireMock.get(WireMock.urlPathEqualTo(path))
                           .willReturn(WireMock.aResponse()
                                               .withStatus(403)
                                               .withBody(errorContent)));

        assertThatThrownBy(s3.getObject(r -> r.bucket(BUCKET).key(KEY), AsyncResponseTransformer.toBytes())::join)
            .hasCauseInstanceOf(S3Exception.class)
            .hasMessageContaining("User does not have permission")
            .hasMessageContaining("Status Code: 403");
    }

    @Test
    public void getObject_headObjectOk_getObjectOk_operationSucceeds() {
        s3 = S3AsyncClient.crtBuilder()
                          .endpointOverride(URI.create("http://localhost:" + WM.port()))
                          .forcePathStyle(true)
                          .region(Region.US_EAST_1)
                          .build();

        String path = String.format("/%s/%s", BUCKET, KEY);

        byte[] content = "hello".getBytes(StandardCharsets.UTF_8);

        WM.stubFor(WireMock.head(WireMock.urlPathEqualTo(path))
                           .willReturn(WireMock.aResponse()
                                               .withStatus(200)
                                               .withHeader("ETag", "etag")
                                               .withHeader("Content-Length", Integer.toString(content.length))));
        WM.stubFor(WireMock.get(WireMock.urlPathEqualTo(path))
                           .willReturn(WireMock.aResponse()
                                               .withStatus(200)
                                               .withHeader("Content-Type", "text/plain")
                                               .withBody(content)));

        String objectContent = s3.getObject(r -> r.bucket(BUCKET).key(KEY), AsyncResponseTransformer.toBytes())
                                 .join()
                                 .asUtf8String();

        assertThat(objectContent.getBytes(StandardCharsets.UTF_8)).isEqualTo(content);
    }

    @Test
    public void getObject_headObjectThrows_operationThrows() {
        s3 = S3AsyncClient.crtBuilder()
                          .endpointOverride(URI.create("http://localhost:" + WM.port()))
                          .forcePathStyle(true)
                          .region(Region.US_EAST_1)
                          .build();

        String path = String.format("/%s/%s", BUCKET, KEY);


        WM.stubFor(WireMock.head(WireMock.urlPathEqualTo(path))
                           .willReturn(WireMock.aResponse()
                                               .withStatus(403)));

        assertThatThrownBy(s3.getObject(r -> r.bucket(BUCKET).key(KEY), AsyncResponseTransformer.toBytes())::join)
            .hasCauseInstanceOf(S3Exception.class)
            .hasMessageContaining("Status Code: 403");
    }
}
