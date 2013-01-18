package au.com.nicta.ct.db.hibernate;
// Generated 24/01/2011 2:48:48 PM by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * CtUsers generated by hbm2java
 */
public class CtUsers  implements java.io.Serializable {


     private int pkUser;
     private String name;
     private String password;
     private String firstname;
     private String lastname;
     private Set ctUsersGroupses = new HashSet(0);

    public CtUsers() {
    }

    public CtUsers(int pkUser, String name) {
        this.pkUser = pkUser;
        this.name = name;
    }
    public CtUsers(int pkUser, String name, String password, String firstname, String lastname, Set ctUsersGroupses) {
       this.pkUser = pkUser;
       this.name = name;
       this.password = password;
       this.firstname = firstname;
       this.lastname = lastname;
       this.ctUsersGroupses = ctUsersGroupses;
    }
   
    public int getPkUser() {
        return this.pkUser;
    }
    
    public void setPkUser(int pkUser) {
        this.pkUser = pkUser;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstname() {
        return this.firstname;
    }
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return this.lastname;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public Set getCtUsersGroupses() {
        return this.ctUsersGroupses;
    }
    
    public void setCtUsersGroupses(Set ctUsersGroupses) {
        this.ctUsersGroupses = ctUsersGroupses;
    }




}


