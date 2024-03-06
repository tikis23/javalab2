package com.bustoskaiciuokle.front;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.util.function.UnaryOperator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import com.bustoskaiciuokle.back.Calculator;

final class InputNode extends VBox {
    public interface OnCalculate {
        void onCalculate(Calculator calculator);
    }
    public InputNode(OnCalculate onCalculate) {
        super();
        
        Font font = Font.font(15);

        // paskolos suma
        TextField paskolosSuma = new TextField();
        paskolosSuma.setFont(font);
        {
            int numDigits = 2;
            Text text = new Text("Paskolos suma:");
            text.setFont(font);
            UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
                String newText = change.getText().replace(',', '.');
                change.setText(newText);
            
                if (!change.getControlNewText().matches("(^0(\\.[0-9]{0," + numDigits + "})?$)?([1-9][0-9]{0,30})?(\\.[0-9]{0," + numDigits + "})?")) {
                    return null;
                }
                return change;
            };
            paskolosSuma.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), null, doubleFilter));
            paskolosSuma.setPromptText("Paskolos suma");

            this.getChildren().addAll(text, paskolosSuma);
        }
        // metinis procentas
        TextField procentai = new TextField();
        procentai.setFont(font);
        {
            int numDigits = 8;
            Text text = new Text("Metinis procentas:");
            text.setFont(font);
            UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
                String newText = change.getText().replace(',', '.');
                change.setText(newText);
            
                if (!change.getControlNewText().matches("(^0(\\.[0-9]{0," + numDigits + "})?$)?([1-9][0-9]{0,30})?(\\.[0-9]{0," + numDigits + "})?")) {
                    return null;
                }
                return change;
            };
            procentai.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), null, doubleFilter));
            procentai.setPromptText("Metinis procentas");

            this.getChildren().addAll(text, procentai);
        }
        // paskolos terminas
        TextField metai = new TextField();
        metai.setFont(font);
        TextField menesiai = new TextField();
        menesiai.setFont(font);
        {
            Text text = new Text("Paskolos terminas:");
            text.setFont(font);
            UnaryOperator<TextFormatter.Change> integerFilter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("(^0$)?([1-9][0-9]*)?")) {
                    return change;
                }
                return null;
            };
            metai.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, integerFilter));
            metai.setPromptText("Metai");
            menesiai.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, integerFilter));
            menesiai.setPromptText("Menesiai");

            this.getChildren().addAll(text, metai, menesiai);
        }
        // grafiko tipas
        RadioButton anuiteto = new RadioButton("Anuiteto"); 
        anuiteto.setFont(font);
        RadioButton linijinis = new RadioButton("Linijinis"); 
        linijinis.setFont(font);
        {
            Text text = new Text("Paskolos grąžinimo grafikas:");
            text.setFont(font);
            ToggleGroup group = new ToggleGroup();
            anuiteto.setToggleGroup(group);
            linijinis.setToggleGroup(group);

            this.getChildren().addAll(text, anuiteto, linijinis);
        }
        // skaiciuoti button
        {
            Button skaiciuoti = new Button("Skaičiuoti");
            skaiciuoti.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    Calculator ret = new Calculator();
                    try {
                        ret.paskolosSuma = Double.parseDouble(paskolosSuma.getText());
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(AlertType.ERROR, "Įveskite paskolos sumą", ButtonType.CLOSE);
                        alert.showAndWait();
                        return;
                    }
                    if (ret.paskolosSuma == 0) {
                        Alert alert = new Alert(AlertType.ERROR, "Paskolos suma negali būti 0", ButtonType.CLOSE);
                        alert.showAndWait();
                        return;
                    }
                    try {
                        ret.procentai = Double.parseDouble(procentai.getText());
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(AlertType.ERROR, "Įveskite metinį procentą", ButtonType.CLOSE);
                        alert.showAndWait();
                        return;
                    }
                    try {
                        ret.metai = Integer.parseInt(metai.getText());
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(AlertType.ERROR, "Įveskite metus", ButtonType.CLOSE);
                        alert.showAndWait();
                        return;
                    }
                    try {
                        ret.menesiai = Integer.parseInt(menesiai.getText());
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(AlertType.ERROR, "Įveskite mėnesius", ButtonType.CLOSE);
                        alert.showAndWait();
                        return;
                    }
                    if (ret.menesiai == 0 && ret.metai == 0) {
                        Alert alert = new Alert(AlertType.ERROR, "Paskolos laikotarpis negali būti 0", ButtonType.CLOSE);
                        alert.showAndWait();
                        return;
                    }
                    if (anuiteto.isSelected()) {
                        ret.graphType = Calculator.GraphType.ANUITETO;
                    } else if (linijinis.isSelected()) {
                        ret.graphType = Calculator.GraphType.LINIJINIS;
                    } else {
                        Alert alert = new Alert(AlertType.ERROR, "Pasirinkite paskolos grąžinimo grafiką", ButtonType.CLOSE);
                        alert.showAndWait();
                        return;
                    }
                    onCalculate.onCalculate(ret);
                }
            });
            
            this.getChildren().add(skaiciuoti);
        }
    }
}
