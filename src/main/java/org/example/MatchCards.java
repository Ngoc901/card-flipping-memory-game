package org.example;
import javax.sound.sampled.*;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;


public class MatchCards {
    ArrayList<Card> cardSet;
    ArrayList<JButton> board;//create a deck of cards with cardNames and cardImageIcons
    ImageIcon backCardImageIcon;
    Timer hideCardTimer;
    Configs config = new Configs();

    boolean gameReady = false;

    // board
    int errorCount = 0;
    int boardWidth = config.columns * config.cardWidth;
    int boardHeight = config.rows * config.cardHeight;

    // frame, error panel, restart button, card1 and card2
    JFrame frame = new JFrame("Memory Game");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JPanel changeThemesPanel = new JPanel();
    JButton restartButton = new JButton("Restart");
    JButton changeLightThemeButton = new JButton("Change Light Themes");
    JButton changeDarkThemeButton = new JButton("Change Dark Themes");
    JButton card1Selected;
    JButton card2Selected;

    Clip backgroundClip;



    MatchCards(){


        backgroundMusicSound();
            setupCard();
            shuffleCard();

            // frame customizing
            frame.setLayout(new BorderLayout());
            frame.setSize(boardWidth, boardHeight);
            frame.setLocationRelativeTo(null); //center of the screen
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // error text
            textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            textLabel.setHorizontalAlignment(JLabel.CENTER);
            textLabel.setText("Misses: " + Integer.toString(errorCount));

            textPanel.setPreferredSize(new Dimension(boardWidth, 30));
            textPanel.add(textLabel);
            frame.add(textPanel, BorderLayout.NORTH);

            //add buttons to panel
            board = new ArrayList<JButton>();
            boardPanel.setLayout(new GridLayout(config.rows, config.columns));
            for (int i = 0; i < cardSet.size(); i++){
                JButton tile = new JButton();
                tile.setPreferredSize(new Dimension(config.cardWidth, config.cardHeight));
                tile.setOpaque(true);
                tile.setIcon(cardSet.get(i).cardImageIcon);
                tile.setFocusable(false);
                tile.addActionListener(this::handleCardClick);
                board.add(tile);
                boardPanel.add(tile);
            }


            frame.add(boardPanel, BorderLayout.CENTER);

            //restart game button
            restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
            restartButton.setText("Restart");
            restartButton.setPreferredSize(new Dimension(boardWidth, 30));
            restartButton.setFocusable(false);
            restartButton.setEnabled(false);
            restartButton.addActionListener(this::handleRestartClick);
            restartGamePanel.add(restartButton);
            frame.add(restartGamePanel, BorderLayout.SOUTH);

            //change themes button

            //light theme
            changeLightThemeButton.setFont(new Font("Arial", Font.PLAIN, 16));
            changeLightThemeButton.setText("☆");
            changeLightThemeButton.setPreferredSize(new Dimension(30, 30));
            changeLightThemeButton.setFocusable(false);
            changeLightThemeButton.setEnabled(false);
            changeLightThemeButton.addActionListener(this::handleLightThemeChangeClick);

            //dark theme
            changeDarkThemeButton.setFont(new Font("Arial", Font.PLAIN, 16));
            changeDarkThemeButton.setText("★");
            changeDarkThemeButton.setPreferredSize(new Dimension(30, 30));
            changeDarkThemeButton.setFocusable(false);
            changeDarkThemeButton.setEnabled(false);
            changeDarkThemeButton.addActionListener(this::handleDarkThemeChangeClick);

            changeThemesPanel.setLayout(new BoxLayout(changeThemesPanel, BoxLayout.Y_AXIS));
            changeThemesPanel.add(changeLightThemeButton);
            changeThemesPanel.add(changeDarkThemeButton);
            frame.add(changeThemesPanel, BorderLayout.WEST);


            frame.pack(); //recalculate width and height after adding all components
            frame.setVisible(true);

            // start game
            hideCardTimer = new Timer(1500, this::timerAction);
                hideCardTimer.setRepeats(false);
                hideCardTimer.start();

            }


    void setupCard(){
        String path;

        cardSet = new ArrayList<Card>();
        for (String cardName : config.cardList) {
            path = "/images/light_theme/" + cardName + ".jpg";
            Image cardImg = new ImageIcon(getClass().getResource(path)).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(config.cardWidth, config.cardHeight, java.awt.Image.SCALE_SMOOTH));

            Card card = new Card(cardName, cardImageIcon);
                cardSet.add(card);
        }
        cardSet.addAll(cardSet); // doubles the cards

        Image cardBackImg = new ImageIcon(getClass().getResource("/images/light_theme/back.jpg")).getImage();
        backCardImageIcon = new ImageIcon(cardBackImg.getScaledInstance(config.cardWidth, config.cardHeight, java.awt.Image.SCALE_SMOOTH));

    }

    void shuffleCard(){
        System.out.println(cardSet);
            //shuffle
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());

                //swap
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);

        }
        System.out.println(cardSet);
    }

    void setupCardDarkTheme(){
        String path;

        cardSet = new ArrayList<Card>();
        for (String cardName : config.darkThemeCardList) {
            path = "/images/dark_theme/" + cardName + ".jpg";
            Image cardImg = new ImageIcon(getClass().getResource(path)).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(config.cardWidth, config.cardHeight, java.awt.Image.SCALE_SMOOTH));

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet); // doubles the cards

        Image cardBackImg = new ImageIcon(getClass().getResource("/images/dark_theme/back.jpg")).getImage();
        backCardImageIcon = new ImageIcon(cardBackImg.getScaledInstance(config.cardWidth, config.cardHeight, java.awt.Image.SCALE_SMOOTH));

    }

    void hideCards(){

        if (gameReady && card1Selected != null && card2Selected != null){
            card1Selected.setIcon(backCardImageIcon);
            card1Selected = null; // removes the reference to the JButton object stored in card1Selected
            card2Selected.setIcon(backCardImageIcon);
            card2Selected = null;
        }else{
            for (int i = 0; i < board.size(); i++){
                board.get(i).setIcon(backCardImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
            changeDarkThemeButton.setEnabled(true);
            changeLightThemeButton.setEnabled(true);


        }
    }

    void backgroundMusicSound(){

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/audios/elevator_music_crazy.wav"));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }

    public void handleCardClick(ActionEvent e) {
        if (!gameReady) {
            return;
        }
        JButton tile = (JButton) e.getSource();
        if (tile.getIcon() == backCardImageIcon) {
            if (card1Selected == null) {
                card1Selected = tile;
                int index = board.indexOf(card1Selected);
                card1Selected.setIcon(cardSet.get(index).cardImageIcon);
            } else if (card2Selected == null) {
                card2Selected = tile;
                int index = board.indexOf(card2Selected);
                card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                // Check if cards match
                if (card1Selected.getIcon() != card2Selected.getIcon()) {
                    errorCount++;
                    textLabel.setText("Misses: " + Integer.toString(errorCount));
                    hideCardTimer.start();
                } else {
                    card1Selected = null;
                    card2Selected = null;
                }
            }
        }
    }

    public void handleRestartClick(ActionEvent e){
        if(!gameReady){
            return;
        }
        // resetting everything
        gameReady = false;
        restartButton.setEnabled(false);
        card1Selected = null;
        card2Selected = null;
        shuffleCard();

        //reassigning buttons with new cards

        for(int i = 0; i < board.size(); i++){
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }

        errorCount = 0;
        textLabel.setText("Misses: " + Integer.toString(errorCount));
        hideCardTimer.start();
    }

    public void handleDarkThemeChangeClick(ActionEvent e){
        if(!gameReady){
            return;
        }

        // resetting everything
        gameReady = false;
        changeDarkThemeButton.setEnabled(false);
        card1Selected = null;
        card2Selected = null;
        setupCardDarkTheme();
        shuffleCard();

        //reassigning buttons with new cards

        for(int i = 0; i < board.size(); i++){
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }

        errorCount = 0;
        textLabel.setText("Misses: " + Integer.toString(errorCount));
        hideCardTimer.start();
    }

    public void handleLightThemeChangeClick(ActionEvent e){
        if(!gameReady){
            return;
        }
        // resetting everything
        gameReady = false;
        changeDarkThemeButton.setEnabled(false);
        card1Selected = null;
        card2Selected = null;
        setupCard();
        shuffleCard();

        //reassigning buttons with new cards

        for(int i = 0; i < board.size(); i++){
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }

        errorCount = 0;
        textLabel.setText("Misses: " + Integer.toString(errorCount));
        hideCardTimer.start();
    }

    public void timerAction(ActionEvent e) {
        hideCards();
    }
}
