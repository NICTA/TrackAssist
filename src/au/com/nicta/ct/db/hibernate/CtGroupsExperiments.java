package au.com.nicta.ct.db.hibernate;
// Generated 24/01/2011 2:48:48 PM by Hibernate Tools 3.2.1.GA



/**
 * CtGroupsExperiments generated by hbm2java
 */
public class CtGroupsExperiments  implements java.io.Serializable {


     private int pkGroupExperiment;
     private CtGroups ctGroups;
     private CtExperiments ctExperiments;

    public CtGroupsExperiments() {
    }

	
    public CtGroupsExperiments(int pkGroupExperiment) {
        this.pkGroupExperiment = pkGroupExperiment;
    }
    public CtGroupsExperiments(int pkGroupExperiment, CtGroups ctGroups, CtExperiments ctExperiments) {
       this.pkGroupExperiment = pkGroupExperiment;
       this.ctGroups = ctGroups;
       this.ctExperiments = ctExperiments;
    }
   
    public int getPkGroupExperiment() {
        return this.pkGroupExperiment;
    }
    
    public void setPkGroupExperiment(int pkGroupExperiment) {
        this.pkGroupExperiment = pkGroupExperiment;
    }
    public CtGroups getCtGroups() {
        return this.ctGroups;
    }
    
    public void setCtGroups(CtGroups ctGroups) {
        this.ctGroups = ctGroups;
    }
    public CtExperiments getCtExperiments() {
        return this.ctExperiments;
    }
    
    public void setCtExperiments(CtExperiments ctExperiments) {
        this.ctExperiments = ctExperiments;
    }




}


