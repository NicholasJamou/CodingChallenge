import org.example.TrafficCounter;
import org.example.TrafficRecord;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TrafficCounterTest {
    private TrafficCounter counter;

    @BeforeEach
    void setUp() {
        counter = new TrafficCounter();
    }

    @Test
    void readFile_andGetTotalCards(@TempDir Path tempDir) throws IOException {
        Path testFile = happyPathTestFile(tempDir);
        counter.readFile(testFile.toString());

        int total = counter.getTotalCars();
        assertEquals(398, total);
    }

    @Test
    void readFile_andGetCarsPerDay(@TempDir Path tempDir) throws IOException {
        Path testFile = happyPathTestFile(tempDir);
        counter.readFile(testFile.toString());

        Map<String, Integer> carsPerDay = counter.getCarsPerDay();

        assertEquals(4, carsPerDay.size());
        assertEquals(179, carsPerDay.get("2021-12-01"));
        assertEquals(81, carsPerDay.get("2021-12-05"));
        assertEquals(134, carsPerDay.get("2021-12-08"));
        assertEquals(4, carsPerDay.get("2021-12-09"));
    }

    @Test
    void readFile_andGetTopHalfHours(@TempDir Path tempDir) throws IOException {
        Path testFile = happyPathTestFile(tempDir);
        counter.readFile(testFile.toString());

        List<TrafficRecord> top = counter.getTopHalfHours(3);

        assertEquals(3, top.size());
        assertEquals(46, top.get(0).getCount());
        assertEquals(42, top.get(1).getCount());
        assertEquals(33, top.get(2).getCount());
    }

    @Test
    void readFile_andGetLeast1Hour5Period(@TempDir Path tempDir) throws IOException {
        Path testFile = happyPathTestFile(tempDir);
        counter.readFile(testFile.toString());

        List<TrafficRecord> leastPeriod = counter.getLeastTraffic1Hour5Period();

        assertEquals(3, leastPeriod.size());
        assertEquals(5, leastPeriod.get(0).getCount());
        assertEquals(12, leastPeriod.get(1).getCount());
        assertEquals(14, leastPeriod.get(2).getCount());
    }

    @Test
    void readFile_withBlankLines_andGetCounts(@TempDir Path tempDir) throws IOException {
        Path testFile = carCountWithBlankLineFile(tempDir);
        counter.readFile(testFile.toString());

        assertEquals(31, counter.getTotalCars());

        //test total top half hours
        List<TrafficRecord> top = counter.getTopHalfHours(3);
        assertEquals(3, top.size());
        assertEquals(14, top.get(0).getCount());
        assertEquals(12, top.get(1).getCount());
        assertEquals(5, top.get(2).getCount());

        //least 1.5 hour period
        List<TrafficRecord> leastPeriod = counter.getLeastTraffic1Hour5Period();
        assertEquals(3, leastPeriod.size());
        assertEquals(5, leastPeriod.get(0).getCount());
        assertEquals(12, leastPeriod.get(1).getCount());
        assertEquals(14, leastPeriod.get(2).getCount());
    }

    @Test
    void readFile_withZeroCountCars_andGetCounts(@TempDir Path tempDir) throws IOException {
        Path testFile = zeroCarCountTestFile(tempDir);
        counter.readFile(testFile.toString());

        assertEquals(0, counter.getTotalCars());
        assertEquals(0, counter.getCarsPerDay().get("2021-12-01"));
        assertEquals(0, counter.getTopHalfHours(1).get(0).getCount());
    }

    @Test
    void readFile_withTimeThatCrossesMidnight_andGetCounts(@TempDir Path tempDir) throws IOException {
        Path testFile = carCountWithMidnightCrossing(tempDir);
        counter.readFile(testFile.toString());

        //car per day across midnight
        Map<String, Integer> carsPerDay = counter.getCarsPerDay();
        assertEquals(25, carsPerDay.get("2021-12-01"));
        assertEquals(45, carsPerDay.get("2021-12-02"));

        //least 1.5 hour period
        List<TrafficRecord> leastPeriod = counter.getLeastTraffic1Hour5Period();
        assertEquals(3, leastPeriod.size());
        assertEquals(10, leastPeriod.get(0).getCount());
        assertEquals(15, leastPeriod.get(1).getCount());
        assertEquals(20, leastPeriod.get(2).getCount());
    }

    private Path happyPathTestFile(Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("sample_traffic.txt");
        List<String> lines = Arrays.asList(
                "2021-12-01T05:00:00 5",  // Contiguous Period 1: 05:00-06:30 (5+12+14=31)
                "2021-12-01T05:30:00 12", // Contiguous Period 2: 05:30-07:00 (12+14+15=41)
                "2021-12-01T06:00:00 14", // Contiguous Period 3: 06:00-07:30 (14+15+25=54)
                "2021-12-01T06:30:00 15", // Contiguous Period 4: 06:30-08:00 (15+25+46=86)
                "2021-12-01T07:00:00 25",
                "2021-12-01T07:30:00 46",
                "2021-12-01T08:00:00 42",
                "2021-12-01T15:00:00 9",
                "2021-12-01T15:30:00 11",
                "2021-12-01T23:30:00 0",
                "2021-12-05T09:30:00 18",
                "2021-12-05T10:30:00 15",
                "2021-12-05T11:30:00 7",
                "2021-12-05T12:30:00 6",
                "2021-12-05T13:30:00 9",
                "2021-12-05T14:30:00 11",
                "2021-12-05T15:30:00 15",
                "2021-12-08T18:00:00 33",
                "2021-12-08T19:00:00 28",
                "2021-12-08T20:00:00 25",
                "2021-12-08T21:00:00 21",
                "2021-12-08T22:00:00 16",
                "2021-12-08T23:00:00 11",
                "2021-12-09T00:00:00 4"
        );
        Files.write(testFile, lines);
        return testFile;
    }

    private Path carCountWithBlankLineFile(Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("sample_traffic.txt");
        List<String> lines = Arrays.asList(
                "2021-12-01T05:00:00 5",
                "\n",
                "2021-12-01T05:30:00 12",
                "\n",
                "2021-12-01T06:00:00 14"
        );
        Files.write(testFile, lines);
        return testFile;
    }

    private Path zeroCarCountTestFile(Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("sample_traffic.txt");
        List<String> lines = Arrays.asList(
                "2021-12-01T05:00:00 0",
                "2021-12-01T05:30:00 0",
                "2021-12-01T06:00:00 0"
        );
        Files.write(testFile, lines);
        return testFile;
    }

    private Path carCountWithMidnightCrossing(Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("sample_traffic.txt");
        List<String> lines = Arrays.asList(
                "2021-12-01T23:00:00 10",
                "2021-12-01T23:30:00 15",
                "2021-12-02T00:00:00 20",
                "2021-12-02T00:30:00 25"
        );
        Files.write(testFile, lines);
        return testFile;
    }
}