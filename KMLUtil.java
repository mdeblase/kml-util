package com.mdeblase.kml;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.SchemaData;
import de.micromata.opengis.kml.v_2_2_0.SimpleData;

public class KMLUtil {

    public static void main(String args[]) {
        Kml countyBorders = Kml.unmarshal(new File("gz_2010_us_050_00_500k.kml"));
        KMLParseToCSV parser = new KMLParseToCSV(countyBorders);
        parser.generateCSV("countycentroids.csv");
    }
    
    public static double getArea(Polygon p) {
        Polygon poly = p;
        double total = 0;
        Boundary outerBoundaryIs = poly.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coords = linearRing.getCoordinates();
        for (int i = 0; i < coords.size()-1; i++ ) {
           total += ((coords.get(i).getLatitude() * coords.get(i+1).getLongitude()) - (coords.get(i+1).getLatitude() * coords.get(i).getLongitude()));
        }
        return total/2;
    }
    
    public static String getCentroid(double area, Polygon p) {
        Polygon poly = p;
        double latTotal = 0;
        double lonTotal = 0;
        Boundary outerBoundaryIs = poly.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coords = linearRing.getCoordinates();
        for (int i = 0; i < coords.size()-1; i++ ) {
            latTotal += (coords.get(i).getLatitude()+coords.get(i+1).getLatitude()) *((coords.get(i).getLatitude() * coords.get(i+1).getLongitude()) - (coords.get(i+1).getLatitude() * coords.get(i).getLongitude()));
            lonTotal += (coords.get(i).getLongitude()+coords.get(i+1).getLongitude()) *((coords.get(i).getLatitude() * coords.get(i+1).getLongitude()) - (coords.get(i+1).getLatitude() * coords.get(i).getLongitude()));
        }
        double centLat = latTotal/(6*area);
        double centLon = lonTotal/(6*area);
        return ""+centLat+","+centLon;
    }
    
    public static double getAvgLon(Polygon p) {
        Polygon poly = p;
        double lonTotal = 0;
        int count = 0;
        Boundary outerBoundaryIs = poly.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coordinates = linearRing.getCoordinates();
        Set<Coordinate> cordSet = new HashSet<Coordinate>();
        for (Coordinate c : coordinates) {
            cordSet.add(c);
        }
        System.out.println(coordinates.size() + " " + cordSet.size());

        for (Coordinate c : cordSet) {
            double lon = c.getLongitude();
            lonTotal += lon;
            count++;
          }
        
        return lonTotal/count;
        
    }

    public static double getAvgLat(Polygon p) {
        Polygon poly = p;
        double latTotal = 0;
        int count = 0;
        Boundary outerBoundaryIs = poly.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coordinates = linearRing.getCoordinates();
        Set<Coordinate> cordSet = new HashSet<Coordinate>();
        for (Coordinate c : coordinates) {
            cordSet.add(c);
        }
        
        for (Coordinate c : cordSet) {
            double lat = c.getLatitude();
            latTotal += lat;
            count++;
          }
        
        return latTotal/count;
    }

    public static String getFIPS(Placemark p) {
        SchemaData sd = p.getExtendedData().getSchemaData().get(0);
        List<SimpleData> dataList = sd.getSimpleData();
        String geoID = "";
        for(SimpleData sData : dataList) {
            System.out.println(sData.getName() + "" + sData.getValue());
            if(sData.getName().equals("GEO_ID")) {
                geoID =sData.getValue();
                
                break;
            }
        }
        String[] splitID = geoID.split("US");
        return splitID[1];
    }
}
