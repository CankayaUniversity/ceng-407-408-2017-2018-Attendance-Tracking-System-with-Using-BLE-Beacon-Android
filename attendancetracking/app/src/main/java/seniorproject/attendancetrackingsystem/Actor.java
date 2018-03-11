package seniorproject.attendancetrackingsystem;


public class Actor {
    private int id;
    private String name, surname, password, mail;

    public void setPassword(String password){ this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setMail(String mail){ this.mail = mail; }
    public String getPassword() { return this.password; }
    public String getName() { return this.name; }
    public String getSurname() { return this.surname; }
    public String getMail() { return this.mail; }
}
