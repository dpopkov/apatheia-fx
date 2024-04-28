package io.dpopkov.apatheiafx;

import io.dpopkov.apatheiafx.ui.AppUI;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppMainSpring {
    public static void main(String[] args) {
        Application.launch(AppUI.class);
    }
}
