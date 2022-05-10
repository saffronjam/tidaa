#Uppgift
##Heuristik för rollbesättningsproblemet
Om du redovisar labben senast den 10 december får du en labbleveranspoäng, som kan ge högre betyg på labbmomentet. 
Till labben hör teoriuppgifter som kan redovisas för en teoripoäng till tentan, och detta görs på övningen den 1 
december (ingen annan redovisningsmöjlighet finns). Det är frivilligt att redovisa teoriuppgifterna, men för att klara 
av att göra labben bör du ha gjort dom.

Målen för labb 5 är att du ska

- Öva på att attackera ett problem som inte kan lösas optimalt <br>
- Implementera en heuristik för ett NP-svårt problem <br>

Du ska välja att implementera valfri heuristik som löser konstruktionsproblemet: Vilka skådespelare ska ha vilka roller 
för att lösa rollbesättningsinstansen med så få skådespelare som möjligt? Indataformatet för rollbesättningsproblemet 
är detsamma som i labb 4. Divorna är 1 och 2.

**Utdataformat**:<br>
Rad ett: antal skådespelare som fått roller <br>
En rad för varje skådespelare (som fått roller) med skådespelarens nummer, antalet roller skådespelaren tilldelats samt 
numren på dessa roller

**Indata**: <br>
Roller<br>
Scener<br>
Skådespelare<br>

Problemet ska lösas enligt villkoren som specificerats för rollbesättningsproblemet, dvs divorna måste vara med men får 
inte mötas, ingen roll får spelas av flera personer, och ingen skådespelare får spela mot sig själv i någon scen. 
Bättre heuristik (dvs färre skådespelare) ger bättre betyg. Endast lösbara instanser kommer att ges som indata, men 
för att heuristiken i polynomisk tid säkert ska kunna hitta en lösning så är det tillåtet att använda högst _n-1_ 
särskilda superskådisar med nummer _k+1, k+2, ..._ Varje superskådis kan spela vilken roll som helst, men kan bara spela 
en enda roll.

Några testfall att testa ditt program med finns på 
/afs/kth.se/misc/info/kurser/DD2350/adk21/labb5/testfall/

Problemet heter [kth.adk.castingheuristic](https://kth.kattis.com/problems/kth.adk.castingheuristic/) (Länkar till en externa sida.) på Kattis. Kattis summerar antalet använda 
skådespelare i testfallen och returnerar summan. För godkänt krävs ett resultat bättre än 600.

I Kattis testfall är antalet roller aldrig större än 600, antalet scener aldrig större än 4000 och antalet skådespelare 
aldrig större än 400.

Notera att det finns en betygshöjande extralabb där kraven är strängare. Ett krav är att programmet måste ge ett bättre 
resultat än programmet du redovisade i labb 5. För att få redovisa extralabben krävs betyg C på labbkursen, dvs att 
alla labbar är godkända och att minst 4 labbleveranspoäng uppnåtts. Extralabben ska göras individuellt.

**Tips**
Det kan underlätta om man använder en verifikator för den producerade lösningen, så att man upptäcker om en otillåten 
lösning produceras av heuristiken. En av kursens assistenter har skrivit en verifikator som finns kompilerad för KTH:s 
datorsystem på /afs/kth.se/misc/info/kurser/DD2350/adk21/labb5/verifyLab5

Verifikatorn förväntar sig att få först en instans av rollbesättningsproblemet och sedan en föreslagen lösning på 
instansen (som får innehålla superskådisar). Kör verifikatorn med t.ex:
- cat instance.txt cast.txt | /afs/kth.se/misc/info/kurser/DD2350/adk21/labb5/verifyLab5

där instance.txt innehåller en instans av rollbesättningsproblemet och cast.txt lösningen för samma instans.

Redovisning

Länk till bokningslistor för labbredovisning 10 december 2021 kommer att publiceras här ett dygn före passets start.


##PLAN
1. Hitta enkelt en dålig men trivial lösning
2. Anta att detta är den bästa lösning, än så länge
3. Hitta ett sätta att värdesätta en lösning
4. Utför lokala modifieringar som förbättrar värdet på lösningen
5. Fortsätt göra detta tills att det inte går att hitta fler lösningar, eller tills att ett threshhold nått