/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.database.mariadb;

import org.apache.shardingsphere.infra.database.core.connection.DataSourceMetaData;
import org.apache.shardingsphere.infra.database.core.connection.DataSourceMetaDataBuilder;
import org.apache.shardingsphere.infra.database.core.connection.UnrecognizedDatabaseURLException;
import org.apache.shardingsphere.infra.database.core.spi.DatabaseTypedSPILoader;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.util.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.test.util.PropertiesBuilder;
import org.apache.shardingsphere.test.util.PropertiesBuilder.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Properties;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MariaDBDataSourceMetaDataTest {
    
    private final DataSourceMetaDataBuilder builder = DatabaseTypedSPILoader.getService(DataSourceMetaDataBuilder.class, TypedSPILoader.getService(DatabaseType.class, "MariaDB"));
    
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(NewConstructorTestCaseArgumentsProvider.class)
    void assertNewConstructorWithSimpleJdbcUrl(final String name, final String url, final String hostname, final int port, final String catalog, final String schema, final Properties queryProps) {
        DataSourceMetaData actual = builder.build(url, null, null);
        assertThat(actual.getHostname(), is(hostname));
        assertThat(actual.getPort(), is(port));
        assertThat(actual.getCatalog(), is(catalog));
        assertThat(actual.getSchema(), is(schema));
        assertThat(actual.getQueryProperties(), is(queryProps));
    }
    
    @Test
    void assertNewConstructorFailure() {
        assertThrows(UnrecognizedDatabaseURLException.class, () -> builder.build("jdbc:mariadb:xxxxxxxx", null, null));
    }
    
    private static class NewConstructorTestCaseArgumentsProvider implements ArgumentsProvider {
        
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of("simple", "jdbc:mariadb://127.0.0.1/foo_ds", "127.0.0.1", 3306, "foo_ds", null, new Properties()),
                    Arguments.of("complex", "jdbc:mariadb:replication://127.0.0.1:9999, 127.0.0.2:9999/foo_ds?serverTimezone=UTC&useSSL=false", "127.0.0.1", 9999, "foo_ds", null,
                            PropertiesBuilder.build(new Property("serverTimezone", "UTC"), new Property("useSSL", Boolean.FALSE.toString()))));
        }
    }
}
