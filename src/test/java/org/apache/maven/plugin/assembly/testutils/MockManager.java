package org.apache.maven.plugin.assembly.testutils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.easymock.MockControl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.AssertionFailedError;

public class MockManager
{

    private final Set<MockControl> mockControls = new HashSet<MockControl>();

    public void add( final MockControl control )
    {
        mockControls.add( control );
    }

    public void clear()
    {
        mockControls.clear();
    }

    public void replayAll()
    {
        for ( final Iterator<MockControl> it = mockControls.iterator(); it.hasNext(); )
        {
            final MockControl control = it.next();

            control.replay();
        }
    }

    public void verifyAll()
    {
        for ( final Iterator<MockControl> it = mockControls.iterator(); it.hasNext(); )
        {
            final MockControl control = it.next();

            try
            {
                control.verify();
            }
            catch ( final AssertionFailedError err )
            {
                final String message =
                    "MockControl: " + control + " of: " + control.getMock() + " failed.\n" + err.getMessage();
                final AssertionFailedError e = new AssertionFailedError( message );
                e.initCause( err );

                throw e;
            }
        }
    }

}
