package F4;

public class TesterArrayQueue8_1 {
    public static void main(String[] args) {
        var queue = new ArrayQueue8_1<String>(10);

        for (int i = 0; i < 10; i++) {
            queue.offer("e" + (i + 1));
        }
        /*
        Fungerar inte
        Ger mycket konstiga resultat, men det går att hitta ett mönster,
        och det ser ut ha att göra med modulus problem.

        size=1, maxSize=5, data=[e1, null, null, null, null]
        size=2, maxSize=5, data=[e1, e2, null, null, null]
        size=3, maxSize=5, data=[e1, e2, e3, null, null]
        size=1, maxSize=5, data=[null, null, null, e1, null]
        size=2, maxSize=5, data=[null, null, null, e1, e2]
         */


        queue = new ArrayQueue8_1<String>(5);

        for (int i = 0; i < 3; i++) {
            queue.offer("e" + (i + 1));
            System.out.println(queue);
        }

        // Töm
        while (queue.poll() != null) {
        }

        // Lägger till 5 element (borde fungera utan resize)
        for (int i = 0; i < 5; i++) {
            queue.offer("e" + (i + 1));
            System.out.println(queue);
        }

        // OK!

        // Lägger till 5 element för att se resize
        for (int i = 0; i < 5; i++) {
            queue.offer("e" + (i + 1));
            System.out.println(queue);
        }

        /*
        Resize fungerar inte
        size=4, maxSize=5, data=[e3, e4, null, e1, e2]
        size=5, maxSize=5, data=[e3, e4, e5, e1, e2]
        size=6, maxSize=10, data=[e3, e4, e5, e1, e2, null, null, null, null, null]
        size=7, maxSize=10, data=[e3, e4, e5, e1, e2, null, null, null, null, null]
        size=8, maxSize=10, data=[e3, e4, e5, e1, e2, e3, null, null, null, null]
        Här är reallocate fel, den borde utgå från front och modulus från ny maxSize vid kopiering
         */

        // Efter en bra reallocate:
        System.out.println(queue);

        /*
        Problem!
        offer() verkar lägga till innan read incrementas
         */

        // Testar ta bort de fem första elementen
        for (int i = 0; i < 5; i++) {
            queue.poll();
            System.out.println(queue);
        }
        // De gammla elementen sätts inte till null, kan orsaka större minneskonsumption då större datatyper hålls vid lid

        // Testar sätta in 5 tal till för att se att allt fungerar
        for (int i = 0; i < 5; i++) {
            queue.offer("e" + (i + 6));
        }
        System.out.println(queue);
        // OK!

        // Fix 1
        // Fixa modulus (parantes saknades)

        // Fix 2
        // Implementera en fungerande reallocate()

        // Fix 3
        // Rensa oanvända element i listan, sätt till null

    }

}
