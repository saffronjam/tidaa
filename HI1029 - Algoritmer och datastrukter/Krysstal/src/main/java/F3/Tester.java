package F3;

import java.util.EmptyStackException;

public class Tester {
    public static void main(String[] args) {
        /*
        NB4
         */
        System.out.println("\nPARAENTHESES BALANCER");
        ParenthesesBalancer trueBalancer = new ParenthesesBalancer("({{}})");
        System.out.println("Should be true: " + trueBalancer.check());

        ParenthesesBalancer falseBalancer = new ParenthesesBalancer("({{} ] })()");
        System.out.println("Should be false: " + falseBalancer.check());

        /*
        NB5
         */
        System.out.println("\nARRAYLIST STACK");
        var myArrayListStack = new ArrayListStack<Integer>();
        testStack(myArrayListStack);


        /*
        NB7
         */
        System.out.println("\nLINKED STACK");
        var myLinkedStack = new LinkedStack<Integer>();
        testStack(myLinkedStack);
        for (int i = 0; i < 10; i++) {
            myLinkedStack.push(i + 5);
        }
        System.out.println("New pushed elements: " + myLinkedStack);
        System.out.println("Data at peek(3): " + myLinkedStack.peek(3));
        System.out.println("Size: " + myLinkedStack.size());
        System.out.println("Flushed returned: " + myLinkedStack.flush());
    }

    private static void testStack(StackInt<Integer> stack) {
        for (int i = 0; i < 20; i++) {
            stack.push(i * 3);
        }
        System.out.println("After pushing: " + stack);

        for (int i = 0; i < 4; i++) {
            stack.pop();
        }
        System.out.println("After popping: " + stack);
        try {
            for (int i = 0; i < 1000; i++) {
                stack.pop();
            }
        } catch (EmptyStackException e) {
            System.out.println("EmptyStackException exeception caught when popping 1000 elements!");
        }
    }
}
