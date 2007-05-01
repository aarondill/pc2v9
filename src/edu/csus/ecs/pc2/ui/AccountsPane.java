package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Account Pane list.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$$
public class AccountsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 114647004580210428L;

    private MCLB accountListBox = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private JButton filterButton = null;

    private JButton loadButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;
    
    private PermissionList permissionList = new PermissionList();
    
    private EditAccountFrame editAccountFrame = new EditAccountFrame();
    
    private Log log;

    /**
     * This method initializes
     * 
     */
    public AccountsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(625, 216));
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getRunsListBox(), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Accounts Panel";
    }

    protected Object[] buildAccountRow(Account account) {
        // Object[] cols = { "Site", "Type", "Account Id", "Display Name" };
        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            s[0] = getSiteTitle("" + account.getSiteNumber());
            s[1] = clientId.getClientType().toString().toLowerCase();
            s[2] = "" + clientId.getClientNumber();

            s[3] = getTeamDisplayName(account);
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildAccountRow()", exception);
        }
        return null;
    }

    private String getSiteTitle(String string) {
        // TODO Auto-generated method stub
        return "Site " + string;
    }

    private String getTeamDisplayName(Account account) {
        if (account != null) {
            return account.getDisplayName();
        }

        return account.getDisplayName();
    }

    /**
     * This method initializes accountListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getRunsListBox() {
        if (accountListBox == null) {
            accountListBox = new MCLB();

            Object[] cols = { "Site", "Type", "Account Id", "Display Name" };
            accountListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site
            accountListBox.setColumnSorter(0, sorter, 3);

            // Type
            accountListBox.setColumnSorter(1, sorter, 2);

            // Account Id
            accountListBox.setColumnSorter(2, numericStringSorter, 1);

            // Display Name
            accountListBox.setColumnSorter(3, sorter, 4);

            cols = null;

            accountListBox.autoSizeAllColumns();

        }
        return accountListBox;
    }

    public void updateAccountRow(final Account account) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildAccountRow(account);
                int rowNumber = accountListBox.getIndexByKey(account.getClientId());
                if (rowNumber == -1) {
                    accountListBox.addRow(objects, account.getClientId());
                } else {
                    accountListBox.replaceRow(objects, rowNumber);
                }
                accountListBox.autoSizeAllColumns();
                accountListBox.sort();
            }
        });
    }

    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in model.
     */
    private Account[] getAllAccounts() {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (getContest().getAccounts(ctype).size() > 0) {
                Vector<Account> accounts = getContest().getAccounts(ctype);
                allAccounts.addAll(accounts);
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }

    public void reloadAccountList() {

        Account[] accounts = getAllAccounts();

        // TODO bulk load these record

        for (Account account : accounts) {
            updateAccountRow(account);
        }
    }

    private boolean isAllowed (Permission.Type type){
        return permissionList.isAllowed(type);
    }
    
    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        permissionList.clearAndLoadPermissions(account.getPermissionList());
    }

    private void updateGUIperPermissions() {
        addButton.setVisible(isAllowed(Permission.Type.EDIT_ACCOUNT));
        editButton.setVisible(isAllowed(Permission.Type.EDIT_ACCOUNT));
    }
    
    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();
        getContest().addAccountListener(new AccountListenerImplementation());
        
        initializePermissions();
        
        editAccountFrame.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadAccountList();
                updateGUIperPermissions();
            }
        });
    }

    /**
     * Account Listener Implementation
     * 
     * @author pc2@ecs.csus.edu
     * 
     */

    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            updateAccountRow(accountEvent.getAccount());
        }

        public void accountModified(AccountEvent accountEvent) {
            updateAccountRow(accountEvent.getAccount());
            
            // check if is this account
            Account account = accountEvent.getAccount();
            /**
             * If this is the account then update the GUI display per
             * the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });
                
            }
        }
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getEditButton(), null);
            buttonPane.add(getFilterButton(), null);
            buttonPane.add(getLoadButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setToolTipText("Add new account");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addAccount();
                }
            });
        }
        return addButton;
    }

    protected void addAccount() {
        editAccountFrame.setAccount(null);
        editAccountFrame.setVisible(true);
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setText("Edit");
            editButton.setToolTipText("Edit account");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedAccount();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedAccount() {
        
        int selectedIndex = accountListBox.getSelectedIndex();
        if(selectedIndex == -1){
            showMessage("Select a account to edit");
            return;
        }
        
        try {
            ClientId clientId = (ClientId) accountListBox.getKeys()[selectedIndex];
            Account accountToEdit = getContest().getAccount(clientId);
            editAccountFrame.setAccount(accountToEdit);
            editAccountFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit account, check log");
        }
    }

    /**
     * This method initializes filterButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getFilterButton() {
        if (filterButton == null) {
            filterButton = new JButton();
            filterButton.setText("Filter");
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    filterAccounts();
                }
            });
        }
        return filterButton;
    }

    protected void filterAccounts() {
        // TODO Auto-generated method stub
        
        showMessage("Would have filtered ");
    }

    /**
     * This method initializes loadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton();
            loadButton.setText("Load");
            loadButton.setToolTipText("Load Account Information from file");
            loadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadAccountsFromDisk();
                }
            });
        }
        return loadButton;
    }

    protected void loadAccountsFromDisk() {
        // TODO Auto-generated method stub
        showMessage("Would have loaded from disk ");

    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setText("");
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new Dimension(25, 25));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
