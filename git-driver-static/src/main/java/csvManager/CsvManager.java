package csvManager;

import gitManager.CollectedMergeMethodData;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class CsvManager {

    private static final String CSV_FILE_NAME = "results.csv";

    public CsvManager(){}

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(List<String[]> dataLines) throws IOException {
        File csvOutputFile = new File(CSV_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        assertTrue(csvOutputFile.exists());
    }

    public void transformCollectedDataIntoCsv(List<CollectedMergeMethodData> collectedMergeMethodDataList) throws IOException {
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[] {"project", "merge commit", "className", "method", "left modifications", "left deletions", "right modifications", "right deletions"});

        for(CollectedMergeMethodData c : collectedMergeMethodDataList){
            dataLines.add(new String[] {c.getProject().getName(), c.getMergeCommit().getSHA(), c.getClassName(), c.getMethodSignature(), c.getLeftAddedLines().toString(),
                    c.getLeftDeletedLines().toString(), c.getRightAddedLines().toString(), c.getRightDeletedLines().toString()});
        }
        this.givenDataArray_whenConvertToCSV_thenOutputCreated(dataLines);
    }
}
