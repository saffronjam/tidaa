package Labb1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Kordans {

    private static class WordData {
        int count, offset;

        WordData(int count, int offset) {
            this.count = count;
            this.offset = offset;
        }
    }

    public static int bucketSize(String filepath, int bucketIndex) {
        var file = new File(filepath);

        try (var dataInputStream = new DataInputStream(new FileInputStream(file))) {
            dataInputStream.skipBytes((bucketIndex + 1) * 4);

            int rawIndex;
            do {
                rawIndex = dataInputStream.readInt();
            } while (rawIndex == -1);

            return rawIndex - bucketIndex;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int readPrehash(String filepath, int lazyHash) {
        var file = new File(filepath);
        int result = -1;

        try (var dataInputStream = new DataInputStream(new FileInputStream(file))) {
            dataInputStream.skipBytes(lazyHash * 4);
            result = dataInputStream.readInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getBinInt(DataInputStream in) throws IOException {
        int tkn = 0;
        try {
            tkn = in.readInt();
        } catch (IOException e) {
            System.err.println("Fel i GetChar");
        }
        return tkn;
    }

    public static String[] lookupKorpus(int[] indices, String searchWord) {
        var result = new String[25];
        try (var ranAccessPtr = new RandomAccessFile(Settings.KorpusPath, "r")) {

            int readPadding = 30;
            var charBuffer = new byte[readPadding + searchWord.length() + readPadding];

            for (int i = 0; i < 25; i++) {
                var index = indices[i];
                if (index == Integer.MAX_VALUE) {
                    break;
                } else {
                    var nPrepadSpaces = Math.abs(Math.min(0, index - readPadding));
                    var prePadding = " ".repeat(nPrepadSpaces);

                    ranAccessPtr.seek(Math.max(0, index - readPadding));
                    int x = ranAccessPtr.read(charBuffer, 0, charBuffer.length);

                    var resultString = prePadding + new String(charBuffer, StandardCharsets.ISO_8859_1);
                    resultString = resultString.replace('\n', ' ');
                    resultString = resultString.replace('\r', ' ');
                    result[i] = resultString;
                    Arrays.fill(charBuffer, (byte) ' ');
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
        } catch (IOException e) {
            System.out.println("Error printing to file.");
        }
        return result;
    }

    /**
     * @param offset
     * @param indices
     * @param searchWord, pre-padded for words with length < 3
     * @return
     */
    public static int getKorpusIndices(int offset, int[] indices, String searchWord) {
        var file = new File(Settings.IndexPath);
        int resultIndicesIndex = 0;
        int addedOffset = 0;

        try (var ranAccessPtr = new RandomAccessFile(file, "r")) {
            ranAccessPtr.seek(offset);

            // Get searchWord and location
            var word = "";
            do {
                var line = ranAccessPtr.readLine();
                if (line == null) {
                    break;
                }
                addedOffset += line.length() + System.lineSeparator().length();

                String[] splitLine = line.split(" ");
                word = Utils.fillWithSpaces(splitLine[0]);
                if (searchWord.equals(word)) {
                    var korpusIndex = Integer.parseInt(splitLine[1]);
                    indices[resultIndicesIndex++] = korpusIndex;
                }
            } while (resultIndicesIndex < indices.length && searchWord.substring(0, 3).equals(word.substring(0, 3)));

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
        } catch (IOException e) {
            System.out.println("Error printing to file.");
        }


        Arrays.sort(indices);

        return addedOffset;
    }

    public static WordData getWordData(int baseOffset, String searchWord) {
        int count = 0;
        int offset = 0;
        var file = new File(Settings.CounterPath);
        var searchTrio = searchWord.substring(0, 3);
        try (var ranAccessPtr = new RandomAccessFile(file, "r")) {
            ranAccessPtr.seek(baseOffset);

            // Get searchWord and location
            var word = "";
            do {
                var line = ranAccessPtr.readLine();
                String[] splitLine = line.split(" ");
                word = Utils.fillWithSpaces(splitLine[0]);
                if (searchWord.equals(word)) {
                    count = Integer.parseInt(splitLine[1]);
                    offset = Integer.parseInt(splitLine[2]);
                    break;
                }
            } while (searchTrio.equals(word.substring(0, 3)));

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
        } catch (IOException e) {
            System.out.println("Error printing to file.");
        }
        return new WordData(count, offset);
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Invalid use of program. Enter word to search for.");
            System.exit(1);
        }


        // Setup
        var searchWord = Utils.fillWithSpaces(args[0].toLowerCase(Locale.ROOT));

        long time = System.currentTimeMillis();

        // Temporary word to search for
        searchWord = Utils.fillWithSpaces(searchWord);

        // Variables
        var resultIndices = new int[25];
        var resultRows = new String[25];

        var lazyHash = Utils.lazyManHash(searchWord);

        // Setup
        Arrays.fill(resultIndices, Integer.MAX_VALUE);

        long prehashtime = System.currentTimeMillis();
        var wordDataOffset = readPrehash(Settings.PrehashWordData, lazyHash);
        prehashtime = System.currentTimeMillis() - prehashtime;


        if (wordDataOffset == -1) {
            System.out.println("No occurances");
            System.exit(1);
        }

        long indexTime, indexDoneTime, countLookupTime, countLookupDoneTime, korpusTime, korpusDoneTime, lookupTime, lookupDoneTime;

        // Find word wordData
        countLookupTime = System.currentTimeMillis();
        var wordData = getWordData(wordDataOffset, searchWord);
        countLookupDoneTime = System.currentTimeMillis() - countLookupTime;

        var currentRawIndexOffset = wordData.offset;

        // Display info to user
        System.out.printf("Found %d occurrence for '%s'\n", wordData.count, searchWord);

        int loops = 1;
        // While more than 25 to display, prompt user
        while (true) {
            lookupTime = System.currentTimeMillis();
            // Look up Raw index
            indexTime = System.currentTimeMillis();
            var addedOffset = getKorpusIndices(currentRawIndexOffset, resultIndices, searchWord);
            currentRawIndexOffset += addedOffset;
            indexDoneTime = System.currentTimeMillis() - indexTime;

            // Get words from Kropus
            korpusTime = System.currentTimeMillis();
            resultRows = lookupKorpus(resultIndices, searchWord);
            korpusDoneTime = System.currentTimeMillis() - korpusTime;
            lookupDoneTime = System.currentTimeMillis() - lookupTime;
            for (var row : resultRows) {
                if (row != null && !row.isEmpty()) {
                    System.out.println(row);
                }
            }

            int low = 25 * (loops - 1) + 1;
            int high = Math.min(25 * loops, wordData.count);
            System.out.printf("\nDisplaying [%d - %d]\n", low, high);
            System.out.printf("%dms  (%dms / %dms)", lookupDoneTime, indexDoneTime, korpusDoneTime);

            if (25 * loops < wordData.count) {

                System.out.printf("Press enter to display more (%d left)\n", wordData.count - high);
                try {
                    System.in.read();
                } catch (Exception ignored) {
                    System.out.println("Exiting");
                    break;
                }

                Arrays.fill(resultIndices, Integer.MAX_VALUE);
                Arrays.fill(resultRows, "");
            } else {
                break;
            }
            loops++;
        }

        System.out.println("");
        System.out.println("Found " + wordData.count + " occurances");
        System.out.println("Took " + (prehashtime) + "ms to read prehashes");
        System.out.println("Took " + indexDoneTime + "ms to read indexes");
        System.out.println("Took " + countLookupDoneTime + "ms to read word wordData");
        System.out.println("Took " + (korpusDoneTime) + "ms to read korpus");
        System.out.println("Took " + (System.currentTimeMillis() - time) + "ms");

    }
}
