package org.example;

import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.pow;

public class Main {
    static File song;
    static Clip clip;
    static long skip = 5000000;
    static Long lastSkipVal = null;
    static ImageIcon icon = new ImageIcon("appIcon.png");
    static Image image = icon.getImage();

    public static void main(String[] args){
        initialize();
    }

    private static void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(null);

        if(result == JFileChooser.APPROVE_OPTION) {
            try {
                song = fileChooser.getSelectedFile();
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(song);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Invalid file. File must be .WAV.", "Invalid File", JOptionPane.ERROR_MESSAGE);
                song = null;
            }
        }
    }

    public static void initialize(){
        JFrame frame = new JFrame("MusicPlayer 1.0");
        JPanel panel = new JPanel();
        JLabel text = new JLabel();

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem open = new JMenuItem("Open");
        JMenuItem settings = new JMenuItem("Settings");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem changelog = new JMenuItem("Changelog");
        fileMenu.add(open);
        fileMenu.add(settings);
        fileMenu.add(exit);
        helpMenu.add(changelog);

        menu.add(fileMenu);
        menu.add(helpMenu);
        frame.setJMenuBar(menu);

        JButton play = new JButton();
        ImageIcon playIcon = new ImageIcon("playIcon.png");
        play.setIcon(playIcon);
        play.setEnabled(false);
        JButton pause = new JButton();
        ImageIcon pauseIcon = new ImageIcon("pauseIcon.png");
        pause.setIcon(pauseIcon);
        pause.setEnabled(false);
        JButton restart = new JButton();
        ImageIcon restartIcon = new ImageIcon("restartIcon.png");
        restart.setIcon(restartIcon);
        restart.setEnabled(false);
        JButton rewind = new JButton();
        ImageIcon rewindIcon = new ImageIcon("rewindIcon.png");
        rewind.setIcon(rewindIcon);
        rewind.setEnabled(false);
        JButton forward = new JButton();
        ImageIcon forwardIcon = new ImageIcon("forwardIcon.png");
        forward.setIcon(forwardIcon);
        forward.setEnabled(false);

        AtomicLong position = new AtomicLong();

        //button functions
        panel.add(new JLabel("Controls"));
        panel.add(rewind);
        rewind.addActionListener(a->{
            if(song != null){
                position.set(clip.getMicrosecondPosition() - skip);
                rewindMusic(clip, position.get());
            }
        });

        panel.add(play);
        play.addActionListener(a->{
            if(song != null){
                playMusic(clip, position.get());
                play.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        panel.add(pause);
        pause.addActionListener(a->{
            if(song != null){
                position.set(pauseMusic(clip));
                play.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        panel.add(restart);
        restart.addActionListener(a->{
            if(song != null) {
                position.set(restartMusic(clip));

                if(!pause.isEnabled()){
                    play.setEnabled(false);
                    pause.setEnabled(true);
                }
            }
        });

        panel.add(forward);
        forward.addActionListener(a->{
            if(song != null){
                position.set(clip.getMicrosecondPosition() + skip);
                forwardMusic(clip, position.get());
            }
        });

        open.addActionListener(a-> {
            loadFile();
            position.set(0);
            text.setText("NOW PLAYING: " + song.getName());
            play.setEnabled(true);
            pause.setEnabled(true);
            restart.setEnabled(true);
            rewind.setEnabled(true);
            forward.setEnabled(true);
        });

        settings.addActionListener(a-> {
            openSettings();
        });

        exit.addActionListener(a-> {
            frame.dispose();
        });

        changelog.addActionListener(a-> {
            openChangelog();
        });

        frame.add(text);
        frame.setLayout(new CardLayout());
        frame.setSize(400, 300);
        frame.setIconImage(image);
        frame.setLocation(650, 350);
        frame.setResizable(false);
        //text.setLocation(50,30);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(true);
    }

    public static void rewindMusic(Clip clip, long position){
        clip.setMicrosecondPosition(position);
    }

    public static void playMusic(Clip clip, long position){
        try{
            clip.setMicrosecondPosition(position);
            clip.start();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }

    public static long pauseMusic(Clip clip){
        long position = clip.getMicrosecondPosition();
        clip.stop();

        return position;
    }

    public static int restartMusic(Clip clip){
        clip.setMicrosecondPosition(0);
        clip.start();
        return 0;
    }

    public static void forwardMusic(Clip clip, long position){
        clip.setMicrosecondPosition(position);
    }

    public static void openSettings(){
        JFrame frame = new JFrame("Settings");
        JPanel panel = new JPanel();
        JLabel skipText = new JLabel("Skip Duration: ");
        Long[] skipVals = {5L, 10L, 20L, 30L};
        JComboBox<Long> skipBox = new JComboBox<>(skipVals);

        if(lastSkipVal != null){
            skipBox.setSelectedItem(lastSkipVal);
        }

        panel.add(skipText);
        panel.add(skipBox);

        skipBox.addActionListener(a-> {
            skip = (long)skipBox.getSelectedItem() * (long) pow(10, 6);
            lastSkipVal = (Long)skipBox.getSelectedItem();
        });

        frame.setSize(500, 500);
        frame.setLocation(650, 350);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(image);
        frame.add(panel);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void openChangelog(){
        JFrame frame = new JFrame("Changelog");
        JTextPane changelogTextArea = new JTextPane();

        frame.setSize(400, 400);
        frame.setLocation(650, 350);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(image);
        frame.add(changelogTextArea);
        frame.setResizable(false);
        frame.setVisible(true);

        changelogTextArea.setFont(changelogTextArea.getFont().deriveFont(Font.BOLD));
        changelogTextArea.setText("1.0 \n-Initial release");
        changelogTextArea.setEditable(false);
    }
}