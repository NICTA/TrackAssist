package au.com.nicta.ct.db.hibernate;
// Generated 24/01/2011 2:48:48 PM by Hibernate Tools 3.2.1.GA



/**
 * CtTracksDetections generated by hbm2java
 */
public class CtTracksDetections  implements java.io.Serializable {


     private int pkTrackDetection;
     private CtDetections ctDetections;
     private CtTracks ctTracks;

    public CtTracksDetections() {
    }

	
    public CtTracksDetections(int pkTrackDetection) {
        this.pkTrackDetection = pkTrackDetection;
    }
    public CtTracksDetections(int pkTrackDetection, CtDetections ctDetections, CtTracks ctTracks) {
       this.pkTrackDetection = pkTrackDetection;
       this.ctDetections = ctDetections;
       this.ctTracks = ctTracks;
    }
   
    public int getPkTrackDetection() {
        return this.pkTrackDetection;
    }
    
    public void setPkTrackDetection(int pkTrackDetection) {
        this.pkTrackDetection = pkTrackDetection;
    }
    public CtDetections getCtDetections() {
        return this.ctDetections;
    }
    
    public void setCtDetections(CtDetections ctDetections) {
        this.ctDetections = ctDetections;
    }
    public CtTracks getCtTracks() {
        return this.ctTracks;
    }
    
    public void setCtTracks(CtTracks ctTracks) {
        this.ctTracks = ctTracks;
    }




}

