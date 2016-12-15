package my.kmucs.com.koo_timer;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;

public class StatisticActivity extends ActionBarActivity {

    public static MyDB myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);


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





        private ColumnChartView chart;
        private PreviewColumnChartView previewChart;
        private ColumnChartData data;
        /**
         * Deep copy of data.
         */
        private ColumnChartData previewData;

        public PlaceholderFragment() {
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

            chart = (ColumnChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewColumnChartView) rootView.findViewById(R.id.chart_preview);

            // Generate data for previewed chart and copy of that data for preview chart.
            generateDefaultData();

            chart.setColumnChartData(data);
            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            chart.setZoomEnabled(false);
            chart.setScrollEnabled(false);

            previewChart.setColumnChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(false);

            return rootView;
        }

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.preview_column_chart, menu);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                generateDefaultData();
                chart.setColumnChartData(data);
                previewChart.setColumnChartData(previewData);
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_both) {
                previewXY();
                previewChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                return true;
            }
            if (id == R.id.action_preview_horizontal) {
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_vertical) {
                previewY();
                return true;
            }
            if (id == R.id.action_change_color) {
                int color = ChartUtils.pickColor();
                while (color == previewChart.getPreviewColor()) {
                    color = ChartUtils.pickColor();
                }
                previewChart.setPreviewColor(color);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void generateDefaultData() {

            cal = new GregorianCalendar();
            timeZone = TimeZone.getTimeZone("Asia/Seoul");
            cal.setTimeZone(timeZone);

            int curYear = cal.get(Calendar.YEAR);               //현재의 년도수를 받아옴
            int curMonth = cal.get(Calendar.MONTH) + 1;         //현재의 월을 받아옴
            int curDay = cal.get(Calendar.DAY_OF_MONTH);       //현재의 일을 받아옴
            int lastDayOfMonth = getLastDayOfMonth(curYear, curMonth);     // 해당월의 마지막 날짜를 받아옴
            int[] hour = new int[lastDayOfMonth + 1];           //해당일의 전체시간을 저장하기 위한 배열
            int[] min = new int[lastDayOfMonth + 1];            //해당일의 전체분을 저장하기 위한 배열
            int[] sec = new int[lastDayOfMonth + 1];            //해당일의 전체초를 저장하기 위한 배열
            double[] totaltime = new double[lastDayOfMonth + 1]; //전체 시간 + 분 + 초를 소수화시켜서 값을 매겨주기 위한 배열

            for(int i=1 ; i<=lastDayOfMonth ; i++){ //배열 초기화
                hour[i] = 0;
                min[i] = 0;
                sec[i] = 0;
            }


            sqlite = myDB.getReadableDatabase(); //읽기 전용
            sql = "SELECT * FROM timeRecord WHERE year="+curYear+" and month="+curMonth;
            cursor = sqlite.rawQuery(sql, null);

            while(cursor.moveToNext()){ //데이터베이스에서 일수마다의 총 시간을 저장
                for(int i=1 ; i <= lastDayOfMonth ; i++){
                    if(Integer.parseInt(cursor.getString(3)) == i){ //day가 i와 같다면
                        hour[i] += Integer.parseInt(cursor.getString(4));
                        min[i] += Integer.parseInt(cursor.getString(5));
                        sec[i] += Integer.parseInt(cursor.getString(6));
                    }
                }
            }

            for(int i=1 ; i <= lastDayOfMonth ; i++){
                while(sec[i]>=60){ //초가 60초넘어가면 60초뺴주고 1분 더해줌
                    sec[i] -= 60;
                    min[i] += 1;
                }
                while (min[i]>=60){ //분이 60분 넘어가면 60분 뺴주고 1시간 더해줌
                    min[i] -= 60;
                    hour[i] += 1;
                }
            }

            for(int i=1 ;i <= lastDayOfMonth ; i++){
                totaltime[i] = hour[i] + (min[i] * 0.01) + (sec[i] * 0.0001) ;

            }


            int numSubcolumns = 1;
            int numColumns = lastDayOfMonth; //columns 개수는 한달의 일 수
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;

            for (int i = 1; i <= numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float)totaltime[i], ChartUtils.pickColor()));
                }

                columns.add(new Column(values));
            }

            data = new ColumnChartData(columns);
            data.setAxisXBottom(new Axis());
            data.setAxisYLeft(new Axis().setHasLines(true));

            // prepare preview data, is better to use separate deep copy for preview chart.
            // set color to grey to make preview area more visible.
            previewData = new ColumnChartData(data);
            for (Column column : previewData.getColumns()) {
                for (SubcolumnValue value : column.getValues()) {
                    value.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
                }
            }

        }

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

        private void previewY() {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dy = tempViewport.height() / 4;
            tempViewport.inset(0, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
            previewChart.setZoomType(ZoomType.VERTICAL);
        }

        private void previewX(boolean animate) {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dx = tempViewport.width() / 4;
            tempViewport.inset(dx, 0);
            if (animate) {
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            } else {
                previewChart.setCurrentViewport(tempViewport);
            }
            previewChart.setZoomType(ZoomType.HORIZONTAL);
        }

        private void previewXY() {
            // Better to not modify viewport of any chart directly so create a copy.
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            // Make temp viewport smaller.
            float dx = tempViewport.width() / 4;
            float dy = tempViewport.height() / 4;
            tempViewport.inset(dx, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        }

        /**
         * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
         * viewport of upper chart.
         */
        private class ViewportListener implements ViewportChangeListener {

            @Override
            public void onViewportChanged(Viewport newViewport) {
                // don't use animation, it is unnecessary when using preview chart because usually viewport changes
                // happens to often.
                chart.setCurrentViewport(newViewport);
            }

        }
    }
}