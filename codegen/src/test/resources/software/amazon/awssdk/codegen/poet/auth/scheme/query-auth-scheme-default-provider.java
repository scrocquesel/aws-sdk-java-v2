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

package software.amazon.awssdk.services.query.auth.scheme.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.Generated;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.AuthSchemeOption;
import software.amazon.awssdk.services.query.auth.scheme.QueryAuthSchemeParams;
import software.amazon.awssdk.services.query.auth.scheme.QueryAuthSchemeProvider;

@Generated("software.amazon.awssdk:codegen")
@SdkInternalApi
public final class DefaultQueryAuthSchemeProvider implements QueryAuthSchemeProvider {
    private static final DefaultQueryAuthSchemeProvider DEFAULT = new DefaultQueryAuthSchemeProvider();

    private DefaultQueryAuthSchemeProvider() {
    }

    public static DefaultQueryAuthSchemeProvider create() {
        return DEFAULT;
    }

    @Override
    public List<AuthSchemeOption> resolveAuthScheme(QueryAuthSchemeParams params) {
        List<AuthSchemeOption> options = new ArrayList<>();
        switch (params.operation()) {
        case "BearerAuthOperation":
            options.add(AuthSchemeOption.builder().schemeId("smithy.api#httpBearerAuth").build());
            break;
        case "OperationWithNoneAuthType":
            options.add(AuthSchemeOption.builder().schemeId("smithy.api#noAuth").build());
            break;
        default:
            options.add(AuthSchemeOption.builder().schemeId("aws.auth#sigv4")
                    .putSignerProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, "query-service")
                    .putSignerProperty(AwsV4HttpSigner.REGION_NAME, params.region().id()).build());
            break;
        }
        return Collections.unmodifiableList(options);
    }
}
