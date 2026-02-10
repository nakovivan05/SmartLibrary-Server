package SmartLibrary;

import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Serializable {
    private String username;
    private String password;
    public User (String username, String password)
    {
        setUsername(username);
        setPassword(password);
    }
    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return this.password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public abstract UserType getUserType();
    @Override
    public String toString()
    {
        return "Username: " + this.getUsername() + ", password: " + this.getPassword();
    }
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass()!=o.getClass()) return false;
        User user = (User)o;
        return Objects.equals(this.getUsername(),user.getUsername())&&Objects.equals(this.getPassword(),user.getPassword());
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(this.getUsername(),this.getPassword());
    }
}
