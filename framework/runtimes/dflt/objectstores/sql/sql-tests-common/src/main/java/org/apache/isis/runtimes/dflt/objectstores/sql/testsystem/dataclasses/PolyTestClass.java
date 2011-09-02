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

/**
 * 
 */
package org.apache.isis.runtimes.dflt.objectstores.sql.testsystem.dataclasses;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;

/**
 * @author Kevin
 * 
 */
public class PolyTestClass extends AbstractDomainObject implements PolyTestInterface {
    public String title() {
        return string;
    }

    // {{ String type
    private String string;

    public String getString() {
        return string;
    }

    public void setString(final String string) {
        this.string = string;
    }

    // }}

    // {{ PolyBaseClass
    private List<PolyBaseClass> polyBaseClasses = new ArrayList<PolyBaseClass>();

    @MemberOrder(sequence = "1")
    public List<PolyBaseClass> getPolyBaseClasses() {
        return polyBaseClasses;
    }

    public void setPolyBaseClasses(final List<PolyBaseClass> polyBaseClasses) {
        this.polyBaseClasses = polyBaseClasses;
    }

    // }}

    // {{ PolyBaseClass
    private List<PolyTestInterface> polyTestClasses = new ArrayList<PolyTestInterface>();

    @MemberOrder(sequence = "2")
    public List<PolyTestInterface> getPolyTestClasses() {
        return polyTestClasses;
    }

    public void setPolyTestClasses(final List<PolyTestInterface> polyTestClasses) {
        this.polyTestClasses = polyTestClasses;
    }
    // }}

}
