package us.malfeasant.notecalc;

import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        ONE(1), /*TWO(2),*/ FIVE(5), TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100);
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
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setPadding(new Insets(5));

        int i = 0;
        for (var d : Denominations.values()) {
            var value = d.value;
            var label = new Label("$" + value);
            label.setMinWidth(30);
            var count = new TextField("0");
            count.setMaxWidth(70);
            count.setMinWidth(60);
            count.setPrefWidth(50);
            var amount = new Label();
            amount.setMinWidth(50);
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
                    if (sb.isEmpty()) {
                        sb.append("0");
                    }
                    // Limit to 5 characters
                    if (sb.length() > 5) {
                        sb.setLength(5);
                    }
                    // replace the text in the input with the number found
                    count.textProperty().set(sb.toString());
                    return Integer.parseInt(count.getText());   // shouldn't throw exception...
                }, count.textProperty()));
            amount.textProperty().bind(Bindings.convert(intermediate));

            pane.add(label, 0, i, 1, 1);
            pane.add(count, 1, i, 2, 1);
            pane.add(amount, 3, i, 1, 1);
            
            if (result == null) result = intermediate;  // first one
            else result = Bindings.add(result, intermediate);
            ++i;
        }

        pane.add(new Label("Total: "), 0, i, 2, 1);
        var resultLabel = new Label();
        resultLabel.textProperty().bind(result.asString());
        pane.add(resultLabel, 3, i);

        stage.setScene(new Scene(pane));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}