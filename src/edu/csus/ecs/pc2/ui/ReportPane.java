package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.report.AccountPermissionReport;
import edu.csus.ecs.pc2.core.report.AccountsReport;
import edu.csus.ecs.pc2.core.report.AllReports;
import edu.csus.ecs.pc2.core.report.BalloonDeliveryReport;
import edu.csus.ecs.pc2.core.report.BalloonSettingsReport;
import edu.csus.ecs.pc2.core.report.BalloonSummaryReport;
import edu.csus.ecs.pc2.core.report.ClarificationsReport;
import edu.csus.ecs.pc2.core.report.ClientSettingsReport;
import edu.csus.ecs.pc2.core.report.ContestAnalysisReport;
import edu.csus.ecs.pc2.core.report.ContestReport;
import edu.csus.ecs.pc2.core.report.EvaluationReport;
import edu.csus.ecs.pc2.core.report.ExtractPlaybackLoadFilesReport;
import edu.csus.ecs.pc2.core.report.FastestSolvedReport;
import edu.csus.ecs.pc2.core.report.GroupsReport;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.InternalDumpReport;
import edu.csus.ecs.pc2.core.report.JudgementNotificationsReport;
import edu.csus.ecs.pc2.core.report.JudgementReport;
import edu.csus.ecs.pc2.core.report.LanguagesReport;
import edu.csus.ecs.pc2.core.report.ListRunLanguages;
import edu.csus.ecs.pc2.core.report.LoginReport;
import edu.csus.ecs.pc2.core.report.OldRunsReport;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.report.RunJudgementNotificationsReport;
import edu.csus.ecs.pc2.core.report.RunsByTeamReport;
import edu.csus.ecs.pc2.core.report.RunsReport;
import edu.csus.ecs.pc2.core.report.RunsReport5;
import edu.csus.ecs.pc2.core.report.SolutionsByProblemReport;
import edu.csus.ecs.pc2.core.report.StandingsReport;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;

/**
 * Report Pane, allows picking and viewing reports.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$
public class ReportPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5165297328068331675L;

    private JPanel jPanel = null;

    private JPanel buttonPane = null;

    private JPanel mainPane = null;

    private JButton viewReportButton = null;

    private JCheckBox breakdownBySiteCheckbox = null;

    private JPanel reportChoicePane = null;

    private JComboBox reportsComboBox = null;

    private JLabel messageLabel = null;

    /**
     * List of reports.
     */
    private IReport[] listOfReports;

    private Log log;

    private String reportDirectory = "reports";

    private JCheckBox thisClientFilterButton = null;

    private JPanel filterPane = null;

    private JPanel filterButtonPane = null;

    private JButton editReportFilter = null;

    private JLabel filterLabel = null;

    private Filter filter = new Filter();
    
    private EditFilterFrame editFilterFrame = null;

    public String getReportDirectory() {
        return reportDirectory;
    }

    /**
     * This method can change the directory that the reports will be written to. The default is "reports".
     * 
     * @param reportDirectory
     *            what directory to write the reports to
     */
    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    /**
     * This method initializes
     * 
     */
    public ReportPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(505, 291));
        this.add(getJPanel(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainPane(), java.awt.BorderLayout.CENTER);

        // populate list of reports
        Vector <IReport> reports = new Vector <IReport> ();
        
        reports.add(new AccountsReport());
        reports.add(new BalloonSummaryReport());

        reports.add(new AllReports());
        reports.add(new ContestReport());

        reports.add(new ContestAnalysisReport());
        reports.add(new SolutionsByProblemReport());
        reports.add(new ListRunLanguages());
        reports.add(new FastestSolvedReport());

        reports.add(new StandingsReport());
        reports.add(new LoginReport());

        reports.add(new RunsReport());
        reports.add(new ClarificationsReport());
        reports.add(new ProblemsReport());
        reports.add(new LanguagesReport());

        reports.add(new JudgementReport());
        reports.add(new RunsByTeamReport());
        reports.add(new BalloonSettingsReport());
        reports.add(new ClientSettingsReport());
        reports.add(new GroupsReport());

        reports.add(new EvaluationReport());

        reports.add(new OldRunsReport());
        reports.add(new RunsReport5());

        reports.add(new AccountPermissionReport());
        reports.add(new BalloonDeliveryReport());
        reports.add(new ExtractPlaybackLoadFilesReport());
        
        reports.add(new RunJudgementNotificationsReport());
        reports.add(new JudgementNotificationsReport());
        
        reports.add(new InternalDumpReport());

        listOfReports = (IReport[]) reports.toArray(new IReport[reports.size()]);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        this.log = getController().getLog();
        
        getEditFilterFrame().setContestAndController(inContest, inController);
        refreshGUI();
    }

    protected void refreshGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshReportComboBox();
            }
        });
    }

    private void refreshReportComboBox() {

        getReportsComboBox().removeAllItems();

        for (IReport report : listOfReports) {
            getReportsComboBox().addItem(report.getReportTitle());
        }

        getReportsComboBox().setSelectedIndex(0);

    }

    @Override
    public String getPluginTitle() {
        return "Reports Pane";
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.setPreferredSize(new java.awt.Dimension(30, 30));
            jPanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return jPanel;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new java.awt.Dimension(45, 45));
            buttonPane.add(getViewReportButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            mainPane = new JPanel();
            mainPane.setLayout(null);
            mainPane.add(getBreakdownBySiteCheckbox(), null);
            mainPane.add(getReportChoicePane(), null);
            mainPane.add(getThisClientFilterButton(), null);
            mainPane.add(getFilterPane(), null);
        }
        return mainPane;
    }

    /**
     * This method initializes viewReportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewReportButton() {
        if (viewReportButton == null) {
            viewReportButton = new JButton();
            viewReportButton.setText("View Report");
            viewReportButton.setToolTipText("View the selected Report");
            viewReportButton.setMnemonic(java.awt.event.KeyEvent.VK_V);
            viewReportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (getBreakdownBySiteCheckbox().isSelected()){
                        generateSelectedReportBySite();
                    } else {
                        generateSelectedReport();
                    }
                }
            });
        }
        return viewReportButton;
    }

    private String getFileName(IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();
        
        while (reportName.indexOf(' ') > -1){
            reportName = reportName.replace(" ", "_");
        }
        return "report."+ reportName+ "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    private void viewFile(String filename, String title) {
        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }

    protected void generateSelectedReport() {

        try {

  
            IReport selectedReport = null;

            String selectedReportTitle = (String) getReportsComboBox().getSelectedItem();
            for (IReport report : listOfReports) {
                if (selectedReportTitle.equals(report.getReportTitle())) {
                    selectedReport = report;
                }
            }
            
            String filename = getFileName(selectedReport);
            File reportDirectoryFile = new File(getReportDirectory());
            if (reportDirectoryFile.exists()) {
                if (reportDirectoryFile.isDirectory()) {
                    filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                }
            } else {
                if (reportDirectoryFile.mkdirs()) {
                    filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                }
            }

            selectedReport.setContestAndController(getContest(), getController());
            
            // TODO insure that each report createReportFile sets the filter too
            /**
             * Using setFilter because createReportFile may not set the filter
             */
            
            selectedReport.setFilter(filter);
            selectedReport.createReportFile(filename, filter);
            
            viewFile(filename, selectedReport.getReportTitle());

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to output report, check logs");
        }

    }
    
    /**
     * Generate the selected report for each site defined in the contest.
     *
     */
    protected void generateSelectedReportBySite() {

        try {

            IReport selectedReport = null;

            String selectedReportTitle = (String) getReportsComboBox().getSelectedItem();
            for (IReport report : listOfReports) {
                if (selectedReportTitle.equals(report.getReportTitle())) {
                    selectedReport = report;
                }
            }

            String filename = getFileName(selectedReport);
            File reportDirectoryFile = new File(getReportDirectory());
            if (reportDirectoryFile.exists()) {
                if (reportDirectoryFile.isDirectory()) {
                    filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                }
            } else {
                if (reportDirectoryFile.mkdirs()) {
                    filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                }
            }
            
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
            
            printWriter.println();
            printWriter.println(new VersionInfo().getSystemName());
            printWriter.println("Date: " + Utilities.getL10nDateTime());
            printWriter.println(new VersionInfo().getSystemVersionInfo());
            printWriter.println();
            printWriter.println("Report "+selectedReport.getReportTitle() + " Report ");
            printWriter.println();

            selectedReport.setContestAndController(getContest(), getController());
            
            Site[] sites = getContest().getSites();
            Arrays.sort(sites, new SiteComparatorBySiteNumber());
            for (Site site : sites) {
                Filter reportFitler = new Filter();
                try {
                    reportFitler.addSite(site);
                    selectedReport.setFilter(reportFitler);
                    printWriter.println();
                    printWriter.println("Report   "+selectedReport.getReportTitle() + " Report ");
                    printWriter.println("For site "+site.getSiteNumber()+" "+site.getDisplayName());
                    
                    selectedReport.writeReport(printWriter);
                    
                } catch (Exception e) {
                    printWriter.println("Exception in report: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }
            
            printWriter.println();
            printWriter.println("end report");
            
            viewFile(filename, selectedReport.getReportTitle());

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to output report, check logs");
        }

    }

    /**
     * show message to user
     * 
     * @param string
     */
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });

    }

    /**
     * This method initializes thisSiteCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getBreakdownBySiteCheckbox() {
        if (breakdownBySiteCheckbox == null) {
            breakdownBySiteCheckbox = new JCheckBox();
            breakdownBySiteCheckbox.setBounds(new java.awt.Rectangle(21,80,187,21));
            breakdownBySiteCheckbox.setMnemonic(java.awt.event.KeyEvent.VK_F);
            breakdownBySiteCheckbox.setToolTipText("Break down by site");
            breakdownBySiteCheckbox.setText("Breakdown by site");
            breakdownBySiteCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    changeSiteFiltering();
                }
            });
        }
        return breakdownBySiteCheckbox;
    }

    protected void changeSiteFiltering() {
//        if (getThisClientFilterButton().isSelected()){
//            filter.setFilterOn();
//            filter.setSiteNumber(getContest().getSiteNumber());
//            filter.setThisSiteOnly(true);
//        } else {
//            filter.setThisSiteOnly(false);
//        }
//        
//        refreshFilterLabel();
    }

    /**
     * This method initializes reportChoicePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getReportChoicePane() {
        if (reportChoicePane == null) {
            reportChoicePane = new JPanel();
            reportChoicePane.setLayout(new BorderLayout());
            reportChoicePane.setBounds(new java.awt.Rectangle(22,9,445,53));
            reportChoicePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reports", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            reportChoicePane.add(getReportsComboBox(), java.awt.BorderLayout.CENTER);
        }
        return reportChoicePane;
    }

    /**
     * This method initializes reportsComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getReportsComboBox() {
        if (reportsComboBox == null) {
            reportsComboBox = new JComboBox();
        }
        return reportsComboBox;
    }

    /**
     * This method initializes thisClientFilterButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getThisClientFilterButton() {
        if (thisClientFilterButton == null) {
            thisClientFilterButton = new JCheckBox();
            thisClientFilterButton.setBounds(new java.awt.Rectangle(21,114,192,21));
            thisClientFilterButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            thisClientFilterButton.setText("Filter for this client only");
            thisClientFilterButton.setVisible(false);
            thisClientFilterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    changeThisClientFiltering();
                }
            });
        }
        return thisClientFilterButton;
    }

    protected void changeThisClientFiltering() {
        if (thisClientFilterButton.isSelected()){
            filter.clearAccountList();

        } else {
            filter.setFilterOn();
            filter.clearAccountList();
            filter.addAccount(getContest().getClientId());
        }
        
        refreshFilterLabel();
    }

    /**
     * This method initializes filterPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFilterPane() {
        if (filterPane == null) {
            filterLabel = new JLabel();
            filterLabel.setText("");
            filterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            filterPane = new JPanel();
            filterPane.setLayout(new BorderLayout());
            filterPane.setBounds(new java.awt.Rectangle(233,76,231,93));
            filterPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    null, null));
            filterPane.add(getFilterButtonPane(), java.awt.BorderLayout.SOUTH);
            filterPane.add(filterLabel, java.awt.BorderLayout.CENTER);
        }
        return filterPane;
    }

    /**
     * This method initializes filterButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFilterButtonPane() {
        if (filterButtonPane == null) {
            filterButtonPane = new JPanel();
            filterButtonPane.add(getEditReportFilter(), null);
        }
        return filterButtonPane;
    }

    /**
     * This method initializes editReportFilter
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditReportFilter() {
        if (editReportFilter == null) {
            editReportFilter = new JButton();
            editReportFilter.setText("Edit Filter");
            editReportFilter.setToolTipText("Edit Filter");
            editReportFilter.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showReportFilter();
                }
            });
        }
        return editReportFilter;
    }

    protected void showReportFilter() {

        // Added in reverse order (right to left)
        getEditFilterFrame().addList(ListNames.LANGUAGES);
        getEditFilterFrame().addList(ListNames.PROBLEMS);
        getEditFilterFrame().addList(ListNames.ACCOUNTS);
        getEditFilterFrame().addList(ListNames.RUN_STATES);
        getEditFilterFrame().addList(ListNames.JUDGEMENTS);
        getEditFilterFrame().addList(ListNames.SITES);

        getEditFilterFrame().setFilter(filter);
        getEditFilterFrame().validate();
        
        getEditFilterFrame().setVisible(true);
    }

    public EditFilterFrame getEditFilterFrame() {
        if (editFilterFrame == null){
            Runnable callback = new Runnable() {
                public void run() {
                    refreshFilterLabel();
                };
            };
            editFilterFrame = new EditFilterFrame(filter, "Report Filter", callback);
        }
        return editFilterFrame;
    }

    private void refreshFilterLabel() {
        filterLabel.setText(filter.toString());
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
