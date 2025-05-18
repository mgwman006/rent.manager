package com.tante.landlordtenant.models.User;


import javax.persistence.*;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String FirsName;
    private  String LastName;
    private  String Email;

    public User(String firsName, String lastName, String email) {
        FirsName = firsName;
        LastName = lastName;
        Email = email;
    }

    public Long getId() {
        return Id;
    }

    public String getFirsName() {
        return FirsName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setFirsName(String firsName) {
        FirsName = firsName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", FirsName='" + FirsName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }
}
