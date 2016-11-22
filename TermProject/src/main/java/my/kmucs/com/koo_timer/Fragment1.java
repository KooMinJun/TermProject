package my.kmucs.com.koo_timer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment1 extends Fragment {
    Intent i;
    Button countUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        countUp = (Button)rootView.findViewById(R.id.countUp);
        i = new Intent(getActivity(), CountUpActivity.class);

        countUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        return rootView;
    }
}
