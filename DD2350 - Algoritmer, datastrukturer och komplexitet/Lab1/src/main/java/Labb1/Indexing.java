package Labb1;

import java.io.*;
import java.util.Arrays;

public class Indexing {

    public static void main(String... args) {

        long totalTime = System.currentTimeMillis();
        var preHashesWordInfo = new int[27_930];
        Arrays.fill(preHashesWordInfo, -1);
        long setputDuration = System.currentTimeMillis() - totalTime;

        System.out.println("Took " + setputDuration + "ms to setup");

        long indexTime = System.currentTimeMillis();
        String lastWord = "";
        File file = new File(Settings.IndexPath);
        try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO_8859_1"));
             var brWordCount = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Settings.CounterPath), "ISO_8859_1"))) {

            int wordCount = 0;
            int wordIndexOffset = 0;
            int wordCountOffset = 0;
            int uniqueWordOffset = 0;

            for (String line = br.readLine(); line != null; line = br.readLine()) {

                var lineSplits = line.split(" ");
                var word = lineSplits[0];

                if (lastWord.isEmpty() || word.equals(lastWord)) {
                    wordCount++;
                } else {
                    var hash = Utils.lazyManHash(Utils.fillWithSpaces(lastWord));
                    if (preHashesWordInfo[hash] == -1) {
                        preHashesWordInfo[hash] = wordCountOffset;
                    }

                    brWordCount.write(String.format("%s %d %d%s", lastWord, wordCount, uniqueWordOffset, System.lineSeparator()));
                    wordCountOffset += lastWord.length() + 1 + String.valueOf(wordCount).length() + 1 + String.valueOf(uniqueWordOffset).length() + System.lineSeparator().length();

                    uniqueWordOffset = wordIndexOffset;
                    wordCount = 1;
                }

                lastWord = word;
                wordIndexOffset += word.length() + 1 + lineSplits[1].length() + System.lineSeparator().length();
            }

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
        } catch (IOException e) {
            System.out.println("Error printing to file.");
        }
        long indexTimeDuration = System.currentTimeMillis() - indexTime;
        System.out.println("Took " + indexTimeDuration + "ms to read indices");

        long writeTime = System.currentTimeMillis();
        try (DataOutputStream outputFileCounter = new DataOutputStream(new FileOutputStream(Settings.PrehashWordData))) {
            // Write preHashesRawIndex to file
            for (int preHash : preHashesWordInfo) {
                outputFileCounter.writeInt(preHash);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long writeDuration = System.currentTimeMillis() - writeTime;
        System.out.println("Took " + writeDuration + "ms to write prehash");

        long totalDuration = System.currentTimeMillis() - totalTime;

        System.out.println("Total " + totalDuration + "ms");
    }
}