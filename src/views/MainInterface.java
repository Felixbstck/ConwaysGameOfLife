package views;

import gameoflife.*;
import listeners.*;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;

public class MainInterface extends JFrame implements ListenerModel {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth() * 3/4;
    double height = screenSize.getHeight() * 3/4;
    
    JButton play;
    JButton pause;
    JRadioButton hashlife;
    JSlider slider;
    JComboBox<String> patternPicker;

    Game game;

    GridInterface gridInterface;

    public MainInterface() {

        // Menu buttons
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        play = new JButton("Play");
        pause = new JButton("Pause");
        hashlife = new JRadioButton("Hashlife");

        JPanel sliderPanel = new JPanel(); sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        JLabel sliderLabel = new JLabel("Delay in ms (1 - 1000)");

        // Creation of slider that controls the delay between steps
        slider = new JSlider(1, 1000, 500); //slider.setSnapToTicks(true);
        slider.setPaintTrack(true); slider.setPaintTicks(true);
        slider.setMajorTickSpacing(100);
        slider.setMinorTickSpacing(25); 

        sliderPanel.add(sliderLabel); sliderPanel.add(slider);
        buttons.add(play); buttons.add(pause); buttons.add(hashlife); buttons.add(sliderPanel);

        // Adds .txt file names to the JComboBox
        File dir = new File("./patterns/");
        File[] patterns = dir.listFiles();
        ArrayList<String> args = new ArrayList<>();
        args.add("Random");
        for (File pattern : patterns) {
            args.add(pattern.getName());
        }

        JPanel patternPickerPanel = new JPanel(); patternPickerPanel.setLayout(new BoxLayout(patternPickerPanel, BoxLayout.Y_AXIS));
        patternPicker = new JComboBox<>(args.toArray(new String[0]));
        JLabel patternPickerLabel = new JLabel("Select pattern");
        patternPickerPanel.add(patternPickerLabel); patternPickerPanel.add(this.patternPicker);

        buttons.add(patternPickerPanel);

        this.buttonsEvent();

        this.add(buttons, BorderLayout.NORTH);

        Grid grid = new Grid(256);
        //grid.createPattern("p24gliderlesslwssgun.txt");
        //grid.fillGrid();

        this.game = new Game(grid);
        this.game.getGrid().addListener(this);

        this.gridInterface = new GridInterface((int) this.height, this.game);

        this.add(this.gridInterface);

        // Window settings
        this.setTitle("Conway's GOL");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    private void buttonsEvent() {
        // Play simulation
        this.play.addActionListener(event -> this.game.runGame());
        
        // Pause simulation
        this.pause.addActionListener(event -> this.game.stopGame());

        // Swaps algorithms
        this.hashlife.addActionListener(event -> {
            if (this.hashlife.isSelected()) {
                this.game.setUseHashlife(true);
            } else {
                this.game.setUseHashlife(false);
            }
        });

        this.slider.addChangeListener(event -> this.game.setStepDelay(this.slider.getValue()));

        this.patternPicker.addActionListener(event -> {
            if (this.patternPicker.getSelectedItem() == "Random") {
                this.game.getGrid().fillGrid();
            } else {
                this.game.getGrid().createPattern((String) this.patternPicker.getSelectedItem());
            }
        });

    }

    @Override
    public void modeleMisAJour(Object source) {
        this.game.gridChanged();
		this.gridInterface.repaint();
	}
}
