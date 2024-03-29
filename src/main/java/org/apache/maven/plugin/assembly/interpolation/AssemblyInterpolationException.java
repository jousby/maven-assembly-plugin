package org.apache.maven.plugin.assembly.interpolation;

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

/**
 * @version $Id: AssemblyInterpolationException.java 999612 2010-09-21 20:34:50Z jdcasey $
 */
public class AssemblyInterpolationException
    extends Exception
{
    private static final long serialVersionUID = 1L;

    private String expression;

    private String originalMessage;

    public AssemblyInterpolationException( final String message )
    {
        super( message );
    }

    public AssemblyInterpolationException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public AssemblyInterpolationException( final String expression, final String message, final Throwable cause )
    {
        super( "The Assembly expression: " + expression + " could not be evaluated. Reason: " + message, cause );

        this.expression = expression;
        originalMessage = message;
    }

    public AssemblyInterpolationException( final String expression, final String message )
    {
        super( "The Assembly expression: " + expression + " could not be evaluated. Reason: " + message );

        this.expression = expression;
        originalMessage = message;
    }

    public String getExpression()
    {
        return expression;
    }

    public String getOriginalMessage()
    {
        return originalMessage;
    }
}
