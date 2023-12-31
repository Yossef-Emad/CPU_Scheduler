package Os_project;

import Scheduling_Algorithms.Process;
import Scheduling_Algorithms.Scheduler;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

public class FCFS_SJF_Controller implements Initializable {

    private int ProcessCount;

    private String algoFlag;

    private Stage primaryStage;

    private FXMLLoader loader;

    private Parent root;

    @FXML // fx:id="btn1"
    private Button btn1; // Value injected by FXMLLoader

    @FXML // fx:id="btn2"
    private Button btn2; // Value injected by FXMLLoader

    @FXML // fx:id="btn3"
    private Button btn3; // Value injected by FXMLLoader

    @FXML // fx:id="imageView"
    private ImageView imageView; // Value injected by FXMLLoader

    @FXML
    private TreeTableColumn<RowRecord,Number> AT;    // Arrival Time Column

    @FXML
    private TreeTableColumn<RowRecord,Number> BT;     // Burst Time Column

    @FXML
    private TreeTableColumn<RowRecord,String> PN;     // Process Name Column

    @FXML
    private TreeTableView<RowRecord> TableView;

    private TreeItem<RowRecord>[] Row = null;

    private TreeItem<RowRecord> tableRoot = new TreeItem<>(new RowRecord("Process[i]", 0, 0));

    public FCFS_SJF_Controller(int ProcessCount, String algoFlag)
    {
        this.ProcessCount = ProcessCount;
        this.algoFlag = algoFlag;
        Row = new TreeItem[ProcessCount];
    }

    public void initialize(URL location, ResourceBundle resources)
    {
        Image image = new Image("E:\\Coding\\OS_Project\\Assets\\icon.png");
        //Image image = new Image("file:Assets/icon.png");
        imageView.setImage(image);

        createEditableTable();
    }

    void createEditableTable()
    {
        for(int i = 0; i < ProcessCount; i++)
        {
            Row[i] = new TreeItem<>(new RowRecord("P" + (i + 1), 0, 0));
        }

        tableRoot.getChildren().setAll(Row);

        PN.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<RowRecord,String> param) -> param.getValue().getValue().getProcessNameProperty()
        );

        AT.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<RowRecord, Number>, ObservableValue<Number>>() {
                                   @Override
                                   public ObservableValue<Number> call(TreeTableColumn.CellDataFeatures<RowRecord, Number> param) {
                                       return param.getValue().getValue().getArrivalTimeProperty();
                                   }
                               }
        );

        AT.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));

        AT.setOnEditCommit(event -> {
            TreeTablePosition<RowRecord, Number> pos = event.getTreeTablePosition();
            Number newAT = event.getNewValue();
            int row = pos.getRow();
            RowRecord rowRecord = pos.getTreeItem().getValue();
            rowRecord.setArrivalTimeProperty(newAT.intValue());
        });

        BT.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<RowRecord, Number>, ObservableValue<Number>>() {
                                   @Override
                                   public ObservableValue<Number> call(TreeTableColumn.CellDataFeatures<RowRecord, Number> param) {
                                       return param.getValue().getValue().getBurstTimeProperty();
                                   }
                               }
        );

        BT.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));

        BT.setOnEditCommit(event -> {
            TreeTablePosition<RowRecord, Number> pos = event.getTreeTablePosition();
            Number newAT = event.getNewValue();
            int row = pos.getRow();
            RowRecord rowRecord = pos.getTreeItem().getValue();
            rowRecord.setBurstTimeProperty(newAT.intValue());
        });

        TableView.setEditable(true);
        TableView.setRoot(tableRoot);
        TableView.setShowRoot(false);
    }


    @FXML
    void OnLiveSchedulingButton(ActionEvent event) throws IOException {
        Queue<Process> readyQueue =  new LinkedList<>();
        for(int i = 0; i < ProcessCount; i++)
        {
            Process p = new Process(Row[i].getValue().getProcessName(),Row[i].getValue().getBurstTime(),Row[i].getValue().getArrivalTime());
            Row[i].getValue().setRemainingTimeProperty(Row[i].getValue().getBurstTime());
            readyQueue.add(p);
        }

        Scheduler sch = new Scheduler(algoFlag,readyQueue);
        loader = new FXMLLoader(getClass().getResource("FCFS_SJF_RR_Live.fxml"));
        loader.setControllerFactory(param -> new FCFS_SJF_RR_Live_Controller(Row,sch));
        root = loader.load();
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setTitle("CPU Scheduler");
        primaryStage.resizableProperty();
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.show();

    }

    @FXML
    void onBackbutton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Selection.fxml"));
        root = loader.load();
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setTitle("CPU Scheduler");
        primaryStage.resizableProperty();
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @FXML
    void onNonLiveSchedulingbutton(ActionEvent event) throws IOException
    {
        Queue<Process> readyQueue =  new LinkedList<>();
        for(int i = 0; i < ProcessCount; i++)
        {
            Process p = new Process(Row[i].getValue().getProcessName(),Row[i].getValue().getBurstTime(),Row[i].getValue().getArrivalTime());
            readyQueue.add(p);
        }

        Scheduler sch = new Scheduler(algoFlag,readyQueue);

        loader = new FXMLLoader(getClass().getResource("FCFS_SJF_RR_NonLive.fxml"));
        loader.setControllerFactory(param -> new FCFS_SJF_RR_NonLive_Controller(Row,sch));
        root = loader.load();
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setTitle("CPU Scheduler");
        primaryStage.resizableProperty();
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

}