package com.mdeblase.kml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.*;

public class KMLParseToCSV {

    private Kml kml = null;
    
    public KMLParseToCSV(Kml k) {
        setKml(k);
    }

    public Kml getKml() {
        return kml;
    }

    public void setKml(Kml kml) {
        this.kml = kml;
        
    }
    
    public void generateCSV(String filename) {    
        try {
            FileWriter writer = new FileWriter(filename);
            writer.append("FIPS");
            writer.append(',');
            writer.append("LAT");
            writer.append(',');
            writer.append("LON");
            writer.append('\n');
            Document doc = (Document)kml.getFeature();
            Folder folder = (Folder)doc.getFeature().get(0).withName("simp_cnty_existing");
            for (int i = 0; i < folder.getFeature().size(); i++) {
                Placemark p = (Placemark)folder.getFeature().get(i);
                String fips = getFIPS(p);
                double aveLat = getAvgLat(p);
                double aveLon =getAvgLon(p);
                String latStr = Double.toString(aveLat);
                System.out.println(latStr);
                String lonStr = Double.toString(aveLon);
                System.out.println(lonStr);
                writer.append(fips);
                writer.append(',');
                writer.append(latStr);
                writer.append(',');
                writer.append(lonStr);
                writer.append('\n');
                
            }
            writer.flush();
            writer.close();
            
            
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private double getAvgLon(Placemark p) {
        Polygon poly = (Polygon)p.getGeometry();
        Boundary outerBoundaryIs = poly.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coordinates = linearRing.getCoordinates();
        int count = 0;
        double lonTotal = 0;
        for (int i = 0; i < coordinates.size() -1; i++) {
            double lon = coordinates.get(i).getLongitude();
            lonTotal += lon;
            count++;
          }
        
        return lonTotal/count;
        
    }

    private double getAvgLat(Placemark p) {
        Polygon poly = (Polygon)p.getGeometry();
        Boundary outerBoundaryIs = poly.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coordinates = linearRing.getCoordinates();
        int count = 0;
        double latTotal = 0;
        for (int i = 0; i < coordinates.size() -1; i++) {
            double lat = coordinates.get(i).getLatitude();
            latTotal += lat;
            count++;
          }
        
        return latTotal/count;
    }

    private String getFIPS(Placemark p) {
        String desc = p.getDescription();
        String[] split1 = desc.split("<br>");
        String[] split2 = split1[0].split(" ");
        System.out.println(split2[2]);
        return split2[2];
    }
    
}
