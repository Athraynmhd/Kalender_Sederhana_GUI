import javafx.scene.control.ListView;

public class ActivityListView extends ListView<Activity> implements CalendarComponent {

    @Override
    public void updateComponent() {
        this.refresh();
    }
}
