package au.com.nicta.ct.db.hibernate;
// Generated 24/01/2011 2:48:48 PM by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * CtImages generated by hbm2java
 */
public class CtImages  implements java.io.Serializable {


     private int pkImage;
     private CtExperiments ctExperiments;
     private String uri;
     private Set ctDetectionses = new HashSet(0);
     private Set ctImagesCoordinateses = new HashSet(0);
     private Set ctUwellses = new HashSet(0);

    public CtImages() {
    }

	
    public CtImages(int pkImage, String uri) {
        this.pkImage = pkImage;
        this.uri = uri;
    }
    public CtImages(int pkImage, CtExperiments ctExperiments, String uri, Set ctDetectionses, Set ctImagesCoordinateses, Set ctUwellses) {
       this.pkImage = pkImage;
       this.ctExperiments = ctExperiments;
       this.uri = uri;
       this.ctDetectionses = ctDetectionses;
       this.ctImagesCoordinateses = ctImagesCoordinateses;
       this.ctUwellses = ctUwellses;
    }
   
    public int getPkImage() {
        return this.pkImage;
    }
    
    public void setPkImage(int pkImage) {
        this.pkImage = pkImage;
    }
    public CtExperiments getCtExperiments() {
        return this.ctExperiments;
    }
    
    public void setCtExperiments(CtExperiments ctExperiments) {
        this.ctExperiments = ctExperiments;
    }
    public String getUri() {
        return this.uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    public Set getCtDetectionses() {
        return this.ctDetectionses;
    }
    
    public void setCtDetectionses(Set ctDetectionses) {
        this.ctDetectionses = ctDetectionses;
    }
    public Set getCtImagesCoordinateses() {
        return this.ctImagesCoordinateses;
    }
    
    public void setCtImagesCoordinateses(Set ctImagesCoordinateses) {
        this.ctImagesCoordinateses = ctImagesCoordinateses;
    }
    public Set getCtUwellses() {
        return this.ctUwellses;
    }
    
    public void setCtUwellses(Set ctUwellses) {
        this.ctUwellses = ctUwellses;
    }




}

