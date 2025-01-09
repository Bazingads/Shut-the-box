package morimoto.ethan;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Purpose: a JavaFX version of shut the box
 * @author Ethan Morimoto
 * @date January 8, 2025
 */
public class GUIDriver extends Application {
   int round = 1; 
   int totalScore = 0; 
   int totalSum = 0;  
   int diceSum = 0;   
   Die d1 = new Die();
   Die d2 = new Die();
   boolean rollOneDie = false; 
   
   /**
    * Starts the JavaFX application
    * @param stage The primary stage for this application
    */
   @Override
   public void start(Stage stage) {
       HBox tileBox = new HBox(10);
       Button[] tileBtns = new Button[9];
       boolean[] tileStatus = new boolean[9]; // if tile is up (all false by default)
       for (int i = 0; i < tileBtns.length; i++) {
           tileBtns[i] = new Button(String.valueOf(i + 1));
           tileBtns[i].setDisable(true); // Initially disable all tile buttons
           tileBtns[i].setStyle("-fx-background-color: #e4e4e4;");
           tileBox.getChildren().add(tileBtns[i]);
       }
       tileBox.setAlignment(Pos.CENTER);
       VBox vbox = new VBox(10);
       HBox menuBox = new HBox(20);
       Label title = new Label("Shut The Box - Round " + round);
       Button btnRollDouble = new Button("ROLL 2 DICE");
       Button btnRollOne = new Button("ROLL 1 DIE");
       btnRollOne.setDisable(true);
       Button btnLock = new Button("END TURN");
       btnLock.setDisable(true);
       Button btnGiveUp = new Button("GIVE UP");
       Label lblResult = new Label("Dice Result: 0");
       Label lblValue = new Label("Total Sum: 0");
       Label lblScore = new Label();
       vbox.getChildren().addAll(title, tileBox, menuBox, lblResult, lblValue, lblScore);
       menuBox.getChildren().addAll(btnRollDouble, btnRollOne, btnLock, btnGiveUp);
       vbox.setAlignment(Pos.CENTER);
       menuBox.setAlignment(Pos.CENTER);
       Scene scene = new Scene(vbox, 500, 300);
       stage.setScene(scene);
       stage.setTitle("Shut The Box");
       stage.show();
       
       // when clicking on ROLL 2 DICE
       btnRollDouble.setOnAction(e -> {
           diceSum = d1.roll() + d2.roll();
           lblResult.setText("Dice Result: " + diceSum);
           btnRollDouble.setDisable(true); // Disable rolling two dice
           btnLock.setDisable(false);     // Enable the lock button
           enableTileButtons(tileBtns, tileStatus); // Enable tile buttons after rolling
           // Recheck the state for rolling one die
           checkOneDieOption(tileStatus, btnRollOne);
           // If rolling two dice, explicitly disable rolling one die
           btnRollOne.setDisable(true);
       });

       // Rolling 1 die
       btnRollOne.setOnAction(e -> {
           diceSum = d1.roll();
           lblResult.setText("Dice Result: " + diceSum);
           btnRollDouble.setDisable(true);
           btnRollOne.setDisable(true);
           btnLock.setDisable(false);
           enableTileButtons(tileBtns, tileStatus); // Enable tile buttons after rolling
       });

       // Tile button behavior
       for (int i = 0; i < tileBtns.length; i++) {
           Button btn = tileBtns[i];
           btn.setOnAction(e -> {
               if (!btn.getStyle().contains("#a1e44c")) {
                   // Set background color to green and add to totalSum
                   btn.setStyle("-fx-background-color: #a1e44c;");
                   totalSum += Integer.parseInt(btn.getText());
               } else {
                   // Revert background color and subtract from totalSum
                   btn.setStyle("-fx-background-color: #e4e4e4;");
                   totalSum -= Integer.parseInt(btn.getText());
               }
               lblValue.setText("Total Sum: " + totalSum);
           });
       }

       // Lock button behavior
       btnLock.setOnAction(e -> {
           if (totalSum == diceSum) {
               // Successful turn
               shutTiles(tileBtns, tileStatus);
               checkOneDieOption(tileStatus, btnRollOne); // Check if rolling 1 die is now allowed
               // Reset for the next turn
               totalSum = 0;
               lblValue.setText("Total Sum: 0");
               btnRollDouble.setDisable(false);
               btnRollOne.setDisable(!rollOneDie);
               btnLock.setDisable(true);
               disableTileButtons(tileBtns); // Disable tile buttons at the end of the turn
               if (isGameOver(tileStatus)) {
                   endRound(lblScore, lblResult, title, tileBtns, tileStatus, btnRollDouble, btnRollOne, btnLock, btnGiveUp);
               }
           } else {
               // Failed turn: Retry with the same dice result
               // Re-enable all tiles that are not shut
               for (int i = 0; i < tileBtns.length; i++) {
                   if (!tileStatus[i]) {
                       tileBtns[i].setDisable(false);
                       tileBtns[i].setStyle("-fx-background-color: #e4e4e4;");
                   }
               }
               // Reset totalSum for retry
               totalSum = 0;
               lblValue.setText("Total Sum: 0");
               // Ensure dice cannot be rerolled, lock button remains active
               btnRollDouble.setDisable(true);
               btnRollOne.setDisable(true);
               btnLock.setDisable(false);
           }
       });

       // Give Up button behavior
       btnGiveUp.setOnAction(e -> {
           int score = calculateRemainingScore(tileBtns, tileStatus);
           totalScore += score;
           lblScore.setText("You gave up! Your final score: " + score);
           totalSum = 0; // Reset totalSum when giving up
           lblValue.setText("Total Sum: 0");
           disableTileButtons(tileBtns); // Disable tile buttons when giving up
           endRound(lblScore, lblResult, title, tileBtns, tileStatus, btnRollDouble, btnRollOne, btnLock, btnGiveUp);
       });
   }
   /**
    * Enables tile buttons if they haven't been shut
    * @param tileBtns Tile buttons
    * @param tileStatus Status of tiles
    */
   public void enableTileButtons(Button[] tileBtns, boolean[] tileStatus) {
       for (int i = 0; i < tileBtns.length; i++) {
           if (!tileStatus[i]) {
               tileBtns[i].setDisable(false);
           }
       }
   }
   /** 
    * Disables all tiles buttons
    * @param tileBtns Tile buttons
    */
   public void disableTileButtons(Button[] tileBtns) {
       for (Button btn : tileBtns) {
           btn.setDisable(true);
       }
   }
   /**
    * Sets up for next round or ends game
    * @param lblScore Score Label
    * @param lblResult Result Label
    * @param title Tile
    * @param tileBtns Tile buttons
    * @param tileStatus Status of tiles
    * @param btnRollDouble Roll 2 dice button
    * @param btnRollOne Roll 1 die button
    * @param btnLock End turn button
    * @param btnGiveUp Give up button
    */
   public void endRound(Label lblScore, Label lblResult, Label title, Button[] tileBtns, boolean[] tileStatus,
                         Button btnRollDouble, Button btnRollOne, Button btnLock, Button btnGiveUp) {
       if (round < 5) {
           round++;
           title.setText("Shut The Box - Round " + round);
           lblResult.setText("Dice Result: 0");
           lblScore.setText("Total Score: " + totalScore);
           for (int i = 0; i < tileBtns.length; i++) {
               tileBtns[i].setDisable(true); // Ensure tile buttons are disabled at the start of a new round
               tileBtns[i].setStyle("-fx-background-color: #e4e4e4;");
               tileStatus[i] = false;
           }
           btnRollDouble.setDisable(false);
           btnRollOne.setDisable(true);
           btnLock.setDisable(true);
       } else {
           lblScore.setText("Game Over! Final Total Score: " + totalScore);
           lblResult.setText("Game Over! Thank you for playing.");
           btnRollDouble.setDisable(true);
           btnRollOne.setDisable(true);
           btnLock.setDisable(true);
           btnGiveUp.setDisable(true);
           for (Button btn : tileBtns) {
               btn.setDisable(true);
           }
       }
   }
   /** 
    * Method to shut tiles after successful turn
    * @param tileBtns Tile Buttons
    * @param tileStatus Status of tiles
    */
   public void shutTiles(Button[] tileBtns, boolean[] tileStatus) {
    for (int i = 0; i < tileBtns.length; i++) {
        if (tileBtns[i].getStyle().contains("#a1e44c")) {
            tileBtns[i].setDisable(true);
            tileStatus[i] = true;
        	}
    	}
   }
   /**
    * Check if tile 7, 8 and 9 are down allowing the option to roll 1 die
    * @param tileStatus Status of Tiles
    * @param btnRollOne Button to roll 1 die
    */
   public void checkOneDieOption(boolean[] tileStatus, Button btnRollOne) {
       // Check directly if tiles 7, 8, and 9 are shut
       if (tileStatus[6] && tileStatus[7] && tileStatus[8]) { // Indexes for tiles 7, 8, and 9
           rollOneDie = true;
           btnRollOne.setDisable(false);
       } else {
           rollOneDie = false;
           btnRollOne.setDisable(true);
       }
   }
   /**
    * Calculate the remaining score of tiles still up after giving up
    * @param tileBtns Tile of buttons
    * @param tileStatus Status of tiles
    * @return score Total score
    */
   public int calculateRemainingScore(Button[] tileBtns, boolean[] tileStatus) {
       int score = 0;
       for (int i = 0; i < tileBtns.length; i++) {
           if (!tileStatus[i]) {
               score += Integer.parseInt(tileBtns[i].getText());
           }
       }
       return score;
   }
  /**
   * Checks if all tiles are shut
   * @param tileStatus Status of tiles
   * @return false - not all are shut | true - all are shut
   */
   public boolean isGameOver(boolean[] tileStatus) {
       for (boolean status : tileStatus) {
           if (!status) {
               return false; // At least one tile is not shut
           }
       }
       return true; // All tiles are shut
   }
   
   public static void main(String[] args) {
       launch(args);
   }
}