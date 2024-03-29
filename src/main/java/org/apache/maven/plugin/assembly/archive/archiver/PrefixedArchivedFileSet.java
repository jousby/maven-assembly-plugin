package org.apache.maven.plugin.assembly.archive.archiver;

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

import org.codehaus.plexus.archiver.ArchivedFileSet;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;

import java.io.File;

/**
 * @version $Id: PrefixedArchivedFileSet.java 727431 2008-12-17 16:38:37Z jdcasey $
 */
public class PrefixedArchivedFileSet
    implements ArchivedFileSet
{

    private final String rootPrefix;
    private final ArchivedFileSet fileSet;
    private final FileSelector[] selectors;

    public PrefixedArchivedFileSet( ArchivedFileSet fileSet, String rootPrefix, FileSelector[] selectors )
    {
        this.fileSet = fileSet;
        this.selectors = selectors;

        if ( rootPrefix.length() > 0 && ! rootPrefix.endsWith( "/" ) )
        {
            this.rootPrefix = rootPrefix + "/";
        }
        else
        {
            this.rootPrefix = rootPrefix;
        }
    }

    public File getArchive()
    {
        return fileSet.getArchive();
    }

    public String[] getExcludes()
    {
        return fileSet.getExcludes();
    }

    public FileSelector[] getFileSelectors()
    {
        FileSelector[] sel = fileSet.getFileSelectors();
        if ( ( sel != null ) && ( selectors != null ) )
        {
            FileSelector[] temp = new FileSelector[ sel.length + selectors.length ];

            System.arraycopy( sel, 0, temp, 0, sel.length );
            System.arraycopy( selectors, 0, temp, sel.length, selectors.length );

            sel = temp;
        }
        else if ( ( sel == null ) && ( selectors != null ) )
        {
            sel = selectors;
        }

        return sel;
    }

    public String[] getIncludes()
    {
        return fileSet.getIncludes();
    }

    public String getPrefix()
    {
        String prefix = fileSet.getPrefix();
        if ( prefix.startsWith( "/" ) )
        {
            if ( prefix.length() > 1 )
            {
                prefix = prefix.substring( 1 );
            }
            else
            {
                prefix = "";
            }
        }

        return rootPrefix + prefix;
    }

    public boolean isCaseSensitive()
    {
        return fileSet.isCaseSensitive();
    }

    public boolean isIncludingEmptyDirectories()
    {
        return fileSet.isIncludingEmptyDirectories();
    }

    public boolean isUsingDefaultExcludes()
    {
        return fileSet.isUsingDefaultExcludes();
    }

}
