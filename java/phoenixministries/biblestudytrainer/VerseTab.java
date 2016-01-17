package phoenixministries.biblestudytrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Drew.Sartorius on 5/14/2015.
 */
public class VerseTab extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int defaultBackgroundColor = Color.parseColor("#fcfbe3");
    private int selectedBackgroundColor = Color.parseColor("#42a5f5");

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.tab_verse, container, false);

        Context context = v.getContext();
        sharedPreferences = context.getSharedPreferences(
                getString(R.string.preference_file_key),Context.MODE_PRIVATE);

        if(sharedPreferences.getInt("selected_chapter_id",-1) >= 0){
            editor = sharedPreferences.edit();

            //get selected book and chapter
            final int selectedBook = sharedPreferences.getInt("selected_book_id", -1);
            final int selectedChapter = sharedPreferences.getInt("selected_chapter_id",-1);

            int numOfVerses = BibleStructure.getVerses()[selectedBook][selectedChapter];
            TextView textViewVerses[] = new TextView[numOfVerses];

            LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.verseList);

            for(int i=0; i < numOfVerses; i++){
                //set layout for verse tab
                textViewVerses[i] = new TextView(context);
                textViewVerses[i].setTextAppearance(context, R.style.chooser_item_style);
                textViewVerses[i].setGravity(Gravity.CENTER_HORIZONTAL);
                textViewVerses[i].setText(String.valueOf(i + 1));

                //set id for verse
                textViewVerses[i].setId(i);

                //highlight verse if selected
                if(sharedPreferences.getInt("selected_verse_id",-1) == textViewVerses[i].getId()){
                    textViewVerses[i].setBackgroundColor(selectedBackgroundColor);
                }

                //layout verse tab
                linearLayout.addView(textViewVerses[i]);
            }

            //setup onClick handlers
            for(final TextView textView : textViewVerses){
                textView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //Highlight selected verse  (Removed since it auto-navigates)
                        //textView.setBackgroundColor(selectedBackgroundColor);

                        //Save selected verse
                        editor.putInt("selected_verse_id", textView.getId());
                        editor.commit();

                        CharSequence bookName = BibleStructure.getBooks()[selectedBook];
                        ((ChooserActivity) getActivity()).goToReading(bookName, selectedChapter + 1, textView.getId() + 1);
                    }
                });
            }
        }

        return v;
    }
}
