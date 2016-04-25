package com.googlegame.battleship;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    // 0 = computer, 1 = human
    int activePlayer = 1;

    int shipselectionsize[] = {3,4,4};
    ArrayList <Integer> chosenlocations = new ArrayList<Integer>();
    int currentship = 0;

    int boardSize = 36;
    PlayerBoard computer;
    PlayerBoard human;

    boolean gameIsActive = true;

    LinkedList<Integer> possible_ship_locations = new LinkedList<Integer>();
    ArrayList<Integer> unattacked_human_locations = new ArrayList<Integer>();

    Random random = new Random();

    String TAG = "Battleship MainActivity";

    boolean checkGameFinish(){
        LinearLayout restartView = (LinearLayout) findViewById(R.id.playAgainLayout);
        if(computer.lost()){

            TextView textView = (TextView) findViewById(R.id.winnerMessage);
            Log.d(TAG, "The game ended, winner is you");
            textView.setText("You won!");
            restartView.setVisibility(restartView.VISIBLE);
            return true;

        } else if (human.lost()){
            TextView textView = (TextView) findViewById(R.id.winnerMessage);
            Log.d(TAG, "The game ended, winner is me");
            textView.setText("You lost!");
            restartView.setVisibility(restartView.VISIBLE);
            return true;
        }

        return false;
    }

    public void computersTurn(){
        // Check for winner..
        if(checkGameFinish()){
            // Handle display for Winner
            gameIsActive = false;
        } else {
            int nextIndex;
            int nextAttack;

            Log.d(TAG, "Computers Turn");

            // Computer's turn
            if (!possible_ship_locations.isEmpty()) {
                // Get next location
                nextAttack = possible_ship_locations.pop().intValue();
                Log.d(TAG, "There is a number on the queue: " + nextAttack + " " + possible_ship_locations.size());
            } else {
                // Randomly pick a location
                nextIndex = random.nextInt(unattacked_human_locations.size());
                nextAttack = unattacked_human_locations.get(nextIndex);
            }

            Log.d(TAG, "next: " + nextAttack);
            Battleship attackedShip = human.checkCoord(nextAttack);

            // Remove the choice from possible_ship_locations and unattacked_human_location
            // so there cannot be duplicate attacks on location
            possible_ship_locations.remove(Integer.valueOf(nextAttack));
            unattacked_human_locations.remove(Integer.valueOf(nextAttack));

            // Add the locations the ships will most likely be
            if(handlePlayerTurn(attackedShip, "human", nextAttack)){
                int x = nextAttack - 6;
                Log.d(TAG, "This is the possible location " + x);
                if(unattacked_human_locations.contains(x) && !possible_ship_locations.contains(x)){
                    possible_ship_locations.push(x);
                }
                x = nextAttack - 1;
                Log.d(TAG, "This is the possible location " + x);
                if(unattacked_human_locations.contains(x) && !possible_ship_locations.contains(x)){
                    possible_ship_locations.push(x);
                }
                x = nextAttack + 1;
                Log.d(TAG, "This is the possible location " + x);
                if(unattacked_human_locations.contains(x) && !possible_ship_locations.contains(x)){
                    possible_ship_locations.push(x);
                }
                x = nextAttack + 6;
                Log.d(TAG, "This is the possible location " + x);
                if(unattacked_human_locations.contains(x) && !possible_ship_locations.contains(x)){
                    possible_ship_locations.push(x);
                }
            }

            // Check for winner..
            if(checkGameFinish()){
                // Handle display for Winner
                gameIsActive = false;
            }

            activePlayer = 1;
        }
    }

    private boolean handlePlayerTurn(Battleship attackedShip, String playerBoard, int shipnum){

        // returns true of the ship is hit, if miss or sunk return false
        // playerBaord is the board that is being attacked

        if(playerBoard == "computer") shipnum += 36;
        String name = "imageView" + shipnum;
        int resID = getResources().getIdentifier(name , "id", this.getPackageName());
        ImageView clickedLocation = (ImageView) findViewById(resID);

        if(attackedShip != null){
            if(attackedShip.isSunk()){
                // Display for sunken ship
                Log.d(TAG, "sunken ship on " + playerBoard + "'s board");
                LinearLayout notificationdisplay = (LinearLayout) findViewById(R.id.notificationdisplay);
                TextView notification = (TextView) findViewById(R.id.notification);
                notificationdisplay.setVisibility(notification.VISIBLE);
                clickedLocation.setImageResource(R.drawable.hit);
                if(playerBoard == "human"){
                    notification.setText("Your ship has sunk");
                    human.addSunkenShip();
                } else {
                    notification.setText("My ship has sunk");
                    computer.addSunkenShip();
                }
            } else {
                // Display for hit
                Log.d(TAG, "ship hit on " + playerBoard + "'s board");
                clickedLocation.setImageResource(R.drawable.hit);
                return true;
            }
        } else {
            // Display for miss
            Log.d(TAG, "Miss on " + playerBoard + "'s board");
            clickedLocation.setImageResource(R.drawable.miss);
        }
        return false;
    }

    public void closeNotification(View view){
        LinearLayout layout = (LinearLayout)findViewById(R.id.notificationdisplay);
        layout.setVisibility(View.INVISIBLE);
    }

    public void playAgain(View view) {

        // Remove the play again popup
        LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.INVISIBLE);

        // Set player to human
        activePlayer = 1;

        // Create new player boards
        computer = new PlayerBoard();
        human = new PlayerBoard();

        // Reset datastructures used for computer's turn
        possible_ship_locations = new LinkedList<Integer>();
        unattacked_human_locations = new ArrayList<>();
        for(int i = 1; i < boardSize+1; i++) {
            unattacked_human_locations.add(Integer.valueOf(i));
        }

        gameIsActive = true;

        // Get drawable battleship images
        Drawable drawableplayer = ContextCompat.getDrawable(this, R.drawable.playercoord);
        Drawable drawablecomputer = ContextCompat.getDrawable(this, R.drawable.coord);

        // Change the image for human board back to ships
        for(int i = 1; i < boardSize+1; i++){
            String name = "imageView" + i;
            int resID = getResources().getIdentifier(name , "id", this.getPackageName());
            ImageView shipimage = (ImageView) findViewById(resID);
            shipimage.setImageDrawable(drawableplayer);
        }

        // Change the image for computer's board back to ships
        // Enable the clicks on all computer's board buttons
        for(int i = 37; i < boardSize+37; i++){
            String name = "imageView" + i;
            int resID = getResources().getIdentifier(name , "id", this.getPackageName());
            ImageView shipimage = (ImageView) findViewById(resID);
            shipimage.setImageDrawable(drawablecomputer);
            shipimage.setClickable(true);
        }

        // For testing
        Battleship ship = new Battleship(4);
        computer.addPlayerShip(ship, new Integer []{1,2,3,4});
        Battleship shiphuman = new Battleship(4);
        human.addPlayerShip(shiphuman, new Integer []{1,2,3,4});
    }

    public void coordClick(View view){
        Integer tag = Integer.parseInt((String) view.getTag());
        if(chosenlocations.contains(tag)) {
            ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.playercoord));
            chosenlocations.remove(tag);
        } else if(chosenlocations.size() != shipselectionsize[currentship]) {
            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.playership));
            chosenlocations.add(tag);
        }
    }

    public void setShip(View view){
        Log.d(TAG, "Trying to set the ship for player");
        if(checkShip()){
            Log.d(TAG, "Validation passed");
            Battleship newship = new Battleship(shipselectionsize[currentship]);
            human.addPlayerShip(newship, chosenlocations.toArray(new Integer[chosenlocations.size()]));
            if(currentship!=shipselectionsize.length-1){
                // Set the chosen ones unclickable
                for(Integer i : chosenlocations){
                    String name = "imageView" + i.toString();
                    Log.d(TAG, "Setting " + name + " to unclickable");
                    int resID = getResources().getIdentifier(name , "id", this.getPackageName());
                    ImageView shipimage = (ImageView) findViewById(resID);
                    shipimage.setClickable(false);
                }
                currentship++;
                chosenlocations = new ArrayList<Integer>();
                // update UI
                LinearLayout notificationdisplay = (LinearLayout) findViewById(R.id.notificationdisplay);
                TextView notification = (TextView) findViewById(R.id.notification);
                notification.setText("Your ship position is saved, chose the next one");
                notificationdisplay.setVisibility(notification.VISIBLE);
                Log.d(TAG, "Ship added, pick a new one");
                TextView beginInstr = (TextView) findViewById((R.id.beginInstr));
                beginInstr.setText("Choose your ship's " + shipselectionsize[currentship] + " coordinates by clicking the coordinates on your board. Click next to place the next ship");
            } else {
                // Update UI
                Log.d(TAG, "Start game....");
                LinearLayout notificationdisplay = (LinearLayout) findViewById(R.id.notificationdisplay);
                TextView notification = (TextView) findViewById(R.id.notification);
                notification.setText("Game start");
                notificationdisplay.setVisibility(notification.VISIBLE);
                // Start game
                // Set the imageviews to visible
                Drawable drawablecomputer = ContextCompat.getDrawable(this, R.drawable.coord);
                for(int i = 37; i < boardSize+37; i++){
                    String name = "imageView" + i;
                    int resID = getResources().getIdentifier(name , "id", this.getPackageName());
                    ImageView shipimage = (ImageView) findViewById(resID);
                    shipimage.setImageDrawable(drawablecomputer);
                    shipimage.setVisibility(shipimage.VISIBLE);
                    shipimage.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View view) {
                            if( gameIsActive ){
                                if( activePlayer == 0 )
                                    return;
                                else if( activePlayer == 1 ){
                                    // Get the tag
                                    ImageView clickedLocation = (ImageView) view;
                                    String tag = (String)clickedLocation.getTag();
                                    Log.d(TAG, "Tag: " + tag);

                                    Battleship attackedShip = computer.checkCoord(Integer.parseInt(tag));
                                    handlePlayerTurn(attackedShip, "computer", Integer.parseInt(tag));

                                    clickedLocation.setClickable(false);
                                }
                                computersTurn();
                            }
                        }
                    });
                }
                // Set unclickable for player board
                for(int i = 1; i < boardSize; i++){
                    String name = "imageView" + i;
                    int resID = getResources().getIdentifier(name , "id", this.getPackageName());
                    ImageView shipimage = (ImageView) findViewById(resID);
                    shipimage.setClickable(false);
                }

                TextView beginInstr = (TextView) findViewById(R.id.beginInstr);
                beginInstr.setVisibility(beginInstr.INVISIBLE);
                Button beginInstrNext = (Button) findViewById(R.id.beginInstrNext);
                beginInstrNext.setClickable(false);
                beginInstrNext.setVisibility(beginInstrNext.INVISIBLE);
            }
        } else {
            Log.d(TAG, "Try again...");
            // Update UI
            LinearLayout notificationdisplay = (LinearLayout) findViewById(R.id.notificationdisplay);
            TextView notification = (TextView) findViewById(R.id.notification);
            notification.setText("Your ship positions are not valid, try again");
            notificationdisplay.setVisibility(notification.VISIBLE);
        }
    }

    public boolean checkShip(){
        Log.d(TAG, "these are the sizes" + chosenlocations.size() + " " + shipselectionsize[currentship] );
        if( chosenlocations.size() == shipselectionsize[currentship] ){
            Log.d(TAG, "it is the correct size " + chosenlocations.size() + " " + shipselectionsize[currentship] );
            if(checkRow(chosenlocations)||checkCol(chosenlocations)){
                Log.d(TAG, "it is either row or column");
                return true;
            }
        }
        return false;
    }

    public static boolean checkRow(ArrayList<Integer> chosenlocations){
        Collections.sort(chosenlocations);
        for(int i = 1; i < chosenlocations.size(); i++){
            Log.d("BattleShip", chosenlocations.get(i-1) + " " + chosenlocations.get(i));
            if(chosenlocations.get(i) != (chosenlocations.get(i-1)+1))
                return false;
        }
        return true;
    }

    public static boolean checkCol(ArrayList<Integer> chosenlocations){
        Collections.sort(chosenlocations);
        for(int i = 1 ; i < chosenlocations.size(); i++){
            Log.d("Battleship", chosenlocations.get(i-1) + " " + chosenlocations.get(i));
            if(chosenlocations.get(i) != (chosenlocations.get(i-1)+6))
                return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        computer = new PlayerBoard();
        human = new PlayerBoard();

        for(int i = 1; i < boardSize+1; i++) {
            unattacked_human_locations.add(Integer.valueOf(i));
        }
//        createBattleboard();

        // Display pop up to tell users to chose a ship

        // For testing
        Battleship ship = new Battleship(4);
        computer.addPlayerShip(ship, new Integer []{1,2,3,4});
//        Battleship shiphuman = new Battleship(4);
//        human.addPlayerShip(shiphuman, new Integer []{1,2,3,4});

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
