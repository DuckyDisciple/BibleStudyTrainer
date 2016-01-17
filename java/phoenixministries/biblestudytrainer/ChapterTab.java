package phoenixministries.biblestudytrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
public class ChapterTab extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int selectedBook;
    private int defaultBackgroundColor = Color.parseColor("#fcfbe3");
    private int selectedBackgroundColor = Color.parseColor("#42a5f5");

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.tab_chapter, container, false);

        updateChapters(v);

        return v;
    }

    public void updateChapters(View v){
        Context context = v.getContext();
        sharedPreferences = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if(sharedPreferences.getInt("selected_book_id", -1) >= 0){

            editor = sharedPreferences.edit();

            //get selected book here
            selectedBook = sharedPreferences.getInt("selected_book_id", -1);
            if(selectedBook>65){    //catch any errors in the saved ID
                selectedBook = 0;
            }
            int numOfChapters = BibleStructure.getChapters()[selectedBook];
            TextView textViewChapters[] = new TextView[numOfChapters];

            for (int i = 0; i < numOfChapters; i++) {
                //set layout for Chapter tab
                textViewChapters[i] = new TextView(context);
                textViewChapters[i].setTextAppearance(context, R.style.chooser_item_style);
                textViewChapters[i].setGravity(Gravity.CENTER_HORIZONTAL);
                textViewChapters[i].setText(String.valueOf(i + 1));

                //set id for Chapter
                textViewChapters[i].setId(i);

                //highlight Chapter if selected
                if (sharedPreferences.getInt("selected_chapter_id", -1) == textViewChapters[i].getId()) {
                    textViewChapters[i].setBackgroundColor(selectedBackgroundColor);
                }

                //layout Chapter tab
                LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.chapterList);
                linearLayout.addView(textViewChapters[i]);
            }

            //setup onClick handlers
            for (final TextView textView : textViewChapters) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Save selected chapter
                        editor.putInt("selected_chapter_id", textView.getId());
                        editor.putInt("selected_verse_id", -1);
                        editor.commit();

                        //Add verse tab and navigate to it
                        CharSequence bookName = BibleStructure.getBooks()[selectedBook];
                        ((ChooserActivity) getActivity()).addVerseTab(bookName, (textView.getId() + 1),true);
                    }
                });
            }
        }
    }

}
