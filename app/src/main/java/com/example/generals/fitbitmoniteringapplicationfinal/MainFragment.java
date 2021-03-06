package com.example.generals.fitbitmoniteringapplicationfinal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.LinkAddress;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainFragment extends Fragment{

    View rootView;
    Context context;

    public static final String MyPreferences="MyPrefs";
    public static final String toRegister="my_first_time";
    public static final String imagePath="profilePath";

    List<Maladie> list_maladie=new ArrayList<>();

    SharedPreferences sharedPreferences;
    Bitmap bitmap;
    String picturePath;

    private LineChart mChart;
    private LineChart mChartRR;
    private static String ip="192.168.43.103";


    private static String url_InsertMaladie="http://"+ip+"/addHistoryMaladie.php";

    private static String url_InsertHrv = "http://"+ip+"/addHRV.php";

    private static String url_getMaladie = "http://"+ip+"/allMaladies.php";

    private static String url_updatePnn50 = "http://"+ip+"/updatePNN.php";

    JSONArray array_maladie=null;
    JSONObject object_i_maladie=null;

    private ProgressDialog pDialog=null;
    JSONArray array_json_hrv=null;
    JSONArray array_json_rr;

    JSONArray array_hrv_value=null;
    JSONArray array_rr_value=null;
    private static String url = "http://"+ip+"/getRRHistory.php";


    JSONObject object_i_rr=null;
    List<HistoryHrv> list_hrv=new ArrayList<HistoryHrv>();
    List<HistoryRr> list_rr=new ArrayList<HistoryRr>();

    int i;
    int j;
    int k;
    int m;
    int id;

    String maladie_nom="";
    int id_hrv_malade=-1;
    String jsonStr2;
    String jsonStr3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootView=inflater.inflate(R.layout.main_fragment_layout, container, false);

        url_InsertMaladie="http://"+ip+"/addHistoryMaladie.php";
        url_InsertHrv = "http://"+ip+"/addHRV.php";

        //  url_getMaladie = "http://"+ip+"/getHRVHistory.php";

        url_updatePnn50 = "http://"+ip+"/updatePNN.php";

        url = "http://"+ip+"/getRRHistory.php";

        url_getMaladie = "http://"+ip+"/allMaladies.php";

        context=rootView.getContext();

        sharedPreferences=getActivity().getSharedPreferences(MyPreferences, 0);

        mChart=(LineChart)rootView.findViewById(R.id.chartHRV);

        mChartRR=(LineChart)rootView.findViewById(R.id.chartRRR);

        mChart.setDescription("");
        mChart.setNoDataTextDescription("no data");
        mChart.setHighlightPerTapEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.BLACK);

        final LineData data=new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l=mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1=mChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        x1.setAxisMaxValue(2000f);

        YAxis y1=mChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(300f);
        y1.setAxisMinValue(0f);
        y1.setDrawGridLines(true);

        YAxis y12=mChart.getAxisRight();
        y12.setEnabled(false);
        mChart.notifyDataSetChanged();
        mChart.setVisibleXRange(0, 6);
        mChart.moveViewToX(data.getXValCount() - 7);

//RR chart
        mChartRR.setDescription("");
        mChartRR.setNoDataTextDescription("no data");
        mChartRR.setHighlightPerTapEnabled(true);
        mChartRR.setTouchEnabled(true);
        mChartRR.setDragEnabled(true);
        mChartRR.setScaleEnabled(true);
        mChartRR.setDrawGridBackground(false);
        mChartRR.setPinchZoom(true);
        mChartRR.setBackgroundColor(Color.BLACK);

        LineData dataRR=new LineData();
        dataRR.setValueTextColor(Color.WHITE);
        mChartRR.setData(dataRR);

        Legend lRR=mChartRR.getLegend();
        lRR.setForm(Legend.LegendForm.LINE);
        lRR.setTextColor(Color.WHITE);

        XAxis x1RR=mChartRR.getXAxis();
        x1RR.setTextColor(Color.WHITE);
        x1RR.setDrawGridLines(false);
        x1RR.setAvoidFirstLastClipping(true);
        x1RR.setAxisMaxValue(2000f);

        YAxis y1RR=mChartRR.getAxisLeft();
        y1RR.setTextColor(Color.WHITE);
        y1RR.setAxisMaxValue(175f);
        y1RR.setAxisMinValue(0f);
        y1RR.setDrawGridLines(true);

        YAxis y12RR=mChartRR.getAxisRight();
        y12RR.setEnabled(false);
        mChartRR.notifyDataSetChanged();
        mChartRR.setVisibleXRange(0, 6);
        mChartRR.moveViewToX(data.getXValCount() - 7);

        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View header = (View) drawer.findViewById(R.id.header);

                ImageView profile_header = (ImageView) header.findViewById(R.id.profile);

                if (sharedPreferences.contains(imagePath)) {
                    picturePath = sharedPreferences.getString(imagePath, "rien");

                    bitmap = (BitmapFactory.decodeFile(picturePath));

                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }

                bitmap = getRoundedShape(bitmap);

                profile_header.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, false));

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        return rootView;
    }

    private void showMaladies() {
        //get All maladies;

        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.custom_box_layout);

        LinearLayout main_linear =(LinearLayout)dialog.findViewById(R.id.dialog_main_layout);

        ScrollView scroll = new ScrollView(context);

        scroll.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT));

        main_linear.addView(scroll);

        LinearLayout LL = new LinearLayout(context);

        dialog.setTitle("maladies...");

        for(int i=0;i<list_maladie.size();++i)
        {
            LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.VERTICAL);
            LL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            LL.setId(i);
            String symptText="";
            TextView sympt=new TextView(context);
            sympt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout LL_header=new LinearLayout(context);
            LL_header.setOrientation(LinearLayout.HORIZONTAL);
            LL_header.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //                            TextView maladieDescription =new TextView(context);
            RadioButton butMaladie=new RadioButton(context);

            butMaladie.setId(i);

            //                              maladieDescription.setText(list_maladie.get(i).getNom());

            //                            LL_header.addView(maladieDescription);
            LL_header.addView(butMaladie);

            LL.addView(LL_header);

            for(int j=0;j<list_maladie.get(i).getList_Ingredient().size();++j)
            {
                symptText=symptText+list_maladie.get(i).getList_Ingredient().get(j).getSymptomes()+"\n";
            }

            sympt.setText(symptText);

            LL.addView(sympt);
            scroll.addView(LL);
        }

        // main_linear.addView(LL);

        Button confirm =new Button(context);
        confirm.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        confirm.setText("confirm");
        main_linear.addView(confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position=-1;
                int count=0;
                for(int i=0;i<list_maladie.size();++i)
                {

                    RadioButton radio= (RadioButton)dialog.findViewById(i);
                    if(radio.isChecked())
                    {
                        //jaurai besoin de la valeur pour envoyer a la base de donner et pour
                        //faire insert dans la table des hrv malade le id du hrv malade avec la maladie;
                        position=i;
                        count++;
                        // break;
                    }
                    else
                    {
                        //rien
                    }

                }
                if(count>1)
                {
                    Snackbar.make(view, "choose one section of symptoms", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
                else
                {
                    insertMaladie(list_hrv.get(i).getIdHistoryHrv(),position);
                    dialog.dismiss();
                    if(position==-1)
                    {
                        Toast.makeText(context,"you are suffering from bloc auriculo-ventriculaire",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context,"you are suffering from "+list_maladie.get(position).getNom(),Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        dialog.show();

    }

    private void continueProcessingData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (list_hrv.isEmpty())
                {
                    for (int i = 0; i < 100; ++i) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addEntry("hrv signal");

                            }
                        });
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    for(i=0;i<list_hrv.size();++i)
                    {
                        Runnable th1=null, th2 = null;
                        //creer la liste dans laquelle je vais compter les valeurs qui sont mauvaises par rapport
                        for (j = 0; j < list_hrv.get(i).getList_valeur().size();++j)
                        {

                            th1=new Runnable() {
                                @Override
                                public void run()
                                {
                                    addEntry(list_hrv.get(i).getList_valeur().get(j).getValue() * 1000, list_rr.get(i).getList_rr_value().get(j).getValue() * 100, "HRV signal (x10e3)", "RR signal(x10e2)");

                                /*    if (j == list_hrv.get(i).getList_valeur().size())
                                    {
                                        if(list_hrv.get(i).getPnn50()<0.03*list_hrv.get(i).getList_valeur().size() || list_hrv.get(i).getPnn50()>0.75*list_hrv.get(i).getList_valeur().size())
                                        {
                                            new GetMaladie(context).execute();

                                        }
                                        else
                                        {
                                            //on affiche rien; on continue par le deuxieme signal;
                                        }

                                    }
                                    else
                                    {
                                        ;
                                    }
                                    */


                                }
                            };

                            getActivity().runOnUiThread(th1);
                            try {
                                Thread.sleep(600);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                      if(list_hrv.get(i).getPnn50()<0.03*list_hrv.get(i).getList_valeur().size() || list_hrv.get(i).getPnn50()>0.75*list_hrv.get(i).getList_valeur().size())
                        {
                            try {
                                synchronized (th2) {

                                    th1.();
                                    th2=new Runnable() {
                                        @Override
                                        public void run() {
                                            new GetMaladie(context).execute();
                                        }
                                    };
                                    getActivity().runOnUiThread(th2);
                                    th2.notifyAll();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        else
                        {
                            //on affiche rien; on continue par le deuxieme signal;
                        }

                    }

                }


            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        //chercher la liste des signaux'
        //ensuite iterer sur chaque signal, en faisant addEntry pour chaque valeur ;

        //
        new GetHistoryGraph(context).execute();
    }

    private void addEntry(String n) {
        LineData data = mChart.getData();

        if (data != null)
        {
            LineDataSet set= (LineDataSet) data.getDataSetByIndex(0);
            if(set ==null)
            {
                set=createSet(n);
                data.addDataSet(set);

            }

            data.addXValue("");
            data.addEntry(new Entry((float) (Math.random() * 75) + 60f, set.getEntryCount()), 0);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(6, 6);
            mChart.moveViewToX(data.getXValCount()-7);

        }


    }

    //cest celle utiliser avec la base de donner;

    private void addEntry(double value,double valueRR,String nHRV,String nRR) {
        LineData data = mChart.getData();
        LineData dataRR =mChartRR.getData();

        if (data != null)
        {
            LineDataSet set= (LineDataSet) data.getDataSetByIndex(0);
            // set.setLabel(nHRV);
            if(set ==null)
            {
                // LineDataSet dataset=createSet(entries,"HRV signal")
                set=createSet(nHRV);
                data.addDataSet(set);

            }

            data.addXValue("");
            data.addEntry(new Entry((float) value, set.getEntryCount()), 0);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(6, 6);
            mChart.moveViewToX(data.getXValCount()-7);

        }

        if (dataRR != null)
        {
            LineDataSet set= (LineDataSet) dataRR.getDataSetByIndex(0);
            if(set ==null)
            {
                set=createSetRR(nRR);
                dataRR.addDataSet(set);
                //    set.setLabel(nRR);

            }

            dataRR.addXValue("");
            dataRR.addEntry(new Entry((float) valueRR, set.getEntryCount()), 0);
            mChartRR.notifyDataSetChanged();
            mChartRR.setVisibleXRange(6, 6);
            mChartRR.moveViewToX(dataRR.getXValCount()-7);
        }
    }

    private void addEntryRR(String n) {
        LineData data = mChartRR.getData();

        if (data != null)
        {
            LineDataSet set= (LineDataSet) data.getDataSetByIndex(0);
            if(set ==null)
            {
                set=createSet(n);
                data.addDataSet(set);

            }

            data.addXValue("");
            data.addEntry(new Entry((float) (Math.random() * 75) + 60f, set.getEntryCount()), 0);
            mChartRR.notifyDataSetChanged();
            mChartRR.setVisibleXRange(6, 6);
            mChartRR.moveViewToX(data.getXValCount()-7);

        }


    }

    private LineDataSet createSet(String n) {
        LineDataSet set=new LineDataSet(null,n);
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(224, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        set.setLabel(n);
        return set;
    }

    private LineDataSet createSetRR(String n) {
        LineDataSet set=new LineDataSet(null,n);
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(224, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        set.setLabel(n);
        return set;
    }



    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }


    private class GetHistoryGraph extends AsyncTask<Void, Void, Void> {

        Context context;

        public  GetHistoryGraph(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            //get JSONRR
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            //modifier url pour emmener les rr de ce user donc:
            String userName=sharedPreferences.getString("username", "rien");
            url=url+"?username="+userName;
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            int pnn50=0;


            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObjRR = new JSONObject(jsonStr);

                    array_json_rr= jsonObjRR.getJSONArray("data");
///ha yotla3le hon tous les signaux rr wara ba3edd;; ta a3ref hot les signaux le ma3 ba3ed bi nafse l signal lezm etala3 bil id;lama yet8ayar l id
                    id=-1;
                    List<RrValue> valuesRr=new ArrayList<>();
                    HistoryRr rr=new HistoryRr();

                    List<HrvValue> valuesHrv=new ArrayList<>();
                    HistoryHrv hrv=new HistoryHrv();

                    for(int i=0;i<array_json_rr.length();++i) {

                        url_InsertMaladie="http://"+ip+"/addHistoryMaladie.php";
                        url_InsertHrv = "http://"+ip+"/addHRV.php";
                        //       url_getMaladie = "http://"+ip+"/getHRVHistory.php";
                        url_updatePnn50 = "http://"+ip+"/updatePNN.php";
                        url = "http://"+ip+"/getRRHistory.php";

                        object_i_rr=array_json_rr.getJSONObject(i);

                        if(id==-1)
                        {
                            //1er iteration
                            id=object_i_rr.getInt("idHistory");
                            rr=new HistoryRr();
                            rr.setIdHistoryRr(id);
                            //       rr.setIdHistoryRr(object_i_rr.getInt("idHistory"));
                            valuesRr=new ArrayList<>();
                            RrValue r=new RrValue();
                            r.setValue(object_i_rr.getDouble("value"));
                            r.setId_rr_value(object_i_rr.getInt("idRRValue"));
                            //   r.setTime(object_i_rr.getInt("time"));
                            valuesRr.add(r);
                            //       hrv.setIdHistoryHrv(object_i_rr.getInt("idHistory"));
                            hrv.setIdHistoryHrv(id);
                        }

                        else
                        {
                            if(id==object_i_rr.getInt("idHistory"))
                            {
                                RrValue r=new RrValue();
                                r.setValue(object_i_rr.getDouble("value"));
                                r.setId_rr_value(object_i_rr.getInt("idRRValue"));
                                //   r.setTime(object_i_rr.getInt("time"));
                                valuesRr.add(r);

                                HrvValue h=new HrvValue();
                                if(object_i_rr.getDouble("value")<array_json_rr.getJSONObject(i-1).getDouble("value"))
                                {
                                    h.setValue(array_json_rr.getJSONObject(i-1).getDouble("value")-object_i_rr.getDouble("value"));
                                }
                                else
                                {
                                    h.setValue(object_i_rr.getDouble("value")-array_json_rr.getJSONObject(i-1).getDouble("value"));
                                }

                                if(h.getValue()>0.05)
                                {
                                    ++pnn50;
                                }

                                //     h.setTime(array_json_rr.getJSONObject(i - 1).getInt("time"));
                                valuesHrv.add(h);

                                url_InsertHrv=url_InsertHrv+"?"+"idHistory="+hrv.getIdHistoryHrv()+"&value="+h.getValue()+"&pnn50=0";

                                //url_InsertHrv=url_InsertHrv+"?"+"idHistory="+object_i_rr.getInt("idHistory")+"&value="+h.getValue()+"&pnn50=0";

                                //   url_InsertHrv=url_InsertHrv+"?"+"idHistory="+counter+"&value="+h.getValue()+"&pnn50=0";
                                jsonStr3=sh.makeServiceCall(url_InsertHrv,ServiceHandler.GET);

                                if (jsonStr3 != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject(jsonStr3);

                                        // Getting JSON Array node

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                                }
                            }
                            else
                            {
                                rr.setList_rr_value(valuesRr);
                                list_rr.add(rr);

                                valuesRr=new ArrayList<>();
                                rr=new HistoryRr();

                                hrv.setList_valeur(valuesHrv);
                                hrv.setPnn50(pnn50);
                                list_hrv.add(hrv);

                                //Update database pnn50;

                                url_updatePnn50=url_updatePnn50+"?"+"idHistory="+hrv.getIdHistoryHrv()+"&value="+pnn50;
                                //  url_updatePnn50=url_updatePnn50+"?"+"idHistory="+object_i_rr.getInt("idHistory")+"&value="+pnn50;

                                jsonStr2=sh.makeServiceCall(url_updatePnn50,ServiceHandler.GET);

                                if (jsonStr2 != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject(jsonStr2);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                                }

                                //
                                pnn50=0;
                                valuesHrv=new ArrayList<>();
                                hrv=new HistoryHrv();

                                object_i_rr=array_json_rr.getJSONObject(i);

                                id=object_i_rr.getInt("idHistory");

                                rr.setIdHistoryRr(id);

                                RrValue r = new RrValue();

                                r.setValue(object_i_rr.getDouble("value"));

                                //     r.setTime(object_i_rr.getInt("time"));

                                valuesRr.add(r);

                                //        hrv.setIdHistoryHrv(object_i_rr.getInt("idHistory"));


                                hrv.setIdHistoryHrv(id);
                            }

                        }

                        if(i==array_json_rr.length()-1)
                        {

                            hrv.setList_valeur(valuesHrv);
                            hrv.setPnn50(pnn50);
                            list_hrv.add(hrv);

                            url_updatePnn50=url_updatePnn50+"?"+"idHistory="+hrv.getIdHistoryHrv()+"&value="+pnn50;
                            //  url_updatePnn50=url_updatePnn50+"?"+"idHistory="+object_i_rr.getInt("idHistory")+"&value="+pnn50;


                            jsonStr2=sh.makeServiceCall(url_updatePnn50,ServiceHandler.GET);

                            if (jsonStr2 != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(jsonStr2);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e("ServiceHandler", "Couldn't get any data from the url");
                            }



                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();
            continueProcessingData();
        }
    }

    private class GetMaladie extends AsyncTask<Void, Void, Void> {

        Context context;

        public  GetMaladie(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            //get JSONRR
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            //modifier url pour emmener les rr de ce user donc:
            String userName=sharedPreferences.getString("username", "rien");

            //url_getMaladie=url_getMaladie+"?username="+userName;

            String jsonStr = sh.makeServiceCall(url_getMaladie, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObjRR = new JSONObject(jsonStr);

                    array_maladie= jsonObjRR.getJSONArray("data");

                    String maladie="";

                    List<Symptomes> symptomesMaladie=new ArrayList<>();

                    Maladie m=new Maladie();

                    for(int i=0;i<array_maladie.length();++i) {

                        object_i_maladie=array_maladie.getJSONObject(i);

                        if(maladie.equals(""))
                        {
                            //1er iteration
                            maladie=object_i_maladie.getString("maladie").toString();
                            m=new Maladie();
                            m.setNom(maladie);
                            symptomesMaladie=new ArrayList<>();
                            Symptomes s=new Symptomes();
                            s.setSymptomes(object_i_maladie.getString("symptome"));
                            symptomesMaladie.add(s);
                        }
                        else
                        {
                            if(maladie.equals(object_i_maladie.getString("maladie")))
                            {
                                Symptomes s=new Symptomes();
                                s.setSymptomes(object_i_maladie.getString("maladie"));
                                symptomesMaladie.add(s);
                            }
                            else
                            {
                                m.setList_Ingredient(symptomesMaladie);
                                list_maladie.add(m);

                                symptomesMaladie=new ArrayList<>();
                                m=new Maladie();

                                maladie=object_i_maladie.getString("maladie").toString();
                                m.setNom(maladie);

                                Symptomes s=new Symptomes();
                                s.setSymptomes(object_i_maladie.getString("symptome"));
                                symptomesMaladie.add(s);
                            }

                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            showMaladies();

        }
    }


    private void insertMaladie(int id_hrv,int maladie_position)
    {

        id_hrv_malade=id_hrv;
        if(maladie_position==-1)
        {
            maladie_nom="bloc auriculo-ventriculaire";
        }
        else
        {
            maladie_nom = list_maladie.get(maladie_position).getNom();
        }
        new InsertMaladie(context).execute();

    }

    private class InsertMaladie extends AsyncTask<Void, Void, Void> {

        Context context;

        public  InsertMaladie(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            //get JSONRR
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            String jsonStr;
            String oldUsername= sharedPreferences.getString("username", "rien");

            url_InsertMaladie=url_InsertMaladie+"?"+"idHistory="+id_hrv_malade+"&nom="+maladie_nom;
            jsonStr=sh.makeServiceCall(url_InsertMaladie,ServiceHandler.GET);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();


        }
    }



}
