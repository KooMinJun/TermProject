package my.kmucs.com.koo_timer;

/**
 * Created by Koo on 2016-12-01.
 */

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

public class Fragment2 extends Fragment {

    Button btn_statistic, btn_all_reset;
    Intent i;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment2, container, false);

        btn_all_reset = (Button)rootView.findViewById(R.id.all_data_reset_btn);
        btn_statistic = (Button)rootView.findViewById(R.id.statistic);
        i = new Intent(getActivity(), StatisticActivity.class);

        btn_statistic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        return rootView;
    }
}