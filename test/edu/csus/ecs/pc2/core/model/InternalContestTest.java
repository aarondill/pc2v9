package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * Tests for InternalContest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class InternalContestTest extends TestCase {
    
    private boolean debugMode = false;

    private static final String CONFIG_ACCOUNTS = "ACCOUNTS";

    private static final String CONFIG_BALLOON_SETTINGS = "BALLOON_SETTINGS";

    private static final String CONFIG_CLARIFICATION = "CLARIFICATION";

    private static final String CONFIG_CLIENT_SETTINGS = "CLIENT_SETTINGS";

    private static final String CONFIG_CONTESTTIMES = "CONTESTTIMES";

    private static final String CONFIG_GROUPS = "GROUPS";

    private static final String CONFIG_JUDGEMENTS = "JUDGEMENTS";

    private static final String CONFIG_LANGUAGES = "LANGUAGES";

    private static final String CONFIG_PROBLEMS = "PROBLEMS";

    private static final String CONFIG_PROBLEM_DATAFILES = "PROBLEM_DATAFILES";

    private static final String CONFIG_PROFILES = "PROFILES";

    private static final String CONFIG_RUNS = "RUNS";

    private static final String CONFIG_RUNFILES = "RUNFILES";

    private static final String CONFIG_RUNRESULTSFILE = "RUNRESULTSFILE";

    private static final String CONFIG_SITES = "SITES";

    private static final String CONFIG_SITE_NUMBER = "SITE_NUMBER";

    private static final String CONFIG_GENERAL_PROBLEM = "GENERAL_PROBLEM";

    private static final String CONFIG_CONTEST_INFORMATION = "CONTEST_INFORMATION";

    private static final String CONFIG_CONTESTIDENTIFIER = "CONTESTIDENTIFIER";

    private static final String CONFIG_CONTEST_PASSWORD = "CONTEST_PASSWORD";

    private static final String CONFIG_CLIENTID = "CLIENTID";
    
    private SampleContest sampleContest = new SampleContest();
    
    protected String getTestDirectoryName(){
        String testDir = "testing";
        
        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    protected IStorage createStorage(String name, int siteNumber){
        Profile profile = new Profile(name);
        profile.setSiteNumber(siteNumber);
        String testdirName = getTestDirectoryName() + File.separator + profile.getProfilePath();
        return new FileStorage(testdirName);
    }
    
    /**
     * Test that General is a default category.
     */
    public void testGeneralCategoryCreation() {
        IInternalContest contest = new InternalContest();
        int siteNumber = 3;
        contest.setSiteNumber(siteNumber);
        
        contest.setStorage(createStorage("testGeneral", siteNumber));
  
        contest.initializeStartupData(siteNumber);
        contest.initializeSubmissions(siteNumber);
        
        Category[] categories = contest.getCategories();
        
//        contest.addCategory(new Category("General"));
//        categories = contest.getCategories();
        
        assertEquals("Missing general category", 1, categories.length);
        
        Category defaultCat = categories[0];
        assertEquals("Default cateory not General", "General", defaultCat.getDisplayName());

    }

    /**
     * Simple clone test.
     * 
     * @throws Exception
     */
    public void testClone() throws Exception {

        InternalContest contest1 = new InternalContest();
        InternalContest contest2 = new InternalContest();
        
        int siteNumber = 4;
        ClientId serverId = new ClientId(siteNumber, Type.SERVER, 0);
        contest1.setClientId(serverId);
        contest1.setSiteNumber(siteNumber);
        
        contest2.setClientId(contest1.getClientId());
        contest2.setSiteNumber(siteNumber);

        contestsEqual("testClone identical", contest1, contest2, true);

        Group group = new Group("Group 1 Title");
        contest1.addGroup(group);
        group = new Group("Group 1 Title");
        contest2.addGroup(group);

        contestsEqual("testClone identical", contest1, contest2, false);
    }
    
    protected Profile createProfile (String name){
        Profile profile = new Profile(name);
        profile.setDescription("Contest "+name);
        return profile;
    }

    public void testCloneComplex() throws Exception {

        IInternalContest contest3 = sampleContest.createContest(3, 3, 20, 12, true);

        Profile profile4 = createProfile("Profile 4");
        Profile origProfile = new Profile("Orig profile");
        ProfileCloneSettings cloneSettings = new ProfileCloneSettings("clone4", "new title", contest3.getContestPassword().toCharArray(), origProfile);
        
        IInternalContest contest4 = contest3.clone(contest3, profile4, cloneSettings);

        /**
         * Should compare well
         */
        contestsEqual("testCloneComplex", contest3, contest4, true);

    }
    
    /**
     * Create profile directory and security/encryption information.
     * 
     * @param profile
     * @param password
     * @throws FileSecurityException
     */
    @SuppressWarnings("unused")
    private void createProfileFilesAndDirs(Profile profile, String password) throws FileSecurityException {

        String profileDirectory = profile.getProfilePath();
        
        if (new File(profileDirectory).isDirectory()){
            new Exception("Directory already exists: "+profileDirectory);
        }
        
        new File(profileDirectory).mkdirs();
        
        FileSecurity fileSecurity = new FileSecurity(profileDirectory);
        fileSecurity.saveSecretKey(password.toCharArray());
    }
    
    /**
     * Test cloning of a clone.
     * 
     * Positive test.
     *  
     * @throws Exception
     */
    public void testDoubleClone() throws Exception {

        String password = "foo";

        IInternalContest contest1 = sampleContest.createContest(3, 2, 22, 2, true);

        Profile profile1 = createProfile("Profile One");
        contest1.setProfile(profile1);

        Profile origProfile = new Profile("Orig profile");
        ProfileCloneSettings settings = new ProfileCloneSettings("name", "title", password.toCharArray(), origProfile);
        settings.setCopyAccounts(true);
        settings.setCopyContestSettings(true);

        Profile profile2 = createProfile("Profile Two");

        IInternalContest contest2 = contest1.clone(contest1, profile2, settings);

        Profile profile3 = createProfile("Profile Three");
        // createProfileFilesAndDirs(profile, password);

        IInternalContest contest3 = contest2.clone(contest2, profile3, settings);

        contestsEqual("testCloneComplete", contest1, contest3, true);
    }

    
    public void testRunsClone() throws Exception {

        String password = "foo";

        Profile origProfile = new Profile("Orig profile");
        ProfileCloneSettings settings = new ProfileCloneSettings("testRunsClone", "testRunsClone title", password.toCharArray(), origProfile);
        settings.setCopyProblems(true);
        settings.setCopyAccounts(true);
        settings.setCopyLanguages(true);
        settings.setCopyRuns(true);

        int numRuns = 5;

        String logName = getTestDirectoryName() + "TestRunClone.log";
        Log log = new Log(logName);

        Profile originalProfile = new Profile("Original");
        String profileDir = ProfileTest.createProfileFilesAndDirs(originalProfile, password);

        FileSecurity security = new FileSecurity(profileDir);
        security.saveSecretKey(password.toCharArray());

        IInternalContest contest = sampleContest.createContest(1, 8, 22, 12, true);
        contest.setContestPassword(password);
        contest.setStorage(security);
        contest.storeConfiguration(log);

        Profile newProfile = new Profile("Cloned Profile");
        newProfile.setProfilePath(getTestDirectoryName() + File.separator + newProfile.getProfilePath());
        if (debugMode){
            System.out.println("Profile clone 1 at "+newProfile.getProfilePath());
        }
         ProfileTest.createProfileFilesAndDirs(newProfile, password);
        IInternalContest newContest = contest.clone(contest, newProfile, settings);

        /**
         * Test clone with no runs
         */
        
        Run[] runs = newContest.getRuns();
        assertEquals("Runs created", runs.length, 0);
        
        /**
         * Test clone with 5 (numRuns) runs.
         */

        Profile newProfile2 = new Profile("Cloned Profile");
        newProfile2.setProfilePath(getTestDirectoryName() + File.separator + newProfile2.getProfilePath());
        if (debugMode){
            System.out.println("Profile clone 2 at "+newProfile2.getProfilePath());
        }
        ProfileTest.createProfileFilesAndDirs(newProfile2, password);
        IInternalContest newContest2 = contest.clone(contest, newProfile2, settings);

        Run[] sampleRuns = sampleContest.createRandomRuns(contest, numRuns, true, true, true);
        for (Run run : sampleRuns) {
            contest.addRun(run);
        }
        Run[] runs2 = newContest2.getRuns();
        assertEquals("Runs created", runs2.length, numRuns);

    }
    /**
     * Compares the contests and returns differences.
     * 
     * Each line starts with the config area followed by a colon. ex. JUDGEMENTS: or LANGUAGES:
     * <P>
     * The config areas are constants starting with CONFIG_ in this class.
     * 
     * @param contest1
     * @param contest2
     * @return
     */
    public String[] rawCompareContests(IInternalContest contest1, IInternalContest contest2) {

        Vector<String> failures = new Vector<String>();

        // private AccountList accountList = new AccountList();
        // private BalloonSettingsList balloonSettingsList = new BalloonSettingsList();
        // private ClarificationList clarificationList = new ClarificationList();
        // private ClientId localClientId = null;
        // private ClientSettingsList clientSettingsList = new ClientSettingsList();
        // private ContestInformation contestInformation = new ContestInformation();
        // private ContestTimeList contestTimeList = new ContestTimeList();
        // private GroupDisplayList groupDisplayList = new GroupDisplayList();
        // private GroupList groupList = new GroupList();
        // private int siteNumber = 1;
        // private JudgementDisplayList judgementDisplayList = new JudgementDisplayList();
        // private LanguageDisplayList languageDisplayList = new LanguageDisplayList();
        // private Problem generalProblem = null;
        // private ProblemDataFilesList problemDataFilesList = new ProblemDataFilesList();
        // private ProblemList problemList = new ProblemList();
        // private ProfilesList profileList = new ProfilesList();
        // private RunFilesList runFilesList = new RunFilesList();
        // private RunList runList = new RunList();
        // private RunResultsFileList runResultFilesList = new RunResultsFileList();
        // private SiteList siteList = new SiteList();
        // private String contestIdentifier = null;
        // private String contestPassword;

        failures.addElement(CONFIG_BALLOON_SETTINGS + ": no_match");
        failures.addElement(CONFIG_CLARIFICATION + ": no_match");
        failures.addElement(CONFIG_CLIENT_SETTINGS + ": no_match");
        failures.addElement(CONFIG_CONTESTTIMES + ": no_match");

        failures.addElement(CONFIG_JUDGEMENTS + ": no_match");
        failures.addElement(CONFIG_LANGUAGES + ": no_match");
        failures.addElement(CONFIG_PROBLEMS + ": no_match");
        failures.addElement(CONFIG_PROBLEM_DATAFILES + ": no_match");
        failures.addElement(CONFIG_PROFILES + ": no_match");
        failures.addElement(CONFIG_RUNS + ": no_match");
        failures.addElement(CONFIG_RUNFILES + ": no_match");
        failures.addElement(CONFIG_RUNRESULTSFILE + ": no_match");

        failures.addElement(CONFIG_GENERAL_PROBLEM + ": no_match");
        failures.addElement(CONFIG_CONTEST_INFORMATION + ": no_match");
        failures.addElement(CONFIG_CONTESTIDENTIFIER + ": no_match");
        failures.addElement(CONFIG_CONTEST_PASSWORD + ": no_match");

        failures = new Vector<String>();
        
        if (! contest1.getClientId().equals(contest2.getClientId())) {
            failures.addElement(CONFIG_CLIENTID + ": " + contest1.getClientId() + " vs " + contest2.getClientId());
        }

        if (contest1.getSiteNumber() != contest2.getSiteNumber()) {
            failures.addElement(CONFIG_SITE_NUMBER + ": " + contest1.getSiteNumber() + " vs " + contest2.getSiteNumber());
        }

        Site[] sites = contest1.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        for (Site site : sites) {

            Site otherSite = contest2.getSite(site.getSiteNumber());

            if (otherSite == null) {
                failures.addElement(CONFIG_SITES + ": < " + site);
            } else {
                compare(failures, CONFIG_SITES, "Site Name", site.getDisplayName(), otherSite.getDisplayName());
                compare(failures, CONFIG_SITES, "Site Password", site.getPassword(), otherSite.getPassword());
                compare(failures, CONFIG_SITES, "Site Element Id", site.getElementId(), otherSite.getElementId());

                compare(failures, CONFIG_SITES, "Site Host", (String) site.getConnectionInfo().get(Site.IP_KEY), (String) otherSite.getConnectionInfo().get(Site.IP_KEY));
                compare(failures, CONFIG_SITES, "Site Port", (String) site.getConnectionInfo().get(Site.PORT_KEY), (String) otherSite.getConnectionInfo().get(Site.PORT_KEY));
            }
        }

        sites = contest2.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        for (Site site : sites) {

            Site otherSite = contest1.getSite(site.getSiteNumber());

            if (otherSite == null) {
                failures.addElement(CONFIG_SITES + ": > " + site);
            }
        }

        for (Group group : contest1.getGroups()) {

            Group otherGroup = contest2.getGroup(group.getElementId());
            if (otherGroup == null) {
                failures.addElement(CONFIG_GROUPS + ": < " + group + " " + group.getElementId());
            } else {
                compare(failures, CONFIG_GROUPS, "Group Name", group.getDisplayName(), otherGroup.getDisplayName());
                compare(failures, CONFIG_GROUPS, "Group Id", group.getElementId(), otherGroup.getElementId());
            }
        }
        for (Group group : contest2.getGroups()) {

            Group otherGroup = contest1.getGroup(group.getElementId());
            if (otherGroup == null) {
                failures.addElement(CONFIG_GROUPS + ": > " + group + " " + group.getElementId());
            }
        }

        Account[] accounts = contest1.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            Account otherAccount = contest2.getAccount(account.getClientId());

            if (otherAccount == null) {
                failures.addElement(CONFIG_ACCOUNTS + ": < " + account + " " + account.getElementId());
            } else {
                compare(failures, CONFIG_ACCOUNTS, "Display Name", account.getDisplayName(), account.getDisplayName());
                compare(failures, CONFIG_ACCOUNTS, "Group", account.getGroupId(), account.getGroupId());
                compare(failures, CONFIG_ACCOUNTS, "Alias", account.getAliasName(), account.getAliasName());

                // TODO all the rest of the account fields.
            }
        }

        accounts = contest2.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            Account otherAccount = contest1.getAccount(account.getClientId());

            if (otherAccount == null) {
                failures.addElement(CONFIG_ACCOUNTS + ": < " + account + " " + account.getElementId());
            }
        }

        return (String[]) failures.toArray(new String[failures.size()]);

    }

    private void compare(Vector<String> failures, String contestConfigArea, String comment, ElementId elementId, ElementId elementId2) {

        if (elementId == null && elementId2 == null) {
            return;
        }

        String elementIdString = null;
        if (elementId != null) {
            elementIdString = elementId.toString();
        }
        String elementIdString2 = null;
        if (elementId2 != null) {
            elementIdString2 = elementId2.toString();
        }

        compare(failures, contestConfigArea, comment, elementIdString, elementIdString2);
    }

    private void compare(Vector<String> vector, String contestConfigArea, String comment, String string1, String string2) {

        if (string2 == null) {
            vector.add(contestConfigArea + ": " + comment + " (null" + " vs " + string2 + ")");
        } else if (!string1.equals(string2)) {
            vector.add(contestConfigArea + ": " + comment + " (" + string1 + " vs " + string2 + ")");
        }
    }
    
    /**
     * Compare InternContests.
     * 
     * Fails assert if contest are not equal and expectingSame is true. 
     * 
     * @param comment
     * @param contest1
     * @param contest2
     * @param expectingSame set to true if fail if contest are equal
     */
    private void contestsEqual(String comment, IInternalContest contest1, IInternalContest contest2, boolean expectingSame) {
        String[] differences = rawCompareContests(contest1, contest2);
        if (differences.length > 0) {
            
            if (expectingSame){
                System.out.println("There were differences in: '" + comment + "' use debugMode to see details.");
//                new Exception("There were differences in: '" + comment + "'").printStackTrace();
            }
            
            if (debugMode){
                for (String line : differences) {
                    System.err.println(line);
                }
            }
        }

        if (expectingSame){
            assertTrue("Contests NOT the same " + comment, differences.length == 0);
        }
    }

}
