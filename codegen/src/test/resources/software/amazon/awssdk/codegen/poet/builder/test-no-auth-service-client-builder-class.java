package software.amazon.awssdk.services.database;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.annotations.Generated;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.builder.AwsDefaultClientBuilder;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.database.endpoints.DatabaseEndpointProvider;
import software.amazon.awssdk.services.database.endpoints.internal.DatabaseRequestSetEndpointInterceptor;
import software.amazon.awssdk.services.database.endpoints.internal.DatabaseResolveEndpointInterceptor;
import software.amazon.awssdk.services.database.internal.DatabaseServiceClientConfigurationBuilder;
import software.amazon.awssdk.services.database.internal.SdkClientConfigurationUtil;
import software.amazon.awssdk.utils.CollectionUtils;

/**
 * Internal base class for {@link DefaultDatabaseClientBuilder} and {@link DefaultDatabaseAsyncClientBuilder}.
 */
@Generated("software.amazon.awssdk:codegen")
@SdkInternalApi
abstract class DefaultDatabaseBaseClientBuilder<B extends DatabaseBaseClientBuilder<B, C>, C> extends
                                                                                              AwsDefaultClientBuilder<B, C> {
    @Override
    protected final String serviceEndpointPrefix() {
        return "database-service-endpoint";
    }

    @Override
    protected final String serviceName() {
        return "Database";
    }

    @Override
    protected final SdkClientConfiguration mergeServiceDefaults(SdkClientConfiguration config) {
        return config.merge(c -> c.option(SdkClientOption.ENDPOINT_PROVIDER, defaultEndpointProvider()).option(
            SdkClientOption.CRC32_FROM_COMPRESSED_DATA_ENABLED, false));
    }

    @Override
    protected final SdkClientConfiguration finalizeServiceConfiguration(SdkClientConfiguration config) {
        List<ExecutionInterceptor> endpointInterceptors = new ArrayList<>();
        endpointInterceptors.add(new DatabaseResolveEndpointInterceptor());
        endpointInterceptors.add(new DatabaseRequestSetEndpointInterceptor());
        ClasspathInterceptorChainFactory interceptorFactory = new ClasspathInterceptorChainFactory();
        List<ExecutionInterceptor> interceptors = interceptorFactory
            .getInterceptors("software/amazon/awssdk/services/database/execution.interceptors");
        List<ExecutionInterceptor> additionalInterceptors = new ArrayList<>();
        interceptors = CollectionUtils.mergeLists(endpointInterceptors, interceptors);
        interceptors = CollectionUtils.mergeLists(interceptors, additionalInterceptors);
        interceptors = CollectionUtils.mergeLists(interceptors, config.option(SdkClientOption.EXECUTION_INTERCEPTORS));
        SdkClientConfiguration.Builder builder = config.toBuilder();
        builder.option(SdkClientOption.EXECUTION_INTERCEPTORS, interceptors);
        return builder.build();
    }

    @Override
    protected final String signingName() {
        return "database-service";
    }

    private DatabaseEndpointProvider defaultEndpointProvider() {
        return DatabaseEndpointProvider.defaultProvider();
    }

    @Override
    protected SdkClientConfiguration setOverrides(SdkClientConfiguration configuration) {
        ClientOverrideConfiguration overrideConfiguration = overrideConfiguration();
        if (overrideConfiguration == null) {
            return configuration;
        }
        return SdkClientConfigurationUtil.copyOverridesToConfiguration(overrideConfiguration, configuration.toBuilder()).build();
    }

    @Override
    protected SdkClientConfiguration invokePlugins(SdkClientConfiguration config) {
        List<SdkPlugin> internalPlugins = internalPlugins();
        List<SdkPlugin> externalPlugins = plugins();
        if (internalPlugins.isEmpty() && externalPlugins.isEmpty()) {
            return config;
        }
        List<SdkPlugin> plugins = CollectionUtils.mergeLists(internalPlugins, externalPlugins);
        DatabaseServiceClientConfigurationBuilder.BuilderInternal serviceConfigBuilder = DatabaseServiceClientConfigurationBuilder
            .builder(config.toBuilder());
        serviceConfigBuilder.overrideConfiguration(overrideConfiguration());
        for (SdkPlugin plugin : plugins) {
            plugin.configureClient(serviceConfigBuilder);
        }
        overrideConfiguration(serviceConfigBuilder.overrideConfiguration());
        return serviceConfigBuilder.buildSdkClientConfiguration();
    }

    private List<SdkPlugin> internalPlugins() {
        List<SdkPlugin> internalPlugins = new ArrayList<>();
        return internalPlugins;
    }

    protected static void validateClientOptions(SdkClientConfiguration c) {
    }
}
