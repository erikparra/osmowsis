package com.osmowsis;

import java.util.HashMap;
import java.util.Map;


public class SharedLawn {
    private static SharedLawn SHAREDLAWN_INSTANCE = null;

    private Map<Integer, Map<Point, LawnSquare>> individualLawns = new HashMap<>();
    private Map<Integer, Point> currentRelativePosition = new HashMap<>();
    private Map<Integer, Map<Integer, Offset>> offsetMappings = new HashMap<>();

    // static method to create instance of Singleton class
    public static SharedLawn getInstance() {
        if (SHAREDLAWN_INSTANCE == null)
            SHAREDLAWN_INSTANCE = new SharedLawn();

        return SHAREDLAWN_INSTANCE;
    }

    public void registerNewMower(int mowerId, Point p, LawnSquare ls) {
        Map<Point, LawnSquare> newIndMap = new HashMap<>();
        newIndMap.put(p, ls);
        individualLawns.put(mowerId, newIndMap);
        offsetMappings.put(mowerId, new HashMap<>());
        currentRelativePosition.put(mowerId, new Point(0, 0));
    }

    public void addPoint(int mowerId, Point p, LawnSquare ls) {
        // if two mowers meet for the first time, add to the offsets map. This helps them translate their relative
        // coordinate systems between each other
        if (ls instanceof LawnSquareMower && ((LawnSquareMower) ls).getId() != mowerId){
            // add a new offset to the mapping
            LawnSquareMower adjacentMowerSquare = (LawnSquareMower) ls;
            int otherMowersId = adjacentMowerSquare.getId();
            if(!offsetMappings.get(mowerId).containsKey(otherMowersId)) {
                // mower is at Point p, so compare p to what the adjacent mower thinks p is to get the offset
                Point otherMowersPoint = currentRelativePosition.get(otherMowersId); // todo how to do this part
                // add the offsets
                Offset primaryOffset = new Offset(p.x - otherMowersPoint.x, p.y - otherMowersPoint.y);
                offsetMappings.get(mowerId).put(otherMowersId, primaryOffset);
                Offset otherOffset = new Offset(otherMowersPoint.x - p.x, otherMowersPoint.y - p.y);
                offsetMappings.get(otherMowersId).put(mowerId, otherOffset);
            }
        }
        individualLawns.get(mowerId).put(p, ls);
        shareData();
    }

    public Map<Point, LawnSquare> getLawn(int mowerId) {
        shareData();
        return individualLawns.get(mowerId);
    }

    public void moveMower(int mowerId, Point mowerLoc, Direction mowerDirection) {
        LawnState origState = individualLawns.get(mowerId).get(mowerLoc).getState();
        Point nextPoint = new Point(mowerLoc, mowerDirection);

        if (origState == LawnState.energymower)
            individualLawns.get(mowerId).put(mowerLoc, new LawnSquare(LawnState.energy));
        else
            individualLawns.get(mowerId).put(mowerLoc, new LawnSquare(LawnState.empty));

        LawnState nextState = individualLawns.get(mowerId).get(nextPoint).getState();
        if (nextState == LawnState.energy) {
            individualLawns.get(mowerId).put(nextPoint, new LawnSquareMower(LawnState.energymower, mowerId, mowerDirection));
        } else {
            individualLawns.get(mowerId).put(nextPoint, new LawnSquareMower(LawnState.mower, mowerId, mowerDirection));
        }

        currentRelativePosition.put(mowerId, nextPoint);
        shareData();
    }

    public void shareData() {
        // for every mower
        for (int mowerId : individualLawns.keySet()){
            // translate all other mowers' individual maps, if the offset is known
            for (int otherMowerId : offsetMappings.get(mowerId).keySet()){
                Map<Point, LawnSquare> translatedMap = translateIndividualMap(mowerId, otherMowerId);

                individualLawns.get(mowerId).putAll(translatedMap);
            }
        }
    }

    private Map<Point, LawnSquare> translateIndividualMap(int primaryId, int secondaryId){
        // apply the known offset to all points in the secondary mower's individual map
        Map<Point, LawnSquare> translatedMap = new HashMap<>();
        Map<Point, LawnSquare> untranslatedMap = new HashMap<>(individualLawns.get(secondaryId));
        Offset translation = offsetMappings.get(primaryId).get(secondaryId);
        for (Point p: untranslatedMap.keySet()){
            translatedMap.put(translation.translate(p), untranslatedMap.get(p));
        }
        return translatedMap;
    }
}

