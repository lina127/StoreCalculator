
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * Calculator GUI calculates value base on user input. Auto store value and can retrieve the value anytime.
 * Five operators (+, -, x, /, ^), log, ln, factorial, square, root, pi and percentage buttons are available.
 * @author Yehyun Kim
 */
public class CalculatorGUI extends Application {
    //Instance Variables for View Components and Model
    /** Store each digit on screen **/
    private ArrayList<String> screenDisplayArrayList = new ArrayList<>();
    /** Store initial value **/
    private double resultOnHold;
    /** Selected operator **/
    private Button selectedOperator;
    /** [True: operation button pressed] [False: not pressed] **/
    private boolean isOperationButtonPressed;
    /** [True: it is first value entered] [False: it is not first value entered] **/
    private boolean isInit = true;

    /** Screen display **/
    private Label screenLabel;
    /** Buttons for calculator**/
    private Button b0,b1,b2,b3,b4,b5,b6,b7,b8,b9,decimalButton,equalButton,divideButton, multiplyButton, plusButton,minusButton,percentButton,exponentButton,piButton,squareButton,rootButton,factorialButton,lnButton,logButton, deleteButton,acButton;
    /** Labels for calculator**/
    private Label storedValueLabel, errorLabel;
    /** Five buttons to store value **/
    private Button storedValueButton1,storedValueButton2, storedValueButton3,storedValueButton4,storedValueButton5;

    /**
     * Methods and private Event handlers
     **/

    String pressedButtonCss ="-fx-font-family:Arial;-fx-font-size:11pt;-fx-text-fill:white;-fx-background-color:salmon;-fx-background-radius:15; -fx-pref-width:45; -fx-pref-height:45";

    String operationButtonCss = "-fx-font-family:Arial;-fx-font-size:11pt;-fx-text-fill:white;-fx-background-color:#D19500;-fx-background-radius:15;-fx-pref-width:45; -fx-pref-height:45";

    /**
     * @param columnIndex index of column
     * @return x position of each button
     */
    private double getXPosition(double columnIndex){ //lineNum starts from 0
        return (90 + (columnIndex * 55));
    }

    /**
     * @param rowIndex index of row
     * @return y position of each button
     */
    private double getYPosition(int rowIndex){ //rowNum starts form 0
        return (199 + (rowIndex * 55));
    }

    /**
     * Draw digit on the screen
     * @param value number value to display on the screen
     */
    private void drawDigit(String value){
        //if operation button is pressed, resets screen display
        if(isOperationButtonPressed) {
            isOperationButtonPressed = false; // set the previous pressed operation button to default button style
            defaultButtonStyle(selectedOperator);
            screenLabel.setText("");
            screenDisplayArrayList.clear();
        }

        //if screen display is equal to latest stored value or math error occurs, resets screen display
        if(screenLabel.getText().equals(storedValueButton1.getText()) || screenLabel.getText().equalsIgnoreCase("Math Error")){
            screenLabel.setText("");
            screenDisplayArrayList.clear();
        }
        int maxLength = screenLabel.getText().contains(".") ? 14 : 13; // if the value contains decimal,  max length is 14. If not, 13

        //display and store digit if screen doesn't exceed the max length
        if (screenLabel.getText().length() < maxLength){
            screenLabel.setText(screenLabel.getText() + value);
            screenDisplayArrayList.add(value);
        }

        errorLabel.setText(""); //reset error label
        deleteButton.setDisable(false); //enable deleteButton
        functionButtonControl(false); // enable other buttons

    }

    /**
     * Store initial value in valueOnHold arrayList, set button style, calculate if needed
     * @param operator selected operator
     */
    private void selectedOperatorSetup(Button operator){
        //if user presses two or more operator buttons continuously, resets all buttons except the last pressed button
        if (isOperationButtonPressed) {
            defaultButtonStyle(selectedOperator);
        }
        try {
            deleteButton.setDisable(false);
            screenDisplayArrayList.clear(); //clear current screen display array
            // if the calculation is not the first calculation
            if(resultOnHold != 0 && !isInit){
                double currentValue = Double.parseDouble(screenLabel.getText());
                double result = calculateValue(resultOnHold, currentValue, selectedOperator);
                String resultString = formatNumbersToString(result);
                screenLabel.setText(resultString);
            }else{ //if entered value is the initial value, returns the same value: nothing to calculate
                double currentValue = Double.parseDouble(screenLabel.getText());
                double result = calculateValue(currentValue, resultOnHold, operator);
                String resultString = formatNumbersToString(result);
                screenLabel.setText(resultString);
            }
            this.selectedOperator = operator;
            isOperationButtonPressed = true;
            pressedButtonStyle(selectedOperator);
            errorLabel.setText("");
            isInit = false;
        } catch (IllegalArgumentException exception){
            errorLabel.setText(exception.getMessage());
            screenLabel.setText("Math Error");
        } catch (NullPointerException exception){
            errorLabel.setText("Enter value");
            screenLabel.setText("Math Error");
        }

    }

    /**
     * Style pressed button differently
     * @param button selected button
     */
    private void pressedButtonStyle(Button button){
        button.setStyle(pressedButtonCss);
    }

    /**
     * Style unpressed button to default style
     * @param button unpressed button
     */
    private void defaultButtonStyle(Button button){
        button.setStyle(operationButtonCss);
    }

    /**
     * Update stored value
     * @param value new value
     */
    private void storeValue(String value){
        storedValueButton5.setText(storedValueButton4.getText());
        storedValueButton4.setText(storedValueButton3.getText());
        storedValueButton3.setText(storedValueButton2.getText());
        storedValueButton2.setText(storedValueButton1.getText());
        storedValueButton1.setText(value);
    }

    /**
     * Format value to user friendly string and apply max and min value to display
     * @param value entered or calculated value
     * @return value to user friendly string
     */
    private String formatNumbersToString(double value){
        String output;
        String valueToString = String.valueOf(value);
        // if the string is too long
        if ( valueToString.length() > 14){
            if(value > 99999999999999.0){           //throws meaningful message if value is too big
                throw new IllegalArgumentException("Value too big");
            }else if (value < -99999999999999.0){   // throws meaningful message if value is too small
                throw new IllegalArgumentException("Value too small");
            } else{                                 // round value to 9-digit after decimal
                output = String.format("%.9f", value);
            }
            return FormatPrintString(String.valueOf(output));
        } // if appropriate length, display it
        else{
            return FormatPrintString(String.valueOf(value));
        }
    }

    /**
     * Calculates value
     * @param num1 stored value
     * @param num2 current value
     * @param operationBtn
     * @return calculated value
     */
    private double calculateValue(double num1, double num2, Button operationBtn){
        deleteButton.setDisable(true); //disable delete button: only need AC button
        double calculatedValue;
        //process if user entered number value(=current value)
        if(!isOperationButtonPressed) {
            if (operationBtn == plusButton) {           //plus button
                calculatedValue = num1 + num2;
            } else if (operationBtn == minusButton) {   //minus button
                calculatedValue = num1 - num2;
            } else if (operationBtn == divideButton) {  //divide button
                if (num2 == 0 && !isInit) { //if the value need to be divided by 0, throw exception
                    throw new IllegalArgumentException("Cannot divide by 0");
                } else if (isInit) { // if it's the first value entered, return the same value
                    calculatedValue = num1;
                } else {
                    calculatedValue = num1 / num2;
                }
            } else if (operationBtn == multiplyButton) { //multiply button
                if (isInit) {
                    calculatedValue = num1;
                } else {
                    calculatedValue = num1 * num2;
                }
            } else {                                     //exponent button
                if(isInit){
                    calculatedValue = num1;
                }else {
                    calculatedValue = Math.pow(num1, num2);
                }
            }
        }
        else{
            calculatedValue = num1;
        }
        resultOnHold = calculatedValue;
        return calculatedValue;
    }

    /**
     * Disable buttons
     * @param isDisable [true: disable] [false: enable]
     */
    private void functionButtonControl(Boolean isDisable){
        exponentButton.setDisable(isDisable);
        plusButton.setDisable(isDisable);
        minusButton.setDisable(isDisable);
        divideButton.setDisable(isDisable);
        multiplyButton.setDisable(isDisable);
        rootButton.setDisable(isDisable);
        factorialButton.setDisable(isDisable);
        piButton.setDisable(isDisable);
        percentButton.setDisable(isDisable);
        lnButton.setDisable(isDisable);
        logButton.setDisable(isDisable);
        deleteButton.setDisable(isDisable);
        squareButton.setDisable(isDisable);
    }

    /**
     * Retrieve stored value
     * @param storedValue selected store value button
     */
    private void storedValueRetrieve(Button storedValue){
        if(!storedValue.getText().equals("-")){
            screenLabel.setText(storedValue.getText()); //updates screen display
            isOperationButtonPressed = false;
            defaultButtonStyle(selectedOperator);
        }
        if(this.selectedOperator == exponentButton)
            functionButtonControl(false);
    }

    /**
     * 0- 10 & decimal ButtonHandlers
     * @param e ActionEvent
     */
    private void digitButtonHandler(ActionEvent e){
        String buttonText = ((Button) e.getSource()).getText();
        switch (buttonText) {
            case "0" -> drawDigit("0");
            case "1" -> drawDigit("1");
            case "2" -> drawDigit("2");
            case "3" -> drawDigit("3");
            case "4" -> drawDigit("4");
            case "5" -> drawDigit("5");
            case "6" -> drawDigit("6");
            case "7" -> drawDigit("7");
            case "8" -> drawDigit("8");
            case "9" -> drawDigit("9");
            case "." -> drawDigit(".");
        }
    }

    /**
     * Update screen display to appropriate value and catch error if needed
     * @param e ActionEvent
     */
    private void equalButtonHandler(ActionEvent e){
        try {
            double currentValue = Double.parseDouble(screenLabel.getText());
            // process if user presses the button without pressing operation button and it's the first value
            if(!isOperationButtonPressed && isInit){
                screenLabel.setText(currentValue + "");
            }
            // process if there are values to be calculated
            else {
                double result = calculateValue(resultOnHold, currentValue, selectedOperator);
                String resultString = formatNumbersToString(result);
                screenLabel.setText(resultString);
                defaultButtonStyle(selectedOperator);
            }
            storeValue(screenLabel.getText());  //store the value
            isOperationButtonPressed = false;
            screenDisplayArrayList.clear();     //clear the screen
            isInit = true;                      //set initial value to true for next calculation
            resultOnHold = 0;                   //reset result on hole


        }catch(NumberFormatException exception){
            errorLabel.setText("Too many decimals");
            screenLabel.setText("Math Error");
        }catch(IllegalArgumentException exception){
            screenLabel.setText("Math Error");
            errorLabel.setText(exception.getMessage());
        }catch(IndexOutOfBoundsException exception){
            screenLabel.setText(screenLabel.getText()); //no change on the screen display
        }
    }

    /**
     * Set up plus button
     * @param e ActionEvent
     */
    private void plusButtonHandler(ActionEvent e){
        selectedOperatorSetup(plusButton);
    }

    /**
     * Set up minus button
     * @param e ActionEvent
     */
    private void minusButtonHandler(ActionEvent e){
        selectedOperatorSetup(minusButton);
    }

    /**
     * Set up divide button
     * @param e ActionEvent
     */
    private void divideButtonHandler(ActionEvent e){
        selectedOperatorSetup(divideButton);
    }

    /**
     * Set up multiply button
     * @param e ActionEvent
     */
    private void multiplyButtonHandler(ActionEvent e){
        selectedOperatorSetup(multiplyButton);
    }

    /**
     * Remove the latest value in screen display and update the screen
     * @param e ActionEvent
     */
    private void deleteButtonHandler(ActionEvent e){
        try {
            screenDisplayArrayList.remove(screenDisplayArrayList.size() - 1); // delete the last value
            java.lang.StringBuilder screenDisplay = new java.lang.StringBuilder();  // stack value in arrayList
            //update screen
            for (String s : screenDisplayArrayList)
                screenDisplay.append(s);
            screenLabel.setText(screenDisplay.toString());
        }catch(IndexOutOfBoundsException exception){
            errorLabel.setText("Nothing to delete");
        }
    }

    /**
     * Remove all the elements on the screen display
     * @param e ActionEvent
     */
    private void acButtonHandler(ActionEvent e){
        screenDisplayArrayList.clear();
        resultOnHold = 0;
        isOperationButtonPressed = false;
        if(selectedOperator != null) //if click ac button before clicking operator button
            defaultButtonStyle(selectedOperator);
        screenLabel.setText("");
        errorLabel.setText("");
        deleteButton.setDisable(true);
        isInit = true;

    }

    /**
     * Update current value in percentage (divide by 100)
     * @param e ActionEvent
     */
    private void percentButtonHandler(ActionEvent e){
        try {
            if(!isOperationButtonPressed) {
                double toPercentage = Double.parseDouble(screenLabel.getText()) / 100;
                screenLabel.setText(formatNumbersToString(toPercentage));
                deleteButton.setDisable(true);
            }
            else{
                throw new IllegalArgumentException("Operator button is pressed. Enter value.");
            }
        }catch(NumberFormatException exception){
            errorLabel.setText("Enter value");
        }catch(IllegalArgumentException exception){
            errorLabel.setText(exception.getMessage());
        }
    }

    /**
     * Update screen with ln of the current value
     * @param e ActionEvent
     */
    private void lnButtonHandler(ActionEvent e){
        try {
            if(!isOperationButtonPressed) {
                double toLn = Math.log(Double.parseDouble(screenLabel.getText())); //convert to double and calculate ln value
                screenLabel.setText(formatNumbersToString(toLn));
                deleteButton.setDisable(true);
            }
            else{
                throw new IllegalArgumentException("Operator button is pressed. Enter value.");
            }
        }catch(NumberFormatException exception){
            errorLabel.setText("Enter value");
        }catch(IllegalArgumentException exception){
            errorLabel.setText(exception.getMessage());
        }
    }

    /**
     * Update screen with log of the current value
     * @param e ActionEvent
     */
    private void logButtonHandler(ActionEvent e){
        try {
            if(!isOperationButtonPressed) {
                deleteButton.setDisable(true);
                double toLog = Math.log10(Double.parseDouble(screenLabel.getText()));
                screenLabel.setText(formatNumbersToString(toLog));
            }
            else{
                throw new IllegalArgumentException("Operator button is pressed. Enter value.");
            }
        }catch(NumberFormatException exception){
            errorLabel.setText("Enter value");
        }catch(IllegalArgumentException exception){
            errorLabel.setText(exception.getMessage());
        }
    }

    /**
     * Update screen with pi value(3.14...)
     * @param e ActionEvent
     */
    private void piButtonHandler(ActionEvent e){
        isOperationButtonPressed = false;
        screenLabel.setText(formatNumbersToString(Math.PI));
        defaultButtonStyle(selectedOperator);
    }

    /**
     * Update screen with factorial of the current value
     * @param e ActionEvent
     */
    private void factorialButtonHandler(ActionEvent e){
        try {
            if(!isOperationButtonPressed) {
                deleteButton.setDisable(true);
                long value = Integer.parseInt(screenLabel.getText());
                if (value > 15){
                    throw new IllegalArgumentException("Value too big");
                }
                long toFactorial = 1;
                for(int i = 1; i <= value; i++) {
                    toFactorial *= i;
                }
                screenLabel.setText(formatNumbersToString(toFactorial));
            }
            else{
                throw new IllegalArgumentException("Operator button is pressed. Enter value.");
            }
        }catch(NumberFormatException exception){
            errorLabel.setText("Enter int value to use factorial function");
            screenLabel.setText("");
        }catch(IllegalArgumentException exception){
            errorLabel.setText(exception.getMessage());
        }
    }

    /**
     * Update screen with root of the current value
     * @param e ActionEvent
     */
    private void rootButtonHandler(ActionEvent e){
        try {
            if(!isOperationButtonPressed) {
                deleteButton.setDisable(true);
                double value =Integer.parseInt(screenLabel.getText());
                double toRoot = Math.sqrt(value);
                screenLabel.setText(formatNumbersToString(toRoot));
            }
            else{
                throw new IllegalArgumentException("Operator button is pressed. Enter value.");
            }
        }catch(NumberFormatException exception){
            errorLabel.setText("Enter int value");
            screenLabel.setText("");
        }catch(IllegalArgumentException exception){
            errorLabel.setText(exception.getMessage());
        }
    }

    /**
     * Set up exponent value and disable other operator buttons for user to enter number value only
     * @param e ActionEvent
     */
    private void exponentButtonHandler(ActionEvent e){
        functionButtonControl(true);
        selectedOperatorSetup(exponentButton);
    }

    /**
     * Update screen with square of the current value
     * @param e  ActionEvent
     */
    private void squareButtonHandler(ActionEvent e){
        try {
            if(!isOperationButtonPressed) {
                deleteButton.setDisable(true);
                Double value = Double.parseDouble(screenLabel.getText());
                double toSquare = value * value;
                screenLabel.setText(formatNumbersToString(toSquare));
            }
            else{
                throw new IllegalArgumentException("Operator button is pressed. Enter value.");
            }
        }catch(NumberFormatException exception){
            errorLabel.setText("Enter value");
        }catch(IllegalArgumentException exception){
            errorLabel.setText(exception.getMessage());
        }
    }

    /**
     * First stored value
     * @param e ActionEvent
     */
    private void value1ButtonHandler(ActionEvent e){
        storedValueRetrieve(storedValueButton1);
    }

    /**
     * Second stored value
     * @param e ActionEvent
     */
    private void value2ButtonHandler(ActionEvent e){
        storedValueRetrieve(storedValueButton2);
    }

    /**
     * Third stored value
     * @param e ActionEvent
     */
    private void value3ButtonHandler(ActionEvent e){
        storedValueRetrieve(storedValueButton3);
    }

    /**
     * Fourth stored value
     * @param e ActionEvent
     */
    private void value4ButtonHandler(ActionEvent e){
        storedValueRetrieve(storedValueButton4);
    }

    /**
     * Fifth stored value
     * @param e ActionEvent
     */
    private void value5ButtonHandler(ActionEvent e){
        storedValueRetrieve(storedValueButton5);
    }

    /**
     * @param stage The main stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, 370, 550);
        stage.setTitle("Calculator"); // set the window title here
        stage.setScene(scene);
        Canvas canvas = new Canvas(370, 550);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,370,550);

        // Create the GUI components

        errorLabel = new Label();
        screenLabel = new Label();

        b0 = new Button("0");
        b1 = new Button("1");
        b2 = new Button("2");
        b3 = new Button("3");
        b4 = new Button("4");
        b5 = new Button("5");
        b6 = new Button("6");
        b7 = new Button("7");
        b8 = new Button("8");
        b9 = new Button("9");
        decimalButton = new Button(".");
        equalButton = new Button("=");
        divideButton = new Button("➗");
        multiplyButton = new Button("✖");
        plusButton = new Button("+");
        minusButton = new Button("-");
        percentButton = new Button("%");
        exponentButton = new Button("^");
        piButton = new Button("\uD835\uDED1");
        squareButton = new Button("x²");
        rootButton = new Button("√");
        factorialButton = new Button("n!");
        lnButton = new Button("ln");
        logButton = new Button("log");
        deleteButton = new Button("⌫");
        acButton = new Button("AC");

        storedValueLabel = new Label("Stored");
        storedValueButton1 = new Button("-");
        storedValueButton2 = new Button("-");
        storedValueButton3 = new Button("-");
        storedValueButton4 = new Button("-");
        storedValueButton5 = new Button("-");

        // Add components to the root
        root.getChildren().addAll(canvas,screenLabel,b0,b1,b2,b3,b4,b5,b6,b7,b8,b9,decimalButton,equalButton,divideButton,multiplyButton,plusButton,minusButton,percentButton,exponentButton,piButton,squareButton,rootButton,factorialButton,lnButton,logButton,deleteButton,acButton,storedValueLabel, storedValueButton1, storedValueButton2, storedValueButton3, storedValueButton4, storedValueButton5, errorLabel);

        // Configure the components (colors, fonts, size, location)
        String defaultNumButtonCss = "-fx-font-family:Arial;-fx-font-size:16pt;-fx-text-fill:white;-fx-background-color:#4C2F09;-fx-background-radius:15;-fx-pref-width:45; -fx-pref-height:45";

        String wideNumButtonCss = "-fx-font-family:Arial;-fx-font-size:16pt;-fx-text-fill:white;-fx-background-color:#4C2F09;-fx-background-radius:15;-fx-pref-width:104; -fx-pref-height:45";

        String deleteButtonCss = "-fx-font-family:Arial;-fx-font-size:11pt;-fx-text-fill:white;-fx-background-color:#A02C20;-fx-background-radius:15; -fx-pref-width:45; -fx-pref-height:45";

        String midOperatorButtonCss = "-fx-font-family:Arial;-fx-font-size:14pt;-fx-text-fill:white;-fx-background-color:#272727;-fx-background-radius:15;-fx-pref-width:70; -fx-pref-height:45";

        String defaultButtonCss = "-fx-font-family:Arial;-fx-font-size:16pt;-fx-text-fill:white;-fx-background-color:#272727;-fx-background-radius:15; -fx-pref-width:45; -fx-pref-height:45";

        String smallFontButtonCss = "-fx-font-family:Arial;-fx-font-size:11pt;-fx-text-fill:white;-fx-background-color:#272727;-fx-background-radius:15;-fx-pref-width:43; -fx-pref-height:45";

        String labelCss= "-fx-font-family:Arial;-fx-font-size:11pt;-fx-text-fill:white;-fx-background-radius:15;-fx-border-radius:15;";

        String storeButtonCss = "-fx-font-family:Arial;-fx-font-size:10pt;-fx-text-fill:white;-fx-background-color:#322515;-fx-background-radius:15; -fx-pref-width:70; -fx-pref-height:20";

        //error label set up
        errorLabel.setStyle(labelCss);
        errorLabel.relocate(2,2);
        errorLabel.setPrefWidth(370);
        errorLabel.prefWidth(370);
        errorLabel.setAlignment(Pos.CENTER);

        //screen label set up
        screenLabel.setStyle("-fx-font-family:Arial;-fx-font-size:26pt;-fx-text-fill:white;-fx-pref-width:330; -fx-pref-height:200");
        screenLabel.setAlignment(Pos.CENTER_RIGHT);
        screenLabel.relocate(10, 10);

        //1st row
        percentButton.setStyle(midOperatorButtonCss);
        percentButton.relocate(getXPosition(0), getYPosition(0));
        lnButton.setStyle(midOperatorButtonCss);
        lnButton.relocate(getXPosition(1.5), getYPosition(0));
        deleteButton.setStyle(deleteButtonCss);
        deleteButton.relocate(getXPosition(3), getYPosition(0));
        acButton.setStyle(deleteButtonCss);
        acButton.relocate(getXPosition(4), getYPosition(0));

        //2nd row
        piButton.setStyle(midOperatorButtonCss);
        piButton.relocate(getXPosition(0), getYPosition(1));
        logButton.setStyle(midOperatorButtonCss);
        logButton.relocate(getXPosition(1.5), getYPosition(1));
        factorialButton.setStyle(smallFontButtonCss);
        factorialButton.relocate(getXPosition(3), getYPosition(1));
        rootButton.setStyle(defaultButtonCss);
        rootButton.relocate(getXPosition(4), getYPosition(1));

        //3rd row
        b7.setStyle(defaultNumButtonCss);
        b7.relocate(getXPosition(0), getYPosition(2));
        b8.setStyle(defaultNumButtonCss);
        b8.relocate(getXPosition(1), getYPosition(2));
        b9.setStyle(defaultNumButtonCss);
        b9.relocate(getXPosition(2), getYPosition(2));
        exponentButton.setStyle(defaultButtonCss);
        exponentButton.relocate(getXPosition(3), getYPosition(2));
        squareButton.setStyle(smallFontButtonCss);
        squareButton.relocate(getXPosition(4), getYPosition(2));

        //4th row
        b4.setStyle(defaultNumButtonCss);
        b4.relocate(getXPosition(0), getYPosition(3));
        b5.setStyle(defaultNumButtonCss);
        b5.relocate(getXPosition(1), getYPosition(3));
        b6.setStyle(defaultNumButtonCss);
        b6.relocate(getXPosition(2), getYPosition(3));
        multiplyButton.setStyle(operationButtonCss);
        multiplyButton.relocate(getXPosition(3), getYPosition(3));
        minusButton.setStyle(operationButtonCss);
        minusButton.relocate(getXPosition(4), getYPosition(3));

        //5th row
        b1.setStyle(defaultNumButtonCss);
        b1.relocate(getXPosition(0), getYPosition(4));
        b2.setStyle(defaultNumButtonCss);
        b2.relocate(getXPosition(1), getYPosition(4));
        b3.setStyle(defaultNumButtonCss);
        b3.relocate(getXPosition(2), getYPosition(4));
        divideButton.setStyle(operationButtonCss);
        divideButton.relocate(getXPosition(3), getYPosition(4));
        plusButton.setStyle(operationButtonCss);
        plusButton.relocate(getXPosition(4), getYPosition(4));

        //6th row
        b0.setStyle(wideNumButtonCss);
        b0.relocate(getXPosition(0), getYPosition(5));
        equalButton.setStyle("-fx-font-family:Arial;-fx-font-size:16pt;-fx-text-fill:white;-fx-background-color:#272727;-fx-background-radius:15;-fx-pref-width:104; -fx-pref-height:45");
        equalButton.relocate(getXPosition(3), getYPosition(5));
        decimalButton.setStyle(defaultNumButtonCss);
        decimalButton.relocate(getXPosition(2), getYPosition(5));

        //Store area styling
        storedValueLabel.setStyle(labelCss);
        storedValueLabel.setPrefWidth(85);
        storedValueLabel.relocate(3,200);
        storedValueLabel.setAlignment(Pos.CENTER);

        storedValueButton1.setStyle(storeButtonCss);
        storedValueButton1.relocate(10, 250);
        storedValueButton2.setStyle(storeButtonCss);
        storedValueButton2.relocate(10, 300);
        storedValueButton3.setStyle(storeButtonCss);
        storedValueButton3.relocate(10, 350);
        storedValueButton4.setStyle(storeButtonCss);
        storedValueButton4.relocate(10, 400);
        storedValueButton5.setStyle(storeButtonCss);
        storedValueButton5.relocate(10, 450);

        //Add Event Handlers and do final setup
        //number & decimal button handlers
        b0.setOnAction (this::digitButtonHandler);
        b1.setOnAction(this::digitButtonHandler);
        b2.setOnAction(this::digitButtonHandler);
        b3.setOnAction(this::digitButtonHandler);
        b4.setOnAction(this::digitButtonHandler);
        b5.setOnAction(this::digitButtonHandler);
        b6.setOnAction(this::digitButtonHandler);
        b7.setOnAction(this::digitButtonHandler);
        b8.setOnAction(this::digitButtonHandler);
        b9.setOnAction(this::digitButtonHandler);
        decimalButton.setOnAction(this::digitButtonHandler);

        //delete & AC button handlers
        deleteButton.setOnAction(this::deleteButtonHandler);
        acButton.setOnAction(this::acButtonHandler);

        //five operator button handlers (+,/,*,-,^)
        plusButton.setOnAction(this::plusButtonHandler);
        minusButton.setOnAction(this::minusButtonHandler);
        multiplyButton.setOnAction(this::multiplyButtonHandler);
        divideButton.setOnAction(this::divideButtonHandler);
        exponentButton.setOnAction(this::exponentButtonHandler);

        //equal button handler
        equalButton.setOnAction(this::equalButtonHandler);

        //Other operator button handlers
        percentButton.setOnAction(this::percentButtonHandler);
        lnButton.setOnAction(this::lnButtonHandler);
        logButton.setOnAction(this::logButtonHandler);
        piButton.setOnAction(this::piButtonHandler);
        factorialButton.setOnAction(this::factorialButtonHandler);
        rootButton.setOnAction(this::rootButtonHandler);
        squareButton.setOnAction(this::squareButtonHandler);
        storedValueButton1.setOnAction(this::value1ButtonHandler);
        storedValueButton2.setOnAction(this::value2ButtonHandler);
        storedValueButton3.setOnAction(this::value3ButtonHandler);
        storedValueButton4.setOnAction(this::value4ButtonHandler);
        storedValueButton5.setOnAction(this::value5ButtonHandler);

        //Show the stage
        stage.show();
    }

    /**
     * Make no changes here.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Remove digits after decimal if its all 0 (ex: 5.0 to 5)
     * @param string number value in String
     * @return string with applied format
     */
    public String FormatPrintString(String string){
        return string.replaceAll("\\.0*$", "");
    }
}
