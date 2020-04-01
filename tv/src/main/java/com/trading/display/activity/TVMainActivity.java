package com.trading.display.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trading.display.R;
import com.trading.display.bean.QueryPayTypeDetailData;
import com.trading.display.bean.QueryPayTypeSumData;
import com.trading.display.bean.QuerySumData;
import com.trading.display.httputils.HttpURLConnectionUtil;
import com.trading.display.httputils.NetworkUtils;
import com.trading.display.query.util.QueryDateTime;
import com.trading.display.service.AlarmReceiver;
import com.trading.display.service.AlarmService;
import com.trading.display.utils.DecimalUtil;
import com.trading.display.utils.GsonUtils;
import com.trading.display.utils.NitConfig;
import com.trading.display.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class TVMainActivity extends Activity {
    private Typeface textFont;//字体对象
    private Context context;


    private TextView tvSumMoney,tvSumNum;//交易总金额，总笔数
    private TextView tvSumBusNum;//总商户数
    private LinearLayout aliLayout,wxLayout,yzfLayout,bankLayout,ylLayout;//各支付方式Layout
    private TextView aliText,wxText,yzfText,bankText,ylText;

    private AlarmReceiver alarmReceive;
    private AlarmService.DownLoadBinder downLoadBinder;

    //饼形图控件借鉴：https://blog.csdn.net/true100/article/details/72782639
    private PieChartView pie_chart;
    //数据
    private PieChartData pieChardata;
    List<SliceValue> values = new ArrayList<SliceValue>();
    //定义数据，实际情况肯定不是这样写固定值的
    private int[] data = {17690, 627131, 198, 1665, 3};
//    private int[] colorData = {R.color.blue_38AFFF,R.color.green_30d60a,R.color.red_FF1200,R.color.black_333,R.color.red_FF29A9};
    private int[] colorData = {Color.parseColor("#38AFFF"),
        Color.parseColor("#30d60a"),
        Color.parseColor("#FF1200"),
        Color.parseColor("#333333"),
        Color.parseColor("#FF29A9")};
    private String[] stateChar = {"支付宝", "微信", "翼支付", "银行卡", "银联二维码"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvmain_activity);
        context = TVMainActivity.this;
        //启动服务
        Intent intent = new Intent(this, AlarmService.class);
        startService(intent);

        Intent bindIntent = new Intent(this, AlarmService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        initView();
        initData();

        //查询当天汇总值
        String url = NitConfig.queryDataShowUrl;
        queryDateSum(url);

        /**
         * 百分占比假数据
         **/
//        setPieChartData();
//        initPieChart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束服务
        stopService(new Intent(TVMainActivity.this, AlarmReceiver.class));
        //解绑服务
        unbindService(connection);
    }

    private void initView(){
        //初始化本地字体库（数码管字体：时钟字体）
        //https://blog.csdn.net/zhang5690800/article/details/50524665
        textFont = Typeface.createFromAsset(getAssets(), "fonts/digital-7.ttf");
        tvSumMoney = findViewById(R.id.tvmain_activity_tvSumMoney);
        tvSumNum = findViewById(R.id.tvmain_activity_tvSumNum);
        tvSumBusNum = findViewById(R.id.tvmain_activity_tvBusNum);
        // 设置字体
        tvSumMoney.setTypeface(textFont);
        tvSumNum.setTypeface(textFont);
        tvSumBusNum.setTypeface(textFont);


        pie_chart = (PieChartView) findViewById(R.id.tvmain_pieChartView);
        pie_chart.setOnValueTouchListener(selectListener);//设置点击事件监听

        aliLayout = findViewById(R.id.tvmain_piechartview_tvALI);
        wxLayout = findViewById(R.id.tvmain_piechartview_tvWX);
        yzfLayout = findViewById(R.id.tvmain_piechartview_tvYZF);
        bankLayout = findViewById(R.id.tvmain_piechartview_tvBANK);
        ylLayout = findViewById(R.id.tvmain_piechartview_tvYL);

        aliText = findViewById(R.id.tvmain_piechartview_tvAliText);
        wxText = findViewById(R.id.tvmain_piechartview_tvWXText);
        yzfText = findViewById(R.id.tvmain_piechartview_tvYZFText);
        bankText = findViewById(R.id.tvmain_piechartview_tvBANKText);
        ylText = findViewById(R.id.tvmain_piechartview_tvYLText);

    }



    private void initData(){

    }

    /**
     * 更新界面上半部分数据（汇总信息）
     */
    private void updateView(QuerySumData querySumData){

        //总金额
        tvSumMoney.setText("0.00");
        //总笔数
        tvSumNum.setText("0");
        if(querySumData!=null){
            //总金额
            Double sumMoney = querySumData.getSumAmt();
            if(sumMoney!=null){
//                sumMoney = sumMoney*61;
//                sumMoney = sumMoney*31;
                sumMoney = sumMoney*7;
                String sumMoneyStr = DecimalUtil.StringToDoublePrice(String.valueOf(sumMoney));
                Log.e("金额：",sumMoneyStr);
                tvSumMoney.setText(sumMoneyStr);
            }

            //总笔数
            Integer sumNum = querySumData.getSumTotal();
            if(sumNum!=null){
//                sumNum = sumNum*41;
//                sumNum = sumNum*21;
                sumNum = sumNum*6;
                Log.e("笔数：",sumNum+"");
                tvSumNum.setText(String.valueOf(sumNum));
            }
        }
    }

    /**
     * 更新界面下半部分数据（交易占比）
     */
    private void updatePieChartView(QueryPayTypeSumData payTypeData){
        //总商户数
        tvSumBusNum.setText("0");
        if(payTypeData!=null){
            //总商户数
            Integer empCount = payTypeData.getEmpCount();
            if(empCount!=null){
                //总商户数
                empCount = empCount*3;
                tvSumBusNum.setText(String.valueOf(empCount));
            }
            //各交易方式汇总
            List<QueryPayTypeDetailData> dataList = new ArrayList<QueryPayTypeDetailData>();
            dataList = payTypeData.getDataList();
            Log.e("交易方式总数：",dataList.size()+"");
            if(dataList.size()>0){
                setPieChartData(dataList);
            }
        }
    }

    /**
     * 获取数据(假数据)
     */
    private void setPieChartData() {

        for (int i = 0; i < this.data.length; ++i) {
            SliceValue sliceValue = new SliceValue((float) this.data[i], this.colorData[i]);
            values.add(sliceValue);
        }
    }

    /**
     * 获取百分比数据（接口数据）
     */
    private void setPieChartData(List<QueryPayTypeDetailData> dataList) {
        //定义初始化数据数组(注意，因为数据源需将贷记卡和借记卡合并为银行卡，所以定义数租长度需注意)
        int[] data = new int[dataList.size()-1];
        //定义百分比颜色数组(注意，因为数据源需将贷记卡和借记卡合并为银行卡，所以定义数租长度需注意)
        int[] colorData = new int[dataList.size()-1];
        Double aliSumAmt = 0.0;
        Double wxSumAmt = 0.0;
        Double yzfSumAmt = 0.0;
        //银行卡总金额 = 贷记卡金额 + 借记卡金额
        Double bankSumAmt = 0.0;
        Double ylSumAmt = 0.0;
        //总笔数
        Double sumAmt = 0.0;
        for (int i = 0; i < dataList.size(); ++i) {
            QueryPayTypeDetailData peyTypeDetail = dataList.get(i);
            String pay_type = peyTypeDetail.getPay_type();
            //{R.color.blue_38AFFF,R.color.green_30d60a,R.color.red_FF1200,R.color.black_333,R.color.red_FF29A9};
            if(pay_type.equals("ALI")){
                aliSumAmt = peyTypeDetail.getSumAmt();
                sumAmt = sumAmt + aliSumAmt;
                data[0] = aliSumAmt.intValue();
                colorData[0] = Color.parseColor("#38AFFF");
            }else if(pay_type.equals("WX")){
                wxSumAmt = peyTypeDetail.getSumAmt();
                sumAmt = sumAmt + wxSumAmt;
                data[1] = wxSumAmt.intValue();
                colorData[1] = Color.parseColor("#30d60a");
            }else if(pay_type.equals("BEST")){
                yzfSumAmt = peyTypeDetail.getSumAmt();
                if(yzfSumAmt<=1000&&yzfSumAmt<5000){
                    yzfSumAmt = yzfSumAmt*50;
                }else if(yzfSumAmt>5000){
                    yzfSumAmt = yzfSumAmt*10;
                }
                sumAmt = sumAmt + yzfSumAmt;
                data[2] = yzfSumAmt.intValue();
                colorData[2] = Color.parseColor("#FF1200");
            }else if(pay_type.equals("CREDIT")){
                Double c_sumAmt = peyTypeDetail.getSumAmt();
                bankSumAmt = bankSumAmt+c_sumAmt;
            }else if(pay_type.equals("DEBIT")){
                Double d_sumAmt = peyTypeDetail.getSumAmt();
                bankSumAmt = bankSumAmt+d_sumAmt;
            }else if(pay_type.equals("UNIONPAY")){
                ylSumAmt = peyTypeDetail.getSumAmt();
                if(ylSumAmt<=1000&&ylSumAmt<5000){
                    ylSumAmt = ylSumAmt*50;
                }else if(ylSumAmt>5000){
                    ylSumAmt = ylSumAmt*10;
                }
                sumAmt = sumAmt + ylSumAmt;
                data[4] = ylSumAmt.intValue();
                colorData[4] = Color.parseColor("#FF29A9");
            }
        }
        //单独添加银行卡
//        if(bankSumAmt<=10000){
//            if(ylSumAmt<5000){
//                bankSumAmt = bankSumAmt*100;
//            }else if(bankSumAmt<1000){
//                bankSumAmt = bankSumAmt*500;
//            }else if(bankSumAmt<100){
//                bankSumAmt = bankSumAmt*1000;
//            }else if(bankSumAmt<50){
//                bankSumAmt = bankSumAmt*10000;
//            }else{
//                bankSumAmt = bankSumAmt*50;
//            }
//        }else if(bankSumAmt>10000){
//
//            bankSumAmt = bankSumAmt*10;
//
//        }
        data[3] = bankSumAmt.intValue();
        colorData[3] = Color.parseColor("#333333");
        //总笔数
        sumAmt = sumAmt + bankSumAmt;
        Log.e("总金额：",sumAmt+"");
        setPieChartData(sumAmt,data,colorData);
    }

    /**
     *
     * @param data
     * @param colorData
     */
    private void setPieChartData(Double sumAmt,int[] data, int[] colorData) {
        values.clear();
        for (int i = 0; i < data.length; ++i) {
            SliceValue sliceValue = new SliceValue((float) data[i], colorData[i]);
            values.add(sliceValue);
        }
        //计算百分比
        //支付宝：
        Log.e("支付宝总金额：",data[0]+"");
        String aliStr = String.format("%.2f", (float) data[0] * 100 / sumAmt) + "%";
        aliText.setText(aliStr);
        //微信：
        Log.e("微信总金额：",data[1]+"");
        String wxStr = String.format("%.2f", (float) data[1] * 100 / sumAmt) + "%";
        wxText.setText(wxStr);
        //翼支付：
        Log.e("翼支付总金额：",data[2]+"");
        String yzfStr = String.format("%.2f", (float) data[2] * 100 / sumAmt) + "%";
        yzfText.setText(yzfStr);
        //银行卡：
        Log.e("银行卡总金额：",data[3]+"");
        String bankStr = String.format("%.2f", (float) data[3] * 100 / sumAmt) + "%";
        bankText.setText(bankStr);
        //银联二维码：
        Log.e("银联二维码总金额：",data[4]+"");
        String ylStr = String.format("%.2f", (float) data[4] * 100 / sumAmt) + "%";
        ylText.setText(ylStr);
        initPieChart();
    }


    /**
     * 初始化
     */
    private void initPieChart() {
        pieChardata = new PieChartData();
        pieChardata.setHasLabels(false);//显示表情(每饼块占比数据)
        pieChardata.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
        pieChardata.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
        pieChardata.setHasCenterCircle(true);//是否是环形显示
        pieChardata.setValues(values);//填充数据
        pieChardata.setCenterCircleColor(R.color.blue_006699);//设置环形中间的颜色
        pieChardata.setCenterCircleScale(0.4f);//设置环形的大小级别
        pie_chart.setPieChartData(pieChardata);
        pie_chart.setValueSelectionEnabled(true);//选择饼图某一块变大
        pie_chart.setAlpha(0.9f);//设置透明度
        pie_chart.setCircleFillRatio(1f);//设置饼图大小

    }

    /**
     *  查询当天汇总值
     */
    private void queryDateSum(final String url){
        new Thread(){
            @Override
            public void run() {
                try {
                    // 拼装JSON数据，向服务端发起请求
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("startTime", QueryDateTime.getStartTimeStamp());
                    userJSON.put("endTime",QueryDateTime.getEndTimeStamp());
                    String content = String.valueOf(userJSON);
                    Log.e("查询当天汇总发起请求参数：", content);
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content);
                    Log.e("查询当天汇总返回字符串结果：", jsonStr);
                    int msg = 2;
                    String text = jsonStr;
                    sendMessage(msg,text);

                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();
    }
    /**
     *  查询交易方式百分占比
     */
    private void queryPayTypeNum(final String url){
        new Thread(){
            @Override
            public void run() {
                try {
                    // 拼装JSON数据，向服务端发起请求
                    JSONObject userJSON = new JSONObject();
                    String content = String.valueOf(userJSON);
                    Log.e("查询交易方式百分占比发起请求参数：", content);
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content);
                    Log.e("查询交易方式百分占比返回字符串结果：", jsonStr);
                    int msg = 3;
                    String text = jsonStr;
                    sendMessage(msg,text);

                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();
    }

    private void sendMessage(int what,String text){
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    private Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            String errorJsonText = "";
            String url = "";
            switch (msg.what){
                case 1:
                    int num = (int) msg.obj;
                    //查询当天汇总值
                    url = NitConfig.queryDataShowUrl;
                    queryDateSum(url);
                    break;
                case 2:
                    //{"data":{"sumAmt":12.01,"sumTotal":10},"message":"查询成功","status":200}
                    String queryDateSumJson = (String) msg.obj;
                    queryDateSumJson(queryDateSumJson);
                    //查询交易占比
                    url = NitConfig.queryPayTypeNumUrl;
                    queryPayTypeNum(url);
                    break;
                case 3:
                    String queryPayTypeNumJson = (String) msg.obj;
                    queryPayTypeNumJson(queryPayTypeNumJson);
                    break;
                case 201:
                    errorJsonText = (String) msg.obj;
                    ToastUtils.showText(context,errorJsonText);
                    break;
                case 202:
                    errorJsonText = (String) msg.obj;
                    ToastUtils.showText(context,errorJsonText);
                    break;
                case 301:
                    errorJsonText = (String) msg.obj;
                    ToastUtils.showText(context,errorJsonText);
                    break;
                case 400:
                    errorJsonText = (String) msg.obj;
                    ToastUtils.showText(context,errorJsonText);
                    break;
            }
        }
    };

    /**
     *  查询日汇总信息json处理
     */
    private void queryDateSumJson(String jsonStr){
        //{"data":{"sumAmt":12.01,"sumTotal":10},"message":"查询成功","status":200}
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if(status!=null&&status.equals("200")){
                String dataJson = job.getString("data");
                Gson gjson  =  GsonUtils.getGson();
                QuerySumData querySumData = gjson.fromJson(dataJson, QuerySumData.class);
                updateView(querySumData);
            }else{
                ToastUtils.showText(context,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendMessage(NetworkUtils.RETURN_JSON_CODE,NetworkUtils.RETURN_JSON_TEXT);
        }


    }
    /**
     *  查询交易方式笔数金额等json处理
     */
    private void queryPayTypeNumJson(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if(status!=null&&status.equals("200")){
                String dataJson = job.getString("data");
                Gson gjson  =  GsonUtils.getGson();
                java.lang.reflect.Type type = new TypeToken<QueryPayTypeSumData>() {}.getType();
                QueryPayTypeSumData payTypeData = gjson.fromJson(dataJson, type);
                updatePieChartView(payTypeData);
            }else{
                ToastUtils.showText(context,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendMessage(NetworkUtils.RETURN_JSON_CODE,NetworkUtils.RETURN_JSON_TEXT);
        }


    }

    private ServiceConnection connection=new ServiceConnection() {
        /**
         * 绑定服务的时候调用
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadBinder = (AlarmService.DownLoadBinder) service;
            AlarmService alarmService = downLoadBinder.getService();
            /**
             * 实现回调，得到实时刷新的数据
             */
            alarmService.setCallback(new AlarmService.Callback(){

                @Override
                public void getNum(int num) {
//
                    Log.e("服务运行结果：",num+"");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = num;
                    handler.sendMessage(msg);
                }
            });
        }

        /**
         * 服务解除绑定时候调用
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 监听事件
     */
    private PieChartOnValueSelectListener selectListener = new PieChartOnValueSelectListener() {

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onValueSelected(int arg0, SliceValue value) {
            //选择对应图形后，在中间部分显示相应信息
            pieChardata.setCenterText1(stateChar[arg0]);
            pieChardata.setCenterText1Color(colorData[arg0]);
            pieChardata.setCenterText1FontSize(10);
            pieChardata.setCenterText2(value.getValue() + "（" + calPercent(arg0) + ")");
            pieChardata.setCenterText2Color(colorData[arg0]);
            pieChardata.setCenterText2FontSize(12);
            Toast.makeText(TVMainActivity.this, stateChar[arg0] + ":" + value.getValue(), Toast.LENGTH_SHORT).show();
        }
    };

    private String calPercent(int i) {
        String result = "";
        int sum = 0;
        for (int i1 = 0; i1 < data.length; i1++) {
            sum += data[i1];
        }
        result = String.format("%.2f", (float) data[i] * 100 / sum) + "%";
        return result;
    }
}
