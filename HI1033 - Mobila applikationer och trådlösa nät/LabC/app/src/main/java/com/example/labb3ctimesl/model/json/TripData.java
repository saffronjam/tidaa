package com.example.labb3ctimesl.model.json;

import com.google.gson.annotations.SerializedName;

public class TripData {
    @SerializedName(value = "Origin")
    public Place origin;
    @SerializedName(value = "Destination")
    public Place destination;
    public String name;
    public String category;
    public String type;
    public String direction;

    public static class Place {
        public String name;
        public String time;
        public String date;
    }

    /*
            "Leg": [
                {
                    "Origin": {
                        "name": "Årsta, Borensvägen 37",
                        "time": "13:08:00",
                    },
                    "Destination": {
                        "name": "Årsta torg",
                        "time": "13:12:00",
                    },
                    "type": "WALK",
                },
                {
                    "Origin": {
                        "name": "Årsta torg",
                        "time": "13:14:00",
                    },
                    "Destination": {
                        "name": "Årstaberg (terminalen)",
                        "time": "13:21:00",
                    },
                    "name": "Buss 160",
                    "category": "BUS",
                    "type": "JNY",
                    "direction": "Årstaberg"
                },
                {
                    "Origin": {
                        "name": "Årstaberg (terminalen)",
                        "time": "13:22:00",
                    },
                    "Destination": {
                        "name": "Årstaberg",
                        "time": "13:23:00"
                    },
                    "type": "WALK",
                },
                {
                    "Origin": {
                        "name": "Årstaberg",
                        "time": "13:23:00"
                    },
                    "Destination": {
                        "name": "Årstaberg",
                        "time": "13:25:00"
                    },
                    "type": "WALK"
                    "dist": 156
                },
                {
                    "Origin": {
                        "name": "Årstaberg",
                        "time": "13:30:00"
                    },
                    "Destination": {
                        "name": "Flemingsberg"
                        "time": "13:43:00"
                    },
                    "name": "Pendeltåg 40",
                    "category": "TRN",
                    "type": "JNY"
                    "direction": "Södertälje centrum"
                },
                {
                    "Origin": {
                        "name": "Flemingsberg",
                        "type": "ST",
                        "id": "A=1@O=Flemingsberg@X=17947206@Y=59219047@U=74@L=400105171@",
                        "extId": "400105171",
                        "lon": 17.947206,
                        "lat": 59.219047,
                        "time": "13:44:00",
                        "date": "2021-12-22",
                        "hasMainMast": true,
                        "mainMastId": "A=1@O=Hälsovägen (Huddinge)@X=17947988@Y=59219236@U=74@L=300107005@",
                        "mainMastExtId": "300107005",
                        "additional": false
                    },
                    "Destination": {
                        "name": "Flemingsberg, Hälsovägen",
                        "type": "ST",
                        "id": "A=1@O=Flemingsberg, Hälsovägen@X=17948618@Y=59219928@U=74@L=510105171@",
                        "extId": "510105171",
                        "lon": 17.948618,
                        "lat": 59.219928,
                        "time": "13:46:00",
                        "date": "2021-12-22",
                        "hasMainMast": true,
                        "mainMastId": "A=1@O=Hälsovägen (Huddinge)@X=17947988@Y=59219236@U=74@L=300107005@",
                        "mainMastExtId": "300107005",
                        "additional": false
                    },
                    "Stops": null,
                    "idx": "5",
                    "name": "",
                    "type": "WALK",
                    "reachable": true,
                    "redirected": false,
                    "duration": "PT2M",
                    "dist": 127,
                    "hide": true
                },
                {
                    "Origin": {
                        "name": "Flemingsberg, Hälsovägen",
                        "type": "ST",
                        "id": "A=1@O=Flemingsberg, Hälsovägen@X=17948618@Y=59219928@U=74@L=510105171@",
                        "extId": "510105171",
                        "lon": 17.948618,
                        "lat": 59.219928,
                        "time": "13:46:00",
                        "date": "2021-12-22",
                        "hasMainMast": true,
                        "mainMastId": "A=1@O=Hälsovägen (Huddinge)@X=17947988@Y=59219236@U=74@L=300107005@",
                        "mainMastExtId": "300107005",
                        "additional": false
                    },
                    "Destination": {
                        "name": "Huddinge, Hälsovägen 11A",
                        "type": "ADR",
                        "id": "A=2@O=Huddinge, Hälsovägen 11A@l=@X=17938487@Y=59222068@u=18@",
                        "lon": 17.938487,
                        "lat": 59.222068,
                        "time": "13:55:00",
                        "date": "2021-12-22",
                        "additional": false
                    },
                    "GisRef": {
                        "ref": "G|1|G@F|A=1@O=Flemingsberg, Hälsovägen@X=17948618@Y=59219928@U=74@L=510105171@|A=2@O=Huddinge, Hälsovägen 11A@l=@X=17938487@Y=59222068@u=18@|22122021|134600|135500|ft|ft@0@2000@120@-1@100@1@1000@0@@@@@false@0@-1@$f@$f@$f@$f@$f@$§bt@0@2000@120@-1@100@1@1000@0@@@@@false@0@-1@$f@$f@$f@$f@$f@$§tt@0@5000@120@-1@100@1@2500@0@@@@@false@0@-1@$f@$f@$f@$f@$f@$§|"
                    },
                    "Stops": null,
                    "idx": "6",
                    "name": "",
                    "type": "WALK",
                    "reachable": true,
                    "redirected": false,
                    "duration": "PT9M",
                    "dist": 719
                }
            ]
     */
}
