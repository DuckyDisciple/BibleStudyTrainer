package phoenixministries.biblestudytrainer;

/**
 * Created by Drew.Sartorius on 6/3/2015.
 */

public class Passage {
    //private int passageId;
    private String book;
    private int chapter;
    private int verse;
    private String version;
    private String versionShort;
    private String copyright;
    //private String textHtml;
    private String text;
    private String heading;

    public Passage(String book, int chapter, int verse, String version){
        this.book=book;
        this.chapter=chapter;
        this.verse=verse;
        //this.text=text;
        this.version=version;
        //this.copyright=copyright;
    }

    public void setText(String text){
        //this.textHtml=text;
        //formatText();
        this.text=text;
    }
    public String getText(){return text;}

    public  void setCopyright(String copyright){
        this.copyright=copyright.replaceAll("&lt;[^&gt;]*&gt;","");
    }
    public String getCopyright(){ return copyright;}

    //private void formatText(){
        //String tempText = textHtml.replaceAll("<p[^>]*>","");
        //tempText = tempText.replaceAll("</p>","\n");
        //tempText = tempText.replaceAll("<sup.*/sup>","");
        //text=tempText;
    //}

    private void setShortVersions(String longVersion){
        switch (longVersion){
            case "English Standard Version":
                versionShort="ESV-eng";
        }
    }
}
