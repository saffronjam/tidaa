# org.ekenlow.Labb1

## Händelseföljd
- Tokenizer skapar en indexfil med alla ord (väldigt stor fil)
- Latmanshashar ett ord som pekar på vart i indexfilen vi hittar ord 
  som börjar på "abc"
- Vi stegar sedan igenom indexfilen för att hitta det rätta ordet, 
  kollar alla siffror tills vi hittar ett ord som int är det som vi söker.
- Pekar latmanshasningen på -1 finns inte ordet. 

## Testningar
- "zzzzzzzzzzzzz" för att kolla det sista ordet i vår första rawindex på windows.
 "ööys" för det sista ordet i rawindex.txt på UNIX.
- "mult" för det sista ordet i Korpus.
- "i" för ett enkelbokstav ord.
- "finns" för att det finns många ord innan som börjar på "fin" och det var då ett bra test för att kolla hastigheten.
- "var" för ett ord som upprepats väldigt många gånger samt skulle väldigt lätt kunna fortsätta in i ett annat ord.
