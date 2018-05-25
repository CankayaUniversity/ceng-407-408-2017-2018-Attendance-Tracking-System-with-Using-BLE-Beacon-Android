package seniorproject.attendancetrackingsystem.utils;

public class Actor {
  private int id;
  private String name;
  private String surname;
  private String password;
  private String mail;
  private String image;

  Actor() {}

  public String getName() {
    return this.name;
  }

  void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return this.surname;
  }

  void setSurname(String surname) {
    this.surname = surname;
  }

  public String getMail() {
    return this.mail;
  }

  void setMail(String mail) {
    this.mail = mail;
  }

  public String getImage() {
    return this.image;
  }

  void setImage(String img) {
    this.image = img;
  }

  public int getId() {
    return id;
  }

  void setId(int id) {
    this.id = id;
  }
}
