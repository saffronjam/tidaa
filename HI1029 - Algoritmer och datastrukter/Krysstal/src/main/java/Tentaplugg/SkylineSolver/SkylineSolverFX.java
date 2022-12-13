/*
    Rutten ges genom att route fylls med index för städerna i den ordning de besöks
    Det förutsätts att rutten börjar i staden med index 0 och route[0] ska sättas till 0.
    Sedan ska route[1] sättas till index för den stad som ligger närmast stad 0 osv.
    route har samma dimension som w och sista platsen i route: route[route.length-1] ska sättas
    till sista staden som besöks innan resan tillbaka till stad med index 0.
    minDistanceGreedy ska returnera längden på hela resan inklusive tillbaka till startstaden.
 */

package Tentaplugg.SkylineSolver;

import java.util.List;

public class SkylineSolverFX {
    public static List<Point> solve(List<Building> buildings) {

        var buildingsArr = new Building[buildings.size()];
        buildings.toArray(buildingsArr);
        var skylineSolver = new SkylineSolver(buildingsArr);

        var skyline = skylineSolver.getSkyline();
        return skyline;
    }
}
