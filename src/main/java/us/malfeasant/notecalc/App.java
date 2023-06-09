package us.malfeasant.notecalc;

import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;


/**
 * JavaFX App
 */
public class App extends Application {
    enum Denominations {
        ONE(1), TWO(2), FIVE(5), TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100);
        final int value;
        Denominations(int v) {
            value = v;
        }
    }

    NumberBinding result;
    final NumberStringConverter conv = new NumberStringConverter();
    final Pattern numbersOnly = Pattern.compile("\\p{Digit}+");

    @Override
    public void start(Stage stage) {
        var pane = new GridPane();
        int i = 0;
        for (var d : Denominations.values()) {
            var value = d.value;
            var label = new Label("$" + value);
            var count = new TextField("0");
            var amount = new Label();
            var intermediate = 
                Bindings.multiply(value, 
                Bindings.createIntegerBinding(() -> {
                    // filter input text so only numbers are allowed
                    var in = count.getText();
                    var sb = new StringBuilder();
                    var matcher = numbersOnly.matcher(in);
                    while (matcher.find()) {
                        sb.append(matcher.group());
                    }
                    // if no valid numbers were found, string will be empty at this point
                    if (sb.isEmpty()) sb.append("0");
                    // replace the text in the input with the number found
                    count.textProperty().set(sb.toString());
                    return Integer.parseInt(count.getText());   // shouldn't throw exception...
                }, count.textProperty()));
            amount.textProperty().bind(Bindings.convert(intermediate));

            pane.add(label, 0, i);
            pane.add(count, 1, i);
            pane.add(amount, 2, i);
            
            if (result == null) result = intermediate;  // first one
            else result = Bindings.add(result, intermediate);
            ++i;
        }

        pane.add(new Label("Total: "), 0, i);
        var resultLabel = new Label();
        resultLabel.textProperty().bind(result.asString());
        pane.add(resultLabel, 2, i);

        stage.setScene(new Scene(pane));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}