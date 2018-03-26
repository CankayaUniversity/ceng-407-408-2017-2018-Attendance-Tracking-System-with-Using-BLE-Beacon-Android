package seniorproject.attendancetrackingsystem.utils;


public class Actor {
    private int id;
    private String name, surname, password, mail;

    Actor() {
        id = 0;
        name = surname = password = mail = "";
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
