package com.bustoskaiciuokle.front;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import java.util.function.UnaryOperator;
import javafx.util.StringConverter;

import com.bustoskaiciuokle.back.Calculator;

final class GraphWindow extends Stage {
    public GraphWindow(Calculator calculator) {
        super();
        Group root = new Group();
        setScene(new Scene(root));
        calculator.process();

        Font font = Font.font(15);

        HBox hbox = new HBox();
        LineChart<Number, Number> chart = createChart(calculator, font);
        hbox.getChildren().add(chart);
        TableView<Calculator.TableCellData> table = createTable(calculator);
        hbox.getChildren().add(table);
        VBox slider = createIntervalSlider(calculator, font);
        VBox.setMargin(slider, new Insets(5, 5, 5, 5));
        slider.getChildren().add(createAtidejimas(calculator, font));
        slider.getChildren().add(createSaveButton(calculator, font));
        hbox.getChildren().add(slider);
        hbox.setPadding(new Insets(10, 10, 10, 10));

        root.getChildren().add(hbox);
    }
    private VBox createIntervalSlider(Calculator calculator, Font font) {
        VBox box = new VBox();
        
        Text text = new Text("Filtruoti:");
        text.setFont(font);
        m_sliderLow = new Slider();
        m_sliderHigh = new Slider();

        HBox sb1 = new HBox();
        HBox sb2 = new HBox();
        Text textNuo = new Text("Nuo: ");
        Text textIki = new Text("Iki: ");
        textNuo.setFont(font);
        textIki.setFont(font);
        sb1.getChildren().addAll(textNuo, m_sliderLow);
        sb2.getChildren().addAll(textIki, m_sliderHigh);
        sb1.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        sb2.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        box.getChildren().addAll(text, sb1, sb2);
        box.setAlignment(javafx.geometry.Pos.TOP_RIGHT);

        m_sliderLow.setMin(1);
        m_sliderLow.setMax(calculator.getMonthCount());
        m_sliderLow.setValue(1);
        m_sliderLow.setShowTickLabels(true);
        m_sliderLow.setShowTickMarks(true);
        m_sliderLow.setMajorTickUnit(1);
        m_sliderLow.setMinorTickCount(0);
        m_sliderLow.setBlockIncrement(1);
        m_sliderLow.valueProperty().addListener((observable, oldValue, newValue) -> {
            int val = newValue.intValue();
            if (val > m_sliderHigh.getValue()) {
                val = (int)m_sliderHigh.getValue();
                m_sliderLow.setValue(val);
            }
            calculator.setMonthMin(val - 1);
            calculator.process();
        });
        m_sliderHigh.setMin(1);
        m_sliderHigh.setMax(calculator.getMonthCount());
        m_sliderHigh.setValue(calculator.getMonthCount());
        m_sliderHigh.setShowTickLabels(true);
        m_sliderHigh.setShowTickMarks(true);
        m_sliderHigh.setMajorTickUnit(1);
        m_sliderHigh.setMinorTickCount(0);
        m_sliderHigh.setBlockIncrement(1);
        m_sliderHigh.valueProperty().addListener((observable, oldValue, newValue) -> {
            int val = newValue.intValue();
            if (val < m_sliderLow.getValue()) {
                val = (int)m_sliderLow.getValue();
                m_sliderHigh.setValue(val);
            }
            calculator.setMonthMax(val - 1);
            calculator.process();
        });

        return box;
    }
    private void updateSliderMaxValues(Calculator calculator) {
        m_sliderLow.setMax(calculator.getMonthCount() + calculator.getAtidejimoLaikotarpis());
        m_sliderHigh.setMax(calculator.getMonthCount() + calculator.getAtidejimoLaikotarpis());
    }
    private VBox createAtidejimas(Calculator calculator, Font font) {
        VBox box = new VBox();
        box.setAlignment(javafx.geometry.Pos.TOP_RIGHT);
        Text text = new Text("Atidejimas:");
        text.setFont(font);

        HBox hb1 = new HBox();
        {
            Text nuo = new Text("Nuo (mėn.): ");
            nuo.setFont(font);
            TextField nuoInput = new TextField();
            nuoInput.setFont(font);
            UnaryOperator<TextFormatter.Change> integerFilter1 = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("(^0$)?([1-9][0-9]*)?")) {
                    try {
                        calculator.setAtidejimasNuo(Integer.parseInt(newText) - 1);
                    } catch (NumberFormatException e) {
                    }
                    calculator.process();
                    updateSliderMaxValues(calculator);
                    return change;
                }
                return null;
            };
            nuoInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, integerFilter1));
            nuoInput.setPromptText("mėn.");
            hb1.getChildren().addAll(nuo, nuoInput);
        }

        HBox hb2 = new HBox();
        {
            Text laikotarpis = new Text("Laikotarpis (mėn.): ");
            laikotarpis.setFont(font);
            TextField laikotarpisInput = new TextField();
            laikotarpisInput.setFont(font);
            UnaryOperator<TextFormatter.Change> integerFilter2 = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("(^0$)?([1-9][0-9]*)?")) {
                    try {
                        calculator.setAtidejimoLaikotarpis(Integer.parseInt(newText));
                    } catch (NumberFormatException e) {
                    }
                    calculator.process();
                    updateSliderMaxValues(calculator);
                    return change;
                }
                return null;
            };
            laikotarpisInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, integerFilter2));
            laikotarpisInput.setPromptText("mėn.");
            hb2.getChildren().addAll(laikotarpis, laikotarpisInput);
        }

        HBox hb3 = new HBox();
        {
            TextField procentai = new TextField();
            procentai.setFont(font);
            int numDigits = 8;
            Text proc = new Text("Metinis atidėjimo procentas: ");
            proc.setFont(font);
            UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
                String newText = change.getText().replace(',', '.');
                change.setText(newText);
                if (!change.getControlNewText().matches("(^0(\\.[0-9]{0," + numDigits + "})?$)?([1-9][0-9]{0,30})?(\\.[0-9]{0," + numDigits + "})?")) {
                    return null;
                }
                try {
                    calculator.atidejimoProcentas = Double.parseDouble(change.getControlNewText());
                } catch (NumberFormatException e) {
                }
                calculator.process();
                updateSliderMaxValues(calculator);
                return change;
            };
            procentai.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter));
            procentai.setPromptText("procentai");
            hb3.getChildren().addAll(proc, procentai);
        }

        hb1.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        hb2.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        hb3.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        box.getChildren().addAll(text, hb1, hb2, hb3);
        return box;
    }
    private Button createSaveButton(Calculator calculator, Font font) {
        Button button = new Button("Išsaugoti ataskaitą");
        button.setFont(font);
        button.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setContentText("Failo pavadinimas:");
            td.setHeaderText("Įrašykite failo pavadinimą, kad išsaugotumėte ataskaitą.");
            td.setTitle("Išsaugoti ataskaitą");
            td.showAndWait().ifPresent(name -> {
                if (!calculator.saveToFile(name)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nepavyko išsaugoti ataskaitos", javafx.scene.control.ButtonType.CLOSE);
                    alert.showAndWait();
                }
            });
        });
        return button;

    }
    private TableView<Calculator.TableCellData> createTable(Calculator calculator) {
        TableView<Calculator.TableCellData> table = new TableView<>();

        TableColumn<Calculator.TableCellData, Integer> col1 = new TableColumn<>("Mėnesis");
        col1.setCellValueFactory(new PropertyValueFactory<>("month"));
        table.getColumns().add(col1);
        TableColumn<Calculator.TableCellData, Double> col2 = new TableColumn<>("Įmoka");
        col2.setCellValueFactory(new PropertyValueFactory<>("payAmount"));
        col2.setCellFactory(tc -> new TableCell<Calculator.TableCellData, Double>() {
            @Override protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", amount.doubleValue()));
                }
            }
        });
        table.getColumns().add(col2);
        TableColumn<Calculator.TableCellData, Double> col4 = new TableColumn<>("Palukanos");
        col4.setCellValueFactory(new PropertyValueFactory<>("payAmountPalukanos"));
        col4.setCellFactory(tc -> new TableCell<Calculator.TableCellData, Double>() {
            @Override protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", amount.doubleValue()));
                }
            }
        });
        table.getColumns().add(col4);
        TableColumn<Calculator.TableCellData, Double> col3 = new TableColumn<>("Liko mokėti");
        col3.setCellValueFactory(new PropertyValueFactory<>("payAmountLeft"));
        col3.setCellFactory(tc -> new TableCell<Calculator.TableCellData, Double>() {
            @Override protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", amount.doubleValue()));
                }
            }
        });
        table.getColumns().add(col3);

        table.setItems(calculator.getTableData());
        return table;
    }
    private LineChart<Number, Number> createChart(Calculator calculator, Font font) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFont(font);
        xAxis.setLabel("Mėnesis");
        yAxis.setTickLabelFont(font);
        yAxis.setLabel("Įmoka");
        StringConverter<Number> converter = new StringConverter<Number>() {
            @Override public String toString(Number object) {
                return object.doubleValue() == object.intValue() ? object.intValue() + "" : "";
            }
            @Override public Number fromString(String string) {return 0;}
        };
        xAxis.setTickLabelFormatter(converter);


        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Paskolos mokėjimo grafikas");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        chart.getData().add(calculator.getGraphData());

        return chart;
    }

    private Slider m_sliderLow;
    private Slider m_sliderHigh;
}
