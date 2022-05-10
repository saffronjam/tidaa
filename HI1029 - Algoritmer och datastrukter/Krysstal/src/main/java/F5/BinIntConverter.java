package F5;

public class BinIntConverter {
    public static void main(String[] args) {
        int convertedToInteger = toInteger("1000000001");
        System.out.println(convertedToInteger);
        String convertedToBinary = toBinary(513);
        System.out.println(convertedToBinary);

        System.out.println("1000001".equals(toBinary(toInteger("1000001"))));
    }

    public static int toInteger(String binary) {
        return toIntegerRecursive(binary, 0);
    }

    public static String toBinary(int integer) {
        int highestPower = 0;
        while (Math.pow(2, highestPower + 1) < integer) {
            highestPower++;
        }
        return toBinaryRecursive(integer, highestPower);
    }

    private static int toIntegerRecursive(String binary, int index) {
        if (index >= binary.length()) {
            return 0;
        }
        int add = binary.charAt(index) == '0' ? 0 : (int) Math.pow(2, binary.length() - index - 1);
        return toIntegerRecursive(binary, index + 1) + add;
    }

    private static String toBinaryRecursive(int total, int power) {
        if (power == -1) {
            return "";
        }
        int attempt = (int) Math.pow(2, power);
        if (total - attempt < 0) {
            return "0" + toBinaryRecursive(total, power - 1);
        } else {
            return "1" + toBinaryRecursive(total - attempt, power - 1);
        }
    }
}
