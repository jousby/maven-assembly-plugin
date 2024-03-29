package org.apache.maven.plugin.assembly.archive.task.testutils;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.assembly.testutils.MockManager;
import org.easymock.MockControl;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ArtifactMock
{

    private final MockControl artifactCtl;

    private final Artifact artifact;

    private final MockControl handlerCtl;

    private final ArtifactHandler handler;

    private String groupId;

    private String artifactId;

    private String baseVersion;

    private File file;

    private Boolean isSnapshot;

    private String version;

    private List<String> dependencyTrail;

    private String id;

    private String dependencyConflictId;

    private String typeAndExt;

    private final String classifier;

    public ArtifactMock( final MockManager mockManager, final String groupId, final String artifactId,
                         final String version, final String type, final boolean isSnapshot )
    {
        this( mockManager, groupId, artifactId, version, type, null, isSnapshot, null, null );
    }

    public ArtifactMock( final MockManager mockManager, final String groupId, final String artifactId,
                         final String version, final String type, final boolean isSnapshot, final String baseVersion )
    {
        this( mockManager, groupId, artifactId, version, type, null, isSnapshot, baseVersion, null );
    }

    public ArtifactMock( final MockManager mockManager, final String groupId, final String artifactId,
                         final String version, final String type, final String classifier, final boolean isSnapshot )
    {
        this( mockManager, groupId, artifactId, version, type, classifier, isSnapshot, null, null );
    }

    public ArtifactMock( final MockManager mockManager, final String groupId, final String artifactId,
                         final String version, final String type, final String classifier, final boolean isSnapshot,
                         final String baseVersion )
    {
        this( mockManager, groupId, artifactId, version, type, classifier, isSnapshot, baseVersion, null );
    }

    public ArtifactMock( final MockManager mockManager, final String groupId, final String artifactId,
                         final String version, final String type, final String classifier, final boolean isSnapshot,
                         final String baseVersion, String scope )
    {
        artifactCtl = MockControl.createControl( Artifact.class );

        mockManager.add( artifactCtl );

        artifact = (Artifact) artifactCtl.getMock();

        if ( scope == null )
        {
            scope = Artifact.SCOPE_COMPILE;
        }

        artifact.getScope();
        artifactCtl.setReturnValue( scope, MockControl.ZERO_OR_MORE );

        handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mockManager.add( handlerCtl );

        handler = (ArtifactHandler) handlerCtl.getMock();

        artifact.getArtifactHandler();
        artifactCtl.setReturnValue( handler, MockControl.ZERO_OR_MORE );

        this.classifier = classifier;
        artifact.getClassifier();
        artifactCtl.setReturnValue( classifier, MockControl.ZERO_OR_MORE );

        setSnapshot( isSnapshot );
        setGroupId( groupId );
        setArtifactId( artifactId );
        setVersion( version );
        setBaseVersion( baseVersion );
        setType( type );

        setId();
        setDependencyConflictId();
    }

    public void setExtension( final String extension )
    {
        setTypeAndExt( extension );
    }

    public MockControl getArtifactCtl()
    {
        return artifactCtl;
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public void setArtifactId( final String artifactId )
    {
        if ( ( artifactId != null ) && ( this.artifactId == null ) )
        {
            artifact.getArtifactId();
            artifactCtl.setReturnValue( artifactId, MockControl.ZERO_OR_MORE );

            this.artifactId = artifactId;
        }
    }

    public void setBaseVersion( final String baseVersion )
    {
        if ( ( baseVersion != null ) && ( this.baseVersion == null ) )
        {
            artifact.getBaseVersion();
            artifactCtl.setReturnValue( baseVersion, MockControl.ZERO_OR_MORE );

            this.baseVersion = baseVersion;
        }
    }

    public void setFile( final File destination )
    {
        if ( ( destination != null ) && ( file == null ) )
        {
            artifact.getFile();
            artifactCtl.setReturnValue( destination, MockControl.ZERO_OR_MORE );

            file = destination;
        }
    }

    public void setGroupId( final String groupId )
    {
        if ( ( groupId != null ) && ( this.groupId == null ) )
        {
            artifact.getGroupId();
            artifactCtl.setReturnValue( groupId, MockControl.ZERO_OR_MORE );

            this.groupId = groupId;
        }
    }

    public void setVersion( final String version )
    {
        if ( ( version != null ) && ( this.version == null ) )
        {
            artifact.getVersion();
            artifactCtl.setReturnValue( version, MockControl.ZERO_OR_MORE );

            this.version = version;

            if ( isSnapshot != Boolean.TRUE )
            {
                setBaseVersion( version );
                setSnapshot( false );
            }
        }
    }

    public void setDependencyTrail( final List<String> dependencyTrail )
    {
        if ( ( dependencyTrail != null ) && ( this.dependencyTrail == null ) )
        {
            artifact.getDependencyTrail();
            artifactCtl.setReturnValue( dependencyTrail, MockControl.ZERO_OR_MORE );

            this.dependencyTrail = dependencyTrail;
        }
    }

    public void setId( final String id )
    {
        if ( ( id != null ) && ( this.id == null ) )
        {
            artifact.getId();
            artifactCtl.setReturnValue( id, MockControl.ZERO_OR_MORE );

            this.id = id;
        }
    }

    public void setDependencyConflictId( final String id )
    {
        if ( ( id != null ) && ( dependencyConflictId == null ) )
        {
            artifact.getDependencyConflictId();
            artifactCtl.setReturnValue( id, MockControl.ZERO_OR_MORE );

            dependencyConflictId = id;
        }
    }

    public void setSnapshot( final boolean snapshot )
    {
        if ( isSnapshot == null )
        {
            artifact.isSnapshot();
            artifactCtl.setReturnValue( snapshot, MockControl.ZERO_OR_MORE );

            isSnapshot = Boolean.valueOf( snapshot );
        }
    }

    public File setNewFile()
        throws IOException
    {
        if ( file == null )
        {
            final File newFile = File.createTempFile( "ArtifactMock.test.", "" );
            newFile.deleteOnExit();

            artifact.getFile();
            artifactCtl.setReturnValue( newFile, MockControl.ZERO_OR_MORE );

            file = newFile;
        }

        return file;
    }

    public void setType( final String type )
    {
        setTypeAndExt( type );
    }

    private void setTypeAndExt( final String type )
    {
        if ( ( type != null ) && ( typeAndExt == null ) )
        {
            artifact.getType();
            artifactCtl.setReturnValue( type, MockControl.ZERO_OR_MORE );

            handler.getExtension();
            handlerCtl.setReturnValue( type, MockControl.ZERO_OR_MORE );

            typeAndExt = type;
        }
    }

    private void setDependencyConflictId()
    {
        if ( ( groupId != null ) && ( artifactId != null ) && ( typeAndExt != null ) )
        {
            final String id =
                groupId + ":" + artifactId + ":" + typeAndExt + ( classifier == null ? "" : ":" + classifier );
            setDependencyConflictId( id );
        }
    }

    private void setId()
    {
        if ( ( groupId != null ) && ( artifactId != null ) && ( typeAndExt != null ) && ( version != null ) )
        {
            final String id =
                groupId + ":" + artifactId + ":" + version + ":" + typeAndExt
                                + ( classifier == null ? "" : ":" + classifier );
            setId( id );
        }
    }

    public void setNullFile()
    {
        artifact.getFile();
        artifactCtl.setReturnValue( null, MockControl.ZERO_OR_MORE );

        file = new File( "set-to-null" );
    }

}
