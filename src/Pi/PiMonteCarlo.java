package Pi;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PiMonteCarlo {

    private static final int Size = 10000000;
    private static final int ThreadNumber = 48;

    static boolean isInside(double x, double y) {
        double l = Math.sqrt(x * x + y * y);
        return l <= 1;
    }

    static double calcPi(List<Point> points) {
        double pi = 0;
        int inside = 0;

        for (Point p : points) {
            if (isInside(p.X(), p.Y())) {
                inside++;
            }
        }

        pi = inside / (points.size() * 1.0) * 4.0;
        return pi;
    }

    static Point generatePoint() {
        return new Point(Math.random(), Math.random());
    }

    static class Container {
        double pi;
        CountDownLatch event;

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

    static public void run(Object obj) {
        Container container = (Container) obj;
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < Size / ThreadNumber; i++) {
            Point p = generatePoint();
            points.add(p);
        }

        double pi = calcPi(points);
        container.setPi(pi);
        container.getEvent().countDown();
    }

    public static void main(String[] args) {
        // One thread
        Instant t1 = Instant.now();
        List<Point> pointsSingleThread = new ArrayList<>();
        for (int i = 0; i < Size; i++) {
            Point p = generatePoint();
            pointsSingleThread.add(p);
        }

        double piSingleThread = calcPi(pointsSingleThread);
        Instant t2 = Instant.now();
        System.out.println(piSingleThread + " for " + Duration.between(t1, t2).toMillis() + " milliseconds");

        // Multi-threading
        Instant t3 = Instant.now();
        List<Container> containers = new ArrayList<>();
        CountDownLatch ev = new CountDownLatch(ThreadNumber);
        for (int i = 0; i < ThreadNumber; i++) {
            Container container = new Container();
            container.setEvent(ev);
            containers.add(container);
            new Thread(() -> run(container)).start();
        }

        try {
            ev.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double pi = 0;
        for (Container c : containers) {
            pi += c.getPi();
        }

        pi /= ThreadNumber;
        Instant t4 = Instant.now();

        System.out.println(pi + " for " + Duration.between(t3, t4).toMillis() + " milliseconds");
    }
}
