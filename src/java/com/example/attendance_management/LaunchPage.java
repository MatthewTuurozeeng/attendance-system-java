package com.example.attendance_management;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.HashMap;

public class LaunchPage extends Application{
    HashMap<String,String> userDetail=new HashMap<>();
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // the window where the user signs in or signs up;
        SceneOne s1=new SceneOne();
        s1.launchPage(stage);
    }

}

//  daniel.kpatamia@ashesi.edu.gh
//  Dan443325#