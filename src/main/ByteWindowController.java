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

public class ByteWindowController {
    @FXML private Pane titlePane;
    @FXML private ImageView btnMinimize, btnClose;
    @FXML private Label lblResult, lblEquation;

    private double x, y;
    private boolean pressUnary;
    private int unaryOperator = 1;


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
    void switchToMain(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindowInterface.fxml"));
        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Calculator");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/calculator.png"))));
        ((MainWindowController)loader.getController()).init(stage);
        stage.show();
    }

    @FXML
    void onNumberClicked(MouseEvent event) {

        switch (unaryOperator) {
            case 1 -> lblEquation.setText("bit");
            case 8 -> lblEquation.setText("byte");
            case 8000 -> lblEquation.setText("kbyte");
            case 8000000 -> lblEquation.setText("mbyte");
        }

        String value = ((Pane)event.getSource()).getId().replace("btn", "");
        String outputResultText = lblResult.getText();

        if (Double.parseDouble(outputResultText) == 0 || pressUnary) {
            lblResult.setText(value);
        } else {
            lblResult.setText(outputResultText + value);
        }

        pressUnary = false;
    }

    @FXML
    public void onUnaryButtonClick(MouseEvent event) {

        double result = Double.parseDouble(lblResult.getText());

        String unaryOperator = ((Pane)event.getSource()).getId().replace("btn", "");
        switch (unaryOperator) {
            case "Bit" -> {
                lblEquation.setText("bit");
                result = result * this.unaryOperator;
                this.unaryOperator = 1;
            }
            case "Byte" -> {
                lblEquation.setText("byte");
                result = result / 8 * this.unaryOperator;
                this.unaryOperator = 8;
            }
            case "Kbyte" -> {
                lblEquation.setText("kbyte");
                result = result / (8 * 1024) * this.unaryOperator;
                this.unaryOperator = 8 * 1024;
            }
            case "Mbyte" -> {
                lblEquation.setText("mbyte");
                result = result / (8 * 1024 * 1024) * this.unaryOperator;
                this.unaryOperator = 8 * 1024 * 1024;
            }
        }

        lblResult.setText(Double.toString(result));

        pressUnary = true;
    }

    @FXML
    public void onDeleteButtonClick(MouseEvent event) {
        String deleteOperator = ((Pane)event.getSource()).getId().replace("btn", "");
        switch (deleteOperator) {
            case "Clear" -> reset();
            case "Delete" -> {
                if (Double.parseDouble(lblResult.getText()) != 0) {
                    lblResult.setText(lblResult.getText().substring(0, lblResult.getText().length() - 1));
                }

                if (lblResult.getText().length() <= 0) {
                    lblResult.setText("0");
                }
                pressUnary = false;
            }
        }
    }

    private void reset() {
        lblResult.setText("0");
        lblEquation.setText("");
        pressUnary = false;
    }

}
