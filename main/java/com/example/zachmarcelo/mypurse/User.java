package com.example.zachmarcelo.mypurse;

/**
 * Created by Zach Marcelo on 11/8/2018.
 */

public class User {
    public String name, email, sex, age;

    public User(){

    }

    public User(String username, String email, String sex, String age) {
        this.name = username;
        this.email = email;
        this.sex = sex;
        this.age = age;

    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}