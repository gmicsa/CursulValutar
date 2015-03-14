/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.model;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *
 * @author AlexSch
 */
@Entity
public class UserEntity implements Serializable {
    
    private static final long serialVersionUID = 2L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;
    
    // Facebook id to connect to the application
    private String fbId;
    
    // email to send notifications
    private String email;
    
    // Facebook user name for addressing
    private String fbUserName;


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 47 * hash + (this.fbId != null ? this.fbId.hashCode() : 0);
        hash = 47 * hash + (this.fbUserName != null ? this.fbUserName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", fbId=" + fbId + ", email=" + email + ", fbUserName=" + fbUserName + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserEntity other = (UserEntity) obj;
        if ((this.fbId == null) ? (other.fbId != null) : !this.fbId.equals(other.fbId)) {
            return false;
        }
        if ((this.email == null) ? (other.email != null) : !this.email.equals(other.email)) {
            return false;
        }
        if ((this.fbUserName == null) ? (other.fbUserName != null) : !this.fbUserName.equals(other.fbUserName)) {
            return false;
        }
        return true;
    }    

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFbUserName() {
        return fbUserName;
    }

    public void setFbUserName(String fbUserName) {
        this.fbUserName = fbUserName;
    }
}
