package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


//Отдельное окно - новое, там калькулятор единиц измерения информатики(бит, байт, кбайт, больше не надо)
//Кнопки B - байт и тд
public class MainWindowController {
    @FXML private Pane titlePane;
    @FXML private ImageView btnMinimize, btnClose;
    @FXML private Label lblResult, lblEquation;

    private double x, y;
    private double num1, num2;
    private boolean pressedBinary, pressedUnary, pressedEqual;
    private boolean storedNum1, storedNum2;
    private boolean wrongMath = false;
    private String binaryOperator;

    public void init(Stage stage) {
        titlePane.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        titlePane.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX()-x);
            stage.setY(mouseEvent.getScreenY()-y);
        });

        btnClose.setOnMouseClicked(mouseEvent -> stage.close());
        btnMinimize.setOnMouseClicked(mouseEvent -> stage.setIconified(true));
    }

    @FXML
    void switchToByte(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ByteWindowInterface.fxml"));
        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Calculator");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/calculator.png"))));
        ((ByteWindowController)loader.getController()).init(stage);
        stage.show();
    }

    @FXML
    void onNumberClicked(MouseEvent event) {
        String value = ((Pane)event.getSource()).getId().replace("btn", "");
        String outputResultText = lblResult.getText();
        wrongMath = false;

        if (shouldReplaceZero(outputResultText)) {
            lblResult.setText(value);

            if (shouldStoreNum2()) {
                storedNum2 = true;
            }

            if (pressedUnary) {
                storedNum1 = false;
            }

        } else {
            lblResult.setText(outputResultText + value);
        }

        pressedEqual = false;
        pressedUnary = false;
        pressedBinary = false;
    }

    private boolean shouldReplaceZero(String resultText) {
        return (storedNum1 && pressedBinary && !storedNum2)
                || pressedUnary
                || pressedEqual
                || (Double.parseDouble(resultText) == 0 && !resultText.equals("0."));
    }

    private boolean shouldStoreNum2() {
        return !storedNum2 && storedNum1 && pressedBinary;
    }

    @FXML
    public void onBinaryButtonClick(MouseEvent event) {
        if (!wrongMath) {
            String binaryOperator = ((Pane)event.getSource()).getId().replace("btn", "");

            if (!storedNum1) {
                num1 = Double.parseDouble(lblResult.getText());
                storedNum1 = true;
            }

            updateBinaryOperator(binaryOperator);

            pressedBinary = true;
            pressedUnary = false;
            pressedEqual = false;
        }
    }

    private void updateBinaryOperator(String binaryOperator) {
        switch (binaryOperator) {
            case "Plus" -> this.binaryOperator = "+";
            case "Minus" -> this.binaryOperator = "-";
            case "Multiply" -> this.binaryOperator = "*";
            case "Divide" -> this.binaryOperator = "/";
            case "Mod" -> this.binaryOperator = "%";
        }

        lblEquation.setText(num1 + " " + this.binaryOperator + " ");
    }

    @FXML
    public void onUnaryButtonClick(MouseEvent event) {
        if (!wrongMath) {

            double result = Double.parseDouble(lblResult.getText());

            String unaryOperator = ((Pane)event.getSource()).getId().replace("btn", "");
            switch (unaryOperator) {
                case "Percent" -> {
                    lblEquation.setText(result + "%");
                    result /= 100;
                }
                case "Negative" -> result *= -1;
                case "Absolute" -> {
                    result = Math.abs(result);
                    lblEquation.setText("|" + result + "|");
                }
                case "Exponent" -> {
                    lblEquation.setText(result + "^2");
                    result = result * result;
                }
                case "SquareRoot" -> {
                    lblEquation.setText("sqrt(" + result + ")");
                    result = Math.sqrt(result);
                }
            }

            if (!storedNum1) {
                num1 = result;
                storedNum1 = true;
            } else if (shouldStoreNum2()) {
                num2 = result;
                storedNum2 = true;
            }

            lblResult.setText(Double.toString(result));
            pressedBinary = false;
            pressedUnary = true;
            pressedEqual = false;
        }
    }

    public void onCommaButtonClick() {
        if (!wrongMath) {
            if (!lblResult.getText().contains(".")) {
                if (pressedUnary || pressedBinary) {
                    lblResult.setText("0.");
                }
                else {
                    lblResult.setText(lblResult.getText() + ".");
                }
            }

            pressedBinary = false;
            pressedUnary = false;
            pressedEqual = false;
        }
    }

    @FXML
    public void onDeleteButtonClick(MouseEvent event) {
        String deleteOperator = ((Pane)event.getSource()).getId().replace("btn", "");
        switch (deleteOperator) {
            case "Clear" -> reset();
            case "Delete" -> {
                if (!wrongMath) {
                    if (Double.parseDouble(lblResult.getText()) != 0) {
                        lblResult.setText(lblResult.getText().substring(0, lblResult.getText().length() - 1));
                    }

                    if (lblResult.getText().length() <= 0) {
                        lblResult.setText("0");
                    }
                }
            }
        }
    }

    private void reset() {
        lblResult.setText("0");
        lblEquation.setText("");
        storedNum1 = false;
        storedNum2 = false;
        pressedBinary = false;
        pressedEqual = false;
        pressedUnary = false;
        wrongMath = false;
    }

    @FXML
    public void onEqualButtonClick() {
        if (shouldStoreNum2()) {
            num2 = num1;
            storedNum2 = true;
        }

        if (shouldCalculate()) {
            calculate();

            pressedEqual = true;
            pressedBinary = false;
            pressedUnary = false;
        }
    }

    private boolean shouldCalculate() {
        return storedNum1 && storedNum2;
    }

    private void calculate() {
        num2 = Double.parseDouble(lblResult.getText());
        storedNum2 = true;

        num1 = performBinCalculation();
        if (!wrongMath) {
            if (Math.ceil(num1) != Math.floor(num1)) {
                lblResult.setText(Double.toString(num1));
            }
            else {
                lblResult.setText(Integer.toString((int)num1));
            }
        }

    }

    private double performBinCalculation() {
        double result = 0;

        switch (binaryOperator) {
            case "+" -> result = num1 + num2;
            case "-" -> result = num1 - num2;
            case "*" -> result = num1 * num2;
            case "/" -> {
                if (num2 == 0) {
                    reset();
                    lblResult.setText("Divide by 0");
                    wrongMath = true;
                }
                else {
                    result = num1 / num2;
                }
            }
            case "%" -> {
                if ((Math.ceil(num1) != Math.floor(num1)) || (Math.ceil(num2) != Math.floor(num2))) {
                    reset();
                    lblResult.setText("Not integer");
                    wrongMath = true;
                }
                else {
                    if (num2 == 0) {
                        reset();
                        lblResult.setText("Divide by 0");
                        wrongMath = true;
                    }
                    else {
                        result = num1 % num2;
                    }
                }
            }
        }

        lblEquation.setText(num1 + " " + binaryOperator + " " + num2 + " = ");

        num1 = 0;
        storedNum1 = false;

        num2 = 0;
        storedNum2 = false;

        return  result;
    }

}
