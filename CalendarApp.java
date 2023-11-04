import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarApp extends Application {
    private DatePicker datePicker;
    private TextField activityNameField;
    private TextField activityTimeField; // TextField for the activity time
    private Button addActivityButton;
    private Button markAsCompletedButton;
    private Button deleteActivityButton;
    private ActivityListView activityListView;
    private List<Activity> activities;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Kalender");
        activities = new ArrayList<>();

        // Initialize UI components
        datePicker = new DatePicker();
        activityNameField = new TextField();
        activityNameField.setPromptText("Nama Kegiatan");

        activityTimeField = new TextField();
        activityTimeField.setPromptText("HH:mm"); // Prompt for time input

        addActivityButton = new Button("Tambah Kegiatan");
        addActivityButton.setOnAction(event -> addActivity());

        markAsCompletedButton = new Button("Tandai Selesai");
        markAsCompletedButton.setOnAction(event -> markAsCompleted());

        deleteActivityButton = new Button("Hapus Kegiatan");
        deleteActivityButton.setOnAction(event -> deleteActivity());

        activityListView = new ActivityListView();
        activityListView.setPrefWidth(300);

        // Cell Factory for ListView
        activityListView.setCellFactory(listView -> new ListCell<Activity>() {
            @Override
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                if (empty || activity == null) {
                    setText(null);
                } else {
                    setText(activity.toString()); // Uses Activity's toString method
                }
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateActivityListForDate(newValue);
        });

        activityListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            markAsCompletedButton.setDisable(newValue == null);
            deleteActivityButton.setDisable(newValue == null);
        });

        // Set up the GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add components to the grid pane
        gridPane.add(datePicker, 0, 0);
        gridPane.add(activityNameField, 0, 1);
        gridPane.add(activityTimeField, 0, 2);
        gridPane.add(addActivityButton, 0, 3);
        gridPane.add(markAsCompletedButton, 0, 4);
        gridPane.add(deleteActivityButton, 0, 5);
        gridPane.add(activityListView, 0, 6);

        // Update DatePicker cell factory
        updateDatePickerCellFactory();

        Scene scene = new Scene(gridPane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addActivity() {
        LocalDate date = datePicker.getValue();
        String activityName = activityNameField.getText();
        String activityTimeString = activityTimeField.getText();

        if (date != null && !activityName.trim().isEmpty() && !activityTimeString.trim().isEmpty()) {
            try {
                LocalTime time = LocalTime.parse(activityTimeString, DateTimeFormatter.ofPattern("HH:mm"));
                Activity activity = new Activity(date, activityName, time);
                activities.add(activity);
                activityListView.getItems().add(activity);
                updateActivityListForDate(date);
            } catch (DateTimeParseException e) {
                // Handle wrong time format
                showAlert("Format Waktu Salah", "Gunakan format HH:mm (misal: 15:30).");
            }
        } else {
            // Handle case where date, time or name is not provided
            showAlert("Data Kegiatan Tidak Lengkap", "Mohon lengkapi tanggal, waktu, dan nama kegiatan.");
        }

        activityNameField.clear();
        activityTimeField.clear();
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void markAsCompleted() {
        Activity selectedActivity = activityListView.getSelectionModel().getSelectedItem();
        if (selectedActivity != null) {
            selectedActivity.setCompleted(true);
            activityListView.refresh();
            updateDatePickerCellFactory();
        }
    }

    private void deleteActivity() {
        Activity selectedActivity = activityListView.getSelectionModel().getSelectedItem();
        if (selectedActivity != null) {
            activities.remove(selectedActivity);
            updateActivityListForDate(datePicker.getValue());
        }
    }

    private void updateActivityListForDate(LocalDate date) {
        List<Activity> filteredActivities = activities.stream()
                .filter(activity -> activity.getDate().equals(date))
                .collect(Collectors.toList());
        activityListView.getItems().setAll(filteredActivities);
    }

    private void updateDatePickerCellFactory() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (!empty) {
                    long count = activities.stream()
                        .filter(activity -> !activity.isCompleted() && activity.getDate().equals(date))
                        .count();
                    setStyle(count > 0 ? "-fx-background-color: #ff4444;" : "");
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
