import kotlin.text.Charsets;
import spark.Spark;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by TylersDesktop on 9/16/2017.
 *
 */


class Server
{
    private String projectFolder;
    private String indexPath;
    private int updateInterval;
    private ArrayList<ProjectFile> projectFiles = new ArrayList<>();
    public boolean isRunning = false;
    public boolean isPaused = false;


    public void start(String projectFolder, String indexPath, int updateInterval)
    {
        this.isRunning = true;
        this.projectFolder = projectFolder;
        this.indexPath = indexPath;
        this.updateInterval = updateInterval;
        this.addPathsForFolder(new File(this.projectFolder));
        get("/", (request, response) -> {
            return this.isRunning ? addUpdateScript(renderContent(indexPath), updateInterval) : Spark.halt(this.addUpdateScript("Server Stopped...", 10));
        });
        get("/*", ((request, response) -> {
            if(request.splat().length < 1)
                return null;
            else if (this.isRunning == false)
                return Spark.halt(this.addUpdateScript("Server Stopped...", 10));
            String filePath = (this.projectFolder + "/" + request.splat()[0]);
            if(new File(filePath).exists() == false){return null;}
            response.raw().getOutputStream().write(Files.readAllBytes(Paths.get(filePath)));
            response.raw().getOutputStream().flush();
            response.raw().getOutputStream().close();
            return response.raw();
        }));

        post("/updater", ((request, response) -> {
            if(projectFilesWereChanged())
                return "TRUE";
            else
                return "FALSE";
        }));
    }

    public void stop(){this.isRunning = false;}

    private String addUpdateScript(String html, int interval)
    {
        String script = "" +
                "\n <script>" +
                "\n setInterval(function(){" +
                "\n     var updateRequest = new XMLHttpRequest();" +
                "\n     updateRequest.open('POST', '/updater');" +
                "\n     updateRequest.onload = function(){" +
                "\n         if(updateRequest.responseText == 'TRUE')" +
                "\n             location.reload();" +
                "\n     };" +
                "\n     updateRequest.send('Should I Update?');" +
                "\n }, " + (interval * 1000) + ");" +
                "\n </script>";
        return (html + script);
    }

    private String renderContent(String path)
    {

        try
        {
            return new String(Files.readAllBytes(Paths.get(path)), Charsets.UTF_8);
        }
        catch(Exception ex){System.out.println(ex.getLocalizedMessage());}
        return "Couldn't Load File...";
    }

    private boolean projectFilesWereChanged()
    {
        if(this.isPaused)
            return false;
        ArrayList<ProjectFile> updatedFiles = new ArrayList<>();
        this.projectFiles.forEach(projectFile -> {
            updatedFiles.add(new ProjectFile(projectFile.path));
        });
        for(int i = 0; i < this.projectFiles.size(); i++)
        {
            ProjectFile oldFile = this.projectFiles.get(i);
            for(int j = 0; j < updatedFiles.size(); j++)
            {
                ProjectFile updatedFile = updatedFiles.get(j);
                if(updatedFile.name.equals(oldFile.name) && updatedFile.hash != oldFile.hash)
                {
                    this.projectFiles = updatedFiles;
                    return true;
                }
            }
        }
        return false;
    }

    private void addPathsForFolder(File folder)
    {
        try
        {
            File[] files = folder.listFiles();
            if(files == null){return;}
            for(File file : files)
            {
                if(file.isDirectory())
                    this.addPathsForFolder(file);
                else
                {
                    this.projectFiles.add(new ProjectFile(file));
                    String route = file.getPath().replace(new File(this.projectFolder).getPath(), "");
                    get(route, ((request, response) -> this.renderContent(file.getPath())));
                }
            }
        }
        catch(Exception ex){System.out.println(ex.getLocalizedMessage());}
    }
}
