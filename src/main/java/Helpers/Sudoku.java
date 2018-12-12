package Helpers;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Sudoku {

    // Board
    ArrayList<ArrayList<ArrayList<ArrayList<Char>>>> originalBoard;
    ArrayList<ArrayList<ArrayList<ArrayList<Char>>>> SudokuBoard = new ArrayList<>();

    // This value is updated everytime the user selects a TextField
    Input selectedTextField;


    // Empty values per difficulty level
    int[] levels = {1, 20, 40};

    Random rand = new Random();
    ArrayList<Character> valueScope = new ArrayList<>();

    public Sudoku(int difficulty) {
        Collections.addAll(valueScope, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F');

        int totalToHide = levels[difficulty];

        for (int i = 0; i < 4; i++) {
            ArrayList<ArrayList<ArrayList<Char>>> outerRow = new ArrayList<>(4);
            for (int j = 0; j < 4; j++) {
                ArrayList<ArrayList<Char>> rowColumn = new ArrayList<>(4);
                for (int l = 0; l < 4; l++) {
                    ArrayList<Char> columnRow = new ArrayList<>(4);
                    for (int k = 0; k < 4; k++) {
                        columnRow.add(new Char('\0'));
                    }
                    rowColumn.add(columnRow);
                }
                outerRow.add(rowColumn);
            }
            SudokuBoard.add(outerRow);
        }

        while(valueScope.size() > 0) {
            Character value = valueScope.get(rand.nextInt(valueScope.size()));
            valueScope.remove(value);
            for (int ORIndex = 0 ; ORIndex < 4 ; ORIndex++) {
                for (int OCIndex = 0 ; OCIndex < 4 ; OCIndex++) {
                    insertValueOnSquare(SudokuBoard, ORIndex, OCIndex, value);
                }
            }
        }

        originalBoard = (ArrayList<ArrayList<ArrayList<ArrayList<Char>>>>) SudokuBoard.clone();

        ArrayList<int[]> hiddenIndexes = new ArrayList<>();

        for (; totalToHide > 0 ; totalToHide--) {
            while(true) {
                int ORIndex = rand.nextInt(4);
                int OCIndex = rand.nextInt(4);
                int IRIndex = rand.nextInt(4);
                int ICIndex = rand.nextInt(4);
                int[] indexes = {ORIndex, OCIndex, IRIndex, ICIndex};
                if (!hiddenIndexes.contains(indexes)) {
                    Char c = new Char('\0');
                    c.hint = false;
                    SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).set(ICIndex, c);
                    hiddenIndexes.add(indexes);
                    break;
                }
            }
        }
    }

    public ArrayList<ArrayList<ArrayList<ArrayList<Character>>>> generateBoard() {

        return null;
    }

    private void insertValueOnSquare(ArrayList<ArrayList<ArrayList<ArrayList<Char>>>> board,
                                     int ORIndex, int OCIndex, Character value) {

        for (int IRIndex = 0 ; IRIndex < 4 ; IRIndex++) {
            for (int ICIndex = 0 ; ICIndex < 4 ; ICIndex++) {
                if(getAllPossibleValues(board, ORIndex, OCIndex, IRIndex, ICIndex).contains(value) &&
                        board.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex).value == '\0'){

                    board.get(ORIndex).get(OCIndex).get(IRIndex).set(ICIndex, new Char(value.charValue()));
                    return;
                }
            }
        }
    }

    public ArrayList<Character> getAllPossibleValues(ArrayList<ArrayList<ArrayList<ArrayList<Char>>>> board,
                                                     int ORIndex, int OCIndex, int IRIndex, int ICIndex) {

        ArrayList<Character> possibleValues = new ArrayList<>();
        Collections.addAll(possibleValues, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F');

        possibleValues.removeAll(getUsedValuesOnRow(board, ORIndex, IRIndex));
        possibleValues.removeAll(getUsedValuesOnColumn(board, OCIndex, ICIndex));
        possibleValues.removeAll(getUsedValuesOnSquare(board.get(ORIndex).get(OCIndex)));

        //System.out.println("Possible Values: "+possibleValues);
        return possibleValues;
    }

    public ArrayList<Character>  getUsedValuesOnRow(ArrayList<ArrayList<ArrayList<ArrayList<Char>>>> board,
                                                    int ORIndex, int IRIndex) {

        ArrayList<Character> usedValues = new ArrayList<>();
        for(int i = 0 ; i < 4 ; i++) {
            for(int j = 0 ; j < 4 ; j++) {
                verifyOccurrence(usedValues, board.get(ORIndex).get(i).get(IRIndex).get(j).value);
            }
        }

        //System.out.println("Used chars at row("+(ORIndex*4+IRIndex+1)+") :"+usedValues);
        return usedValues;
    }

    public ArrayList<Character>  getUsedValuesOnColumn(ArrayList<ArrayList<ArrayList<ArrayList<Char>>>> board,
                                                       int OCIndex, int ICIndex) {

        ArrayList<Character> usedValues = new ArrayList<>();
        for(int i = 0 ; i < 4 ; i++) {
            for(int j = 0 ; j < 4 ; j++) {
                verifyOccurrence(usedValues, board.get(i).get(OCIndex).get(j).get(ICIndex).value);
            }
        }

        //System.out.println("Used chars at Column("+(OCIndex*4+ICIndex+1)+") :"+usedValues);
        return usedValues;
    }

    public ArrayList<Character> getUsedValuesOnSquare(ArrayList<ArrayList<Char>> square) {

        ArrayList<Character> usedValues = new ArrayList<>();
        for(int i = 0 ; i < 4 ; i++) {
            for(int j = 0 ; j < 4 ; j++) {
                verifyOccurrence(usedValues, square.get(i).get(j).value);
            }
        }

        //System.out.println("Used chars on square :"+usedValues);
        return usedValues;
    }

    public ArrayList<Character> verifyOccurrence(ArrayList<Character> usedValues, char currentValue) {
        if (usedValues.indexOf(currentValue) == -1 && currentValue != '-') {
            usedValues.add(currentValue);
        }
        return usedValues;
    }

    public GridPane getBoard(GridPane board) {

        for (int ORIndex = 0 ; ORIndex < 4 ; ORIndex++) {
            for(int OCIndex = 0; OCIndex < 4 ; OCIndex ++) {
                GridPane grid = new GridPane();
                grid.setStyle("-fx-border-color: #6379d3; -fx-border-width: 1px; -fx-border-style: solid;");
                for (int IRIndex = 0 ; IRIndex < 4 ; IRIndex++) {
                    for (int ICIndex = 0 ; ICIndex < 4 ; ICIndex++) {
                        Char c = SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex);
                        String value = c.getValue();
                        Input cell = new Input(c, ORIndex, OCIndex, IRIndex, ICIndex);
                        cell.getStyleClass().set(0, "input");
                        c.setComponent(cell);
                        cell.setEditable(false);

                        cell.setOnMouseClicked((event) -> this.selectedTextField = cell);

                        cell.setMinSize(40,40);
                        cell.setMaxSize(40, 40);
                        cell.setText(value);

                        if(!value.equals("\0")) {
                            cell.setDisable(true);
                        }

                        grid.add(cell, ICIndex, IRIndex, 1, 1);
                    }
                }
                board.add(grid, OCIndex, ORIndex, 1, 1);
            }
        }

        return board;
    }

    public boolean verifyRules() {

        ArrayList<Char> fails = new ArrayList<>();

        for (int ORIndex = 0 ; ORIndex < 4 ; ORIndex++) {
            for(int OCIndex = 0; OCIndex < 4 ; OCIndex ++) {
                fails.addAll(verifySquare(SudokuBoard.get(ORIndex).get(OCIndex)));
                for (int IRIndex = 0 ; IRIndex < 4 ; IRIndex++) {
                    fails.addAll(verifyRow(ORIndex, IRIndex));
                    for (int ICIndex = 0 ; ICIndex < 4 ; ICIndex++) {
                        fails.addAll(verifyColumn(OCIndex, ICIndex));
                    }
                }
            }
        }

        for (Char c:fails) {
            c.Component.getStyleClass().set(0, "ruleFail");
        }


        return !(fails.size() > 0);
    }

    public ArrayList<Char> verifySquare(ArrayList<ArrayList<Char>> square) {

        ArrayList<Char> values = new ArrayList<>();
        ArrayList<Char> fails = new ArrayList<>();

        for (int IRIndex = 0 ; IRIndex < 4 ; IRIndex++) {
            for (int ICIndex = 0 ; ICIndex < 4 ; ICIndex++) {
                char value = square.get(IRIndex).get(ICIndex).value;
                Input Component = square.get(IRIndex).get(ICIndex).Component;
                ArrayList<Char> failList = occurrenceList(values, value);
                if (failList.size() > 0 && value != '\0') {
                    fails.addAll(failList);
                    Component.getStyleClass().set(0, "ruleFail");
                }
                else {
                    values.add(square.get(IRIndex).get(ICIndex));
                    if (Component.getStyleClass().get(0) == "ruleFail") {
                        Component.getStyleClass().set(0, "input");
                    }
                }
            }
        }

        return fails;
    }

    public ArrayList<Char> verifyRow(int ORIndex, int IRIndex) {

        ArrayList<Char> values = new ArrayList<>();
        ArrayList<Char> fails = new ArrayList<>();

        for (int OCIndex = 0 ; OCIndex < 4 ; OCIndex++) {
            for (int ICIndex = 0 ; ICIndex < 4 ; ICIndex++) {
                char value = SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex).value;
                Input Component = SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex).Component;
                ArrayList<Char> failList = occurrenceList(values, value);
                if (failList.size() > 0 && value != '\0') {
                    fails.addAll(failList);
                    fails.add(SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex));
                }
                else {
                    values.add(SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex));
                    if (Component.getStyleClass().get(0) == "ruleFail") {
                        Component.getStyleClass().set(0, "input");
                    }
                }
            }
        }

        return fails;
    }

    public ArrayList<Char> verifyColumn(int OCIndex, int ICIndex) {

        ArrayList<Char> values = new ArrayList<>();
        ArrayList<Char> fails = new ArrayList<>();

        for (int ORIndex = 0 ; ORIndex < 4 ; ORIndex++) {
            for (int IRIndex = 0 ; IRIndex < 4 ; IRIndex++) {
                char value = SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex).value;
                Input Component = SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex).Component;
                ArrayList<Char> failList = occurrenceList(values, value);
                if (failList.size() > 0 && value != '\0') {
                    fails.addAll(failList);
                    fails.add(SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex));
                }
                else {
                    values.add(SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex));
                    if (Component.getStyleClass().get(0) == "ruleFail") {
                        Component.getStyleClass().set(0, "input");
                    }
                }
            }
        }

        return fails;
    }

    public ArrayList<Char> occurrenceList(ArrayList<Char> arr, char value) {
        ArrayList<Char> occurrences = new ArrayList<>();

        for (Char c: arr) {
            if (c.value == value) {
                occurrences.add(c);
            }
        }

        return occurrences;
    }

    public boolean isBoardComplete() {
        for (int ORIndex = 0 ; ORIndex < 4 ; ORIndex++) {
            for (int OCIndex = 0 ; OCIndex < 4 ; OCIndex++) {
                for (int IRIndex = 0 ; IRIndex < 4 ; IRIndex++) {
                    for (int ICIndex = 0 ; ICIndex < 4 ; ICIndex++) {
                        if(originalBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex).value != SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex).value) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean changeFieldValue(String value) {
        if (selectedTextField != null) {
            selectedTextField.setText(value);
            selectedTextField.setValue(value.charAt(0));

            selectedTextField = null;

            if (verifyRules()) {
                return isBoardComplete();
            }
        }

        return false;
    }

    public void resetBoard() {
        for (int ORIndex = 0 ; ORIndex < 4 ; ORIndex++) {
            for (int OCIndex = 0 ; OCIndex < 4 ; OCIndex++) {
                for (int IRIndex = 0 ; IRIndex < 4 ; IRIndex++) {
                    for (int ICIndex = 0 ; ICIndex < 4 ; ICIndex++) {
                        Char c = SudokuBoard.get(ORIndex).get(OCIndex).get(IRIndex).get(ICIndex);
                        c.Component.getStyleClass().set(0, "input");
                        if(c.hint == false) {
                            c.Component.setText("\0");
                            c.value = '\0';
                        }
                    }
                }
            }
        }
    }

    class Input extends TextField {

        Char value;
        int ORIndex;
        int OCIndex;
        int IRIndex;
        int ICIndex;

        public Input(Char c, int OR, int OC, int IR, int IC) {
            value = c;
            ORIndex = OR;
            OCIndex = OC;
            IRIndex = IR;
            ICIndex = IC;
        }

        public void setValue(char c) {
            value.value = c;
        }
    }

    public class Char {

        Input Component;
        char value;
        boolean hint = true;

        public Char(char c) {
            value = c;
        }

        public String getValue() { return ""+value; }

        public void setComponent(Input i) { Component = i; }

    }
}
