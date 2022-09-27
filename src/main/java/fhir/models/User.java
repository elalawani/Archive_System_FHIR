package fhir.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int user_id;
    @NotNull
    @Size(min=4, max = 10)
    @Column(name = "username")
    private String username;
    @NotNull
    @Column(name = "password")
    private String password;
    @NotNull
    @Column(name = "roles")
    private String roles;
    @NotNull
    @Column(name = "active")
    private boolean active = true;
    
    /**
     * 
     * @return
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * 
     * @return
     */
    public int getUser_id() {
        return user_id;
    }
    
    /**
     * 
     * @param user_id
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    
    /**
     * 
     * @return
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * 
     * @return
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * 
     * @return
     */
    public String getRoles() {
        return roles;
    }
    
    /**
     * 
     * @param roles
     */
    public void setRoles(String roles) {
        this.roles = roles;
    }
}
