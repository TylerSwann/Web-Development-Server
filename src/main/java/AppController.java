import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.Inet4Address;

/**
 * Created by TylersDesktop on 9/17/2017.
 *
 */

public class AppController
{

    public TextField projectFolderField;
    public JFXButton browseFolder;
    public TextField indexHtmlField;
    public JFXButton browseFile;
    public JFXSlider intervalSlider;
    public JFXButton startButton;
    public Stage stage;
    public Label serverAddressLabel;
    public JFXButton stopButton;

    private String projectDirectory;
    private String indexPath;
    private int updateInterval = 1;
    private Server server = new Server();

    @FXML
    public void initialize()
    {
        this.serverAddressLabel.setText(this.getServerAddress());
        this.indexHtmlField.setEditable(false);
        this.projectFolderField.setEditable(false);
        this.projectFolderField.deselect();
        this.intervalSlider.valueProperty().addListener((listener) -> this.updateInterval = (int)this.intervalSlider.getValue());
        this.browseFolder.setOnAction((event) -> this.showFolderDialog());
        this.browseFile.setOnAction((event) -> this.showFileDialog());
        this.startButton.setDisable(true);
        this.startButton.setOnAction((event) -> {
            if(this.server.isRunning == false)
            {
                this.startServer();
                this.startButton.setText("PAUSE");
            }
            else if (!this.server.isPaused)
            {
                this.startButton.setText("START");
                this.server.isPaused = true;
            }
            else
            {
                this.startButton.setText("PAUSE");
                this.server.isPaused = false;
            }
        });
        this.stopButton.setOnAction((event) -> {
            this.startButton.setText("START");
            this.server.isPaused = false;
            this.server.stop();
        });
    }

    private void startServer()
    {
        this.server.start(this.projectDirectory, this.indexPath, this.updateInterval);
    }

    private String getServerAddress()
    {
        try
        {
            String ipAddress = Inet4Address.getLocalHost().getHostAddress();
            return ("http://" + ipAddress + ":4567");
        }
        catch(Exception ex){System.out.println(ex.getLocalizedMessage());}
        return "Couldn't get local address...";
    }

    private void showFolderDialog()
    {
        DirectoryChooser folderDialog = new DirectoryChooser();
        folderDialog.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        folderDialog.setTitle("Project Directory");
        File projectFolder = folderDialog.showDialog(this.stage);
        if(projectFolder == null){return;}
        this.projectDirectory = projectFolder.getPath();
        this.projectFolderField.setText(this.projectDirectory);
        checkIfSetupIsCompleted();
    }

    private void showFileDialog()
    {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("Index.html");
        fileDialog.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        File index = fileDialog.showOpenDialog(this.stage);
        if(index == null){return;}
        this.indexPath = index.getPath();
        this.indexHtmlField.setText(this.indexPath);
        checkIfSetupIsCompleted();
    }
    private void checkIfSetupIsCompleted()
    {
        if(this.projectDirectory != null && this.indexPath != null)
            this.startButton.setDisable(false);
    }
}













