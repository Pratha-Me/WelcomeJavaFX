/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author dinesh
 */

public class WelcomeController implements Initializable
{
    @FXML private ScrollPane sp;
    @FXML private VBox spvb, vb;
    @FXML private AnchorPane vbap;
    @FXML private Button btk, btb, btl, btd, btbr;
    @FXML private Button niButton, siButton,intBtn, extBtn;

    @FXML private StackPane stpa;
    @FXML private HBox hbTemp, spaphb, aphb, hb;
    @FXML private TilePane tileRecent;
    @FXML private FlowPane pane;
    @FXML private Label lvb,recentLabel;

    private final Button btext = new Button("Building");

    private final String imgFile = "/Default.jpg";
    private final String imgDirInt = "data/jasminepaint/Interior";

    private final String bg = "-fx-background-color: rgba(40,40,40,1.0);";
    private final String bn1 = "-fx-background-color:transparent;";
    private final String bn2 = "-fx-border-radius: 25; -fx-border-color: yellow ; -fx-border-width: 2;" +
            " -fx-border-style: solid; -fx-background-color:transparent;";
    private final String style1 = "-fx-background-color:transparent;" +
            " -fx-background-radius: 20;-fx-background-insets: 0; -fx-text-fill: white;"
            + "-fx-border-color: yellow ; -fx-border-width: 2.0; -fx-border-radius: 20; -fx-border-style: solid;";
    private final String style2 = "-fx-background-color:transparent; -fx-text-fill: white;";

    private ArrayList<Button> btnBasket = new ArrayList<>();
    static RecentCollection<String> recentFiles = new RecentCollection<String>();

//    private final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        getDirectory();
        if (!Util.applicationStart) {
            checkForUpdate();
            Util.applicationStart=true;
        }

        //Clear Item Fromt the Util file
        Util.setFilePath(null);
        Util.setImg(null);
        Util.setShadeColor(null);
        Util.setPf(null);
        //End of the clear

        welcomeExtractFile();

        niButton.setOnAction((ActionEvent event) -> {
            try {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
                FileChooser.ExtensionFilter extFilterJPEG = new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.jpeg");
                fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterJPEG);
                //Show open file dialog
                File file = fileChooser.showOpenDialog(null);
                if (file != null)
                {
                    recentFiles.add(file.getAbsolutePath());
                    updateRecent();

                    BufferedImage bufferedImage;
                    bufferedImage = ImageIO.read(file);

                    if (bufferedImage.getWidth() > 1200 || bufferedImage.getHeight() > 1200) {
                        Util.setImg(resizeImage(bufferedImage, 1200));
                    } else {
//                        Util.setImg(resizeImage(bufferedImage, bufferedImage.getWidth() > bufferedImage.getHeight() ? bufferedImage.getWidth() : bufferedImage.getHeight()));
                        Util.setImg(bufferedImage);
                    }
                    bufferedImage.flush();
                    try
                    {
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/BrightnessAndContrast.fxml"));
                        Parent root = loader.load();
//                        BrightnessAndContrastController controller = (BrightnessAndContrastController) loader.getController();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.getIcons().add(new Image(getClass().getResource("/Controller/images/logo.png").toExternalForm()));
                        stage.setTitle("Color Visualizer - Jasmine Paints!");
                        stage.setResizable(true);
                        stage.setMaximized(true);
                        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (WindowEvent window) -> {
                            ((Node) event.getSource()).getScene().getWindow().hide();
                        });
                        stage.show();
                    } catch (Exception ex) {
                        Logger.getLogger(WelcomeController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } catch (IOException | HeadlessException ex) {
                Logger.getLogger(VisualizerController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });

        aphb.setSpacing(20); aphb.setStyle(bg);
        aphb.setAlignment(Pos.CENTER);

        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setPannable(true); sp.setStyle(bg);

        // sp Children---------------------------------------------
        spvb.setStyle(bg); spvb.setAlignment(Pos.TOP_CENTER); spvb.setSpacing(25);
        pane.setStyle(bg);

/*
        In order to get the size of the child node (Flowpane, id:spvb) of the node Scrollpane (id:sp), the syntax
        spvb.minWidth(sp.getMinViewportHeight()); can be placed in this controller.
        However, momentarily in the welcome.fxml file the minimum width of the flowpane is bound to the scrollpane
        by implanting minWidth="${sp.viewportBounds.width}"

        spvb.minWidth(sp.getMinViewportWidth());    (In the Controller.java file)
        minWidth="${sp.viewportBounds.width}"       (In the welcome.fxml file)
*/

        spaphb.setStyle("-fx-background-color: rgba(20,20,20, 1.0);");
        spaphb.setAlignment(Pos.CENTER); spaphb.setSpacing(20);

        // spaphb Children-----------------------------------------
        intBtn.setStyle(style1); extBtn.setStyle(style2);
        //-----------------------------------------------------------

        hbTemp.setStyle("-fx-background-color: rgba(20,20,20, 1.0);");
        hbTemp.setAlignment(Pos.CENTER); hbTemp.setSpacing(20);

        // hbTemp Children---------------------------------------------
        try { updateHBoxTemp(imgDirInt + "/" +"Living Room"); }
        catch (NullPointerException | FileNotFoundException e) { e.printStackTrace(); }

        btk.setStyle(bn1); btb.setStyle(bn1); btl.setStyle(bn1);
        btd.setStyle(bn1); btbr.setStyle(bn1); btext.setStyle(bn1);

        buildHBoxBtn(btl,"l.png", imgDirInt); buildHBoxBtn(btk,"k.png", imgDirInt);
        buildHBoxBtn(btd,"d.png", imgDirInt); buildHBoxBtn(btb,"b.png", imgDirInt);
        buildHBoxBtn(btbr,"br.png", imgDirInt);
        //---------------------------------------------------------

        tileRecent.setHgap(20); tileRecent.setVgap(20);
        tileRecent.setPrefColumns(hashCode());
        //---------------------------------------------------------

        stpa.setAlignment(Pos.CENTER);
        stpa.setStyle(bg);

        hb.setStyle("-fx-background-color: rgba(20,20,20, 0.50);");
        HBox.setHgrow(hb, Priority.ALWAYS);
        vb.setStyle(bg);

        // vb Children---------------------------------------------
        vbap.setStyle(bg);
        lvb.setStyle("-fx-background-color: rgba(60,60,60,1.0);");
        niButton.setStyle(bn1); siButton.setStyle(bn1);
        //---------------------------------------------------------

        niButton.setOnMouseEntered((MouseEvent event) -> { niButton.setStyle(bn2); });
        niButton.setOnMouseExited((MouseEvent event) -> { niButton.setStyle(bn1); });

        siButton.setOnMouseEntered((MouseEvent event) -> { siButton.setStyle(bn2); });
        siButton.setOnMouseExited((MouseEvent event) -> { siButton.setStyle(bn1); });
    }

    @FXML private void siHandle(ActionEvent event) {
        try {
            ((Node) (event.getSource())).getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Controller/ImageSelection.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Save Image Selection Area");
            stage.getIcons().add(new Image(getClass().getResource("/Controller/images/logo.png").toExternalForm()));
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();
        }
        catch (Exception ex)
        { Logger.getLogger(VisualizerController.class.getName()).log(Level.SEVERE, null, ex); }
    }

    @FXML private void intAction()
    {
        extBtn.setStyle(style2); intBtn.setStyle(style1);
        spaphb.getChildren().clear();

        buildHBoxBtn(btl,"l.png", imgDirInt); buildHBoxBtn(btk,"k.png", imgDirInt);
        buildHBoxBtn(btd,"d.png", imgDirInt); buildHBoxBtn(btb,"b.png", imgDirInt);
        buildHBoxBtn(btbr,"br.png", imgDirInt);

        if (!hbTemp.getChildren().isEmpty()) { hbTemp.getChildren().clear(); }
        spaphb.getChildren().addAll(btl,btb,btk,btd,btbr);
    }

    @FXML private void extAction()
    {
        extBtn.setStyle(style1); intBtn.setStyle(style2);
        spaphb.getChildren().clear();

        buildHBoxBtn(btext, "buildExt.png", "data/jasminepaint/Exterior");

        if (!hbTemp.getChildren().isEmpty()) { hbTemp.getChildren().clear(); }
        spaphb.getChildren().add(btext);
    }

    private void buildHBoxBtn(Button btn, String btim, String imgDir)
    {
        ImageView iv = new ImageView("Controller/images/" + btim);
        iv.setFitHeight(100); iv.setFitWidth(90);

        btn.setGraphic(iv); btn.setContentDisplay(ContentDisplay.TOP);
        btn.setOnMouseEntered((MouseEvent event) ->
        {
            btn.setStyle(null);
            try { updateHBoxTemp(imgDir + "/" + btn.getText()); }
            catch (NullPointerException | FileNotFoundException e) { e.printStackTrace(); }
        });
        btn.setOnMouseExited((MouseEvent event) -> { btn.setStyle(bn1); });
    }

    private void updateHBoxTemp(String fp) throws NullPointerException, FileNotFoundException
    {
        if (!hbTemp.getChildren().isEmpty()) { hbTemp.getChildren().clear(); }

        File[] subDirectories = new File(fp).listFiles(File::isDirectory);
        if (subDirectories == null) { throw new NullPointerException(); }

        for (File sd : subDirectories)
        {
            File[] f = sd.listFiles(File::isFile);
            if (f == null) { throw new FileNotFoundException(); }

            for (File x : f)
            {
                String tb = "thumbnail.jpg";
                if (x.getName().equals(tb))
                {
                    ImageView img = new ImageView(new Image(new FileInputStream(new File(fp + "/" + sd.getName()
                            + "/" + tb)), 250, 225, false, false));
                    Button upBtn = new Button(sd.getName());
                    upBtn.setStyle(bn1); upBtn.setGraphic(img);
                    upBtn.setContentDisplay(ContentDisplay.TOP);

                    hbTemp.getChildren().add(upBtn); btnBasket.add(upBtn);
                }
            }

            for (Button dum : btnBasket)
            { buttonAction(dum, fp + "/" + dum.getText()); }
        }
    }

    private void buttonAction(Button btn, String fp)
    {
        btn.setOnMouseEntered((MouseEvent event) -> { btn.setStyle(null); });
        btn.setOnMouseExited((MouseEvent event) -> { btn.setStyle(bn1); });
        btn.setOnMouseClicked((MouseEvent event) ->
        {
            if (recentLabel.getText().isEmpty()) { recentLabel.setText("Recent Files"); }
            recentFiles.add(fp + imgFile); updateRecent();

            try
            {
                BufferedImage bufferedImage = ImageIO.read(new File(fp + imgFile));
                Util.setImg(bufferedImage);
                Util.setFilePath(new File(fp));
                bufferedImage.flush();
                VisualizerController frame = new VisualizerController();
                frame.setTitle("Color Visualizer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setIconImage(new ImageIcon(getClass().getResource("/Controller/images/logo.png")).getImage());
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowOpened(java.awt.event.WindowEvent e) {
                        Platform.runLater(() ->
                        { (((Node) event.getSource()).getScene().getWindow()).hide(); });
                    }
                });
                frame.setVisible(true);
                frame.pack();
                frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            }
            catch (IOException | HeadlessException e)
            { Logger.getLogger(SampleImageController.class.getName()).log(Level.SEVERE, null, e); }
        });
    }

    void updateRecent()
    {
        Iterator<String> rcIterator = recentFiles.iterator();

        tileRecent.getChildren().clear();
        while (rcIterator.hasNext())
        {
            try
            {
                File file = new File(rcIterator.next());
                try { tileRecent.getChildren().add(new ImageView(new Image(new FileInputStream(file),
                        250, 225, false, false))); }
                catch (FileNotFoundException e)
                { e.printStackTrace(); }
            }
            catch (NoSuchElementException e)
            { e.printStackTrace(); }
        }
    }

    private void welcomeExtractFile()
    {
        if (!new File(imgDirInt).exists())
        {
            try
            {
                URL input = getClass().getResource("lib.zip");
                File dest = new File("data/lib.zip");
                FileUtils.copyURLToFile(input, dest);
                UnzipUtility unzipFile = new UnzipUtility();
                unzipFile.unzip(dest.getPath(), dest.getParentFile().getPath());
            }
            catch (IOException ex)
            { Logger.getLogger(SampleImageController.class.getName()).log(Level.SEVERE, null, ex); }
        }
    }

    private void getDirectory()
    {
        try
        {
            String OS = (System.getProperty("os.name")).toUpperCase();
            String workingDirectory;
            if (OS.contains("WIN")) {
                workingDirectory = System.getenv("AppData");
            } else {
                workingDirectory = System.getProperty("user.home");
                workingDirectory += "/Library/Application Support";
            }
            workingDirectory += "/jasminepaint";
            String interiorFile = workingDirectory + "/Interior";
            String exteriorFile = workingDirectory + "/Exterior";
            File homeDirectory = new File(workingDirectory);

            if (!homeDirectory.exists()) {
                homeDirectory.mkdir();
            }
            Util.setSaveInterior(interiorFile);
            Util.setSaveExterior(exteriorFile);

            File interior = new File(interiorFile);
            File exterior = new File(exteriorFile);
            if (!interior.exists()) {
                interior.mkdir();
            }
            if (!exterior.exists()) {
                exterior.mkdir();
            }
            //System.out.println(interiorFile + " " + exteriorFile);
        }
        catch (Exception ex)
        { Logger.getLogger(VisualizerController.class.getName()).log(Level.SEVERE, null, ex); }
    }

    private void checkForUpdate() {
        String configName = "config.properties";
        String path = "data";
        File config = new File(path);
        if (!config.exists()) {
            createNewData();
        }
        File[] listFiles = config.listFiles();
        for (File d1 : listFiles != null ? listFiles : new File[0]) {
            if (configName.equals(d1.getName())) {
                try {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(new File("data/" + configName)));
                    String oldVersion = prop.getProperty("version");
                    prop.load(getClass().getResourceAsStream(configName));
                    String newVersion = prop.getProperty("version");
                    System.out.println(newVersion);
                    if (newVersion.equals(oldVersion)) {
                    } else {
                        deleteOldData();
                        createNewData();
                        System.out.println("Need to Delete File");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(WelcomeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Config not exist");
                deleteOldData();
                createNewData();
            }
        }
    }

    private void deleteOldData() {
        File old = new File("data");
        Util.deleteFolder(old);
    }

    private void createNewData() {
        try {
            URL input = getClass().getResource("config.properties");
            File dest = new File("data/config.properties");
            FileUtils.copyURLToFile(input, dest);

        } catch (IOException ex) {
            Logger.getLogger(WelcomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int size) {
        return Scalr.resize(originalImage, size);
    }
}