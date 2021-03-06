package au.com.nicta.ct.db.hibernate;
// Generated 24/01/2011 2:48:48 PM by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * CtAnnotations generated by hbm2java
 */
public class CtAnnotations  implements java.io.Serializable {


     private int pkAnnotation;
     private CtAnnotationsTypes ctAnnotationsTypes;
     private CtImages ctImages;
     private CtSolutions ctSolutions;
     private String value;
     private Double x;
     private Double y;

    public CtAnnotations() {
    }
	
    public CtAnnotations(int pkAnnotation) {
        this.pkAnnotation = pkAnnotation;
    }
    
    public CtAnnotations(
            int pkAnnotation, 
            CtAnnotationsTypes ctAnnotationsTypes,
            CtImages ctImages,
            CtSolutions ctSolutions,
            String value,
            Double x,
            Double y ) {
       this.pkAnnotation = pkAnnotation;
       this.ctAnnotationsTypes = ctAnnotationsTypes;
       this.ctImages = ctImages;
       this.ctSolutions = ctSolutions;
       this.value = value;
       this.x = x;
       this.y = y;
    }
   
    public int getPkAnnotation() {
        return this.pkAnnotation;
    }
    
    public void setPkAnnotation(int pkAnnotation) {
        this.pkAnnotation = pkAnnotation;
    }

    public CtAnnotationsTypes getCtAnnotationsTypes() {
        return this.ctAnnotationsTypes;
    }
    
    public void setCtAnnotationsTypes(CtAnnotationsTypes ctAnnotationsTypes) {
        this.ctAnnotationsTypes = ctAnnotationsTypes;
    }

    public CtImages getCtImages() {
        return this.ctImages;
    }

    public void setCtImages(CtImages ctImages) {
        this.ctImages = ctImages;
    }

    public CtSolutions getCtSolutions() {
        return this.ctSolutions;
    }

    public void setCtSolutions(CtSolutions ctSolutions) {
        this.ctSolutions = ctSolutions;
    }

    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public Double getX() {
        return this.x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}


