package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Controller {
    @FXML private Button _practice;
    @FXML private Button _testMic;
    @FXML private CheckBox _shuffle;
    @FXML private TextArea _nameDetails;
    @FXML private ListView _playlist;
    @FXML private ListView _names;


    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        // Initializing the list of names
        ListView<Name> listView = new ListView<>();
        File folder = new File("names");
        File[] listOfFiles = folder.listFiles();
        ObservableList<Name> names = FXCollections.observableArrayList();
        for (File file : listOfFiles) {
            names.add(new Name(file.getName()));
        }
        _names.setItems(names);

        // Allows the ListView to show Strings of the names, while each index refers
        // to a Name object
        _names.setCellFactory(new Callback<ListView<Name>, ListCell<Name>>(){

            @Override
            public ListCell<Name> call(ListView<Name> p) {

                ListCell<Name> cell = new ListCell<Name>(){

                    @Override
                    protected void updateItem(Name t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.toString());
                        }
                    }

                };

                return cell;
            }
        });

    }

    @FXML
    private void nameSelected() {
        _nameDetails.setText(_names.getSelectionModel().getSelectedItem().toString());
    }
}
