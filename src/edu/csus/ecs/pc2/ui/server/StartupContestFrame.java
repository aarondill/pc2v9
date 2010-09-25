package edu.csus.ecs.pc2.ui.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileComparatorByName;
import edu.csus.ecs.pc2.profile.ProfileLoadException;
import edu.csus.ecs.pc2.profile.ProfileManager;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Contest Password and Profile login screen.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StartupContestFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5334555559411755127L;

    private IInternalContest contest;

    private IInternalController controller;

    private JPanel centerPane = null;

    private JPasswordField confirmPasswordTextField = null;

    private JPasswordField contestPasswordTextField = null;

    private JLabel nameTitleLabel = null;

    private JLabel versionTitleLabel = null;

    private JLabel mainTitleTopLabel = null;

    private JLabel passwordTitleLabel = null;

    private JButton loginButton = null;

    private JButton exitButton = null;

    private JLabel messageLabel = null;

    private JLabel mainTitleBottomLabel = null;

    private LogWindow logWindow = null;

    private JPanel mainPanel;

    private JPanel westPanel;

    private JLabel logoCSUS = null;

    private JPanel bottomPanel = null;

    private JLabel logoICPC = null;

    private JPanel northPanel = null;

    private JLabel spacerLabel = null;

    private boolean bAlreadyLoggingIn = false;

    private JComboBox profilesComboBox = null;

    private JLabel profileTitleLabel = null;

    /**
     * This method initializes
     * 
     */
    public StartupContestFrame() {
        super();
        initialize();
        overRideLookAndFeel();
        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(641, 460));
        this.setPreferredSize(new java.awt.Dimension(628, 430));
        this.setMinimumSize(new java.awt.Dimension(628, 430));
        this.setBackground(new java.awt.Color(253, 255, 255));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("PC^2 Contest Startup Login");
        this.setContentPane(getMainPanel());

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptBeforeExit();
            }
        });

        VersionInfo versionInfo = new VersionInfo();
        versionTitleLabel.setText("PC^2 version " + versionInfo.getVersionNumber() + " " + versionInfo.getBuildNumber());
        
        populateProfileComboBox();
    }

    private void populateProfileComboBox() {

        getProfilesComboBox().removeAllItems();

        ProfileManager manager = new ProfileManager();

        int comboIndex = 0;

        if (manager.hasDefaultProfile()) {

            Profile[] profiles = new Profile[0];
            try {
                profiles = manager.load();
                Profile currentProfile = manager.getDefaultProfile();

                Arrays.sort(profiles, new ProfileComparatorByName());

                int idx = 0;
                for (Profile profile : profiles) {

                    getProfilesComboBox().addItem(profile);
                    if (profile.getProfilePath().equals(currentProfile.getProfilePath())) {
                        comboIndex = idx;
                    }
                    idx++;
                }

            } catch (IOException e) {
                fatalError("Unable to load profile list ", e);
            } catch (ProfileLoadException e) {
                fatalError("Unable to load profile list ", e);
            }

        } else {
            Profile profile = ProfileManager.createNewProfile();
            getProfilesComboBox().addItem(profile);

        }

        getProfilesComboBox().setSelectedIndex(comboIndex);
    }

    private void fatalError(String string, Exception e) {
        
        // FIXME log this exception
        setStatusMessage(string+", check logs");
        e.printStackTrace(System.err);
        
        System.exit(4);
    }

    private void overRideLookAndFeel() {
        // TODO eventually move this method to on location
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")) {
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")) {
            FrameUtilities.setNativeLookAndFeel();
        }
    }

    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            mainPanel.add(getPasswordTitleLabel(), java.awt.BorderLayout.CENTER);
            mainPanel.add(getWestPanel(), java.awt.BorderLayout.WEST);
            mainPanel.add(getBottomPanel(), java.awt.BorderLayout.SOUTH);
            mainPanel.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
        }

        return mainPanel;
    }

    private JPanel getWestPanel() {
        if (westPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setVgap(30);
            flowLayout.setHgap(5);
            westPanel = new JPanel();
            westPanel.setLayout(flowLayout);
            westPanel.setMinimumSize(new java.awt.Dimension(130, 132));
            westPanel.setPreferredSize(new java.awt.Dimension(140, 132));
            westPanel.setBackground(java.awt.Color.white);
            westPanel.add(getLogoCSUS(), null);
        }

        return westPanel;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPasswordTitleLabel() {
        if (centerPane == null) {
            profileTitleLabel = new JLabel();
            profileTitleLabel.setBounds(new Rectangle(32, 146, 105, 22));
            profileTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            profileTitleLabel.setText("Profile");
            messageLabel = new JLabel();
            messageLabel.setForeground(Color.red);
            messageLabel.setText("");
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            messageLabel.setBounds(new Rectangle(32, 233, 393, 26));
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            passwordTitleLabel = new JLabel();
            passwordTitleLabel.setText("Confirm Contest Password");
            passwordTitleLabel.setBounds(new Rectangle(32, 81, 239, 16));
            versionTitleLabel = new JLabel();
            versionTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            versionTitleLabel.setBounds(new Rectangle(98, 263, 306, 23));
            versionTitleLabel.setText("Version XX. XX YYYY vv 22");
            nameTitleLabel = new JLabel();
            nameTitleLabel.setText("Contest Password");
            nameTitleLabel.setBounds(new Rectangle(32, 21, 124, 15));
            nameTitleLabel.setPreferredSize(new java.awt.Dimension(45, 16));
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.setBackground(java.awt.Color.white);
            centerPane.add(getConfirmPasswordTextField(), null);
            centerPane.add(getContestPasswordTextField(), null);
            centerPane.add(nameTitleLabel, null);
            centerPane.add(versionTitleLabel, null);
            centerPane.add(passwordTitleLabel, null);
            centerPane.add(getLoginButton(), null);
            centerPane.add(getExitButton(), null);
            centerPane.add(messageLabel, null);
            centerPane.add(getProfilesComboBox(), null);
            centerPane.add(profileTitleLabel, null);
        }
        return centerPane;
    }

    /**
     * This method initializes jPasswordField
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getConfirmPasswordTextField() {
        if (confirmPasswordTextField == null) {
            confirmPasswordTextField = new JPasswordField();

            confirmPasswordTextField.setBounds(new Rectangle(32, 99, 368, 20));
            confirmPasswordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        attemptToLogin();
                    }
                }
            });
        }
        return confirmPasswordTextField;
    }

    protected void attemptToLogin() {

        setStatusMessage("");
        if (getContestPassword() == null || getContestPassword().length() < 1) {
            setStatusMessage("Enter enter a contest password");
        } else {

            if (getContestPassword().toLowerCase().startsWith("log")) {
                logWindow.setVisible(true);
                return;
            }

            if (bAlreadyLoggingIn) {
                return;
            }

            bAlreadyLoggingIn = true;

        }
    

    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JPasswordField getContestPasswordTextField() {
        if (contestPasswordTextField == null) {
            contestPasswordTextField = new JPasswordField();

            contestPasswordTextField.setBounds(new Rectangle(32, 37, 366, 20));
            contestPasswordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        confirmPasswordTextField.requestFocus();
                    }
                }
            });
        }
        return contestPasswordTextField;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setMnemonic(KeyEvent.VK_L);
            loginButton.setBounds(new Rectangle(32, 191, 95, 26));
            loginButton.setText("Login");
            loginButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    messageLabel.setText("Logging in");
                    attemptToLogin();
                }
            });
        }
        return loginButton;
    }

    /**
     * User hit ok, attempt to login.
     */
    protected void attemptToLoginReference() {

        setStatusMessage("");
        if (getContestPassword() == null || getContestPassword().length() < 1) {
            setStatusMessage("Please enter a login");
        } else {

            if (getContestPassword().toLowerCase().startsWith("log")) {
                logWindow.setVisible(true);
                return;
            }

            if (bAlreadyLoggingIn) {
                return;
            }

            bAlreadyLoggingIn = true;

        }
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setMnemonic(KeyEvent.VK_X);
            exitButton.setBounds(new Rectangle(318, 192, 95, 26));
            exitButton.setText("Exit");
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptBeforeExit();
                }
            });
        }
        return exitButton;
    }

    protected void promptBeforeExit() {

        setStatusMessage("");
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to exit?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * This method initializes logoCSUS
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getLogoCSUS() {
        if (logoCSUS == null) {
            logoCSUS = new JLabel();

            ImageIcon image = loadImageIconFromFile("images/csus_logo.png");
            logoCSUS.setIcon(image);
            logoCSUS.setBounds(new java.awt.Rectangle(-11, 48, 137, 127));
        }
        return logoCSUS;
    }

    /*
     * Given a inFileName attempts to find file in jar, otherwise falls back to file system.
     * 
     * Will return null if file is not found in either location.
     */
    private ImageIcon loadImageIconFromFile(String inFileName) {
        File imgFile = new File(inFileName);
        ImageIcon icon = null;
        // attempt to locate in jar
        URL iconURL = getClass().getResource("/" + inFileName);
        if (iconURL == null) {
            if (imgFile.exists()) {
                try {
                    iconURL = imgFile.toURL();
                } catch (MalformedURLException e) {
                    iconURL = null;
                    StaticLog.log("LoginFrame.loadImageIconFromFile(" + inFileName + ")", e);
                }
            }
        }
        if (iconURL != null) {
            if (verifyImage(inFileName, iconURL)) {
                icon = new ImageIcon(iconURL);
            } else {
                StaticLog.warning(inFileName + "(" + iconURL.toString() + ") checksum failed");
            }
        }
        return icon;
    }

    private boolean verifyImage(String inFileName, URL url) {
        // these are the real checksums
        byte[] csusChecksum = { -78, -82, -33, 125, 3, 20, 3, -51, 53, -82, -66, -19, -96, 82, 39, -92, 16, 52, 17, 127 };
        byte[] icpcChecksum = { -9, -91, 66, 44, 57, 117, 47, 58, 103, -17, 31, 53, 10, 6, 100, 68, 0, 127, -103, -58 };
        // these are the checkums from java jvm under microsoft
        byte[] csusChecksum2 = { 98, 105, -19, -31, -71, -121, 109, -34, 64, 83, -78, -31, 49, -57, 57, 8, 35, -79, 13, -49 };
        byte[] icpcChecksum2 = { 70, -55, 53, -41, 127, 102, 30, 95, -55, -13, 11, -11, -31, -103, -107, -31, 119, 25, -98, 14 };
        // these are the ibm jre checksums
        byte[] csusChecksum3 = { -46, -84, -66, 55, 82, -78, 124, 88, 68, -83, -128, -110, -19, -26, 92, -3, 76, -26, 21, 30 };
        byte[] icpcChecksum3 = { 41, 72, 104, 75, 73, 55, 55, 93, 32, 35, -6, -12, -96, -23, -3, -17, -119, 26, 81, -2 };
        byte[] verifyChecksum;

        try {
            int matchedBytes = 0;
            InputStream is = url.openStream();
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            byte[] b = new byte[1024];
            while (is.read(b) > 0) {
                md.update(b);
            }
            byte[] digested = md.digest();
            if (inFileName.equals("images/csus_logo.png")) {
                switch (digested[0]) {
                    case 98:
                        verifyChecksum = csusChecksum2;
                        break;
                    case -46:
                        verifyChecksum = csusChecksum3;
                        break;
                    default:
                        verifyChecksum = csusChecksum;
                        break;
                }
            } else {
                switch (digested[0]) {
                    case 70:
                        verifyChecksum = icpcChecksum2;
                        break;
                    case 41:
                        verifyChecksum = icpcChecksum3;
                        break;
                    default:
                        verifyChecksum = icpcChecksum;
                        break;
                }
            }
            for (int i = 0; i < digested.length; i++) {
                if (digested[i] == verifyChecksum[i]) {
                    matchedBytes++;
                } else {
                    break;
                }
            }
            return (matchedBytes == verifyChecksum.length);
        } catch (IOException e) {
            StaticLog.log("verifyImage(" + inFileName + ")", e);
        } catch (NoSuchAlgorithmException e) {
            StaticLog.log("verifyImage(" + inFileName + ")", e);
        }

        return false;
    }

    /**
     * This method initializes bottomPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            logoICPC = new JLabel();

            ImageIcon image = loadImageIconFromFile("images/icpc_banner.png");
            logoICPC.setIcon(image);
            bottomPanel = new JPanel();
            bottomPanel.setBackground(java.awt.Color.white);
            bottomPanel.add(logoICPC, null);
        }
        return bottomPanel;
    }

    /**
     * This method initializes northPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
        if (northPanel == null) {
            spacerLabel = new JLabel();
            spacerLabel.setText(" ");
            northPanel = new JPanel();
            northPanel.setLayout(new BorderLayout());
            northPanel.setBackground(java.awt.Color.white);
            mainTitleBottomLabel = new JLabel();
            mainTitleBottomLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainTitleBottomLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            mainTitleBottomLabel.setText("Programming Contest Control System");
            mainTitleBottomLabel.setBackground(java.awt.Color.white);
            mainTitleBottomLabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 26));
            mainTitleTopLabel = new JLabel();
            mainTitleTopLabel.setFont(new Font("Dialog", Font.BOLD, 22));
            mainTitleTopLabel.setText("California State University, Sacramento");
            mainTitleTopLabel.setHorizontalAlignment(SwingConstants.CENTER);
            northPanel.add(mainTitleTopLabel, java.awt.BorderLayout.CENTER);
            northPanel.add(mainTitleBottomLabel, java.awt.BorderLayout.SOUTH);
            northPanel.add(spacerLabel, java.awt.BorderLayout.NORTH);

        }
        return northPanel;
    }

    /**
     * This method initializes profilesComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProfilesComboBox() {
        if (profilesComboBox == null) {
            profilesComboBox = new JComboBox();
            profilesComboBox.setBounds(new Rectangle(154, 145, 259, 25));
        }
        return profilesComboBox;
    }

    /**
     * Display a message for the user.
     * 
     * @param messageString
     *            text to show to user
     */
    public void setStatusMessage(final String messageString) {

        Runnable messageRunnable = new Runnable() {
            public void run() {
                messageLabel.setText(messageString);

            }
        };
        SwingUtilities.invokeLater(messageRunnable);
        FrameUtilities.regularCursor(this);
    }

    /**
     * Fetch Login name for client.
     * 
     * @return the login name
     */
    private String getContestPassword() {
        return contestPasswordTextField.getPassword().toString();
    }

    /**
     * fetch password for client.
     * 
     * @return the password
     */
    private String getPassword() {
        return new String(confirmPasswordTextField.getPassword());
    }

    /**
     * A login listener
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            // TODO log this.
            // System.err.println("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginRemoved(LoginEvent event) {
            // TODO log this.
            // System.err.println("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginDenied(LoginEvent event) {
            setStatusMessage(event.getMessage());
            bAlreadyLoggingIn = false;
        }

        public void loginRefreshAll(LoginEvent event) {
            // TODO Auto-generated method stub

        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        // initialize logWindow so it can add itself as a listener and
        // start populating the mclb
        logWindow = new LogWindow();
        logWindow.setContestAndController(contest, controller);

        contest.addLoginListener(new LoginListenerImplementation());

        setVisible(true);
    }

    public String getPluginTitle() {
        return "Login";
    }

    public void disableLoginButton() {
        getLoginButton().setEnabled(false);
    }

    public static void main(String[] args) {
        new StartupContestFrame().setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"