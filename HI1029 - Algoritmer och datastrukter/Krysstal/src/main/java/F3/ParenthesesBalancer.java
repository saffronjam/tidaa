package F3;

import java.util.Stack;

public class ParenthesesBalancer {

    private final String expression;
    private Stack<Character> stack;
    private final String OPENING = "{[(";
    private final String CLOSING = ")]}";

    public ParenthesesBalancer(String expression) {
        this.expression = expression;
        stack = new Stack<>();
    }

    public boolean check() {
        boolean passed = true;
        for (int i = 0; i < expression.length(); i++) {
            var character = expression.charAt(i);
            if (isOpening(character)) {
                stack.push(character);
            } else if (isClosing(character)) {
                if (stack.empty()) {
                    passed = false;
                    break;
                }
                var top = stack.peek();
                if (!isPair(top, character)) {
                    passed = false;
                    break;
                }
                stack.pop();
            }
        }
        if (!stack.empty()) {
            passed = false;
        }
        stack.clear();
        return passed;
    }

    private boolean isOpening(char character) {
        return OPENING.indexOf(character) != -1;
    }

    private boolean isClosing(char character) {
        return CLOSING.indexOf(character) != -1;
    }

    private boolean isPair(char first, char second) {
        return first == '{' && second == '}' ||
                first == '[' && second == ']' ||
                first == '(' && second == ')';
    }

}
