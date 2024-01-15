package Pi;
import java.util.concurrent.CountDownLatch;

public class Container {

    private double pi;
    private CountDownLatch event;

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }

    public CountDownLatch getEvent() {
        return event;
    }

    public void setEvent(CountDownLatch event) {
        this.event = event;
    }
}