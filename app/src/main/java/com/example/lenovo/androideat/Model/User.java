package com.example.lenovo.androideat.Model;

/**
 * Created by LENOVO on 02.01.2018.
 */

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;

    public User(){


    }

     // public User (String name ,String password){
      //    Name=name;
       //   Password=password;
    //  }


    public User(String name, String password) {
        Name = name;
        Password = password;
        IsStaff="false";

    }

    public String getIsstaff() {
        return IsStaff;
    }

    public void setIsstaff(String isstaff) {
        IsStaff = isstaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName(){


          return Name;
      }

      public void setName(String name){
          Name=name;

      }

      public String getPassword(){
          return Password;


      }

      public void setPassword(String password){

          Password=password;
      }

  }


