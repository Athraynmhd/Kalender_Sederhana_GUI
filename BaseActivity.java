import java.time.LocalDate;

public abstract class BaseActivity {
    private LocalDate date;
    private String name;
    private boolean completed;

    public BaseActivity(LocalDate date, String name) {
        this.date = date;
        this.name = name;
        this.completed = false;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public abstract void complete();
}
