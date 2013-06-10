package com.mdeblase.kml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Folder folder = (Folder)doc.getFeature().get(0).withName("gz_2010_us_050_00_20m");
            for (int i = 0; i < folder.getFeature().size(); i++) {
                Placemark p = (Placemark)folder.getFeature().get(i);
                
                //String fips = getFIPS(p);
                if (p.getGeometry() instanceof MultiGeometry) {
                    MultiGeometry mg = (MultiGeometry) p.getGeometry();
                    double largestArea = 0.0;
                    String centroid = "";
                    for (int j = 0; j < mg.getGeometry().size(); j++) {
                        Polygon poly = (Polygon) mg.getGeometry().get(j);
                        double area = KMLUtil.getArea(poly);
                        if(area > largestArea) {
                            centroid = KMLUtil.getCentroid(area, poly);
                            largestArea = area;
                        }
                        
                    }
                    //I only want the center of the largest Polygon to ignore counties with islands
                    writer.append(KMLUtil.getFIPS(p));
                    writer.append(',');
                    writer.append(centroid);
                    writer.append('\n');
                    
                } else {
                    Polygon poly = (Polygon) p.getGeometry();
                    double area = KMLUtil.getArea(poly);
                    String centroid = KMLUtil.getCentroid(area, poly);
                    writer.append(KMLUtil.getFIPS(p));
                    writer.append(',');
                    writer.append(centroid);
                    writer.append('\n');
                }
                
                
            }
            writer.flush();
            writer.close();
            
            
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    
    
}
