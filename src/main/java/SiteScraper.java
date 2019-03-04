public class SiteScraper {
    final static String SOURSE_URL = "https://rules.sos.ri.gov/organizations";

    public static void main(String args[]){
        ParseManager parseManager = new ParseManager();
        parseManager.parseHtmlSource(SOURSE_URL);
    }
}
