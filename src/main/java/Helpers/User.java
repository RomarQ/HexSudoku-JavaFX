package Helpers;

public class User {

    int Id;
    String username;

    public User(int id, String username) {
        this.Id = id;
        this.username = username;
    }

    public int UserId() { return Id; }
    public String Username() { return username; }

}
