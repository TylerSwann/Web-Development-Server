/*
 *
 * Created by TylersDesktop on 9/17/2017.
 *
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class App extends Application
{
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("App.fxml").openStream());
            primaryStage.setScene(new Scene(root, 471, 603));
            primaryStage.setResizable(false);
            AppController controller = loader.getController();
            controller.stage = primaryStage;
            primaryStage.show();
        }
        catch(IOException ex){ex.printStackTrace();}
    }
}























