// Copyright 2021 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.plan.execution.stores.relational.connection.test;

import org.eclipse.collections.api.block.function.Function;
import org.finos.legend.engine.plan.execution.stores.relational.connection.authentication.strategy.SnowflakePublicAuthenticationStrategy;
import org.finos.legend.engine.plan.execution.stores.relational.connection.driver.vendors.snowflake.SnowflakeManager;
import org.finos.legend.engine.plan.execution.stores.relational.connection.ds.DataSourceSpecification;
import org.finos.legend.engine.plan.execution.stores.relational.connection.ds.specifications.SnowflakeDataSourceSpecification;
import org.finos.legend.engine.plan.execution.stores.relational.connection.ds.specifications.keys.SnowflakeDataSourceSpecificationKey;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.finos.legend.engine.shared.core.identity.factory.*;
import org.finos.legend.engine.shared.core.vault.EnvironmentVaultImplementation;
import org.finos.legend.engine.shared.core.vault.Vault;
import org.junit.Test;

import javax.security.auth.Subject;
import java.sql.Connection;

public class ExternalIntegration_TestConnectionObjectProtocol_Snowflake extends org.finos.legend.engine.plan.execution.stores.relational.connection.test.DbSpecificTests
{
    @Override
    protected Subject getSubject()
    {
        return null;
    }

    @Test
    public void testSnowflakePublicConnection_subject() throws Exception
    {
        testSnowflakePublicConnection(c -> c.getConnectionUsingSubject(getSubject()));
    }

    @Test
    public void testSnowflakePublicConnection_identity() throws Exception
    {
        testSnowflakePublicConnection(c -> c.getConnectionUsingIdentity(IdentityFactoryProvider.getInstance().getAnonymousIdentity()));
    }

    private void testSnowflakePublicConnection(Function<DataSourceSpecification, Connection> toDBConnection) throws Exception
    {

        Vault.INSTANCE.registerImplementation(new EnvironmentVaultImplementation());

        SnowflakeDataSourceSpecification ds =
                new SnowflakeDataSourceSpecification(
                        new SnowflakeDataSourceSpecificationKey("ki79827", "us-east-2",
                                "INTEGRATION_WH1",
                                "INTEGRATION_DB1", "aws", null,
                                "INTEGRATION_ROLE1"),
                        new SnowflakeManager(),
                        new SnowflakePublicAuthenticationStrategy("SNOWFLAKE_INTEGRATION_USER1_PRIVATEKEY", "SNOWFLAKE_INTEGRATION_USER1_PASSWORD", "INTEGRATION_USER1"));
        try (Connection connection = toDBConnection.valueOf(ds))
        {
            testConnection(connection, "select * from INTEGRATION_DB1.INTEGRATION_SCHEMA1.test");
        }
    }
}
