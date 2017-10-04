import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by TylersDesktop on 9/21/2017.
 *
 */

public class ProjectFile
{
    String name;
    int hash;
    String path;

    public ProjectFile(String name, int hash, String path)
    {
        this.name = name;
        this.hash = hash;
        this.path = path;
    }

    public ProjectFile(String path)
    {
        File file = new File(path);
        this.name = new ProjectFile(file).name;
        this.hash = new ProjectFile(file).hash;
        this.path = file.getPath();
    }
    public ProjectFile(File file)
    {
        try
        {
            String contents = new String(Files.readAllBytes(Paths.get(file.getPath())));
            this.name = file.getName();
            this.hash = contents.hashCode();
            this.path = file.getPath();
        }
        catch(Exception ex){System.out.println(ex.getLocalizedMessage());}
    }
}
