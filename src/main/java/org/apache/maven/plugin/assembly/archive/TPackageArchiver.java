package org.apache.maven.plugin.assembly.archive;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

public class TPackageArchiver extends TarArchiver 
{

    @Override
    protected void execute() throws ArchiverException, IOException 
    {        
        String tpackageName = getDestFile().getName().replace(".tpackage", "");
        String tpackageManifestFilename = tpackageName + ".manifest";
        
        // create the manifest file in same location as our intended archive output ($project.dir/target)
        File tpackageManifestFile = new File(getDestFile().getParent(), tpackageManifestFilename); 
        
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(tpackageManifestFile);
            
            // manifest header
            pw.println("manifest-file-version 1");
            pw.format("package %s\n", tpackageName);
            
            // manifest file entries
            ResourceIterator iter = getResources();
            while (iter.hasNext())
            {
                ArchiveEntry entry = iter.next();
                
                // JO - Not sure why a blank entry appears in the first row?
                if (StringUtils.isBlank(entry.getName())) continue;
                
                pw.format("file %s 0444 NOUSER NOGROUP f\n", entry.getName());
            }
        } 
        finally 
        {
            IOUtil.close(pw);            
        }
        
        // add manifest file to list of files to packaged in the archive
        addFile(tpackageManifestFile, tpackageManifestFilename);
        
        // default to the basic tar archiver to package things up
        super.execute();
        
        // delete manifest file now that its been added to the archive
        tpackageManifestFile.delete();
    }


    @Override
    protected String getArchiveType() 
    {
        return "tpackage";
    }

}
