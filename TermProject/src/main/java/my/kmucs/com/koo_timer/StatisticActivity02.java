package my.kmucs.com.koo_timer;

/**
 * Created by Koo on 2016-12-30.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class StatisticActivity02 extends ActionBarActivity {
    public static MyDB myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic2);
        myDB = new MyDB(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        Calendar cal;
        TimeZone timeZone;
        Cursor cursor;

        SQLiteDatabase sqlite;
        String sql;


        public final static String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                "Sep", "Oct", "Nov", "Dec",};

        public final static String[] days = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",};

        private LineChartView chartTop;
        private ColumnChartView chartBottom;

        private LineChartData lineData;
        private ColumnChartData columnData;

        public PlaceholderFragment() {
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_statistic2, container, false);

            // *** TOP LINE CHART ***
            chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

            // Generate and set data for line chart
            generateInitialLineData();

            // *** BOTTOM COLUMN CHART ***

            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);

            generateColumnData();

            return rootView;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void generateColumnData() {
            //날
            cal = new GregorianCalendar();
            timeZone = TimeZone.getTimeZone("Asia/Seoul");
            cal.setTimeZone(timeZone);

            int curYear = cal.get(Calendar.YEAR);               //현재의 년도수를 받아옴
            int curMonth = cal.get(Calendar.MONTH) + 1;         //현재의 월을 받아옴
            int curDay = cal.get(Calendar.DAY_OF_MONTH);       //현재의 일을 받아옴

            int numSubcolumns = 1;
            int numColumns = months.length;

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float)getLastDayOfMonth(curYear, i+1), ChartUtils.pickColor()));
                }

                axisValues.add(new AxisValue(i).setLabel(months[i]));

                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));

            }

            columnData = new ColumnChartData(columns);

            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

            chartBottom.setColumnChartData(columnData);

            // Set value touch listener that will trigger changes for chartTop.
            chartBottom.setOnValueTouchListener(new ValueTouchListener());

            // Set selection mode to keep selected month column highlighted.
            chartBottom.setValueSelectionEnabled(true);

            chartBottom.setZoomType(ZoomType.HORIZONTAL);

            // chartBottom.setOnClickListener(new View.OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // SelectedValue sv = chartBottom.getSelectedValue();
            // if (!sv.isSet()) {
            // generateInitialLineData();
            // }
            //
            // }
            // });

        }

        /**
         * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
         * will select value on column chart.
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        private void generateInitialLineData() {


            int numValues = days.length;

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<PointValue> values = new ArrayList<PointValue>();
            int temp_i = 1;
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, 0));
                axisValues.add(new AxisValue(i).setLabel(days[i]));
            }


            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            lineData = new LineChartData(lines);
            lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));


            chartTop.setLineChartData(lineData);

            // For build-up animation you have to disable viewport recalculation.
            chartTop.setViewportCalculationEnabled(false);

            // And set initial max viewport and current viewport- remember to set viewports after data.
            Viewport v = new Viewport(0, 110, days.length, 0);
            chartTop.setMaximumViewport(v);
            chartTop.setCurrentViewport(v);

            chartTop.setZoomType(ZoomType.HORIZONTAL);
        }

        //윤년계산해서 해당 월의 마지막 일을 구하기
        private int getLastDayOfMonth(int curYear, int curMonth) {
            switch (curMonth) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 31;
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
                case 2:
                    if (curYear % 4 == 0 || curYear % 400 == 0 && curYear % 100 != 0)
                        return 29;
                    else
                        return 28;
                default:
                    return -1;
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        private void generateLineData(int color, float range) {
            //날
            cal = new GregorianCalendar();
            timeZone = TimeZone.getTimeZone("Asia/Seoul");
            cal.setTimeZone(timeZone);

            int curYear = cal.get(Calendar.YEAR);               //현재의 년도수를 받아옴
            int curMonth = cal.get(Calendar.MONTH) + 1;         //현재의 월을 받아옴
            int curDay = cal.get(Calendar.DAY_OF_MONTH);       //현재의 일을 받아옴
            int lastDayOfMonth = getLastDayOfMonth(curYear, curMonth);     // 해당월의 마지막 날짜를 받아옴
            int[][] hour = new int[12][lastDayOfMonth + 1];           //해당일의 전체시간을 저장하기 위한 배열
            int[][] min = new int[12][lastDayOfMonth + 1];            //해당일의 전체분을 저장하기 위한 배열
            int[][] sec = new int[12][lastDayOfMonth + 1];            //해당일의 전체초를 저장하기 위한 배열
            double[][] totaltime = new double[12][lastDayOfMonth + 1]; //전체 시간 + 분 + 초를 소수화시켜서 값을 매겨주기 위한 배열

            for(int i=0 ; i<12 ; i++) {
                for (int j = 1; j <= getLastDayOfMonth(curYear, i+1); j++) { //배열 초기화
                    hour[i][j] = 0;
                    min[i][j] = 0;
                    sec[i][j] = 0;
                }
            }


            sqlite = myDB.getReadableDatabase(); //읽기 전용
            sql = "SELECT * FROM timeRecord WHERE year="+curYear;
            cursor = sqlite.rawQuery(sql, null);

            int temp_i=0;
            while(cursor.moveToNext()){ //데이터베이스에서 일수마다의 총 시간을 저장
                if(Integer.parseInt(cursor.getString(2)) == temp_i+1){
                    for (int j = 1; j <= getLastDayOfMonth(curYear, temp_i+1); j++) {
                        if (Integer.parseInt(cursor.getString(3)) == j) { //day가 i와 같다면
                            hour[temp_i][j] += Integer.parseInt(cursor.getString(4));
                            min[temp_i][j] += Integer.parseInt(cursor.getString(5));
                            sec[temp_i][j] += Integer.parseInt(cursor.getString(6));
                            Log.e("하.....", j + ""); //여기까지..

                        }
                    }
                }
                temp_i++;


            }

            for(int i=0 ; i<12; i++) {
                for (int j = 1; j <= getLastDayOfMonth(curYear, i+1); j++) {
                    while (sec[i][j] >= 60) { //초가 60초넘어가면 60초뺴주고 1분 더해줌
                        sec[i][j] -= 60;
                        min[i][j] += 1;
                    }
                    while (min[i][j] >= 60) { //분이 60분 넘어가면 60분 뺴주고 1시간 더해줌
                        min[i][j] -= 60;
                        hour[i][j] += 1;
                    }
                }
            }


            for(int i=0; i<12 ; i++) {
                for (int j = 1; j <= getLastDayOfMonth(curYear, i+1); j++) {
                    totaltime[i][j] = sec[i][j];/////////////

                }
            }
            ///////////////////////////// 여기까지

            // Cancel last animation if not finished.
            chartTop.cancelDataAnimation();

            // Modify data targets
            Line line = lineData.getLines().get(0);// For this example there is always only one line.
            line.setColor(color);


            for(int i=0;i<12;i++) {
                for(int j=1; j<getLastDayOfMonth(curYear, i+1) ;j++) {
                    for (PointValue value : line.getValues()) {
                        // Change target only for Y value.
                        value.setTarget(value.getX(), (float) totaltime[i][j] * range);
                    }
                }
            }

            // Start new data animation with 300ms duration;

            chartTop.startDataAnimation(300);
        }

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                generateLineData(value.getColor(), 1);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onValueDeselected() {

                generateLineData(ChartUtils.COLOR_GREEN, 0);

            }
        }
    }
}