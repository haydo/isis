/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.metamodel.services.xactn;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.xactn.Transaction;
import org.apache.isis.applib.services.xactn.TransactionService2;
import org.apache.isis.applib.services.xactn.TransactionState;
import org.apache.isis.core.metamodel.services.persistsession.PersistenceSessionServiceInternal;

@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "" + Integer.MAX_VALUE
)
public class TransactionServiceDefault implements TransactionService2 {


    @Override
    public void flushTransaction() {
        persistenceSessionServiceInternal.flush();
    }

    @Override
    public void nextTransaction() {
        persistenceSessionServiceInternal.commit();
        persistenceSessionServiceInternal.beginTran();
    }

    @Override
    public Transaction currentTransaction() {
        return persistenceSessionServiceInternal.currentTransaction();
    }

    @Override
    public TransactionState getTransactionState() {
        return persistenceSessionServiceInternal.getTransactionState();
    }

    @javax.inject.Inject
    PersistenceSessionServiceInternal persistenceSessionServiceInternal;

}
