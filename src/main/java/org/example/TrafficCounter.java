package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TrafficCounter {

    private final List<TrafficRecord> records = new ArrayList<>();

    public void readFile(String filename) throws IOException {
        records.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    parseAndAddRecord(line);
                }
            }
        }

        records.sort(Comparator.comparing(TrafficRecord::getTimestamp));
    }

    private void parseAndAddRecord(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 2) {
            LocalDateTime timestamp = LocalDateTime.parse(parts[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            int count = Integer.parseInt(parts[1]);
            records.add(new TrafficRecord(timestamp, count));
        }
    }

    public int getTotalCars() {
        return records.stream().mapToInt(TrafficRecord::getCount).sum();
    }

    public Map<String, Integer> getCarsPerDay() {
        Map<String, Integer> carsPerDay = new LinkedHashMap<>();

        for (TrafficRecord record : records) {
            String date = record.getDate();
            int count = record.getCount();

            carsPerDay.merge(date, count, Integer::sum);
        }

        return carsPerDay;
    }

    public List<TrafficRecord> getTopHalfHours(int n) {
        //Sort all records by count (highest to smallest)
        List<TrafficRecord> sortedRecords = new ArrayList<>(records);
        sortedRecords.sort((a, b) -> b.getCount() - a.getCount());

        //Return the top 'n' records
        List<TrafficRecord> topRecords = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sortedRecords.size()); i++) {
            topRecords.add(sortedRecords.get(i));
        }

        return topRecords;
    }

    public List<TrafficRecord> getLeastTraffic1Hour5Period() {
        if (records.size() < 3) {
            return new ArrayList<>(records);
        }

        int minSum = Integer.MAX_VALUE;
        int minIndex = 0;

        for (int i = 0; i <= records.size() - 3; i++) {
            if (isContiguousRecords(i)) {
                int sum = records.get(i).getCount() + records.get(i + 1).getCount() +   records.get(i + 2).getCount();

                //update if this period has fewer cars than our current minimum
                if (sum < minSum) {
                    minSum = sum;
                    minIndex = i;
                }
            }
        }

        // Return the 3 records with minimum sum
        return records.subList(minIndex, minIndex + 3);
    }

    private boolean isContiguousRecords(int startIndex) {
        LocalDateTime time1 = records.get(startIndex).getTimestamp();
        LocalDateTime time2 = records.get(startIndex + 1).getTimestamp();
        LocalDateTime time3 = records.get(startIndex + 2).getTimestamp();

        return time2.equals(time1.plusMinutes(30)) &&
                time3.equals(time2.plusMinutes(30));
    }

    public void returnProcessedData(String filename) throws IOException {
        readFile(filename);

        System.out.println("Total cars: " + getTotalCars());
        System.out.println();

        System.out.println("Cars per day:");
        getCarsPerDay().forEach((date, count) ->
                System.out.println(date + " " + count)
        );
        System.out.println();

        System.out.println("Top 3 half hours:");
        getTopHalfHours(3).forEach(System.out::println);
        System.out.println();

        System.out.println("1.5 hour period with least cars:");
        getLeastTraffic1Hour5Period().forEach(System.out::println);
    }

    public static void main(String[] args) {
        TrafficCounter counter = new TrafficCounter();
        try {
            counter.returnProcessedData(args[0]);
        } catch (IOException e) {
            System.err.println("Something went wrong");
            System.exit(1);
        }
    }
}