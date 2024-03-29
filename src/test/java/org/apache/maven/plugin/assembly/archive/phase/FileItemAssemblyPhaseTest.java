package org.apache.maven.plugin.assembly.archive.phase;

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

import org.apache.maven.model.Model;
import org.apache.maven.plugin.assembly.AssemblerConfigurationSource;
import org.apache.maven.plugin.assembly.DefaultAssemblyContext;
import org.apache.maven.plugin.assembly.archive.ArchiveCreationException;
import org.apache.maven.plugin.assembly.format.AssemblyFormattingException;
import org.apache.maven.plugin.assembly.model.Assembly;
import org.apache.maven.plugin.assembly.model.FileItem;
import org.apache.maven.plugin.assembly.testutils.MockManager;
import org.apache.maven.plugin.assembly.testutils.TestFileManager;
import org.apache.maven.plugin.assembly.utils.TypeConversionUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.easymock.MockControl;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class FileItemAssemblyPhaseTest
    extends TestCase
{

    private final TestFileManager fileManager = new TestFileManager( "file-item-phase.test.", "" );

    @Override
    public void tearDown()
        throws IOException
    {
        fileManager.cleanUp();
    }

    public void testExecute_ShouldAddNothingWhenNoFileItemsArePresent()
        throws ArchiveCreationException, AssemblyFormattingException
    {
        final MockManager mm = new MockManager();

        final MockAndControlForConfigSource macCS = new MockAndControlForConfigSource( mm );

        final File basedir = fileManager.createTempDir();

        macCS.expectGetBasedir( basedir );

        final MockAndControlForLogger macLogger = new MockAndControlForLogger( mm );

        final Assembly assembly = new Assembly();
        assembly.setId( "test" );

        mm.replayAll();

        createPhase( macLogger.logger ).execute( assembly, null, macCS.configSource, new DefaultAssemblyContext() );

        mm.verifyAll();
    }

    public void testExecute_ShouldAddAbsoluteFileNoFilterNoLineEndingConversion()
        throws ArchiveCreationException, AssemblyFormattingException, IOException
    {
        final MockManager mm = new MockManager();

        final MockAndControlForConfigSource macCS = new MockAndControlForConfigSource( mm );

        final File basedir = fileManager.createTempDir();

        final File file = fileManager.createFile( basedir, "file.txt", "This is a test file." );

        macCS.expectGetBasedir( basedir );

        final File tempRoot = fileManager.createTempDir();

        macCS.expectGetTemporaryRootDirectory( tempRoot );

        macCS.expectGetProject( new MavenProject( new Model() ) );

        macCS.expectGetFinalName( "final-name" );

        final MockAndControlForLogger macLogger = new MockAndControlForLogger( mm );

        final MockAndControlForArchiver macArchiver = new MockAndControlForArchiver( mm );

        final Assembly assembly = new Assembly();
        assembly.setId( "test" );

        final FileItem fi = new FileItem();
        fi.setSource( file.getAbsolutePath() );
        fi.setFiltered( false );
        fi.setLineEnding( "keep" );
        fi.setFileMode( "777" );

        macArchiver.expectAddFile( file,
                                   "file.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );

        assembly.addFile( fi );
        
        macCS.expectGetEncoding();

        mm.replayAll();

        createPhase( macLogger.logger ).execute( assembly, macArchiver.archiver, macCS.configSource,
                                                 new DefaultAssemblyContext() );

        mm.verifyAll();
    }

    public void testExecute_ShouldAddRelativeFileNoFilterNoLineEndingConversion()
        throws ArchiveCreationException, AssemblyFormattingException, IOException
    {
        final MockManager mm = new MockManager();

        final MockAndControlForConfigSource macCS = new MockAndControlForConfigSource( mm );

        final File basedir = fileManager.createTempDir();

        final File file = fileManager.createFile( basedir, "file.txt", "This is a test file." );

        macCS.expectGetBasedir( basedir );

        final File tempRoot = fileManager.createTempDir();

        macCS.expectGetTemporaryRootDirectory( tempRoot );

        macCS.expectGetProject( new MavenProject( new Model() ) );

        macCS.expectGetFinalName( "final-name" );

        final MockAndControlForLogger macLogger = new MockAndControlForLogger( mm );

        final MockAndControlForArchiver macArchiver = new MockAndControlForArchiver( mm );

        final Assembly assembly = new Assembly();
        assembly.setId( "test" );

        final FileItem fi = new FileItem();
        fi.setSource( "file.txt" );
        fi.setFiltered( false );
        fi.setLineEnding( "keep" );
        fi.setFileMode( "777" );

        macArchiver.expectAddFile( file,
                                   "file.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );

        assembly.addFile( fi );

        macCS.expectGetEncoding();

        mm.replayAll();

        createPhase( macLogger.logger ).execute( assembly, macArchiver.archiver, macCS.configSource,
                                                 new DefaultAssemblyContext() );

        mm.verifyAll();
    }

    public void testExecute_WithOutputDirectory()
        throws Exception
    {
        final MockManager mm = new MockManager();

        final MockAndControlForConfigSource macCS = new MockAndControlForConfigSource( mm );

        final File basedir = fileManager.createTempDir();

        final File readmeFile = fileManager.createFile( basedir, "README.txt", "This is a test file for README.txt." );
        final File licenseFile =
            fileManager.createFile( basedir, "LICENSE.txt", "This is a test file for LICENSE.txt." );
        final File configFile =
            fileManager.createFile( basedir, "config/config.txt", "This is a test file for config/config.txt" );

        macCS.expectGetBasedir( basedir );

        final File tempRoot = fileManager.createTempDir();

        macCS.expectGetTemporaryRootDirectory( tempRoot );

        macCS.expectGetProject( new MavenProject( new Model() ) );

        macCS.expectGetFinalName( "final-name" );

        final MockAndControlForLogger macLogger = new MockAndControlForLogger( mm );

        final MockAndControlForArchiver macArchiver = new MockAndControlForArchiver( mm );

        final Assembly assembly = new Assembly();
        assembly.setId( "test" );
        assembly.setIncludeBaseDirectory( true );

        final FileItem readmeFileItem = new FileItem();
        readmeFileItem.setSource( "README.txt" );
        readmeFileItem.setOutputDirectory( "" );
        readmeFileItem.setFiltered( false );
        readmeFileItem.setLineEnding( "keep" );
        readmeFileItem.setFileMode( "777" );

        final FileItem licenseFileItem = new FileItem();
        licenseFileItem.setSource( "LICENSE.txt" );
        licenseFileItem.setOutputDirectory( "/" );
        licenseFileItem.setFiltered( false );
        licenseFileItem.setLineEnding( "keep" );
        licenseFileItem.setFileMode( "777" );

        final FileItem configFileItem = new FileItem();
        configFileItem.setSource( "config/config.txt" );
        configFileItem.setOutputDirectory( "config" );
        configFileItem.setFiltered( false );
        configFileItem.setLineEnding( "keep" );
        configFileItem.setFileMode( "777" );

        macArchiver.expectAddFile( readmeFile,
                                   "README.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );
        macArchiver.expectAddFile( licenseFile,
                                   "LICENSE.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );
        macArchiver.expectAddFile( configFile,
                                   "config/config.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );

        assembly.addFile( readmeFileItem );
        assembly.addFile( licenseFileItem );
        assembly.addFile( configFileItem );

        macCS.expectGetEncoding();

        mm.replayAll();

        createPhase( macLogger.logger ).execute( assembly, macArchiver.archiver, macCS.configSource,
                                                 new DefaultAssemblyContext() );

        mm.verifyAll();
    }

    public void testExecute_WithOutputDirectoryAndDestName()
        throws Exception
    {
        final MockManager mm = new MockManager();

        final MockAndControlForConfigSource macCS = new MockAndControlForConfigSource( mm );

        final File basedir = fileManager.createTempDir();

        final File readmeFile = fileManager.createFile( basedir, "README.txt", "This is a test file for README.txt." );
        final File licenseFile =
            fileManager.createFile( basedir, "LICENSE.txt", "This is a test file for LICENSE.txt." );
        final File configFile =
            fileManager.createFile( basedir, "config/config.txt", "This is a test file for config/config.txt" );

        macCS.expectGetBasedir( basedir );

        final File tempRoot = fileManager.createTempDir();

        macCS.expectGetTemporaryRootDirectory( tempRoot );

        macCS.expectGetProject( new MavenProject( new Model() ) );

        macCS.expectGetFinalName( "final-name" );

        final MockAndControlForLogger macLogger = new MockAndControlForLogger( mm );

        final MockAndControlForArchiver macArchiver = new MockAndControlForArchiver( mm );

        final Assembly assembly = new Assembly();
        assembly.setId( "test" );
        assembly.setIncludeBaseDirectory( true );

        final FileItem readmeFileItem = new FileItem();
        readmeFileItem.setSource( "README.txt" );
        readmeFileItem.setOutputDirectory( "" );
        readmeFileItem.setDestName( "README_renamed.txt" );
        readmeFileItem.setFiltered( false );
        readmeFileItem.setLineEnding( "keep" );
        readmeFileItem.setFileMode( "777" );

        final FileItem licenseFileItem = new FileItem();
        licenseFileItem.setSource( "LICENSE.txt" );
        licenseFileItem.setOutputDirectory( "/" );
        licenseFileItem.setDestName( "LICENSE_renamed.txt" );
        licenseFileItem.setFiltered( false );
        licenseFileItem.setLineEnding( "keep" );
        licenseFileItem.setFileMode( "777" );

        final FileItem configFileItem = new FileItem();
        configFileItem.setSource( "config/config.txt" );
        configFileItem.setDestName( "config_renamed.txt" );
        configFileItem.setOutputDirectory( "config" );
        configFileItem.setFiltered( false );
        configFileItem.setLineEnding( "keep" );
        configFileItem.setFileMode( "777" );

        macArchiver.expectAddFile( readmeFile,
                                   "README_renamed.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );
        macArchiver.expectAddFile( licenseFile,
                                   "LICENSE_renamed.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );
        macArchiver.expectAddFile( configFile,
                                   "config/config_renamed.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );

        assembly.addFile( readmeFileItem );
        assembly.addFile( licenseFileItem );
        assembly.addFile( configFileItem );

        macCS.expectGetEncoding();

        mm.replayAll();

        createPhase( macLogger.logger ).execute( assembly, macArchiver.archiver, macCS.configSource,
                                                 new DefaultAssemblyContext() );

        mm.verifyAll();
    }

    public void testExecute_WithOutputDirectoryAndDestNameAndIncludeBaseDirectoryFalse()
        throws Exception
    {
        final MockManager mm = new MockManager();

        final MockAndControlForConfigSource macCS = new MockAndControlForConfigSource( mm );

        final File basedir = fileManager.createTempDir();

        final File readmeFile = fileManager.createFile( basedir, "README.txt", "This is a test file for README.txt." );
        final File licenseFile =
            fileManager.createFile( basedir, "LICENSE.txt", "This is a test file for LICENSE.txt." );
        final File configFile =
            fileManager.createFile( basedir, "config/config.txt", "This is a test file for config/config.txt" );

        macCS.expectGetBasedir( basedir );

        final File tempRoot = fileManager.createTempDir();

        macCS.expectGetTemporaryRootDirectory( tempRoot );

        macCS.expectGetProject( new MavenProject( new Model() ) );

        macCS.expectGetFinalName( "final-name" );

        final MockAndControlForLogger macLogger = new MockAndControlForLogger( mm );

        final MockAndControlForArchiver macArchiver = new MockAndControlForArchiver( mm );

        final Assembly assembly = new Assembly();
        assembly.setId( "test" );
        assembly.setIncludeBaseDirectory( false );

        final FileItem readmeFileItem = new FileItem();
        readmeFileItem.setSource( "README.txt" );
        readmeFileItem.setDestName( "README_renamed.txt" );
        readmeFileItem.setFiltered( false );
        readmeFileItem.setLineEnding( "keep" );
        readmeFileItem.setFileMode( "777" );

        final FileItem licenseFileItem = new FileItem();
        licenseFileItem.setSource( "LICENSE.txt" );
        licenseFileItem.setDestName( "LICENSE_renamed.txt" );
        licenseFileItem.setFiltered( false );
        licenseFileItem.setLineEnding( "keep" );
        licenseFileItem.setFileMode( "777" );

        final FileItem configFileItem = new FileItem();
        configFileItem.setSource( "config/config.txt" );
        configFileItem.setDestName( "config_renamed.txt" );
        configFileItem.setOutputDirectory( "config" );
        configFileItem.setFiltered( false );
        configFileItem.setLineEnding( "keep" );
        configFileItem.setFileMode( "777" );

        macArchiver.expectAddFile( readmeFile,
                                   "README_renamed.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );
        macArchiver.expectAddFile( licenseFile,
                                   "LICENSE_renamed.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );
        macArchiver.expectAddFile( configFile,
                                   "config/config_renamed.txt",
                                   TypeConversionUtils.modeToInt( "777", new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) ) );

        assembly.addFile( readmeFileItem );
        assembly.addFile( licenseFileItem );
        assembly.addFile( configFileItem );

        macCS.expectGetEncoding();

        mm.replayAll();

        createPhase( macLogger.logger ).execute( assembly, macArchiver.archiver, macCS.configSource,
                                                 new DefaultAssemblyContext() );

        mm.verifyAll();
    }

    private FileItemAssemblyPhase createPhase( final Logger logger )
    {
        final FileItemAssemblyPhase phase = new FileItemAssemblyPhase();
        phase.enableLogging( logger );

        return phase;
    }

    private final class MockAndControlForArchiver
    {
        Archiver archiver;

        MockControl control;

        public MockAndControlForArchiver( final MockManager mockManager )
        {
            control = MockControl.createControl( Archiver.class );
            mockManager.add( control );

            archiver = (Archiver) control.getMock();
        }

        public void expectAddFile( final File file, final String outputLocation, final int fileMode )
        {
            try
            {
                archiver.addFile( file, outputLocation, fileMode );
            }
            catch ( final ArchiverException e )
            {
                Assert.fail( "Should never happen." );
            }
        }
    }

    private final class MockAndControlForConfigSource
    {
        AssemblerConfigurationSource configSource;

        MockControl control;

        public MockAndControlForConfigSource( final MockManager mockManager )
        {
            control = MockControl.createControl( AssemblerConfigurationSource.class );
            mockManager.add( control );

            configSource = (AssemblerConfigurationSource) control.getMock();

            configSource.getMavenSession();
            control.setReturnValue( null, MockControl.ZERO_OR_MORE );
        }

        public void expectGetProject( final MavenProject project )
        {
            configSource.getProject();
            control.setReturnValue( project, MockControl.ONE_OR_MORE );
        }

        public void expectGetFinalName( final String finalName )
        {
            configSource.getFinalName();
            control.setReturnValue( finalName, MockControl.ONE_OR_MORE );
        }

        public void expectGetTemporaryRootDirectory( final File tempRoot )
        {
            configSource.getTemporaryRootDirectory();
            control.setReturnValue( tempRoot, MockControl.ONE_OR_MORE );
        }

        public void expectGetBasedir( final File basedir )
        {
            configSource.getBasedir();
            control.setReturnValue( basedir, MockControl.ONE_OR_MORE );
        }

        public void expectGetEncoding( )
        {
            configSource.getEncoding();
            control.setReturnValue( "UTF-8", MockControl.ONE_OR_MORE );
        }
}

    private final class MockAndControlForLogger
    {
        Logger logger;

        MockControl control;

        public MockAndControlForLogger( final MockManager mockManager )
        {
            control = MockControl.createControl( Logger.class );
            mockManager.add( control );

            logger = (Logger) control.getMock();
        }
    }

}
