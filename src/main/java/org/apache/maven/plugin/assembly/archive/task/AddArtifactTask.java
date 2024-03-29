package org.apache.maven.plugin.assembly.archive.task;

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
import org.apache.maven.plugin.assembly.AssemblerConfigurationSource;
import org.apache.maven.plugin.assembly.archive.ArchiveCreationException;
import org.apache.maven.plugin.assembly.format.AssemblyFormattingException;
import org.apache.maven.plugin.assembly.utils.AssemblyFormatUtils;
import org.apache.maven.plugin.assembly.utils.TypeConversionUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @version $Id: AddArtifactTask.java 999612 2010-09-21 20:34:50Z jdcasey $
 */
public class AddArtifactTask
    implements ArchiverTask
{

    public static final String[] DEFAULT_INCLUDES_ARRAY = { "**/*" };

    private int directoryMode = -1;

    private int fileMode = -1;

    private boolean unpack = false;

    private List<String> includes;

    private List<String> excludes;

    private final Artifact artifact;

    private MavenProject project;

    private MavenProject moduleProject;

    private Artifact moduleArtifact;

    private String outputDirectory;

    private String outputFileNameMapping;

    private final Logger logger;

    public AddArtifactTask( final Artifact artifact, final Logger logger )
    {
        this.artifact = artifact;
        this.logger = logger;
    }

    public void execute( final Archiver archiver, final AssemblerConfigurationSource configSource )
        throws ArchiveCreationException, AssemblyFormattingException
    {
        // MASSEMBLY-282: We should support adding a project's standard output file as part of an assembly that replaces
        // it.
        if ( ( ( artifact.getFile() != null ) && ( archiver.getDestFile() != null ) )
                        && artifact.getFile()
                                   .equals( archiver.getDestFile() ) )
        {
            final File tempRoot = configSource.getTemporaryRootDirectory();
            final File tempArtifactFile = new File( tempRoot, artifact.getFile()
                                                                      .getName() );

            logger.warn( "Artifact: "
                            + artifact.getId()
                            + " references the same file as the assembly destination file. Moving it to a temporary location for inclusion." );
            try
            {
                FileUtils.copyFile( artifact.getFile(), tempArtifactFile );
            }
            catch ( final IOException e )
            {
                throw new ArchiveCreationException( "Error moving artifact file: '" + artifact.getFile()
                                + "' to temporary location: " + tempArtifactFile + ". Reason: " + e.getMessage(), e );
            }

            artifact.setFile( tempArtifactFile );
        }

        String destDirectory = outputDirectory;

        destDirectory =
            AssemblyFormatUtils.getOutputDirectory( destDirectory, configSource.getProject(), moduleProject, project,
                                                    configSource.getFinalName(), configSource );

        if ( unpack )
        {
            String outputLocation = destDirectory;

            if ( ( outputLocation.length() > 0 ) && !outputLocation.endsWith( "/" ) )
            {
                outputLocation += "/";
            }

            String[] includesArray = TypeConversionUtils.toStringArray( includes );
            if ( includesArray == null )
            {
                includesArray = DEFAULT_INCLUDES_ARRAY;
            }
            final String[] excludesArray = TypeConversionUtils.toStringArray( excludes );

            final int oldDirMode = archiver.getOverrideDirectoryMode();
            final int oldFileMode = archiver.getOverrideFileMode();

            logger.debug( "Unpacking artifact: " + artifact.getId() + " to assembly location: " + outputLocation + "." );

            boolean fileModeSet = false;
            boolean dirModeSet = false;

            try
            {
                if ( fileMode != -1 )
                {
                    archiver.setFileMode( fileMode );
                    fileModeSet = true;
                }

                if ( directoryMode != -1 )
                {
                    archiver.setDirectoryMode( directoryMode );
                    dirModeSet = true;
                }

                final File artifactFile = artifact.getFile();
                if ( artifactFile == null )
                {
                    logger.warn( "Skipping artifact: " + artifact.getId()
                                    + "; it does not have an associated file or directory." );
                }
                else if ( artifactFile.isDirectory() )
                {
                    logger.debug( "Adding artifact directory contents for: " + artifact + " to: " + outputLocation );
                    archiver.addDirectory( artifactFile, outputLocation, includesArray, excludesArray );
                }
                else
                {
                    logger.debug( "Unpacking artifact contents for: " + artifact + " to: " + outputLocation );
                    logger.debug( "includes:\n" + StringUtils.join( includesArray, "\n" ) + "\n" );
                    logger.debug( "excludes:\n"
                                    + ( excludesArray == null ? "none" : StringUtils.join( excludesArray, "\n" ) )
                                    + "\n" );
                    archiver.addArchivedFileSet( artifactFile, outputLocation, includesArray, excludesArray );
                }
            }
            catch ( final ArchiverException e )
            {
                throw new ArchiveCreationException( "Error adding file-set for '" + artifact.getId() + "' to archive: "
                                + e.getMessage(), e );
            }
            finally
            {
                if ( dirModeSet )
                {
                    archiver.setDirectoryMode( oldDirMode );
                }

                if ( fileModeSet )
                {
                    archiver.setFileMode( oldFileMode );
                }
            }
        }
        else
        {
            final String fileNameMapping =
                AssemblyFormatUtils.evaluateFileNameMapping( outputFileNameMapping, artifact,
                                                             configSource.getProject(), moduleProject, moduleArtifact,
                                                             project, configSource );

            final String outputLocation = destDirectory + fileNameMapping;

            try
            {
                final File artifactFile = artifact.getFile();

                logger.debug( "Adding artifact: " + artifact.getId() + " with file: " + artifactFile
                                + " to assembly location: " + outputLocation + "." );

                if ( fileMode != -1 )
                {
                    archiver.addFile( artifactFile, outputLocation, fileMode );
                }
                else
                {
                    archiver.addFile( artifactFile, outputLocation );
                }
            }
            catch ( final ArchiverException e )
            {
                throw new ArchiveCreationException( "Error adding file '" + artifact.getId() + "' to archive: "
                                + e.getMessage(), e );
            }
        }
    }

    public void setDirectoryMode( final int directoryMode )
    {
        this.directoryMode = directoryMode;
    }

    public void setFileMode( final int fileMode )
    {
        this.fileMode = fileMode;
    }

    public void setExcludes( final List<String> excludes )
    {
        this.excludes = excludes;
    }

    public void setIncludes( final List<String> includes )
    {
        this.includes = includes;
    }

    public void setUnpack( final boolean unpack )
    {
        this.unpack = unpack;
    }

    public void setProject( final MavenProject project )
    {
        this.project = project;
    }

    public void setOutputDirectory( final String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    public void setFileNameMapping( final String outputFileNameMapping )
    {
        this.outputFileNameMapping = outputFileNameMapping;
    }

    public void setOutputDirectory( final String outputDirectory, final String defaultOutputDirectory )
    {
        setOutputDirectory( outputDirectory == null ? defaultOutputDirectory : outputDirectory );
    }

    public void setFileNameMapping( final String outputFileNameMapping, final String defaultOutputFileNameMapping )
    {
        setFileNameMapping( outputFileNameMapping == null ? defaultOutputFileNameMapping : outputFileNameMapping );
    }

    public MavenProject getModuleProject()
    {
        return moduleProject;
    }

    public void setModuleProject( final MavenProject moduleProject )
    {
        this.moduleProject = moduleProject;
    }

    public Artifact getModuleArtifact()
    {
        return moduleArtifact;
    }

    public void setModuleArtifact( final Artifact moduleArtifact )
    {
        this.moduleArtifact = moduleArtifact;
    }

}
